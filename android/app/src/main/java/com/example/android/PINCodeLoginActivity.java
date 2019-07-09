package com.example.android;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PINCodeLoginActivity extends AppCompatActivity {

    private EditText mUserIdEdit;
    private EditText mPinEdit;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode_login);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.pincode_login_tittle));
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUserIdEdit = findViewById(R.id.pincode_login_usrId_editText);
        mPinEdit = findViewById(R.id.pincode_login_pin_editText);
        mLoginButton = findViewById(R.id.pincode_login_button);

        mPinEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
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
}
