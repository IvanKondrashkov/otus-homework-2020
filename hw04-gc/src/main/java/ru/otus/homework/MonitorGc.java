package ru.otus.homework;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitorGc implements MonitorGcMBean {
    private static Logger logger = Logger.getLogger(MonitorGc.class.getName());
    private int size;
    private String name;
    private long time;
    private long countBuildGc;

    public MonitorGc() {
        this.size = getSize();
        this.name = getName();
        this.time = getTime();
        this.countBuildGc = 0;
    }

    protected void initState() throws InterruptedException {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            list.add(Integer.valueOf(i));
            if (list.get(i) % 50_000 == 0) {
                System.out.printf("List size = %s\n", list.size());
                Thread.sleep(300);
            }
            if (list.size() == size) {
                while (true) {
                    list.removeIf(el -> el < (list.size() / 2) - 1);
                    Thread.sleep(300);
                    list.add(Integer.valueOf((list.size() / 2) + 1));
                }
            }
        }
    }

    protected void logInfo() {
        StringBuilder buf = new StringBuilder();
        FileHandler handler;
        try {
            handler = new FileHandler("./hw04-gc/log/logs.txt");
            logger.addHandler(handler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        List<GarbageCollectorMXBean> list = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();

        String name = "Gc name: " + getName();
        String count = "Gc build: " + getCountBuildGc();
        String time = "Gc build time: " + (getTime() / 1000) + " sec";

        buf.append("[").append(name).append("]").append("-").append("[").append(count).append("]")
                .append("-").append("[").append(time).append("]");

        for (GarbageCollectorMXBean gcBean : list) {
            setName(gcBean.getName());
            setCountBuildGc(gcBean.getCollectionCount());
            setTime(gcBean.getCollectionTime());
            logger.log(Level.INFO, buf.toString());
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public long getCountBuildGc() {
        return countBuildGc;
    }

    @Override
    public void setCountBuildGc(long countBuildGc) {
        this.countBuildGc = countBuildGc;
    }
}
