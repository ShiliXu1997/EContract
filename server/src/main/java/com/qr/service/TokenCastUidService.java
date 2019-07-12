package com.qr.service;

import java.util.HashMap;
import java.util.Map;

/*
*token和uid的映射
 */
public class TokenCastUidService {
    private static Map<String,String> currentClient;

    public TokenCastUidService() {
        this.currentClient = new HashMap<>();
    }

    public void addClient(String uuid,String uid){
        currentClient.put(uuid, uid);
    }
}
