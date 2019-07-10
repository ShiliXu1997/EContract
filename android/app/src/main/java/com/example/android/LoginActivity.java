package com.example.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import socket.newDeviceLoginWebSocket;
import utils.HttpUtil;
import utils.QRCodeUtil;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //口令登录成功与失败的反馈
    public static final int MESSAGE_PINLOGIN_SUCCESS_RESPONSE = 0x00003001;
    public static final int MESSAGE_PINLOGIN_FAIL_RESPONSE = 0x00003002;
    //从后台获取二维码成功
    public static final int GET_QR_CODE_SUCCESS = 0x00003003;
    //二维码被扫描成功,得到userId
    public static final int QR_LOGIN_SUCCESS = 0x00003004;


    private EditText mUserIdEdit;
    private EditText mPinEdit;
    private Button mLoginButton;
    private Button mqrLoginButton;
    private ImageView qr_image;

    private Handler mHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.R.layout.activity_login);
        setActionBar("用户名登录");

        mUserIdEdit = findViewById(com.example.android.R.id.login_usrId_editText);
        mPinEdit = findViewById(com.example.android.R.id.login_pin_editText);
        mLoginButton = findViewById(com.example.android.R.id.login_button);
        mqrLoginButton = findViewById(com.example.android.R.id.qr_login_button);
        qr_image = (ImageView)findViewById(com.example.android.R.id.qr_image);


        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER);


        mLoginButton.setOnClickListener(this);
        mqrLoginButton.setOnClickListener(this);

        mHandler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int what = message.what;
                JSONObject mess;
                String token;
                Intent intent;
                try {
                    switch (what) {
                        case LoginActivity.GET_QR_CODE_SUCCESS:
                            String qrCode = (String) message.obj;
                            show_qr_image(qrCode);
                            //开启长连接
                            String url = "ws://47.95.214.69:1002/auth/codeStatus?code="+qrCode;
                            System.out.println("url:"+url);
                            newDeviceLoginWebSocket.main_run(url,mHandler);
                            break;

                        case LoginActivity.MESSAGE_PINLOGIN_SUCCESS_RESPONSE:
                            /*
                            拿到message返回的token值
                            携带token值跳转到UserPageActivity
                             */
                            mess= (JSONObject) message.obj;
                            token= (String)mess.get("token");
                            intent = new Intent(LoginActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        case LoginActivity.MESSAGE_PINLOGIN_FAIL_RESPONSE:
                            // 这里写如果登录失败该怎么搞
                            break;

                        case LoginActivity.QR_LOGIN_SUCCESS:
                            /*
                            拿到message返回的userId值
                            携带userID值跳转到UserPageActivity
                             */
                            mess= (JSONObject) message.obj;
                            String userId = (String)mess.get("userId");
                            System.out.println("得到userId:"+userId);
                            //跳转到设定pin码
                            intent = new Intent(LoginActivity.this, QrPinActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("userId",userId);
                            startActivity(intent);
                            break;

                    }

                } catch (Exception e) {

                }
            }
        };

        Log.v(TAG, "准备获取公钥");
        HttpUtil.initServerPublicKey();

    }

    public void show_qr_image(String qr_code) {
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(qr_code, 480, 480);
        qr_image.setImageBitmap(mBitmap);
        mUserIdEdit.setVisibility(EditText.INVISIBLE);
        mPinEdit.setVisibility(EditText.INVISIBLE);
        qr_image.setVisibility(ImageView.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        switch (vid) {

            case R.id.login_button:

                if(getSupportActionBar().getTitle().toString()=="扫码登录") {
                    mUserIdEdit.setVisibility(EditText.VISIBLE);
                    mPinEdit.setVisibility(EditText.VISIBLE);
                    qr_image.setVisibility(ImageView.INVISIBLE);
                    setActionBar("口令登录");
                } else {
                    String userid = mUserIdEdit.getText().toString();
                    String pin = mPinEdit.getText().toString();
                    if(userid.isEmpty()||pin.isEmpty())
                        return;

                    //尝试使用pin码登录
                    setActionBar("登录中...");
                    HttpUtil httpUtil = HttpUtil.getInstance();
                    httpUtil.pinLogin(userid, pin,mHandler);
                }
                break;


            case R.id.qr_login_button:
                setActionBar("扫码登录");
                //向后台请求二维码
                HttpUtil.getInstance().getQrCode(mHandler);
                break;
        }
    }

    private void setActionBar(String barText) {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setTitle(barText);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


}