package socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.android.LoginActivity;

import org.java_websocket.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import utils.HttpUtil;
import utils.SecurityUtil;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NewDeviceLoginWebSocket extends WebSocketClient {

    private Handler mHandler;

    public static void main_run(String url, Handler handler) {
        WebSocketClient client = null;
        try {
            client = new NewDeviceLoginWebSocket(new URI(url),handler);
        } catch (URISyntaxException e) {
            System.out.println("url error");
            e.printStackTrace();
        }
        client.connect();
    }

    public NewDeviceLoginWebSocket(URI serverUri, Handler handler) {
        super(serverUri);
        this.mHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.v(TAG, "成功连上长连接！");
    }

    @Override
    public void onMessage(String message) {
        Log.v(TAG, "服务器长连接消息:" + message);
        try {
            JSONObject data = new JSONObject(message);
            int statusCode = (int) data.get("status");
            if(statusCode == 200) {
                String token = (String)data.get("data");
                //回调
                Message mess = mHandler.obtainMessage();
                mess.what = LoginActivity.GET_TOKEN_SUCCESS;
                mess.obj = token;
                mess.sendToTarget();
            } else if(statusCode == 400) {
                //回调
                System.out.println("服务器长连接返回400!");
            }

            //关闭长连接
            this.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.v(TAG, "Close reason" + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("error: "+ ex.toString());
    }
}
