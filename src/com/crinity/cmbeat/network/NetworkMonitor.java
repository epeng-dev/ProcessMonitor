package com.crinity.cmbeat.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/*
 * ��Ʈ��ũ ���¸� ���� ���� Ŭ����
 */

public class NetworkMonitor {
	private Map<String, Long> rxCurrentMap; // ���� ��Ʈ��ũ ���� ���¸� �����ϱ� ���� ��
	private Map<String, List<Long>> rxChangeMap; // ���� ��Ʈ��ũ ���� ���¸� ������ ��
	private Map<String, Long> txCurrentMap; // ������ ��Ʈ��ũ ���� ���¸� �����ϱ� ���� ��
	private Map<String, List<Long>> txChangeMap; // ������ ��Ʈ��ũ ���� ���¸� ������ ��
	private Sigar sigar;

	public NetworkMonitor(Sigar sigar) {
		this.sigar = sigar;
		this.rxCurrentMap = new HashMap<String, Long>();
		this.rxChangeMap = new HashMap<String, List<Long>>();
		this.txCurrentMap = new HashMap<String, Long>();
		this.txChangeMap = new HashMap<String, List<Long>>();
	}

	public Long[] getMetric() throws SigarException {
		String[] networkInterfaceArray = sigar.getNetInterfaceList();
		String networkInterface;
		for (int i = 0; i < networkInterfaceArray.length; i++) {
			networkInterface = networkInterfaceArray[i];
			NetInterfaceStat netStat = sigar.getNetInterfaceStat(networkInterface);
			NetInterfaceConfig ifConfig = sigar.getNetInterfaceConfig(networkInterface);
			String hwaddr = null; // hardware address�� ���� (=MAC address)
			if (!NetFlags.NULL_HWADDR.equals(ifConfig.getHwaddr())) {
				hwaddr = ifConfig.getHwaddr();
			}
			if (hwaddr != null) {
				long rxCurrenttmp = netStat.getRxBytes();
				saveChange(rxCurrentMap, rxChangeMap, hwaddr, rxCurrenttmp, networkInterface);
				long txCurrenttmp = netStat.getTxBytes();
				saveChange(txCurrentMap, txChangeMap, hwaddr, txCurrenttmp, networkInterface);
			}
		}
		long totalrx = getMetricData(rxChangeMap);
		long totaltx = getMetricData(txChangeMap);
		for (List<Long> l : rxChangeMap.values())
			l.clear();
		for (List<Long> l : txChangeMap.values())
			l.clear();
		return new Long[] { totalrx, totaltx };
	}

	// ��� ��Ʈ��ũ ��ȭ�� ������� ����� �޼ҵ�
	private long getMetricData(Map<String, List<Long>> rxChangeMap) {
		long total = 0;
		for (Entry<String, List<Long>> entry : rxChangeMap.entrySet()) {
			int average = 0;
			for (Long l : entry.getValue()) {
				average += l;
			}
			total += average / entry.getValue().size();
		}
		return total;
	}

	// ��Ʈ��ũ �������̽� ������ ��ȭ���� �����ϱ� ���� �޼ҵ�
	private void saveChange(Map<String, Long> currentMap, Map<String, List<Long>> changeMap, String hwaddr,
			long current, String ni) {
		Long oldCurrent = currentMap.get(ni);
		if (oldCurrent != null) {
			List<Long> list = changeMap.get(hwaddr);
			if (list == null) {
				list = new LinkedList<Long>();
				changeMap.put(hwaddr, list);
			}
			list.add((current - oldCurrent));
		}
		currentMap.put(ni, current);
	}

	public void showNetwork() {
		Long[] m = null;
		try {
			m = getMetric();
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			System.out.println("Network SigarException");
			e.printStackTrace();
		}
		long totalrx = m[0]; // Byte����
		long totaltx = m[1]; // Byte����	
		System.out.println("------------------NetWork-----------------");
		System.out.print("totalrx(download): ");
		System.out.print("\t" + Sigar.formatSize(totalrx));
		System.out.println("\t" + totalrx);
		System.out.print("totaltx(upload): ");
		System.out.print("\t" + Sigar.formatSize(totaltx));
		System.out.println("\t" + totaltx);
		System.out.println("------------------------------------------");
	}
}
