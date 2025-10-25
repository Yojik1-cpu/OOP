package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeapSortTest {
    @Test
    void testSortBasicArray() {
        int[] input = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortAlreadySorted() {
        int[] input = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortReverseSorted() {
        int[] input = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortWithDuplicates() {
        int[] input = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        int[] expected = {1, 1, 2, 3, 4, 5, 5, 6, 9};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortSingleElement() {
        int[] input = {52};
        int[] expected = {52};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortEmptyArray() {
        int[] input = {};
        int[] expected = {};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortNullArray() {
        int[] input = null;
        int[] result = HeapSort.heapsort(input);
        assertNull(result);
    }

    @Test
    void testSortNegativeNumbers() {
        int[] input = {-3, -1, -4, -2, -5};
        int[] expected = {-5, -4, -3, -2, -1};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortMixedNumbers() {
        int[] input = {-3, 5, 0, -1, 2};
        int[] expected = {-3, -1, 0, 2, 5};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortLargeArray() {
        int[] input = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0, -1, -2, -3, 10, 11, 12, 13, 20, 17};
        int[] expected = {-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 17, 20};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortAllSameElements() {
        int[] input = {7, 7, 7, 7, 7};
        int[] expected = {7, 7, 7, 7, 7};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testSortWithMaxIntValues() {
        int[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0};
        int[] expected = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
        int[] result = HeapSort.heapsort(input);
        assertArrayEquals(expected, result);
    }
}