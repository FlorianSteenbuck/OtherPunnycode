package com.github.FlorianSteenbuck.other.array;

public abstract class ArrayPrototype<T> {
    protected T[] array;
    
    protected ArrayPrototype(T[] array) {
        this.array = array;
    }

    public int lastIndexOf(T searchValue) {
        return ArrayUtil.lastIndexOf(array, searchValue);
    }

    public int lastIndexOf(T searchValue, int startIndex) {
        return ArrayUtil.lastIndexOf(array, searchValue, startIndex);
    }

    public T[] subarray(int from, int to) {
        return ArrayUtil.subarray(array, from, to);
    }
    
    public void splice(int start) {
        array = ArrayUtil.splice(array, start);
    }

    public void splice(int start, final int deleteCount) {
        array = ArrayUtil.splice(array, start, deleteCount);
    }

    public void splice(int start, final int deleteCount, final T ... items) {
        array = ArrayUtil.splice(array, start, deleteCount, items);
    }

    public void push(T ch) {
        array = ArrayUtil.push(array, ch);
    }

    public T[] iterable() {
        return array;
    }
}
