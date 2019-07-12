package com.qr.service;

import com.qr.dao.LogDao;
import com.qr.dao.UserLogDao;
import com.qr.entity.UserLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class LogService {
    @Resource
    private LogDao logDao;
    @Resource
    private UserLogDao userLogDao;
    public boolean log(long userId,String detial){
        String serial= UUID.randomUUID().toString();
        long currentTime=System.currentTimeMillis();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("log测试");
        System.out.println(userId);
        System.out.println(serial);
        System.out.println(date);
        if (userLogDao.insert(userId,serial)==1&& logDao.insertLog(serial,detial,date)==1) {
            return true;
        }
        return false;
    }

}
