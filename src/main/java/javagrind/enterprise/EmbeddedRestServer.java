package javagrind.enterprise;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Embedded multithreaded HTTP REST server (`jdk.httpserver`) paired with Java 17's
 * {@link HttpClient}, protected by {@link TokenBucketRateLimiter} and backed by
 * {@link ThreadSafeLruCache} + JMX {@link ManagementFactory} telemetry.
 */
@EnterpriseSkill(
        category = "Networking, REST & JVM Telemetry",
        name = "Embedded Multithreaded HTTP REST Server & Java 17 HttpClient",
        summary = "Loopback REST microservice exposing JMX heap/thread health, LRU-cached skills JSON, and HTTP 429 rate limiting."
)
public final class EmbeddedRestServer implements AutoCloseable {

    private final TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(5, 1.5);
    private final ThreadSafeLruCache<String, String> responseCache = new ThreadSafeLruCache<>(16);
    private final Supplier<String> progressJsonSupplier;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    private HttpServer server;
    private ExecutorService pool;
    private int boundPort = -1;

    public EmbeddedRestServer(Supplier<String> progressJsonSupplier) {
        this.progressJsonSupplier = progressJsonSupplier;
    }

    public synchronized boolean isRunning() {
        return server != null;
    }

    public synchronized int getPort() {
        return boundPort;
    }

    public synchronized int start() throws IOException {
        if (server != null) {
            return boundPort;
        }
        // Port 0 selects an available ephemeral port on 127.0.0.1 automatically
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        pool = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "EmbeddedREST-Worker");
            t.setDaemon(true);
            return t;
        });
        server.setExecutor(pool);

        server.createContext("/api/v1/health", ex -> handleWithMiddleware(ex, EmbeddedRestServer::buildJvmHealthJson));
        server.createContext("/api/v1/skills", ex -> handleWithMiddleware(ex, () ->
                responseCache.get("skills_catalog").orElseGet(() -> {
                    String computed = SkillRegistryScanner.toJsonArray();
                    responseCache.put("skills_catalog", computed);
                    return computed;
                })
        ));
        server.createContext("/api/v1/progress", ex -> handleWithMiddleware(ex, progressJsonSupplier));

        server.start();
        boundPort = server.getAddress().getPort();
        AuditEventBus.getInstance().publish("REST-Server", "SERVER_STARTED", "Listening on http://127.0.0.1:" + boundPort);
        return boundPort;
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
        boundPort = -1;
        AuditEventBus.getInstance().publish("REST-Server", "SERVER_STOPPED", "Graceful shutdown complete");
    }

    @Override
    public void close() {
        stop();
    }

    public ThreadSafeLruCache.CacheStats cacheStats() {
        return responseCache.stats();
    }

    public void resetRateLimiter() {
        rateLimiter.resetAll();
    }

    public ExecutionOutcome<String> invokeLocalGet(String path) {
        long t0 = System.currentTimeMillis();
        int port;
        synchronized (this) {
            if (server == null) {
                return new ExecutionOutcome.Failure<>("NOT_RUNNING", "Start the Embedded REST Server first!");
            }
            port = boundPort;
        }
        try {
            URI uri = URI.create("http://127.0.0.1:" + port + path);
            HttpRequest req = HttpRequest.newBuilder(uri)
                    .GET()
                    .header("Accept", "application/json")
                    .header("X-Client-Id", "studio-local-client")
                    .timeout(Duration.ofSeconds(2))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            long elapsed = System.currentTimeMillis() - t0;

            if (resp.statusCode() == 429) {
                return new ExecutionOutcome.RateLimited<>("studio-local-client", 700);
            }
            String formatted = "HTTP " + resp.statusCode() + " OK (" + elapsed + " ms) — GET " + uri + "\n"
                    + "X-Cache-Stats: " + resp.headers().firstValue("X-Cache-Stats").orElse("n/a") + "\n"
                    + "------------------------------------------------------------\n"
                    + resp.body();
            return new ExecutionOutcome.Success<>(formatted, elapsed);
        } catch (Exception ex) {
            return new ExecutionOutcome.Failure<>("HTTP_CLIENT_ERR", ex.getMessage());
        }
    }

    private void handleWithMiddleware(HttpExchange ex, Supplier<String> payloadSupplier) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                writeJson(ex, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            String clientId = ex.getRequestHeaders().getFirst("X-Client-Id");
            if (clientId == null || clientId.isBlank()) {
                clientId = "anonymous";
            }

            if (!rateLimiter.tryAcquire(clientId)) {
                AuditEventBus.getInstance().publish("REST-Gateway", "HTTP_429_THROTTLED",
                        ex.getRequestURI().getPath() + " (client=" + clientId + ")");
                writeJson(ex, 429, "{\"error\":\"Too Many Requests\",\"retryAfterMs\":700}");
                return;
            }

            String json = payloadSupplier.get();
            ThreadSafeLruCache.CacheStats cs = responseCache.stats();
            ex.getResponseHeaders().set("X-Cache-Stats",
                    String.format("hits=%d,misses=%d,hitRatio=%.1f%%", cs.hits(), cs.misses(), cs.hitRatioPercent()));
            AuditEventBus.getInstance().publish("REST-Gateway", "HTTP_200_OK", ex.getRequestURI().getPath());
            writeJson(ex, 200, json);
        } catch (Exception err) {
            writeJson(ex, 500, "{\"error\":\"" + err.getClass().getSimpleName() + "\"}");
        }
    }

    private void writeJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static String buildJvmHealthJson() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();

        long usedMb = mem.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long maxMb = mem.getHeapMemoryUsage().getMax() / (1024 * 1024);
        long uptimeSec = rt.getUptime() / 1000;

        return String.format("""
                {
                  "status": "UP",
                  "javaVersion": "%s",
                  "vmName": "%s",
                  "uptimeSeconds": %d,
                  "heapUsedMB": %d,
                  "heapMaxMB": %d,
                  "liveThreads": %d,
                  "availableProcessors": %d
                }""",
                System.getProperty("java.version"),
                rt.getVmName().replace("\"", ""),
                uptimeSec,
                usedMb,
                maxMb,
                threads.getThreadCount(),
                Runtime.getRuntime().availableProcessors()
        );
    }
}
