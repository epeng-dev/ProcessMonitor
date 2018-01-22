package com.crinity.cmbeat.main;

import org.hyperic.sigar.Sigar;

import com.crinity.cmbeat.cpu.CpuMonitor;
import com.crinity.cmbeat.memory.MemoryMonitor;
import com.crinity.cmbeat.network.NetworkMonitor;
import com.crinity.cmbeat.process.ProcessMonitor;

public class MainApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sigar sigar = new Sigar();
		CpuMonitor cpuMonitor = new CpuMonitor(sigar);
		NetworkMonitor networkMonitor = new NetworkMonitor(sigar);
		MemoryMonitor memoryMonitor = new MemoryMonitor(sigar);
		ProcessMonitor processMonitor = new ProcessMonitor();
		while(true){
			cpuMonitor.showCpu();
			networkMonitor.showNetwork();
			memoryMonitor.showMemory();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
