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
        String timestring;
        long time;

        int flag = 1;

        CpuMonitor cpuMonitor = new CpuMonitor(sigar, filename);
        NetworkMonitor networkMonitor = new NetworkMonitor(sigar, filename);
        MemoryMonitor memoryMonitor = new MemoryMonitor(sigar, filename);
        ProcessMonitor processMonitor = new ProcessMonitor(filename);
        ProcessThreadMonitor processThreadMonitor = new ProcessThreadMonitor(
                filename);

        while (true) {
            date = new Date(System.currentTimeMillis());
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

            timestring = simpleDateFormat.format(date);
            time = Long.parseLong(timestring);
            
            cpuMonitor.makeCSV(time);
            networkMonitor.makeCSV(time);
            memoryMonitor.makeCSV(time);

            // process 관련 csv는 10초에 1번 만들어줌
            if (flag > 0) {
                processMonitor.makeCSV(time);
                processThreadMonitor.makeCSV(time);
            }
            flag *= -1;

            Thread.sleep(5000);
        }
    }
}
