package ru.otus.homework;

import java.util.*;

public class DIYArrayList<T> implements List<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private static int modCount = 0;
    private T[] array;
    private int size;
    private int index;

    public DIYArrayList() {
        this.array = (T[]) new Object[DEFAULT_CAPACITY];
        this.size = DEFAULT_CAPACITY;
    }

    public DIYArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.array = (T[]) new Object[initialCapacity];
            this.size = initialCapacity;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(array, size);
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public boolean add(T t) {
        checkArrayResize(index + 1);
        this.array[index++] = t;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public T get(int index) {
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return array[index];
    }

    @Override
    public T set(int index, T element) {
        checkArrayResize(index);
        T tmp = array[index];
        array[index] = element;
        return tmp;
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("No support!");
    }

    @Override
    public ListIterator<T> listIterator() {
        return new MyIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new MyIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("No support!");
    }

    private void checkArrayResize(int minCapacity) {
        if (minCapacity > array.length) {
            modCount++;
            initArrayResize();
        }
    }

    private void initArrayResize() {
        this.array = ensureCapacity(array);
        this.size = array.length;
    }

    private T[] ensureCapacity(T[] array) {
        int count = 0;
        for (T t : array) {
            if (t != null) {
                count++;
            }
        }
        return Arrays.copyOf(array, count + 1);
    }

    @Override
    public String toString() {
        return "DIYArrayList{" +
                "array=" + Arrays.toString(array) +
                ", size=" + size +
                '}';
    }

    private class MyIterator implements ListIterator<T> {
        int cursor;
        int lastCursor = -1; //no such elements, return -1;
        int expectedModCount = modCount;

        MyIterator(int index) {
            this.cursor = index;
        }

        @Override
        public boolean hasNext() {
            boolean result;
            result = cursor != size;
            return result;
        }

        @Override
        public T next() {
            int index = cursor;
            if (index > size) {
                throw new NoSuchElementException();
            }
            cursor++;
            return array[lastCursor = index];
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException("No support!");
        }

        @Override
        public T previous() {
            throw new UnsupportedOperationException("No support!");
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException("No support!");
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("No support!");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("No support!");
        }

        @Override
        public void set(T t) {
            if (lastCursor < 0) {
                throw new IllegalStateException();
            }
            DIYArrayList.this.set(lastCursor, t);
        }

        @Override
        public void add(T t) {
            try {
                int index = cursor;
                DIYArrayList.this.add(index, t);
                cursor = index++;
                lastCursor = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
