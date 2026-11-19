package javagrind.learn;

import javagrind.util.TestRunner;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

/**
 * MODULE 4 (LEARN): Java Collections Framework — Your #1 Coding Round Weapon!
 *
 * Knowing these exact methods turns 30-line brute-force code into 6-line O(n) code.
 */
public class L04_CollectionsAndGenerics {

    public static void main(String[] args) {
        TestRunner.printBanner("LEARN 04: Collections Framework (List, Set, Map, Deque, Heap)");

        listsAndSorting();
        setsForFastLookup();
        hashMapPowerMethods();
        stackAndQueueWithArrayDeque();
        priorityQueueHeaps();

        System.out.println("\nReady to practice Collections? Click -> (G05_CollectionsAndHashingGrind.java:1)");
    }

    private static void listsAndSorting() {
        TestRunner.printSection("1. ArrayList & Custom Sorting with Comparator");
        // Generics ONLY accept Reference types: List<Integer>, NEVER List<int>
        List<String> langs = new ArrayList<>(List.of("Python", "C", "Java", "Go", "Rust"));
        langs.add("Kotlin");
        langs.remove("C"); // Removes by value (for Integer list, `remove(Integer.valueOf(5))` removes value 5!)

        // Sort by length ascending, then alphabetically on ties!
        langs.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        System.out.println("Sorted by len then alpha: " + langs);
    }

    private static void setsForFastLookup() {
        TestRunner.printSection("2. HashSet O(1) vs LinkedHashSet (Insertion Order) vs TreeSet O(log n Sorted)");
        Set<Integer> seen = new HashSet<>();
        int[] nums = {4, 2, 4, 1, 2};
        for (int n : nums) {
            boolean wasNew = seen.add(n); // returns FALSE if already in set!
            if (!wasNew) {
                System.out.println("Duplicate detected in O(1): " + n);
            }
        }
        System.out.println("Unique elements: " + seen);
    }

    private static void hashMapPowerMethods() {
        TestRunner.printSection("3. HashMap Power Idioms (Memorize getOrDefault & computeIfAbsent!)");
        String[] words = {"java", "code", "java", "drive", "java", "code"};

        // Idiom A: Frequency Counter in 1 line
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) {
            freq.put(w, freq.getOrDefault(w, 0) + 1);
        }
        System.out.println("Frequencies : " + freq);

        // Idiom B: Grouping into List values in 1 line (`computeIfAbsent`)
        Map<Integer, List<String>> byLength = new HashMap<>();
        for (String w : freq.keySet()) {
            byLength.computeIfAbsent(w.length(), k -> new ArrayList<>()).add(w);
        }
        System.out.println("Grouped len : " + byLength);

        // Idiom C: Iterating entries cleanly
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            System.out.println("  key=" + entry.getKey() + " -> count=" + entry.getValue());
        }

        // Remember: LinkedHashMap preserves insertion order; TreeMap keeps keys sorted O(log n)!
        Map<String, Integer> sortedMap = new TreeMap<>(freq);
        Map<String, Integer> orderedMap = new LinkedHashMap<>();
        orderedMap.put("first", 1);
        orderedMap.put("second", 2);
        System.out.println("TreeMap (sorted keys): " + sortedMap + " | LinkedHashMap: " + orderedMap);
    }

    private static void stackAndQueueWithArrayDeque() {
        TestRunner.printSection("4. Stack (LIFO) & Queue (FIFO) -> ALWAYS Prefer `ArrayDeque`!");
        // Why not `new Stack<>()`? Legacy synchronized class from Java 1.0! `ArrayDeque` is faster.
        Deque<Character> stack = new ArrayDeque<>();
        stack.push('(');
        stack.push('[');
        System.out.println("Stack peek=" + stack.peek() + ", pop=" + stack.pop() + ", next pop=" + stack.pop());

        Deque<String> queue = new ArrayDeque<>(); // Great for BFS!
        queue.offer("Round-1-MCQ");
        queue.offer("Round-2-Coding");
        queue.offer("Round-3-Interview");
        System.out.println("Queue poll (FIFO): " + queue.poll() + " -> next is " + queue.peek());
    }

    private static void priorityQueueHeaps() {
        TestRunner.printSection("5. PriorityQueue: Min-Heap (Default) & Max-Heap");
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // Smallest on top!
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder()); // Largest on top!

        for (int n : new int[]{30, 10, 50, 20}) {
            minHeap.offer(n);
            maxHeap.offer(n);
        }
        System.out.println("Min-Heap poll() -> " + minHeap.poll() + " | Max-Heap poll() -> " + maxHeap.poll());
    }
}
