package hashtable;

import java.util.AbstractMap;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class HashTable<K, V> implements Iterable<Entry<K, V>> {

    private int modCount;
    private static final float LOAD_FACTOR = 0.75f;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node<K, V>[] table;
    private static final int TABLE_CAPACITY = 32;
    private int size;

    private int hash(K key) {
        int hash = key.hashCode();
        return Math.abs(hash) % table.length;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new HashIterator();
    }

    private class HashIterator implements Iterator<Entry<K, V>> {

        private int bucketIndex = 0;
        private Node<K, V> currentNode = null;
        private final int expectedModCount;

        HashIterator() {
            this.expectedModCount = modCount;
            advanceToNextNode();
        }

        private void advanceToNextNode() {
            if (currentNode != null && currentNode.next != null) {
                currentNode = currentNode.next;
                return;
            }

            currentNode = null;
            while (bucketIndex < table.length && currentNode == null) {
                currentNode = table[bucketIndex++];
            }
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Entry<K, V> next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (currentNode == null) {
                throw new NoSuchElementException();
            }

            Node<K, V> result = currentNode;
            advanceToNextNode();
            return new AbstractMap.SimpleEntry<>(result.key, result.value);
        }

    }

    private void resizeIfNeeded() {
        if (size >= table.length * LOAD_FACTOR) {
            resize(table.length * 2);
        }
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Node<K, V>[] oldTable = table;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];

        for (int i = 0; i < oldTable.length; i++) {
            Node<K, V> node = oldTable[i];
            while (node != null) {
                Node<K, V> next = node.next;

                int newIndex = Math.abs(node.key.hashCode()) % newCapacity;
                node.next = newTable[newIndex];
                newTable[newIndex] = node;

                node = next;
            }
        }

        table = newTable;
        modCount++; // чтобы старые итераторы словили ConcurrentModificationException
    }


    public HashTable() {
        this.table = (Node<K, V>[]) new Node[TABLE_CAPACITY];
        this.size = 0;
        this.modCount = 0;
    }

    public void put(K key, V value) {
        int index = hash(key);
        Node<K, V> head = table[index];

        for (Node<K, V> current = head; current != null; current = current.next) {
            if (current.key.equals(key)) {
                current.value = value;
                modCount++;
                return;
            }
        }

        table[index] = new Node<>(key, value, head);
        size++;
        modCount++;
        resizeIfNeeded();
    }

    public V remove(K key) {
        int index = hash(key);
        Node<K, V> head = table[index];
        Node<K, V> prev = null;

        for (Node<K, V> current = head; current != null; current = current.next) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                modCount++;
                return current.value;
            }
            prev = current;
        }

        return null;
    }


    public V get(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;
    }

    public boolean update(K key, V newValue) {
        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = newValue;
                modCount++;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    public boolean containsKey(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HashTable<K, V> other = (HashTable<K, V>) o;

        if (this.size != other.size()) return false;
        for (Entry<K, V> entry : this) {
            K key = entry.getKey();
            V value = entry.getValue();

            if (!other.containsKey(key)) {
                return false;
            }

            V otherValue = other.get(key);

            if (value == null) {
                if (otherValue != null) {
                    return false;
                }
            } else {
                if (!value.equals(otherValue)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (Entry<K, V> entry : this) {
            if (entry != null) {
                h += entry.hashCode();
            }
        }
        return h;
    }


    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Entry<K, V> entry : this) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            sb.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue());
        }

        sb.append("}");
        return sb.toString();
    }

}
