package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import utils.HttpUtil;

import static android.content.ContentValues.TAG;

public class QrPinActivity extends AppCompatActivity implements View.OnClickListener {

    //二维码授权登录的过程中信息提交完毕,完成授权
    public static final int QR_LOGIN_SUCCESS = 0x00005001;
    public static final int QR_LOGIN_FAIL = 0x00005002;


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

    private Button pinLogin;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpin);

        mPinEdit = findViewById(com.example.android.R.id.login_pin_editText);
        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        mPinText1 = findViewById(com.example.android.R.id.login_pin_textView1);
        mPinText2 = findViewById(com.example.android.R.id.login_pin_textView2);
        mPinText3 = findViewById(com.example.android.R.id.login_pin_textView3);
        mPinText4 = findViewById(com.example.android.R.id.login_pin_textView4);
        mPinText5 = findViewById(com.example.android.R.id.login_pin_textView5);
        mPinText6 = findViewById(com.example.android.R.id.login_pin_textView6);
        mPinText7 = findViewById(com.example.android.R.id.login_pin_textView7);
        mPinText8 = findViewById(com.example.android.R.id.login_pin_textView8);

        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        mPinEdit.setCursorVisible(false);

        mPinCodeTextWatcher = new PinCodeTextWatcher();
        mPinEdit.addTextChangedListener(mPinCodeTextWatcher);

        pinLogin = (Button) findViewById(R.id.qr_pin_login_button);
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
                String pinCode = getPinCode();
                if (pinCode.length() != 8) {
                    Log.v(TAG, "口令必须是8位");
                    return;
                }

                if(!pinCode.isEmpty()) {
                    String userId = getIntent().getStringExtra("userId");
                    HttpUtil.getInstance().qrLogin(userId,pinCode,mHandler);
                }
                break;
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
                    Toast.makeText(QrPinActivity.this, "最多只能输入8位口令！", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(QrPinActivity.this, "已经删无可删了！", Toast.LENGTH_LONG).show();
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
};

