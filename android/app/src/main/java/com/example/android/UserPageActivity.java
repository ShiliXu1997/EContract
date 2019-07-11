package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import utils.HttpUtil;

public class UserPageActivity extends Activity implements View.OnClickListener {

    private TextView userInfoText;
    private ImageButton mScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);

        userInfoText = findViewById(R.id.userInfoText);
        String userInfo = "userID: "+ HttpUtil.getInstance().getUserId() + "\nphoneID: " + HttpUtil.getInstance().getPhoneId();
        userInfoText.setText(userInfo);
        mScanButton = findViewById(R.id.scan_button);
        mScanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.scan_button:
                Intent intent = new Intent(UserPageActivity.this, com.google.zxing.activity.CaptureActivity.class);
                startActivity(intent);
                break;
        }
    }
}
