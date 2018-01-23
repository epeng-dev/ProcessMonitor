package com.crinity.cmbeat.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ProcessMonitor {
    private HashMap<Integer, ProcessDao> processMap;

    public ProcessMonitor() {
        processMap = new HashMap<Integer, ProcessDao>();
    }

    private HashMap<Integer, ProcessDao> getProcessInfo() throws IOException {
        String line;
        Process p = Runtime.getRuntime().exec("ps -e -T -o pid,uname,pcpu,pmem,command");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

        input.close();
        return null;
    }

    public void showProcess() {

    }
}
