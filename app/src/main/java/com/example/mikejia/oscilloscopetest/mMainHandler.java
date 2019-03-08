package com.example.mikejia.oscilloscopetest;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class mMainHandler extends Handler {
    WeakReference<MainActivity> mActivity;

    mMainHandler(MainActivity activity){
        mActivity = new WeakReference<MainActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        TextView receive_message = null;
        String response = null;
        switch (msg.what){
            case 0:
                receive_message.append(response+"\r\n");
//              receive_message.setText(response);
                break;
        }
    }
}
