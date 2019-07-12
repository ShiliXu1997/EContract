package com.qr.service.impl;

import com.qr.dao.UserDeviceDao;
import com.qr.service.UserDeviceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserDeviceServiceImpl implements UserDeviceService {
    @Resource
    private UserDeviceDao userDeviceDao;

    @Override
    public int insert(long userId, String deviceId, String publicKey) {
        return userDeviceDao.insert(userId,deviceId,publicKey);
    }

    @Override
    public String getPublicKey(long userId, String deviceId) {
        return userDeviceDao.getPublicKeyByUserIdAndDeviceId(userId,deviceId);
    }
}
