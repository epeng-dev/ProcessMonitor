package com.crinity.cmbeat.memory;

/*
 *	��ũ ������ ��� Ŭ���� 
 */
public class DiskInfoDao {
	String fileSystemDir;
	double usedPercent; // ������� �ƴ� �׳� �Ҽ��� ex)0.7612345
	double freePercent; // ������� �ƴ� �׳� �Ҽ��� ex)0.7612345

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
