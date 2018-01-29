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
    private CpuPerc cpu = null;
    private CpuPerc[] cpus = null;
    private Sigar sigar;
    private String filename = null;
    private BufferedWriter out = null;

    // 테스트용 생성자, 메소드 show()만 가능
    public CpuMonitor() {

    }

    // 일반적인 시작, CSV 파일 생성
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
    public void makeCSV(long time) {
        getCpuUsage();

        try {
            out.write(String.format("0,%f,%f,%f,%f,%f,%d\n",
                    cpu.getCombined() * 100, cpu.getUser() * 100,
                    cpu.getSys() * 100, cpu.getNice() * 100,
                    cpu.getIdle() * 100, time));
            for (int i = 0; i < cpus.length; i++) {
                out.write(String.format((i + 1) + ",%f,%f,%f,%f,%f,%d\n",
                        cpus[i].getCombined() * 100, cpus[i].getUser() * 100,
                        cpus[i].getSys() * 100, cpus[i].getNice() * 100,
                        cpus[i].getIdle() * 100, time));
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
                out.write("CPUNUM,TOTAL,USER,SYSTEM,NICE,IDLE,TIME\n");
            }
        } catch (IOException e) {
            System.out.println("CSV Log File Create or Write Error!");
            e.printStackTrace();
        }
    }
}
