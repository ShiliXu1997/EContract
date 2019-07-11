package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import utils.HttpUtil;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class WebsignActivity extends Activity {

    public static final int SIGN_SUCCESS = 0x00006001;
    public static final int SIGN_NETWORK_FAIL = 0x00006002;
    public static final int SIGN_STATUS_FAIL = 0x00006003;


    private Button mSignButton;
    private Button mCancalButton;
    private String qrCode;

    private Handler mHander;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websign);

        //拿到扫描的二维码的内容
        Bundle receive = getIntent().getExtras();
        qrCode = receive.getString("qrCode");

        Log.v(TAG, "成功得到二维码:"+qrCode);


        //实例化按钮并设置监听
        mSignButton = (Button)findViewById(R.id.confirm_websign_button);
        mCancalButton = (Button)findViewById(R.id.cancel_websign_button);
        setListener();

        mHander = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                try {
                    switch (message.what) {
                        case WebsignActivity.SIGN_SUCCESS:
//                            WebsignActivity.this.finish();
                            Toast.makeText(WebsignActivity.this, "扫码签名成功！", Toast.LENGTH_LONG).show();
                            Log.v(TAG, "扫码签名成功！");
                            break;
                        case WebsignActivity.SIGN_NETWORK_FAIL:
//                            WebsignActivity.this.finish();
                            Toast.makeText(WebsignActivity.this, "网络出错！", Toast.LENGTH_LONG).show();
                            Log.v(TAG, "网络出错！");
                            break;

                        case WebsignActivity.SIGN_STATUS_FAIL:
//                            WebsignActivity.this.finish();
                            Toast.makeText(WebsignActivity.this, "二维码失效,尝试刷新Web端二维码！", Toast.LENGTH_LONG).show();
                            Log.v(TAG, "二维码失效,尝试刷新Web端二维码！");
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
        mSignButton.setOnClickListener(onClick);
        mCancalButton.setOnClickListener(onClick);
    }


    //异步向后台post签名
    private void websign(String qrCode) {
        HttpUtil.getInstance().websign(qrCode,this.mHander);
    }

    private class OnClick implements android.view.View.OnClickListener {

        @Override
        public void onClick(android.view.View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.confirm_websign_button:
                    websign(qrCode);
                    intent = new Intent(WebsignActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case R.id.cancel_websign_button:
                    intent = new Intent(WebsignActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
            }
            startActivity(intent);
        }
    }
}
