package com.github.FlorianSteenbuck.other.array;

import java.lang.reflect.Array;

public class ArrayUtil {
    public static <T>T[] subarray(final T[] array, int from, int to) {
        T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), to - from);
        for (; from < to; from++) {
            newArray[from] = array[from];
        }
        return newArray;
    }

    public static <T>T[] push(final T[] array, T pushValue) {
        int i = 0;
        T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length+1);
        while (i < array.length) {
            newArray[i] = array[i];
            i++;
        }
        newArray[i] = pushValue;
        return newArray;
    }

    public static <T>int lastIndexOf(final T[] array, T searchValue) {
        return lastIndexOf(array, searchValue, 0);
    }

    public static <T>int lastIndexOf(final T[] array, T searchValue, int startIndex) {
        for (int i = startIndex; i < array.length; i++) {
            T mayValue = array[i];
            if (mayValue == searchValue) {
                return i;
            }
        }
        return -1;
    }

    public static <T>T[] splice(final T[] array, int start) {
        if (start < 0)
            start += array.length;

        return splice(array, start, array.length - start);
    }

    @SuppressWarnings("unchecked")
    public static <T>T[] splice(final T[] array, int start, final int deleteCount) {
        if (start < 0)
            start += array.length;

        final T[] spliced = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount);
        if (start != 0)
            System.arraycopy(array, 0, spliced, 0, start);

        if (start + deleteCount != array.length)
            System.arraycopy(array, start + deleteCount, spliced, start, array.length - start - deleteCount);

        return spliced;
    }

    @SuppressWarnings("unchecked")
    public static <T>T[] splice(final T[] array, int start, final int deleteCount, final T ... items) {
        if (start < 0)
            start += array.length;

        final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount + items.length);
        if (start != 0)
            System.arraycopy(array, 0, spliced, 0, start);

        if (items.length > 0)
            System.arraycopy(items, 0, spliced, start, items.length);

        if (start + deleteCount != array.length)
            System.arraycopy(array, start + deleteCount, spliced, start + items.length, array.length - start - deleteCount);

        return spliced;
    }
}
