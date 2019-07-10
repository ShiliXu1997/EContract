package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import utils.HttpUtil;

public class ConfirmActivity extends Activity implements View.OnClickListener {

    public static final int CONFIRM_SUCCESS = 0x00004001;
    public static final int CONFIRM_FAIL = 0x00004002;

    private Button confire_login;
    private Button cancal_confire_login;
    private String qrCode;

    private Handler mHander;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);


        //拿到扫描的二维码的内容
        Bundle receive=getIntent().getExtras();
        this.qrCode=receive.getString("qr_code");
        System.out.println("成功得到二维码:"+qrCode);

        //实例化按钮并设置监听
        this.confire_login = (Button)findViewById(R.id.confirm_login_button);
        confire_login.setOnClickListener(this);
        this.cancal_confire_login = (Button)findViewById(R.id.cancal_login_button);
        cancal_confire_login.setOnClickListener(this);

        this.mHander = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int what = message.what;
                try {
                    switch (what) {
                        case ConfirmActivity.CONFIRM_SUCCESS:
                            ConfirmActivity.this.finish();
                            break;
                        case ConfirmActivity.CONFIRM_FAIL:
                            ConfirmActivity.this.finish();
                            break;
                    }

                } catch (Exception e) {
                    System.out.println("网络出错3");
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        if(vid==this.confire_login.getId()) {
            authorization(qrCode);
            Intent intent = new Intent(ConfirmActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if(vid==this.cancal_confire_login.getId()) {
            Intent intent = new Intent(ConfirmActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void authorization(String qrCode) {

        HttpUtil.getInstance().confirm(qrCode,this.mHander);
    }
}
