package com.example.mikejia.oscilloscopetest3;

import java.net.InetAddress;
import java.net.Socket;



public abstract class TcpClient implements Runnable{
    private int port;
    private String hostIP;
    private boolean connect = false;
    private SocketTransceiver transceiver;

    public void connect(String hostIP, int port) {
        this.hostIP = hostIP;
        this.port = port;
        new Thread(this).start();
    }
    @Override
    public void run() {
        try {
            Socket socket = new Socket(hostIP, port);
            transceiver = new SocketTransceiver(socket) {

                @Override
                public void onReceive(InetAddress addr, String s) {
                    TcpClient.this.onReceive(this, s);
                }

                @Override
                public void onDisconnect(InetAddress addr) {
                    connect = false;
                    TcpClient.this.onDisconnect(this);
                }
            };
            transceiver.start();
            connect = true;
            this.onConnect(transceiver);
        } catch (Exception e) {
            e.printStackTrace();
            this.onConnectFailed();
        }
    }
    public void disconnect() {
        if(transceiver != null) {
            transceiver.stop();
            transceiver = null;
        }
    }

    public boolean isConnected() {
        return connect;
    }

    public SocketTransceiver getTransceiver() {
        return isConnected() ? transceiver : null;
    }


    public abstract void onConnectFailed();
    public abstract void onDisconnect(SocketTransceiver socketTransceiver);
    public abstract void onConnect(SocketTransceiver transceiver2);
    protected abstract void onReceive(SocketTransceiver socketTransceiver, String s);
}
