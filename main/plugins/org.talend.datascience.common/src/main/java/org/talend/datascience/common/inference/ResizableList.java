package org.talend.datascience.common.inference;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A {@link List} that can resize to a given maximum size and ensure that all index in list have an instance of
 * <i>T</i>. <b>Important:</b>type <i>T</i> must have a public zero args constructor.
 * 
 * @param <T> A class with a zero-arg constructor.
 * @see #resize(int)
 */
public class ResizableList<T> implements List<T> {

    private Class<T> itemClass;

    private List<T> innerList;

    /**
     * Creates a list with explicit {@link #resize(int) resize} that contains instances of <i>T</i>.
     * 
     * @param itemClass The class of <i>T</i>.
     * @throws IllegalArgumentException If <code>itemClass</code> does not have a zero args constructor.
     */
    public ResizableList(Class<T> itemClass) {
        try {
            itemClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Item class must have a zero arg constructor.");
        }
        this.itemClass = itemClass;
        this.innerList = new ArrayList<T>();
    }

    /**
     * Resize the list so it contains <code>size</code> instances of <i>T</i>. Method only scales up, never down.
     * 
     * @param size The new size for the list. Must be a positive number.
     */
    public void resize(int size) {
        try {
            if (size < 0) {
                throw new IllegalArgumentException("Size must be a positive number.");
            }
            final int missing = size - innerList.size();
            for (int i = 0; i < missing; i++) {
                innerList.add(itemClass.newInstance());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to resize list of items.", e);
        }
    }

    public int size() {
        return innerList.size();
    }

    public boolean isEmpty() {
        return innerList.isEmpty();
    }

    public boolean contains(Object o) {
        return innerList.contains(o);
    }

    public Iterator<T> iterator() {
        return innerList.iterator();
    }

    public Object[] toArray() {
        return innerList.toArray();
    }

    public <T1> T1[] toArray(T1[] t1s) {
        return innerList.toArray(t1s);
    }

    public boolean add(T t) {
        return innerList.add(t);
    }

    public boolean remove(Object o) {
        return innerList.remove(o);
    }

    public boolean containsAll(Collection<?> collection) {
        return innerList.containsAll(collection);
    }

    public boolean addAll(Collection<? extends T> collection) {
        return innerList.addAll(collection);
    }

    public boolean addAll(int i, Collection<? extends T> collection) {
        return innerList.addAll(i, collection);
    }

    public boolean removeAll(Collection<?> collection) {
        return innerList.removeAll(collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return innerList.retainAll(collection);
    }

    public void replaceAll(UnaryOperator<T> operator) {
        innerList.replaceAll(operator);
    }

    public void sort(Comparator<? super T> c) {
        innerList.sort(c);
    }

    public void clear() {
        innerList.clear();
    }

    @Override
    public boolean equals(Object o) {
        return innerList.equals(o);
    }

    @Override
    public int hashCode() {
        return innerList.hashCode();
    }

    public T get(int i) {
        return innerList.get(i);
    }

    public T set(int i, T t) {
        return innerList.set(i, t);
    }

    public void add(int i, T t) {
        innerList.add(i, t);
    }

    public T remove(int i) {
        return innerList.remove(i);
    }

    public int indexOf(Object o) {
        return innerList.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return innerList.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return innerList.listIterator();
    }

    public ListIterator<T> listIterator(int i) {
        return innerList.listIterator(i);
    }

    public List<T> subList(int i, int i1) {
        return innerList.subList(i, i1);
    }

    public Spliterator<T> spliterator() {
        return innerList.spliterator();
    }

    public boolean removeIf(Predicate<? super T> filter) {
        return innerList.removeIf(filter);
    }

    public Stream<T> stream() {
        return innerList.stream();
    }

    public Stream<T> parallelStream() {
        return innerList.parallelStream();
    }

    public void forEach(Consumer<? super T> action) {
        innerList.forEach(action);
    }
}
