package com.qr.service.impl;

import com.qr.dao.LogDao;
import com.qr.dao.UserLogDao;
import com.qr.entity.Log;
import com.qr.service.UserLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserLogServiceImpl implements UserLogService {
    @Resource
    private UserLogDao userLogDao;
    @Resource
    private LogDao logDao;
//    @Override
//    public int insert(long userId,String detials,String time) {
//        String logSerial=UUID.randomUUID().toString();
//        if (userLogDao.insert(userId,logSerial)==1&&logDao.insertLog(logSerial,detials,time)==1)
//            return 1;
//        return 0;
//    }

    @Override
    public List<Log> getLogByUserId(long id) {
        List<String> logSerials=userLogDao.getLogSerialByUserId(id);
        List<Log> logs=new ArrayList<Log>();
        for(int i=0;i<logSerials.size();i++){
            logs.add(logDao.getLogBySerial(logSerials.get(i)));
        }
        return logs;
    }
}
