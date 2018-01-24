package com.crinity.cmbeat.process;

public class ProcessDao {
    private int pid;
    private String user;
    private float cpuUsage;
    private float ramUsage;
    private String command;

    public ProcessDao() {

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

    public float getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public float getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(float ramUsage) {
        this.ramUsage = ramUsage;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
