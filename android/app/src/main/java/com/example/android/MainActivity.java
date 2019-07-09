package com.example.android;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private TextView mHelloText;
    private Button mRegisterButton;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelloText = findViewById(R.id.hello_textView);
        mRegisterButton = findViewById(R.id.register_button);
        mLoginButton = findViewById(R.id.login_button);

        setListener();
        Log.v(TAG, "应用已成功打开");
//        FileUtil.writeFile("miao.txt", "我爱你");
//        FileUtil.readFile("miao.txt");
    }

    private void setListener() {
        OnClick onClick = new OnClick();
        mRegisterButton.setOnClickListener(onClick);
        mLoginButton.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch(v.getId()) {
                case R.id.register_button:
                    intent = new Intent(MainActivity.this, RegisterActivity.class);
                    break;
                case R.id.login_button:
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    break;
                default:
                    break;
            }
            startActivity(intent);
        }
    }
}
