package com.qr.model;


import javax.websocket.Session;

public class WebSocketClient {
    private String code;
    private Session session;

    public void setCode(String code) {
        this.code = code;
    }

    public Session getSession() {
        return session;
    }

    public String getCode() {

        return code;
    }

    public void setSession(Session session) {

        this.session = session;
    }
}
