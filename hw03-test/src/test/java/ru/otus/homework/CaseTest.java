package ru.otus.homework;

import annotation.After;
import annotation.Before;
import annotation.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;

public class CaseTest {
    @Before
    @DisplayName("method1")
    public void method1() {
        System.out.println("@Before-test: setting up the environment.");
    }

    @Before
    @DisplayName("method2")
    public void method2() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("method3")
    public void method3() {
        Assertions.assertEquals(42, Integer.sum(21, 21));
    }

    @Test
    @DisplayName("method4")
    public void method4() {
        int[] array = new int[0];
        array[0] = Integer.parseInt(null);
        RuntimeException ex = Assertions.assertThrows(NumberFormatException.class, () -> {
            Arrays.sort(array);
        });
        System.out.println(ex.getClass() + " " + ex.getMessage());
    }

    @After
    @DisplayName("method5")
    public void method5() {
        Assertions.assertTrue(false);
    }

    @After
    @DisplayName("method6")
    public void method6() {
        System.out.println("@After-test: clearing the environment.");
    }
}
