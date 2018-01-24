package com.crinity.cmbeat.monitor;

import java.io.BufferedWriter;

public abstract class CMMonitor implements Monitor{
    private BufferedWriter out = null;
    private String filename = null;
}
