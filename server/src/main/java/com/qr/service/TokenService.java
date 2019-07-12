package com.qr.service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenService {
    static Key key = new SecretKeySpec("tC0bL0m$SDKE7tgp6@u$ZGcoYv0wN*O#BUZU*GoJmqVqXt#S&R".getBytes(), SignatureAlgorithm.HS512.getJcaName());
    //过期时间一天
    static long timeOut = 24*60*60*1000;

    public static String createToken(String type,String id){
        Map<String,Object> header = new HashMap<>();
        header.put("type", type);

        JsonObject paylaod = new JsonObject();
        paylaod.addProperty("id", id);
        long timeNow = new Date().getTime();
        paylaod.addProperty("timeStamp",timeNow);
        paylaod.addProperty("timeOut",timeNow+timeOut);

        System.out.println("payload"+paylaod.toString());

        String token = Jwts.builder().setHeader(header).setPayload(paylaod.toString()).signWith(SignatureAlgorithm.HS512,key ).compact();
        System.out.println("--------------------产生的token:"+token);
        return token;
    }

    public static String parseToken(String token){
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        if(claimsJws == null)
            return null;
        JwsHeader header = claimsJws.getHeader();
        Claims body = claimsJws.getBody();
        if(body==null)
            return null;
        long timeNow = new Date().getTime();
        long tokenTimeOut = body.get("timeOut",Long.class);
        if(timeNow > tokenTimeOut )
            return null;
        String id = body.get("id", String.class);
        return id;
    }
}
