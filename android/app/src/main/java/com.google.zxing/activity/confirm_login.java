package com.google.zxing.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.LoginActivity;
import com.example.android.MainActivity;
import com.example.android.R;

import org.json.JSONObject;

public class confirm_login extends Activity implements View.OnClickListener {
    public static final int CONFIRM_LOGIN_FINISHED=1;

    private Button confire_login;
    private Button cancal_confire_login;
    private String login_code;

    private Handler confire_login_hander;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmlogin);


        //拿到扫描的二维码的内容
        Bundle receive=getIntent().getExtras();
        this.login_code=receive.getString("qr_code");
        System.out.println("成功得到二维码:"+login_code);

        //实例化按钮并设置监听
        this.confire_login = (Button)findViewById(R.id.confirm_login_button);
        confire_login.setOnClickListener(this);
        this.cancal_confire_login = (Button)findViewById(R.id.cancal_login_button);
        cancal_confire_login.setOnClickListener(this);

        this.confire_login_hander = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                int what = message.what;
                try {
                    switch (what) {
                        case confirm_login.CONFIRM_LOGIN_FINISHED:
                            JSONObject mesObj = (JSONObject) message.obj;
                            Boolean if_success = (Boolean) mesObj.get("if_success");
                            System.out.println("发送状态:"+if_success);
//                            Toast toast=new Toast(this);
//                            toast.setDuration(Toast.LENGTH_SHORT);//设置持续时间
//                            toast.setGravity(Gravity.CENTER,0, 0);//设置对齐方式
//                            LinearLayout ll=new LinearLayout(this);//创建一个线性布局管理器
//                            ImageView imageView=new ImageView(this);
//                            imageView.setImageResource(R.drawable.stop);
//                            imageView.setPadding(0, 0, 5, 0);
//                            ll.addView(imageView);
//                            TextView tv=new TextView(this);
//                            tv.setText("我是通过构造方法创建的消息提示框");
//                            ll.addView(tv);
//                            toast.setView(ll);//设置消息提示框中要显示的视图
//                            toast.show();//显示消息提示框
                            break;
                    }

                } catch (Exception e) {

                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        if(vid==this.confire_login.getId()) {
            authorization();
            Intent intent = new Intent(confirm_login.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if(vid==this.cancal_confire_login.getId()) {
            Intent intent = new Intent(confirm_login.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void authorization() {
        socket.socketclient.main_run("ws://47.95.214.69:1001",this.confire_login_hander);
    }
}
