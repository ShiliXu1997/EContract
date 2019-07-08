package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private TextView mHintTextView;
    private EditText mUserNameEdit;
    private EditText mCardIdEdit;
    private EditText mPinEdit;
    private Button mRegisterButton;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.register_tittle));
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mHintTextView = findViewById(R.id.register_hint_textView);
        mUserNameEdit = findViewById(R.id.register_usrName_editText);
        mCardIdEdit = findViewById(R.id.register_cardId_editText);
        mPinEdit = findViewById(R.id.register_pin_editText);
        mRegisterButton = findViewById(R.id.register_button);

        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

        setListener();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                mUserNameEdit.setText("");
                mCardIdEdit.setText("");
                mPinEdit.setText("");
                try {
                    switch (message.what) {
                        case HttpUtil.MESSAGE_REGISTER_SUCCESS_RESPONSE:
                            JSONObject mesObj = (JSONObject) message.obj;
                            String userId = (String) mesObj.get("user_id");
                            String successString = getString(R.string.register_waitForApprove_hint);
                            successString = String.format(successString, userId);
                            mHintTextView.setText(successString);
                            break;
                        case HttpUtil.MESSAGE_REGISTER_FAIL_RESPONSE:
                            String failString = getString(R.string.register_fail_hint);
                            mHintTextView.setText(failString);
                            break;
                        default:
                            break;
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        };
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
                case R.id.register_button:
                    String userName = mUserNameEdit.getText().toString();
                    String cardId = mCardIdEdit.getText().toString();
                    String pin = mPinEdit.getText().toString();

                    HttpUtil httpUtil = HttpUtil.getInstance();
                    httpUtil.register(userName, cardId, mHandler);
                    break;
                default:
                    break;
            }
        }
    }
}