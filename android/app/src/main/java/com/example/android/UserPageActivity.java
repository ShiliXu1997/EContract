package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserPageActivity extends Activity implements View.OnClickListener {

    private Button scan_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpage);

        scan_button = findViewById(R.id.scan_button);
        scan_button.setOnClickListener(this);

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
