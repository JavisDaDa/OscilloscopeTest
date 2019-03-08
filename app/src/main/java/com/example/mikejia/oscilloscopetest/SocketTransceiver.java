package com.example.mikejia.oscilloscopetest;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public abstract class SocketTransceiver implements Runnable{
    protected Socket socket;
    protected InetAddress addr;
    protected DataInputStream in;
    protected DataOutputStream out;
    private boolean runFlag;

    //构造器

    public SocketTransceiver(Socket socket) {
        this.socket = socket;
        this.addr = socket.getInetAddress();
    }

    //获取ip地址

    public InetAddress getInetAddress() {
        return addr;
    }

    //开启新的线程
    public void start() {
        runFlag = true;
        new Thread(this).start();
    }
    //停止
    public void stop() {
        runFlag = false;
        try {
            socket.shutdownInput();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //发送
    public boolean send(String s) {
        if (out != null) {
            try {
                out.writeUTF(s);
                out.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //运行
    @Override
    public void run() {
        try {
            in = new DataInputStream(this.socket.getInputStream());
            out = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            runFlag = false;
        }
        while (runFlag) {
            try {
                final String s = in.readUTF();
                this.onReceive(addr, s);
            } catch (IOException e) {
                runFlag = false;
            }
        }
        try {
            in.close();
            out.close();
            socket.close();
            in = null;
            out = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.onDisconnect(addr);
    }

    public abstract void onReceive(InetAddress addr, String s);
    public abstract void onDisconnect(InetAddress addr);
}
