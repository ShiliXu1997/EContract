package com.example.android;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUserNameEdit;
    private EditText mPasswordEdit;
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
        mPasswordEdit = findViewById(R.id.register_cardId_editText);
        mPinEdit = findViewById(R.id.register_pin_editText);
        mRegisterButton = findViewById(R.id.register_button);

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