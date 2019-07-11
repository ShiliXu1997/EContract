package com.example.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.security.PublicKey;

import socket.NewDeviceLoginWebSocket;
import utils.HttpUtil;
import utils.QRCodeUtil;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //口令登录成功与失败的反馈
    public static final int MESSAGE_PINLOGIN_SUCCESS_RESPONSE = 0x00003001;
    public static final int MESSAGE_PINLOGIN_FAIL_RESPONSE = 0x00003002;

    //从后台获取二维码成功
    public static final int GET_QR_CODE_SUCCESS = 0x00003003;
    public static final int GET_QR_CODE_FAIL = 0x00003004;
    //二维码被扫描成功,得到token
    public static final int GET_TOKEN_SUCCESS = 0x00003005;
    public static final int GET_TOKEN_FAIL = 0x00003006;
    //携带token获取userId成功
    public static final int GET_USERID_SUCCESS = 0x00003007;
    public static final int GET_USERID_FAIL = 0x00003008;


    private EditText mUserIdEdit;
    private Button mLoginButton;
    private Button mqrLoginButton;
    private ImageView qr_image;

    private EditText mPinEdit;
    private PinCodeTextWatcher mPinCodeTextWatcher;
    private TextView mPinText1;
    private TextView mPinText2;
    private TextView mPinText3;
    private TextView mPinText4;
    private TextView mPinText5;
    private TextView mPinText6;
    private TextView mPinText7;
    private TextView mPinText8;

    private ImageView line1;
    private ImageView line2;
    private ImageView line3;
    private ImageView line4;
    private ImageView line5;
    private ImageView line6;
    private ImageView line7;


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
        mPinEdit = findViewById(com.example.android.R.id.login_pin_editText);
        mPinText1 = findViewById(com.example.android.R.id.login_pin_textView1);
        mPinText2 = findViewById(com.example.android.R.id.login_pin_textView2);
        mPinText3 = findViewById(com.example.android.R.id.login_pin_textView3);
        mPinText4 = findViewById(com.example.android.R.id.login_pin_textView4);
        mPinText5 = findViewById(com.example.android.R.id.login_pin_textView5);
        mPinText6 = findViewById(com.example.android.R.id.login_pin_textView6);
        mPinText7 = findViewById(com.example.android.R.id.login_pin_textView7);
        mPinText8 = findViewById(com.example.android.R.id.login_pin_textView8);

        line1 = findViewById(R.id.login_pin_line1);
        line2 = findViewById(R.id.login_pin_line2);
        line3 = findViewById(R.id.login_pin_line3);
        line4 = findViewById(R.id.login_pin_line4);
        line5 = findViewById(R.id.login_pin_line5);
        line6 = findViewById(R.id.login_pin_line6);
        line7 = findViewById(R.id.login_pin_line7);

        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        mPinEdit.setCursorVisible(false);

        mPinCodeTextWatcher = new PinCodeTextWatcher();
        mPinEdit.addTextChangedListener(mPinCodeTextWatcher);

        mLoginButton.setOnClickListener(this);
        mqrLoginButton.setOnClickListener(this);

        mHandler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);

                int what = message.what;
                String token;
                Intent intent;

                try {
                    switch (what) {
                        case LoginActivity.GET_QR_CODE_SUCCESS:
                            String qrCode = (String) message.obj;
                            //生成二维码图片时给qrcode做标记,方便扫码的设备区分二维码类型
                            show_qr_image("qrCode"+qrCode);
                            //开启长连接
                            String url = "ws://47.95.214.69:1002/auth/codeStatus?code="+qrCode;
                            System.out.println("url:"+url);
                            NewDeviceLoginWebSocket.main_run(url,mHandler);
                            break;

                        case LoginActivity.GET_QR_CODE_FAIL:
                            Toast.makeText(LoginActivity.this, "服务器出错了,请稍后再试", Toast.LENGTH_LONG).show();
                            break;

                        case LoginActivity.MESSAGE_PINLOGIN_SUCCESS_RESPONSE:
                            /*
                            拿到message返回的token值
                            携带token值跳转到UserPageActivity
                             */
                            token= (String)((JSONObject) message.obj).get("token");
                            intent = new Intent(LoginActivity.this, UserPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        case LoginActivity.MESSAGE_PINLOGIN_FAIL_RESPONSE:
                            // 这里写如果登录失败该怎么搞
                            String errorMessage = (String) ((JSONObject) message.obj).get("error");
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            setActionBar("用户名登录");
                            break;

                        case LoginActivity.GET_TOKEN_SUCCESS:
                            /**
                             * 成功拿到token值
                             * 携带token值请求userId
                             */
                            token = (String) message.obj;
                            System.out.println("成功token值:"+token);
                            getUserIdWithToken(token);

                        case LoginActivity.GET_USERID_SUCCESS:
                            /*
                            拿到message返回的userId
                            携带userId值跳转到UserPageActivity
                             */
                            String userId = (String) message.obj;
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

    private void show_qr_image(String qr_code) {
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(qr_code, 480, 480);
        qr_image.setImageBitmap(mBitmap);
        mUserIdEdit.setVisibility(EditText.INVISIBLE);
        mPinEdit.setVisibility(EditText.INVISIBLE);

        mPinText1.setVisibility(TextView.INVISIBLE);
        mPinText2.setVisibility(TextView.INVISIBLE);
        mPinText3.setVisibility(TextView.INVISIBLE);
        mPinText4.setVisibility(TextView.INVISIBLE);
        mPinText5.setVisibility(TextView.INVISIBLE);
        mPinText6.setVisibility(TextView.INVISIBLE);
        mPinText7.setVisibility(TextView.INVISIBLE);
        mPinText8.setVisibility(TextView.INVISIBLE);

        line1.setVisibility(ImageView.INVISIBLE);
        line2.setVisibility(ImageView.INVISIBLE);
        line3.setVisibility(ImageView.INVISIBLE);
        line4.setVisibility(ImageView.INVISIBLE);
        line5.setVisibility(ImageView.INVISIBLE);
        line6.setVisibility(ImageView.INVISIBLE);
        line7.setVisibility(ImageView.INVISIBLE);

        qr_image.setVisibility(ImageView.VISIBLE);
    }

    private void getUserIdWithToken(String token) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getUserIdByTokenToLogin(token , mHandler);
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
                    mPinText1.setVisibility(TextView.VISIBLE);
                    mPinText2.setVisibility(TextView.VISIBLE);
                    mPinText3.setVisibility(TextView.VISIBLE);
                    mPinText4.setVisibility(TextView.VISIBLE);
                    mPinText5.setVisibility(TextView.VISIBLE);
                    mPinText6.setVisibility(TextView.VISIBLE);
                    mPinText7.setVisibility(TextView.VISIBLE);
                    mPinText8.setVisibility(TextView.VISIBLE);

                    line1.setVisibility(ImageView.VISIBLE);
                    line2.setVisibility(ImageView.VISIBLE);
                    line3.setVisibility(ImageView.VISIBLE);
                    line4.setVisibility(ImageView.VISIBLE);
                    line5.setVisibility(ImageView.VISIBLE);
                    line6.setVisibility(ImageView.VISIBLE);
                    line7.setVisibility(ImageView.VISIBLE);

                    qr_image.setVisibility(ImageView.INVISIBLE);
                    setActionBar("用户名登录");
                } else {
                    String userid = mUserIdEdit.getText().toString();
                    String pin = getPinCode();
                    if (pin.length() != 8) {
                        Log.v(TAG, "口令必须是8位");
                        return;
                    }

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

    private String getPinCode() {
        return mPinCodeTextWatcher.getKey();
    }

    private class PinCodeTextWatcher implements TextWatcher {

        private static final int mMaxLength = 8;
        private static final String mDot = "●";

        private String key = "";

        public String getKey() {
            return key;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // 新增字符
            if (i1 == 0) {
                if (key.length() == mMaxLength) {
                    Toast.makeText(LoginActivity.this, "最多只能输入8位口令！", Toast.LENGTH_LONG).show();
                    Log.v(TAG, "最多只能输入8位口令！");
                    Log.v(TAG, key);
                }
                else {
                    char now = charSequence.charAt(i);
                    key += now;
                    if (mPinText1.getText().toString().isEmpty())
                        mPinText1.setText(mDot);
                    else if (mPinText2.getText().toString().isEmpty())
                        mPinText2.setText(mDot);
                    else if (mPinText3.getText().toString().isEmpty())
                        mPinText3.setText(mDot);
                    else if (mPinText4.getText().toString().isEmpty())
                        mPinText4.setText(mDot);
                    else if (mPinText5.getText().toString().isEmpty())
                        mPinText5.setText(mDot);
                    else if (mPinText6.getText().toString().isEmpty())
                        mPinText6.setText(mDot);
                    else if (mPinText7.getText().toString().isEmpty())
                        mPinText7.setText(mDot);
                    else if (mPinText8.getText().toString().isEmpty())
                        mPinText8.setText(mDot);
                }
            }
            // 删除字符
            if (i2 == 0) {
                if (key.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "已经删无可删了！", Toast.LENGTH_LONG).show();
                    Log.v(TAG, "已经删无可删了！");
                }
                else {
                    key = key.substring(0, key.length() - 1);
                    if (!mPinText8.getText().toString().isEmpty())
                        mPinText8.setText("");
                    else if (!mPinText7.getText().toString().isEmpty())
                        mPinText7.setText("");
                    else if (!mPinText6.getText().toString().isEmpty())
                        mPinText6.setText("");
                    else if (!mPinText5.getText().toString().isEmpty())
                        mPinText5.setText("");
                    else if (!mPinText4.getText().toString().isEmpty())
                        mPinText4.setText("");
                    else if (!mPinText3.getText().toString().isEmpty())
                        mPinText3.setText("");
                    else if (!mPinText2.getText().toString().isEmpty())
                        mPinText2.setText("");
                    else if (!mPinText1.getText().toString().isEmpty())
                        mPinText1.setText("");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}