package com.qr.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@ServerEndpoint("/api/signCodeStatus")
public class SignCodeService {
    private static Map<String,Session> client = new HashMap<>();
    //@Resource
    //private SignCodeStatusService signCodeSatausService;
    private static long TIMEOUT = 10*60*1000;
    @OnOpen
    public void onOpen(Session session){
        System.out.println("--------------------------文件状态长连接建立连接-------------------------");
        System.out.println(session.toString());
        String str = session.getQueryString();
        System.out.println("-----文件hash---:"+str);
        String code = str.substring(18);
        System.out.println("文件保存健："+code);

        client.put(code,session);

    }
    @OnClose
    public void onClose(Session session){
        System.out.println("--------------------------文件状态长连接关闭连接-------------------------");
        for(String signCode:client.keySet()){
            if(client.get(signCode) == session)
                client.remove(signCode);

            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Session session,String message){
        if(session == null)
            return;

        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Session getSession(String signCode){
        System.out.println("文件长链接客户键："+signCode);
        System.out.println("用户所有键：");
        for(String key : client.keySet())
            System.out.println(key);
        Session session = client.get(signCode);
        System.out.println("session："+session);
        return session;
    }
}
