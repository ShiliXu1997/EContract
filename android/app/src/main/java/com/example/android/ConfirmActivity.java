package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import utils.HttpUtil;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ConfirmActivity extends Activity {

    public static final int CONFIRM_SUCCESS = 0x00004001;
    public static final int CONFIRM_FAIL = 0x00004002;

    private Button mConfirmButton;
    private Button mCancalButton;
    private String qrCode;

    private Handler mHander;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        //拿到扫描的二维码的内容
        Bundle receive = getIntent().getExtras();
        qrCode = receive.getString("qrCode");

        Log.v(TAG, "成功得到二维码:"+qrCode);

        //实例化按钮并设置监听
        mConfirmButton = (Button)findViewById(R.id.confirm_login_button);
        mCancalButton = (Button)findViewById(R.id.cancel_button);
        setListener();

        mHander = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                try {
                    switch (message.what) {
                        case ConfirmActivity.CONFIRM_SUCCESS:
                            ConfirmActivity.this.finish();
                            Log.v(TAG, "扫码助登成功！");
                            break;
                        case ConfirmActivity.CONFIRM_FAIL:
                            ConfirmActivity.this.finish();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v(TAG, "扫码抛出未知异常！");
                }
            }
        };
    }

    private void setListener() {
        OnClick onClick = new OnClick();
        mConfirmButton.setOnClickListener(onClick);
        mCancalButton.setOnClickListener(onClick);
    }

    private void authorization(String qrCode) {

        HttpUtil.getInstance().confirm(qrCode,this.mHander);
    }

    private class OnClick implements android.view.View.OnClickListener {

        @Override
        public void onClick(android.view.View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.confirm_login_button:
                    authorization(qrCode);
                    intent = new Intent(ConfirmActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case R.id.cancel_button:
                    intent = new Intent(ConfirmActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
            }
            startActivity(intent);
        }
    }
}
