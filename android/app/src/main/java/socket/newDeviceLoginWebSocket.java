package socket;

import android.os.Handler;
import android.os.Message;

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

public class newDeviceLoginWebSocket extends WebSocketClient {

    private Handler mHandler;

    public newDeviceLoginWebSocket(URI serverUri,Handler handler) {
        super(serverUri);
        this.mHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("open success");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("new message:" + message);

        try {
            JSONObject data = new JSONObject(message);
            String userId = (String) data.get("user_id");
            String signedHash = (String)data.get("signed_hash");
            if (SecurityUtil.verifyStringByRSAPublicKeyString(userId,signedHash, HttpUtil.getServerPublicKey())) {
                System.out.println("userId验签成功");
                Message mess = mHandler.obtainMessage();
                mess.what = LoginActivity.GET_QR_CODE_SUCCESS;
                JSONObject mesObj = new JSONObject();
                mesObj.put("userId",userId);
                mess.obj = mesObj;
                mess.sendToTarget();
            } else {
                System.out.println("userId验签失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed:" + code +"  reason:" +reason+"  remote:"+remote);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("error: "+ ex.toString());
    }

    public static void main_run(String url, Handler handler) {
        WebSocketClient client = null;
        try {
            client = new newDeviceLoginWebSocket(new URI(url),handler);
        } catch (URISyntaxException e) {
            System.out.println("url error");
            e.printStackTrace();
        }
        client.connect();
    }
}
