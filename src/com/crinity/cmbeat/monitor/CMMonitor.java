package com.crinity.cmbeat.monitor;

import java.io.BufferedWriter;

public abstract class CMMonitor implements Monitor{
    private BufferedWriter out;
    private String filename;
}
