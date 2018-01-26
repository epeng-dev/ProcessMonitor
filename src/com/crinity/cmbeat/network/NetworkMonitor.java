package com.crinity.cmbeat.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import com.crinity.cmbeat.monitor.Monitor;

/*
 *네트워크 상태를 보기 위한 클래스
 */

public class NetworkMonitor implements Monitor {
    private Map<String, Long> rxCurrentMap; // 받은 네트워크 현재 상태를 저장하기 위한 맵
    private Map<String, List<Long>> rxChangeMap; // 받은 네트워크 예전 상태를 저장한 맵
    private Map<String, Long> txCurrentMap; // 보내는 네트워크 현재 상태를 저장하기 위한 맵
    private Map<String, List<Long>> txChangeMap; // 보내는 네트워크 예전 상태를 저장한 맵
    private Sigar sigar;
    private BufferedWriter out;
    private String filename;

    // 주로 사용하는 생성자, CSV를 만들 때 파일 생성이 포함되어있음
    public NetworkMonitor(Sigar sigar, String filename) {
        this.sigar = sigar;
        this.filename = "./log/network/" + filename + ".csv";
        this.rxCurrentMap = new HashMap<String, Long>();
        this.rxChangeMap = new HashMap<String, List<Long>>();
        this.txCurrentMap = new HashMap<String, Long>();
        this.txChangeMap = new HashMap<String, List<Long>>();
        prepareCSV();
    }

    // 테스트 생성자 (파일 생성 x)
    public NetworkMonitor(Sigar sigar, int flag) {
        this.sigar = sigar;
        this.rxCurrentMap = new HashMap<String, Long>();
        this.rxChangeMap = new HashMap<String, List<Long>>();
        this.txCurrentMap = new HashMap<String, Long>();
        this.txChangeMap = new HashMap<String, List<Long>>();
    }

    // 네트워크 트래픽 상태를 받는 메소드
    public Long[] getMetric() throws SigarException {
        String[] networkInterfaceArray = sigar.getNetInterfaceList();
        String networkInterface;

        for (int i = 0; i < networkInterfaceArray.length; i++) {
            networkInterface = networkInterfaceArray[i];
            NetInterfaceStat netStat = sigar
                    .getNetInterfaceStat(networkInterface);
            NetInterfaceConfig ifConfig = sigar
                    .getNetInterfaceConfig(networkInterface);

            String macAddr = null; // hardware address를 뜻함 (=MAC address)

            if (!NetFlags.NULL_HWADDR.equals(ifConfig.getHwaddr())) {
                macAddr = ifConfig.getHwaddr();
            }

            if (macAddr != null) {
                long rxCurrenttmp = netStat.getRxBytes();
                saveChange(rxCurrentMap, rxChangeMap, macAddr, rxCurrenttmp,
                        networkInterface);
                long txCurrenttmp = netStat.getTxBytes();
                saveChange(txCurrentMap, txChangeMap, macAddr, txCurrenttmp,
                        networkInterface);
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

    // 모든 네트워크 변화를 평균으로 만드는 메소드
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

    // 네트워크 인터페이스 마다의 변화율을 저장하기 위한 메소드
    private void saveChange(Map<String, Long> currentMap,
            Map<String, List<Long>> changeMap, String macAddr, long current,
            String ni) {
        Long oldCurrent = currentMap.get(ni);
        if (oldCurrent != null) {
            List<Long> list = changeMap.get(macAddr);
            if (list == null) {
                list = new LinkedList<Long>();
                changeMap.put(macAddr, list);
            }
            list.add((current - oldCurrent));
        }
        currentMap.put(ni, current);
    }

    @Override
    public void show() {
        Long[] m = null;
        try {
            m = getMetric();
        } catch (SigarException e) {
            // TODO Auto-generated catch block
            System.out.println("Network SigarException");
            e.printStackTrace();
        }
        long totalrx = m[0] / 1024; // KB단위 -> Byte / 1024
        long totaltx = m[1] / 1024; // KB단위 -> Byte / 1024
        System.out.println("------------------NetWork-----------------");
        System.out.print("totalrx(download): ");
        System.out.print("\t" + Sigar.formatSize(totalrx));
        System.out.println("\t" + totalrx);
        System.out.print("totaltx(upload): ");
        System.out.print("\t" + Sigar.formatSize(totaltx));
        System.out.println("\t" + totaltx);
        System.out.println("------------------------------------------");
    }

    @Override
    public void makeCSV() {
        Long[] m = null;
        try {
            m = getMetric();
        } catch (SigarException e) {
            // TODO Auto-generated catch block
            System.out.println("Network SigarException");
            e.printStackTrace();
        }
        long totalrx = m[0] / 1024; // KB단위 -> Byte / 1024
        long totaltx = m[1] / 1024; // KB단위 -> Byte / 1024

        try {
            out.write(totalrx + "," + totaltx + "\n");
            out.flush();
        } catch (IOException e) {
            System.out.println("FILE IOException!");
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
                out.write("RX,TX\n");
            }
        } catch (IOException e) {
            System.out.println("CSV Log File CError!");
            e.printStackTrace();
        }

    }
}
