package org.example;
public class HeapSort {
    private HeapSort() {}
    public static int[] heapsort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return arr;
        }

        int[] result = arr.clone();
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(result, n, i);
        }

        for (int i = n - 1; i > 0; i--) {
            int temp = result[0];
            result[0] = result[i];
            result[i] = temp;
            heapify(result, i, 0);
        }
        return result;
    }

    private static void heapify(int[] array, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if (left < n && array[left] > array[largest]) {
            largest = left;
        }
        if (right < n && array[right] > array[largest]) {
            largest = right;
        }
        if (largest != i) {
            int swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;
            heapify(array, n, largest);
        }
    }
}

