package ru.otus.homework;

/*
java -javaagent:Main.jar -jar Main.jar
*/
public class Main {
    public static void main(String[] args) {
        Logging obj1 = new Logging();
        obj1.operation("sum");

        Logging obj2 = new Logging();
        obj2.calculation(21);

        Logging obj3 = new Logging();
        obj3.pop(250.50);
    }
}