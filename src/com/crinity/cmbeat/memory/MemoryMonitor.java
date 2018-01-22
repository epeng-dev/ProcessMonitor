package com.crinity.cmbeat.memory;

import java.util.ArrayList;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/*
 *	메모리(Ram, Hdd)의 정보를 보는 클래스 
 */

public class MemoryMonitor {
	private Sigar sigar;

	public MemoryMonitor(Sigar sigar) {
		this.sigar = sigar;
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

	public void showMemory() {
		double[] ramMems; // [0]번에는 사용한 메모리의 백분율(소수) [1]번에는 남은 용량 메모리의 백분율(소수)
		try {
			ramMems = getRamMemory();
		} catch (SigarException e1) {
			System.out.println("Ram Sigar Exception!");
			return;
		}
		System.out.println("---------------------RAM------------------");
		System.out.println("사용량: " + ramMems[0]);
		System.out.println("남은 용량: " + ramMems[1]);
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
			System.out.println("사용량: " + diskInfo.getUsedPercent());
			System.out.println("남은 용량: " + diskInfo.getFreePercent());
		}
		System.out.println("------------------------------------------");
	}
}
