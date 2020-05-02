package ru.otus.homework;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class MonitorGc implements MonitorGcMBean {
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

    protected void initState() throws InterruptedException, IOException {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            list.add(Integer.valueOf(i));
            if (list.get(i) % 500_000 == 0) {
                System.out.printf("List size = %s\n", list.size());
                Thread.sleep(300);
                logInfo();
            }
        }
    }

    protected void logInfo() throws IOException {
        StringBuilder buf = new StringBuilder();
        OutputStream out = new FileOutputStream("./hw04-gc/log/logs.txt", true);

        List<GarbageCollectorMXBean> list = ManagementFactory.getGarbageCollectorMXBeans();

        String name = "Gc name: " + getName();
        String count = "Gc build: " + getCountBuildGc();
        String time = "Gc build time: " + (getTime() / 1000.0) + " sec";

        buf.append("[").append(name).append("]").append("-").append("[").append(count).append("]")
                .append("-").append("[").append(time).append("]").append("\n");

        for (int i = 0; i < list.size() - 1; i++) {
            setName(list.get(i).getName());
            setCountBuildGc(list.get(i).getCollectionCount());
            setTime(list.get(i).getCollectionTime());

            if (list.get(i).getCollectionCount() != list.get(i + 1).getCollectionCount()) {
                out.write(buf.toString().getBytes());
            }
        }
        out.close();
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
