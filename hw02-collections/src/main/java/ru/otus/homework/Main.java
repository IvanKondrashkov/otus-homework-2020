package ru.otus.homework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> integersList1 = new DIYArrayList<>();
        List<Integer> integersList2 = new DIYArrayList<>();


        Collections.addAll(integersList1, 0, 0, 0, 0, 0, 0, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22);
        Collections.addAll(integersList2, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22);
        System.out.println(integersList1);
        System.out.println(integersList2);
        assertEquals(integersList1.get(0), 0);
        assertEquals(integersList1.get(5), 0);

        Collections.copy(integersList1, integersList2);
        System.out.println(integersList1);
        System.out.println(integersList2);
        assertEquals(integersList1.get(0), 1);
        assertEquals(integersList1.get(5), 6);

        Collections.sort(integersList1, Collections.reverseOrder());
        System.out.println(integersList1);
        assertEquals(integersList1.get(0), 22);
        assertEquals(integersList1.get(20), 1);
    }

    private static void assertEquals(Integer i1, Integer i2) {
        if (i1.equals(i2)) {
            return;
        }
        throw new AssertionError(String.format("%d not equals %d", i1, i2));
    }
}
