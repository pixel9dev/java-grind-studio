package javagrind.enterprise;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates enterprise non-blocking fan-out / fan-in orchestration with
 * {@link CompletableFuture}, timeout resilience, graceful fallback (`exceptionally`),
 * Strategy pricing rules, and a fluent immutable Builder.
 */
@EnterpriseSkill(
        category = "Async Concurrency & GoF Patterns",
        name = "CompletableFuture Fan-Out Pipeline + Builder & Strategy",
        summary = "Parallel non-blocking service composition with orTimeout, exceptionally fallback, Strategy & Builder."
)
public final class AsyncPipelineOrchestrator {

    private AsyncPipelineOrchestrator() {}

    /**
     * Strategy Pattern: Pluggable discount policies without `if/else` sprawl (Open/Closed Principle!).
     */
    @FunctionalInterface
    public interface DiscountStrategy {
        double applyDiscount(double subtotal);

        static DiscountStrategy standard() { return subtotal -> subtotal; }
        static DiscountStrategy seasonalTenPercent() { return subtotal -> subtotal * 0.90; }
        static DiscountStrategy enterpriseTier() { return subtotal -> subtotal >= 1000 ? subtotal * 0.82 : subtotal * 0.92; }
    }

    /**
     * Fluent Builder Pattern producing an immutable CheckoutQuote object.
     */
    public static final class CheckoutQuote {
        private final String sku;
        private final double basePrice;
        private final double discountedPrice;
        private final double taxRate;
        private final double finalTotal;
        private final String inventoryStatus;
        private final long totalWallTimeMs;

        private CheckoutQuote(Builder b) {
            this.sku = Objects.requireNonNull(b.sku);
            this.basePrice = b.basePrice;
            this.discountedPrice = b.discountedPrice;
            this.taxRate = b.taxRate;
            this.finalTotal = b.finalTotal;
            this.inventoryStatus = Objects.requireNonNull(b.inventoryStatus);
            this.totalWallTimeMs = b.totalWallTimeMs;
        }

        public String summaryText() {
            return String.format(
                    "SKU=%s | Base=%.2f | Discounted=%.2f | Tax=%.0f%% | Final=%.2f | Stock=%s | Parallel Latency=%d ms",
                    sku, basePrice, discountedPrice, taxRate * 100, finalTotal, inventoryStatus, totalWallTimeMs
            );
        }

        public static final class Builder {
            private String sku = "UNKNOWN";
            private double basePrice;
            private double discountedPrice;
            private double taxRate;
            private double finalTotal;
            private String inventoryStatus = "UNKNOWN";
            private long totalWallTimeMs;

            public Builder sku(String sku) { this.sku = sku; return this; }
            public Builder basePrice(double v) { this.basePrice = v; return this; }
            public Builder discountedPrice(double v) { this.discountedPrice = v; return this; }
            public Builder taxRate(double v) { this.taxRate = v; return this; }
            public Builder finalTotal(double v) { this.finalTotal = v; return this; }
            public Builder inventoryStatus(String s) { this.inventoryStatus = s; return this; }
            public Builder totalWallTimeMs(long ms) { this.totalWallTimeMs = ms; return this; }
            public CheckoutQuote build() { return new CheckoutQuote(this); }
        }
    }

    /**
     * Runs 3 simulated downstream microservice calls IN PARALLEL (~80ms total instead of ~240ms sequential!),
     * including simulated timeout fallback on the tax service when `simulateTaxFault` is true.
     */
    public static ExecutionOutcome<CheckoutQuote> executeParallelCheckout(
            String sku, DiscountStrategy strategy, boolean simulateTaxFault) {
        long t0 = System.currentTimeMillis();
        ExecutorService ioPool = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r, "AsyncPipeline-IO");
            t.setDaemon(true);
            return t;
        });

        try {
            CompletableFuture<Double> catalogPriceFuture = CompletableFuture.supplyAsync(() -> {
                sleepMs(75);
                return 1250.00;
            }, ioPool);

            CompletableFuture<Double> taxRateFuture = CompletableFuture.supplyAsync(() -> {
                if (simulateTaxFault) {
                    throw new RuntimeException("Remote Tax Service 503 Unavailable");
                }
                sleepMs(75);
                return 0.18;
            }, ioPool)
            .orTimeout(300, TimeUnit.MILLISECONDS)
            .exceptionally(ex -> 0.15); // Resilient default tax fallback!

            CompletableFuture<String> inventoryFuture = CompletableFuture.supplyAsync(() -> {
                sleepMs(75);
                return "IN_STOCK (Warehouse-BLR-02)";
            }, ioPool);

            CheckoutQuote quote = catalogPriceFuture
                    .thenCombine(taxRateFuture, (base, tax) -> {
                        double discounted = strategy.applyDiscount(base);
                        double total = Math.round(discounted * (1.0 + tax) * 100.0) / 100.0;
                        return new CheckoutQuote.Builder()
                                .sku(sku)
                                .basePrice(base)
                                .discountedPrice(discounted)
                                .taxRate(tax)
                                .finalTotal(total);
                    })
                    .thenCombine(inventoryFuture, (builder, stock) -> builder
                            .inventoryStatus(stock + (simulateTaxFault ? " [Tax Fallback 15% Applied]" : ""))
                            .totalWallTimeMs(System.currentTimeMillis() - t0)
                            .build())
                    .join();

            return new ExecutionOutcome.Success<>(quote, System.currentTimeMillis() - t0);
        } catch (Exception ex) {
            return new ExecutionOutcome.Failure<>("PIPELINE_ERR", ex.getMessage());
        } finally {
            ioPool.shutdownNow();
        }
    }

    private static void sleepMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
