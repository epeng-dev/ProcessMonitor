package com.crinity.cmbeat.cpu;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class CpuMonitor {
	private CpuPerc cpu = null;
	private CpuPerc[] cpus = null;
	private Sigar sigar;

	public CpuMonitor(Sigar sigar) {
		this.sigar = sigar;
	}

	public void showCpu() {
		try {
			cpu = sigar.getCpuPerc();
			cpus = sigar.getCpuPercList();
		} catch (SigarException e) {
			System.out.println("CPU SigarException");
			e.printStackTrace();
		}

		// 전체 CPU 시간 비율 모니터링
		System.out.println("---------------------CPU------------------");
		System.out.println(
				String.format("Total Time: %s    User Time: %s    System Time: %s    Nice Time: %s    Idle Time: %s",
						CpuPerc.format(cpu.getCombined()), CpuPerc.format(cpu.getUser()), CpuPerc.format(cpu.getSys()),
						CpuPerc.format(cpu.getNice()), CpuPerc.format(cpu.getIdle())));

		// 코어 개수에 따른 CPU 시간 비율 모니터링
		for (int i = 0; i < cpus.length; i++) {
			System.out.println(String.format(
					"[" + (i + 1) + "] "
							+ "Total Time: %s    User Time: %s    System Time: %s    Nice Time: %s    Idle Time: %s",
					CpuPerc.format(cpus[i].getCombined()), CpuPerc.format(cpus[i].getUser()),
					CpuPerc.format(cpus[i].getSys()), CpuPerc.format(cpus[i].getNice()),
					CpuPerc.format(cpus[i].getIdle())));
		}
		System.out.println("------------------------------------------");
	}
}
