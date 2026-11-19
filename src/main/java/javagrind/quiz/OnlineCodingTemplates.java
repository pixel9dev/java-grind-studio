package javagrind.quiz;

import javagrind.util.TestRunner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * ==========================================================================
 * ROUND 2 SURVIVAL GUIDE: Online Assessment (OA) Input/Output Templates
 * ==========================================================================
 * Many candidates know the algorithm, THEN lose 20 minutes in online coding
 * assessments because `Scanner.nextInt()` + `Scanner.nextLine()` skips a line!
 *
 * Run this file (`▶`) to see both safe templates work on simulated STDIN!
 */
public class OnlineCodingTemplates {

    public static void main(String[] args) throws IOException {
        TestRunner.printBanner("ROUND 2 OA TEMPLATES: Safe Scanner & Fast BufferedReader I/O");

        scannerNewlineTrapDemo();
        fastBufferedReaderDemo();
    }

    private static void scannerNewlineTrapDemo() {
        TestRunner.printSection("1. THE #1 OA BUG: `nextInt()` followed by `nextLine()`!");
        // Simulated input from an online judge:
        // Line 1: 2          (number of candidates)
        // Line 2: Aarav Shah (full name with spaces)
        // Line 3: 5          (array size)
        // Line 4: 10 20 30 40 50
        String simulatedStdin = "2\nAarav Shah\n5\n10 20 30 40 50\n";

        Scanner sc = new Scanner(simulatedStdin);

        int count = sc.nextInt();
        sc.nextLine(); // <-- CRITICAL! Consumes the leftover '\n' after `2`! Without this, `fullName` becomes ""!
        String fullName = sc.nextLine();

        int n = sc.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }

        System.out.println("Parsed safely -> count=" + count + ", fullName=\"" + fullName
                + "\", arr=" + Arrays.toString(arr));
    }

    private static void fastBufferedReaderDemo() throws IOException {
        TestRunner.printSection("2. Fast I/O Template (`BufferedReader` + `StringTokenizer`) for N >= 10^5");
        String simulatedStdin = "3\n100 200 300\n";

        // In real OA, replace `new StringReader(simulatedStdin)` with `new InputStreamReader(System.in)`!
        FastReader in = new FastReader(new BufferedReader(new StringReader(simulatedStdin)));
        int n = in.nextInt();
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += in.nextLong();
        }
        System.out.println("FastReader parsed N=" + n + ", sum=" + sum);
    }

    /**
     * Copy-pasteable FastReader for large-input coding rounds:
     * Just construct with `new BufferedReader(new InputStreamReader(System.in))`.
     */
    static class FastReader {
        private final BufferedReader br;
        private StringTokenizer st;

        FastReader(BufferedReader br) {
            this.br = br;
        }

        String next() throws IOException {
            while (st == null || !st.hasMoreTokens()) {
                String line = br.readLine();
                if (line == null) return null;
                st = new StringTokenizer(line);
            }
            return st.nextToken();
        }

        int nextInt() throws IOException {
            return Integer.parseInt(next());
        }

        long nextLong() throws IOException {
            return Long.parseLong(next());
        }

        String nextLine() throws IOException {
            st = null;
            return br.readLine();
        }
    }
}
