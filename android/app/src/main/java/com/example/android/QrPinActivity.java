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

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import socket.NewDeviceLoginWebSocket;
import utils.HttpUtil;

import static android.content.ContentValues.TAG;

public class QrPinActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int QR_LOGIN_SUCCESS = 0x00005001;


    private TextView pinText;
    private Button pinLogin;
    private String userId;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_pin);

        pinText = (TextView)findViewById(R.id.qr_pin_editText);
        pinLogin = (Button)findViewById(R.id.qr_pin_login_button);
        userId = getIntent().getStringExtra("userId");
        pinLogin.setOnClickListener(this);

        mHandler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int what = message.what;
                switch (what) {
                    case QR_LOGIN_SUCCESS:
                        Intent intent = new Intent(QrPinActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
                    HttpUtil.getInstance().qrLogin(userId,pinCode,mHandler);
                }
                break;
        }

    }
};

