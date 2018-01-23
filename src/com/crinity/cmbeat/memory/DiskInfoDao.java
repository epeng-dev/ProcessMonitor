package com.crinity.cmbeat.memory;

/*
 *	디스크 정보를 담는 클래스
 */
public class DiskInfoDao {
    String fileSystemDir;
    double usedPercent; // 백분율이 아닌 그냥 소수 ex)0.7612345
    double freePercent; // 백분율이 아닌 그냥 소수 ex)0.7612345

    public String getFileSystemDir() {
        return fileSystemDir;
    }

    public void setFileSystemDir(String fileSystemDir) {
        this.fileSystemDir = fileSystemDir;
    }

    public double getUsedPercent() {
        return usedPercent;
    }

    public void setUsedPercent(double usedPercent) {
        this.usedPercent = usedPercent;
    }

    public double getFreePercent() {
        return (double) 1 - usedPercent;
    }

}
