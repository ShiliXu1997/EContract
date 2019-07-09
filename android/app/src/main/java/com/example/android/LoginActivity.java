package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextView mLoginHintTextView;
    private Button mPINCodeLoginButton;
    private Button mQRCodeLoginButton;

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

        mLoginHintTextView = findViewById(R.id.login_hint_textView);
        mQRCodeLoginButton = findViewById(R.id.qrcode_login_button);
        mPINCodeLoginButton = findViewById(R.id.pincode_login_button);

        setListener();
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
        LoginActivity.OnClick onClick = new LoginActivity.OnClick();
        mQRCodeLoginButton.setOnClickListener(onClick);
        mPINCodeLoginButton.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch(v.getId()) {
                case R.id.qrcode_login_button:
                    intent = new Intent(LoginActivity.this, QRCodeLoginActivity.class);
                    break;
                case R.id.pincode_login_button:
                    intent = new Intent(LoginActivity.this, PINCodeLoginActivity.class);
                    break;
                default:
                    break;
            }
            startActivity(intent);
        }
    }
}