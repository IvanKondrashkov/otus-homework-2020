package ru.otus.homework;

import annotation.After;
import annotation.Before;
import annotation.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class TestRunner {
    private void testInit(Class<?> className, List<Method> listBefore, List<Method> listTest, List<Method> listAfter) {
        for (Method m : className.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Before.class)) {
                listAfter.add(m);
            } else if (m.isAnnotationPresent(Test.class)) {
                listTest.add(m);
            } else if (m.isAnnotationPresent(After.class)) {
                listBefore.add(m);
            }
        }
    }

    private void testRun(Class<?> className) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Method> listBefore = new ArrayList<>();
        List<Method> listTest = new ArrayList<>();
        List<Method> listAfter = new ArrayList<>();
        int test = 0;
        int passed = 0;

        testInit(className, listBefore, listTest, listAfter);
        Object obj = className.getConstructor().newInstance();

        for (int k = 0; k < listTest.size(); k++) {
            for (int i = 0; i < listBefore.size(); i++) {
                test++;
                try {
                    listBefore.get(i).invoke(obj);
                    passed++;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            test++;
            try {
                listTest.get(k).invoke(obj);
                passed++;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < listAfter.size(); j++) {
                test++;
                try {
                    listAfter.get(j).invoke(obj);
                    passed++;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Test: " + test + " Passed: " + passed + " Failed: " + (test - passed));
    }
}
