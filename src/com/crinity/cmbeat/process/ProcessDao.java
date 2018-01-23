package com.crinity.cmbeat.process;

import java.util.ArrayList;

public class ProcessDao {
    private int pid;
    private String user;
    private ArrayList<Double> cpuUsage;
    private double memoryUsage;

    public ProcessDao() {
        cpuUsage = new ArrayList<Double>();
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<Double> getCpuUsage() {
        return cpuUsage;
    }

}
