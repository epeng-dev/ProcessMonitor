package com.crinity.cmbeat.monitor;

public interface Monitor {
    public void show();
    public void makeCSV(long time);
    public void prepareCSV();
}
