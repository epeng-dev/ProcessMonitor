package com.crinity.cmbeat.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hyperic.sigar.Sigar;

import com.crinity.cmbeat.cpu.CpuMonitor;
import com.crinity.cmbeat.memory.MemoryMonitor;
import com.crinity.cmbeat.network.NetworkMonitor;
import com.crinity.cmbeat.process.ProcessMonitor;
import com.crinity.cmbeat.process.ProcessThreadMonitor;

public class MainApplication {

    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub
        Sigar sigar = new Sigar();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");
        
        String filename = simpleDateFormat.format(date);
        
        CpuMonitor cpuMonitor = new CpuMonitor(sigar, filename);
        NetworkMonitor networkMonitor = new NetworkMonitor(sigar, filename);
        MemoryMonitor memoryMonitor = new MemoryMonitor(sigar, filename);
        ProcessMonitor processMonitor = new ProcessMonitor(filename);
        ProcessThreadMonitor processThreadMonitor = new ProcessThreadMonitor(filename);
        
        while(true){
            cpuMonitor.makeCSV();
            networkMonitor.makeCSV();
            memoryMonitor.makeCSV();
            processMonitor.makeCSV();
            processThreadMonitor.makeCSV();
            Thread.sleep(5000);
        }
    }

}
