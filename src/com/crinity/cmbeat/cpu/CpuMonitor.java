package com.crinity.cmbeat.cpu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.crinity.cmbeat.monitor.Monitor;

public class CpuMonitor implements Monitor {
    private CpuPerc cpu;
    private CpuPerc[] cpus;
    private Sigar sigar;
    private String filename;
    private BufferedWriter out;

    public CpuMonitor(Sigar sigar, String filename) {
        this.filename = "./log/cpu/" + filename + ".csv";
        this.sigar = sigar;
        prepareCSV();
    }

    public void getCpuUsage() {
        try {
            cpu = sigar.getCpuPerc();
            cpus = sigar.getCpuPercList();
        } catch (SigarException e) {
            System.out.println("CPU SigarException");
            e.printStackTrace();
        }

    }

    @Override
    public void makeCSV() {
        getCpuUsage();

        try {
            out.write(String.format("0,%f,%f,%f,%f,%f\n", cpu.getCombined(),
                      cpu.getUser(), cpu.getSys(), cpu.getNice(), cpu.getIdle()));
            for (int i = 0; i < cpus.length; i++) {
                out.write(String.format((i + 1) + ",%f,%f,%f,%f,%f\n",
                        cpus[i].getCombined(), cpus[i].getUser(),
                        cpus[i].getSys(), cpus[i].getNice(), cpus[i].getIdle()));
            }
            out.flush();
        } catch (IOException e) {
            System.out.println("File IOException!");
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        getCpuUsage();
        // 전체 CPU 시간 비율 모니터링
        System.out.println("---------------------CPU------------------");
        System.out
                .println(String
                        .format("Total Time: %s    User Time: %s    System Time: %s    Nice Time: %s    Idle Time: %s",
                                CpuPerc.format(cpu.getCombined()),
                                CpuPerc.format(cpu.getUser()),
                                CpuPerc.format(cpu.getSys()),
                                CpuPerc.format(cpu.getNice()),
                                CpuPerc.format(cpu.getIdle())));

        // 코어 개수에 따른 CPU 시간 비율 모니터링
        for (int i = 0; i < cpus.length; i++) {
            System.out
                    .println(String
                            .format("["
                                    + (i + 1)
                                    + "] "
                                    + "Total Time: %s    User Time: %s    System Time: %s    Nice Time: %s    Idle Time: %s",
                                    CpuPerc.format(cpus[i].getCombined()),
                                    CpuPerc.format(cpus[i].getUser()),
                                    CpuPerc.format(cpus[i].getSys()),
                                    CpuPerc.format(cpus[i].getNice()),
                                    CpuPerc.format(cpus[i].getIdle())));
        }
        System.out.println("------------------------------------------");
    }

    @Override
    public void prepareCSV() {
        try {
            File file = new File(filename);
            boolean isExist = file.exists();
            this.out = new BufferedWriter(new FileWriter(file, true));

            if (!isExist) {
                out.write("CPUNUM,TOTAL,USER,SYSTEM,NICE,IDLE\n");
            }
        } catch (IOException e) {
            System.out.println("CSV Log File Create or Write Error!");
            e.printStackTrace();
        }
    }
}
