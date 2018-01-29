package com.crinity.cmbeat.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.crinity.cmbeat.monitor.Monitor;

public class ProcessMonitor implements Monitor {
    private BufferedWriter out = null;
    private String filename = null;

    // 테스트용 생성자, 메소드 show()만 가능
    public ProcessMonitor() {

    }

    // 일반적인 시작, CSV 파일 생성
    public ProcessMonitor(String filename) {
        this.filename = "./log/process/" + filename + ".csv";
        prepareCSV();
    }

    private ArrayList<ProcessDao> getProcessInfo() throws IOException {
        String line;
        ArrayList<ProcessDao> processList = new ArrayList<ProcessDao>();

        Process p = Runtime.getRuntime().exec(
                "ps -e -o pid,uname,pcpu,pmem,command");
        Scanner input = new Scanner(p.getInputStream());

        input.nextLine(); // ps 명령어 초반 PID USER %CPU %MEM COMMAND 부분 무시
        while (input.hasNext()) {
            ProcessDao pDao = new ProcessDao();
            String command = "";

            line = input.nextLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line);

            pDao.setPid(Integer.parseInt(stringTokenizer.nextToken()));
            pDao.setUser(stringTokenizer.nextToken());
            pDao.setCpuUsage(Float.parseFloat(stringTokenizer.nextToken()));
            pDao.setRamUsage(Float.parseFloat(stringTokenizer.nextToken()));

            // countTokens가 1보다 많도록 한 이유 : 1로 하지 않으면 마지막에 공백이 남기 때문에 while문 밑에다가
            // 공백을 처리하기 위해 token을 하나 남겨두고 while문을 통과한 후에 nextToken 하나 붙여줌

            while (stringTokenizer.countTokens() > 1) {
                command += stringTokenizer.nextToken() + " ";
            }
            command += stringTokenizer.nextToken();// 인자 없는 command와 마지막 인자
            pDao.setCommand(command);

            processList.add(pDao);
        }
        input.close();

        return processList;
    }

    @Override
    public void show() {
        ArrayList<ProcessDao> processList = null;
        try {
            processList = getProcessInfo();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("----------------Process------------------");
        for (int i = 0; i < processList.size(); i++) {
            ProcessDao process = processList.get(i);

            System.out.println(String.format(
                    "PID: %d    User: %s    CPU: %f    RAM: %f    Command: %s",
                    process.getPid(), process.getUser(), process.getCpuUsage(),
                    process.getRamUsage(), process.getCommand()));
        }
        System.out.println("------------------------------------------");
    }

    @Override
    public void makeCSV(long time) {
        ArrayList<ProcessDao> processList = null;
        try {
            processList = getProcessInfo();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < processList.size(); i++) {
            ProcessDao process = processList.get(i);

            try {
                out.write(String.format("%d,%s,%f,%f,\"%s\",%d\n",
                        process.getPid(), process.getUser(),
                        process.getCpuUsage(), process.getRamUsage(),
                        process.getCommand(), time));
            } catch (IOException e) {
                System.out.println("File IOException!");
                e.printStackTrace();
            }
        }

        try {
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
                out.write("PID,USER,CPU,MEM,COMM,TIME\n");
            }

        } catch (IOException e) {
            System.out.println("CSV Log File Create or Write Error!");
            e.printStackTrace();
        }
    }
}
