package ru.otus.homework;

import annotation.Log;
/*
 javap -c -verbose Logging.class
 */

public class Logging {
    @Log
    public void operation(String name) {
        //System.out.println("calculate the operation: " + name);
    }
    @Log
    public void calculation(int x) {
        //System.out.println("initial params: " + x);
    }
    @Log
    public void pop(double y) {
        //System.out.println("initial params: " + y);
    }
}