package ru.otus.homework;

import com.google.common.base.MoreObjects;

public class HelloOtus {
    private String name;
    private int age;
    private String profession;

    public static void main(String[] args) {
        HelloOtus object = new HelloOtus("Ivan", 33, "Desinger");
        System.out.println(object);
    }

    public HelloOtus(String name, int age, String profession) {
        this.name = name;
        this.age = age;
        this.profession = profession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String toString() {
        return MoreObjects.toStringHelper(HelloOtus.class)
                .add("name", name)
                .add("age", age)
                .add("profession", profession)
                .toString();
    }
}
