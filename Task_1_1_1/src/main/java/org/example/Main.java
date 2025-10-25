package org.example;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] arr = {1, 5, 7, 9, 2};

        System.out.println("Input array: " + Arrays.toString(arr));

        int[] sortedArray = HeapSort.heapsort(arr);

        System.out.println("Sorted array: " + Arrays.toString(sortedArray));
    }
}
