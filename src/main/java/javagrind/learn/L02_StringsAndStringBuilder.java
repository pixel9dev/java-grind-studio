package javagrind.learn;

import javagrind.util.TestRunner;
import java.util.Arrays;

/**
 * MODULE 2 (LEARN): Strings, String Pool, StringBuilder & Character Utilities
 *
 * Almost EVERY Java coding drive has at least 1 String manipulation problem
 * and 2 MCQs on String Pool / Immutability!
 */
public class L02_StringsAndStringBuilder {

    public static void main(String[] args) {
        TestRunner.printBanner("LEARN 02: Strings, String Pool, StringBuilder & Character APIs");

        stringPoolAndEquality();
        essentialStringMethods();
        whyStringBuilderMatters();
        characterTricksForCodingRounds();

        System.out.println("\nReady to practice? Click -> (G02_StringsGrind.java:1)");
    }

    private static void stringPoolAndEquality() {
        TestRunner.printSection("1. String Immutability & String Constant Pool (SCP)");
        String s1 = "Java";               // Placed in String Constant Pool (inside Heap)
        String s2 = "Java";               // Reuses the EXACT same pooled object!
        String s3 = new String("Java");   // Forces a brand-new object on the Heap
        String s4 = s3.intern();          // Returns the canonical String Pool reference

        System.out.println("s1 == s2           : " + (s1 == s2) + "  (Both point to String Pool)");
        System.out.println("s1 == s3           : " + (s1 == s3) + " (s3 is a distinct Heap object)");
        System.out.println("s1 == s4 (intern)  : " + (s1 == s4) + "  (intern() resolves to Pool)");
        System.out.println("s1.equals(s3)      : " + s1.equals(s3) + "  (ALWAYS use .equals() for content!)");

        // Immutability trap: calling s1.concat() or s1.toUpperCase() returns a NEW String!
        s1.toUpperCase(); // Return value ignored -> s1 is STILL "Java"!
        System.out.println("After ignored s1.toUpperCase(), s1 is still: " + s1);
    }

    private static void essentialStringMethods() {
        TestRunner.printSection("2. Must-Know String Methods for Coding Rounds (Memorize these!)");
        String raw = "  Hello   Java  Drive 2026  ";

        System.out.println("length()               : " + raw.length() + "  (Method! Notice parentheses, unlike array.length)");
        System.out.println("trim() / strip()       : '" + raw.strip() + "'");
        System.out.println("substring(start, end)  : '" + "Interview".substring(0, 5) + "' (start inclusive, end EXCLUSIVE!)");
        System.out.println("indexOf / contains     : idx=" + "banana".indexOf("na") + ", last=" + "banana".lastIndexOf("na"));

        // Splitting words cleanly (handles multiple spaces using regex \\s+)
        String[] words = raw.trim().split("\\s+");
        System.out.println("trim().split(\"\\\\s+\")   : " + Arrays.toString(words));
        System.out.println("String.join(\"-\", words): " + String.join("-", words));

        // Converting between String, char[], and int
        char[] chars = "listen".toCharArray();
        Arrays.sort(chars);
        String sortedKey = new String(chars); // Great canonical key for Anagrams!
        int parsed = Integer.parseInt("42");
        String backToStr = String.valueOf(parsed);
        System.out.println("Sorted char[] -> String: " + sortedKey + " | Parsed int: " + backToStr);
    }

    private static void whyStringBuilderMatters() {
        TestRunner.printSection("3. String (+) in Loop = O(n^2) Trap vs StringBuilder = O(n)");
        // NEVER do `res = res + ch` inside a loop of 100,000 chars — it copies the entire string each iteration!
        // Use StringBuilder (fast, NOT synchronized) — vs StringBuffer (thread-safe, slower, legacy).
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i);
            if (i < 5) sb.append("->");
        }
        System.out.println("Built string     : " + sb.toString());
        System.out.println("charAt / setChar : first='" + sb.charAt(0) + "', length=" + sb.length());
        System.out.println("Reversed         : " + new StringBuilder("racecar_test").reverse());
    }

    private static void characterTricksForCodingRounds() {
        TestRunner.printSection("4. Character Class & Frequency Array (`ch - 'a'`) Tricks");
        char c = 'G';
        System.out.println("isLetterOrDigit('G') : " + Character.isLetterOrDigit(c));
        System.out.println("isDigit('9')         : " + Character.isDigit('9'));
        System.out.println("toLowerCase('G')     : " + Character.toLowerCase(c));

        // 0-indexed alphabet bucket: 'a' - 'a' = 0, 'b' - 'a' = 1, ..., 'z' - 'a' = 25
        String word = "apple";
        int[] freq = new int[26];
        for (char ch : word.toCharArray()) {
            freq[ch - 'a']++;
        }
        System.out.println("Frequency of 'p' in 'apple': " + freq['p' - 'a']);
        System.out.println("Digit char '7' to int 7    : " + ('7' - '0'));
    }
}
