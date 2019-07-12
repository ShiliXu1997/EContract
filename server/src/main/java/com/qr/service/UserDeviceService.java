package com.qr.service;

public interface UserDeviceService {
    public int insert(long userId,String deviceId,String publicKey);
    public String getPublicKey(long userId, String deviceId);
}
