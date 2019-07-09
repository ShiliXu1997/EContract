package socket;

import android.graphics.Bitmap;
import android.os.Message;
import android.widget.EditText;
import android.widget.ImageView;


import utils.HttpUtil;
import utils.QRCodeUtil;
import com.example.android.LoginActivity;


import org.java_websocket.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import android.os.Handler;

public class socketclient extends WebSocketClient {

    private Handler handler;

    public socketclient(URI serverUri,Handler handler) {
        super(serverUri);
        this.handler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("open success");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("new message:" + message);
        Message mess = handler.obtainMessage();
        mess.what = LoginActivity.GET_QR_CODE;
        JSONObject mesObj = new JSONObject();
        try {
            mesObj.put("qr_code",message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mess.obj = mesObj;

        mess.sendToTarget();
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
            client = new socketclient(new URI(url),handler);
        } catch (URISyntaxException e) {
            System.out.println("url error");
            e.printStackTrace();
        }
        client.connect();
    }
}
