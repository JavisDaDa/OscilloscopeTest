package com.example.mikejia.oscilloscopetest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    //变量

    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    mMainHandler mMainHandler;
    // Socket变量
    private Socket socket;

    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;
    //ip，端口变量
    private EditText ip_EditText;
    private EditText port_EditText;

    //显示接收服务器消息

    private TextView receive_message;
    //连接,断开,发送按钮
    private Button button_connect, button_disconnect;
    //功能按键
    private Button button_get_message;

//    //接收服务器发送过来的消息
//    String response;

    //发送消息到服务器变量
    //输出流对象
    OutputStream os;
    PrintWriter pw;
    OutputStreamWriter osw;
    BufferedWriter bw;
//    //计数器和判断变量
//    public static int COUNT = 1;
//    public static boolean JUDGE = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //初始化ip,端口变量
        ip_EditText = findViewById(R.id.editText_ip);
        port_EditText = findViewById(R.id.editText_port);
        receive_message =  findViewById(R.id.textView_message);

        //初始化按钮
        button_connect = findViewById(R.id.button_connect);
        button_disconnect = findViewById(R.id.button_disconnect);
        button_get_message = findViewById(R.id.button_message);


        //获取ip地址和端口数字
        final String ip_addr = ip_EditText.getText().toString();
        final int ip_port = Integer.parseInt(port_EditText.getText().toString());

        mThreadPool = Executors.newCachedThreadPool();
        //给接收框添加滚动条
        receive_message.setMovementMethod(ScrollingMovementMethod.getInstance());

        mMainHandler.sendEmptyMessage(0);

        //创建连接
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启一个线程池
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            socket = new Socket(ip_addr, ip_port);
                            System.out.println("是否连接上:" + socket.isConnected());
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        //获取仪器信息
        button_get_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //发送
                            TcpClient c1 = new TcpClient() {

                                @Override
                                public void onReceive(SocketTransceiver st, String s) {
                                    System.out.println("Client1 Receive: " + s);
                                }

                                @Override
                                public void onDisconnect(SocketTransceiver st) {
                                    System.out.println("Client1 Disconnect");
                                }

                                @Override
                                public void onConnect(SocketTransceiver transceiver) {
                                    System.out.println("Client1 Connect");
                                }

                                @Override
                                public void onConnectFailed() {
                                    System.out.println("Client1 Connect Failed");
                                }
                            };
                            socket = new Socket(ip_addr, ip_port);
                            os=socket.getOutputStream();
                            osw=new OutputStreamWriter(os,"GBK");
                            bw=new BufferedWriter(osw);
                            String str = "*IDN?";
                            if (c1.isConnected()) {
                                bw.write(str + "\r");
                                bw.newLine();
                                bw.flush();
                            }

                            //接收
                            InputStream inputStream = socket.getInputStream();
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GBK");
                            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
//                            response = bufferReader.readLine();
//                            receive_message.setText(socket.getInetAddress().getHostAddress()+":"+socket.getPort()+">>>>>"+response);
                            Message msg=Message.obtain();
                            msg.what=0;
                            mMainHandler.sendMessage(msg);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        //断开连接
        button_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    os.close();
                    osw.close();
                    bw.close();


                    socket.close();
                    System.out.println("是否连接上" + socket.isConnected());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
