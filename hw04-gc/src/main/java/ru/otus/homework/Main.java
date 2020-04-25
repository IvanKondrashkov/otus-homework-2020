package ru.otus.homework;

/*
-Xms256m
-Xmx256m
-verbose:gc
-Xlog:gc=debug:file=./hw04-gc/log/gc-%p-%t
-XX:+HeapDumpOnOutOfMemoryError
-XX:+UseSerialGC
-XX:+UseParallelGC
-XX:+UseG1GC

-Djava.util.logging.config.file=./hw04-gc/src/main/resources/logger.properties
*/

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) {
        int size = 25 * 1000 * 1000;
        MonitorGc mbean = new MonitorGc();

        System.out.println("Starting pid: " + ManagementFactory.getRuntimeMXBean().getName());

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName("ru.otus.homework:type=MonitorGc");
            mbs.registerMBean(mbean, name);
            mbean.setSize(size);
            mbean.initState();
            mbean.logInfo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
