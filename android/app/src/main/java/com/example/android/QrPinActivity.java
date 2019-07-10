package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import socket.NewDeviceLoginWebSocket;
import utils.HttpUtil;

import static android.content.ContentValues.TAG;

public class QrPinActivity extends AppCompatActivity implements View.OnClickListener {

    //二维码授权登录的过程中信息提交完毕,完成授权
    public static final int QR_LOGIN_SUCCESS = 0x00005001;
    public static final int QR_LOGIN_FAIL = 0x00005002;


    private TextView pinText;
    private Button pinLogin;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_pin);

        pinText = (TextView)findViewById(R.id.qr_pin_editText);
        pinLogin = (Button)findViewById(R.id.qr_pin_login_button);
        pinLogin.setOnClickListener(this);

        mHandler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int what = message.what;
                switch (what) {
                    case QR_LOGIN_SUCCESS:
                        Intent intent = new Intent(QrPinActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case QR_LOGIN_FAIL:
                        Toast.makeText(QrPinActivity.this, "服务器出错了,请稍后再试", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.qr_pin_login_button:
                String pinCode = pinText.getText().toString();
                if(!pinCode.isEmpty()) {
                    String userId = getIntent().getStringExtra("userId");
                    HttpUtil.getInstance().qrLogin(userId,pinCode,mHandler);
                }
                break;

        }

    }
};

