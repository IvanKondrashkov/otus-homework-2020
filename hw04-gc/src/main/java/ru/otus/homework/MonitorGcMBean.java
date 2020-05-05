package ru.otus.homework;

public interface MonitorGcMBean {
    String getName();
    void setName(String name);

    int getSize();
    void setSize(int size);

    long getCountBuildGc();
    void setCountBuildGc(long countBuildGc);

    long getTime();
    void setTime(long time);
}
