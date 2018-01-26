package com.crinity.cmbeat.memory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.crinity.cmbeat.monitor.Monitor;

/*
 *	메모리(Ram, Hdd)의 정보를 제공하는 클래스
 */

public class MemoryMonitor implements Monitor {
    private Sigar sigar;
    private BufferedWriter out;
    private String filename;

    public MemoryMonitor(Sigar sigar, String filename) {
        this.filename = "./log/memory/" + filename + ".csv";
        this.sigar = sigar;
        prepareCSV();
    }

    private double[] getRamMemory() throws SigarException {
        Mem mem = sigar.getMem();
        double[] mems = new double[2];
        mems[0] = mem.getUsedPercent();
        mems[1] = mem.getFreePercent();
        return mems;
    }

    private ArrayList<DiskInfoDao> getHddMemory() throws SigarException {
        FileSystem[] fsList = sigar.getFileSystemList();
        ArrayList<DiskInfoDao> fsUsageList = new ArrayList<DiskInfoDao>();

        for (int i = 0; i < fsList.length; i++) {
            FileSystem fs = fsList[i];
            String fsDir = fs.getDirName();
            FileSystemUsage usage = sigar.getFileSystemUsage(fsDir);

            DiskInfoDao diskInfoDao = new DiskInfoDao();
            diskInfoDao.setFileSystemDir(fsDir);
            diskInfoDao.setUsedPercent(usage.getUsePercent());

            fsUsageList.add(diskInfoDao);
        }

        return fsUsageList;
    }

    @Override
    public void show() {
        double[] ramMems; // [0]번에는 사용한 메모리의 백분율(소수) [1]번에는 남은 용량 메모리의 백분율(소수)
        try {
            ramMems = getRamMemory();
        } catch (SigarException e1) {
            System.out.println("Ram Sigar Exception!");
            return;
        }
        System.out.println("---------------------RAM------------------");
        System.out.println("Used: " + ramMems[0]);
        System.out.println("Free: " + ramMems[1]);
        System.out.println("------------------------------------------");

        ArrayList<DiskInfoDao> hddMems;
        try {
            hddMems = getHddMemory();
        } catch (SigarException e) {
            System.out.println("Hdd Sigar Exception!");
            return;
        }

        System.out.println("---------------------HDD------------------");
        for (int i = 0; i < hddMems.size(); i++) {
            DiskInfoDao diskInfo = hddMems.get(i);
            System.out.println(diskInfo.getFileSystemDir());
            System.out.println("Used: " + diskInfo.getUsedPercent());
            System.out.println("Free: " + diskInfo.getFreePercent());
        }
        System.out.println("------------------------------------------");
    }

    @Override
    public void makeCSV() {
        double[] ramMems; // [0]번에는 사용한 메모리의 백분율(소수) [1]번에는 남은 용량 메모리의 백분율(소수)
        ArrayList<DiskInfoDao> hddMems; // HDD, SDD 안에 들어있는 파일 시스템의 용량

        try {
            ramMems = getRamMemory();
            hddMems = getHddMemory();
        } catch (SigarException e) {
            System.out.println("Ram Sigar Exception!");
            return;
        }

        try {
            out.write("RAM," + ramMems[0] + "," + ramMems[1] + "\n");
            for (int i = 0; i < hddMems.size(); i++) {
                DiskInfoDao diskInfo = hddMems.get(i);
                out.write("\"" + diskInfo.getFileSystemDir() + "\"" + ","
                        + (diskInfo.getUsedPercent() * 100) + ","
                        + (diskInfo.getFreePercent() * 100) + "\n");
            }
            out.flush();
        } catch (IOException e) {
            System.out.println("File IOException!");
            e.printStackTrace();
        }
    }

    @Override
    public void prepareCSV() {
        try {
            File file = new File(filename);
            boolean isExist = file.exists();
            this.out = new BufferedWriter(new FileWriter(file, true));

            if (!isExist) {
                out.write("DISK,USED,FREE\n");
            }
        } catch (IOException e) {
            System.out.println("CSV Log File CError!");
            e.printStackTrace();
        }
    }
}
