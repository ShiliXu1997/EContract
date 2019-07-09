package com.example.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import utils.HttpUtil;
import utils.QRCodeUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int GET_QR_CODE = 1;
    public static final int GET_USR_ID = 2;

    private EditText mUserIdEdit;
    private EditText mPinEdit;
    private Button mLoginButton;
    private Button mqrLoginButton;
    private ImageView qr_image;

    private Handler login_handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.login_tittle));
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUserIdEdit = findViewById(R.id.login_usrId_editText);
        mPinEdit = findViewById(R.id.login_pin_editText);
        mLoginButton = findViewById(R.id.login_button);
        mqrLoginButton = findViewById(R.id.qr_login_button);
        qr_image = (ImageView)findViewById(R.id.qr_image);


        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER);


        mLoginButton.setOnClickListener(this);
        mqrLoginButton.setOnClickListener(this);

        login_handler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int what = message.what;
                try {
                    switch (what) {
                        case LoginActivity.GET_QR_CODE:
                            JSONObject mesObj = (JSONObject) message.obj;
                            String qr_code = (String) mesObj.get("qr_code");
                            show_qr_image(qr_code);
                            break;

                    }

                } catch (Exception e) {

                }
            }
        };


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
        if(view.getId()==mLoginButton.getId()) {
            String userid = mUserIdEdit.getText().toString();
            String pin = mPinEdit.getText().toString();

            if(userid.isEmpty()||pin.isEmpty())
                return;

            HttpUtil httpUtil = HttpUtil.getInstance();
            httpUtil.login(userid, pin,login_handler);






            Intent intent = new Intent(LoginActivity.this, UserPageActivity.class);
            startActivity(intent);
            qr_image.setVisibility(ImageView.INVISIBLE);
            mUserIdEdit.setVisibility(EditText.VISIBLE);
            mPinEdit.setVisibility(EditText.VISIBLE);

        } else if(view.getId()==mqrLoginButton.getId()) {
            socket.socketclient.main_run("ws://47.95.214.69:1001",login_handler);
        }
    }

}