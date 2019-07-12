package com.qr.service;

import com.google.gson.JsonObject;
import com.qr.model.WebSocketClient;
import com.qr.model.codeStatus;
import com.qr.model.WebSocketClient;
import com.qr.model.codeStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.websocket.OnClose;
import javax.websocket.OnOpen;

import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ServerEndpoint(value = "/auth/codeStatus")
@Component

public class QRCodeStatusService {
    @Autowired
    private QRCodeService qrCodeService = new QRCodeService();
   // @Autowired
    //private TokenCastUidService tokenCastUidService = new TokenCastUidService();
    private static Map<String, WebSocketClient> client = new HashMap<>();
    @OnOpen
    public void Open(Session session){
            WebSocketClient newClient = new WebSocketClient();
            newClient.setSession(session);
            System.out.println(session.toString());
            String str = session.getQueryString();
            System.out.println("-----长连接建立请求参数"+str);
            String code = str.substring(5);
            System.out.println(code);
            if(!qrCodeService.isCodeExist(code)) {
                    System.out.println(code+"不存在");
                return;
            }

            newClient.setCode(code);
            client.put(session.getId(), newClient);

            System.out.println("连接成功");
    }

    @OnClose
    public void OnClose(Session session){
        System.out.println("-----连接请求关闭:"+session.getId());
        try {
            if(client.get(session.getId()) == null){
                //session.getBasicRemote().sendText("连接关闭失败，不存在该连接");
                System.out.println("连接关闭失败，不存在该连接");
                return;
            }

            client.remove(session.getId());
            System.out.println("连接关闭成功");
            //session.getBasicRemote().sendText("连接关闭成功");
            session.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean sendMessage(String code){
        System.out.println("----验证码【"+code+"】状态返回");
        try{
            Session session = null;
            for(WebSocketClient wsClient:client.values()){
                if(wsClient.getCode().compareTo(code) == 0){
                    session = wsClient.getSession();
                    break;
                }
            }
            if (session != null) {
                codeStatus cs = qrCodeService.getStatus(code);
                JsonObject jsonObject = new JsonObject();
                System.out.println("验证码状态：" + cs);
                if (cs == codeStatus.SUCCESS) {
                    jsonObject.addProperty("status", 200);
                    jsonObject.addProperty("data", TokenService.createToken("ANDROID_CODE", qrCodeService.getUid(code)));
                } else {
                    jsonObject.addProperty("status", 400);
                    jsonObject.addProperty("data", "");
                }

                System.out.println("-----状态码状态返回："+jsonObject.toString());
                session.getBasicRemote().sendText(jsonObject.toString());
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
