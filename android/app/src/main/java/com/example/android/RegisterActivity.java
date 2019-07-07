package com.example.android;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUserNameEdit;
    private EditText mCardIdEdit;
    private EditText mPinEdit;
    private Button mRegisterButton;

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

        mUserNameEdit = findViewById(R.id.register_usrName_editText);
        mCardIdEdit = findViewById(R.id.register_cardId_editText);
        mPinEdit = findViewById(R.id.register_pin_editText);
        mRegisterButton = findViewById(R.id.register_button);

        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

        setListener();

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

//                    TelephonyManager telephonyManager = (TelephonyManager) RegisterActivity.this.getSystemService(TELEPHONY_SERVICE);
//                    String phoneId = telephonyManager.getDeviceId(0);
//                    System.out.println(phoneId);

                    HttpUtil httpUtil = HttpUtil.getInstance();
                    httpUtil.register(userName, cardId);
                    break;
                default:
                    break;
            }
        }
    }
}