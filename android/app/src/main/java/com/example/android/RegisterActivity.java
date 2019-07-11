package com.example.android;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import utils.HttpUtil;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    public static final int MESSAGE_REGISTER_SUCCESS_RESPONSE = 0x00005000;
    public static final int MESSAGE_REGISTER_FAIL_RESPONSE = 0x00005001;

    private TextView mHintTextView;
    private EditText mUserNameEdit;
    private EditText mCardIdEdit;
    private Button mRegisterButton;

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

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.R.layout.activity_register);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setTitle(getResources().getString(com.example.android.R.string.register_tittle));
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mHintTextView = findViewById(com.example.android.R.id.register_hint_textView);
        mUserNameEdit = findViewById(com.example.android.R.id.register_usrName_editText);
        mCardIdEdit = findViewById(com.example.android.R.id.register_cardId_editText);
        mRegisterButton = findViewById(com.example.android.R.id.register_button);

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

        setListener();

        Log.v(TAG, "准备构造句柄");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                mUserNameEdit.setText("");
                mCardIdEdit.setText("");
                mPinEdit.setText("");
                try {
                    switch (message.what) {
                        case MESSAGE_REGISTER_SUCCESS_RESPONSE:
                            JSONObject mesObj = (JSONObject) message.obj;
                            String userId = (String) mesObj.get("user_id");
                            String successString = getString(com.example.android.R.string.register_waitForApprove_hint);
                            successString = String.format(successString, userId);
                            mHintTextView.setText(successString);
                            mUserNameEdit.setVisibility(EditText.INVISIBLE);
                            mCardIdEdit.setVisibility(EditText.INVISIBLE);
                            mPinEdit.setVisibility(EditText.INVISIBLE);
                            mRegisterButton.setText("去登录");

                            break;
                        case MESSAGE_REGISTER_FAIL_RESPONSE:
                            String failString = getString(com.example.android.R.string.register_fail_hint);
                            mHintTextView.setText(failString);
                            Toast.makeText(RegisterActivity.this, "服务器出错了,请稍后再试", Toast.LENGTH_LONG).show();

                            break;
                        default:
                            break;
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        };
        Log.v(TAG, "准备获取公钥");
        HttpUtil.initServerPublicKey();
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

    private void setListener() {
        OnClick onClick = new OnClick();
        mRegisterButton.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case com.example.android.R.id.register_button:
                    if(mRegisterButton.getText().toString().equals("申请注册")) {
                        String userName = mUserNameEdit.getText().toString();
                        String cardId = mCardIdEdit.getText().toString();
                        String pinCode = getPinCode();
                        if (pinCode.length() != 8) {
                            Log.v(TAG, "口令必须是8位");
                            return;
                        }

                        HttpUtil httpUtil = HttpUtil.getInstance();
                        httpUtil.register(userName, cardId, pinCode, mHandler);
                    } else if(mRegisterButton.getText().toString().equals("去登录")){
                        //已经点击过注册按钮并且成功发送至后台,此时点击去登录
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    break;
                default:
                    break;
            }
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
                if (key.isEmpty())
                    Log.v(TAG, "已经删无可删了！");
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