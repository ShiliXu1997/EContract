package com.qr.service.impl;

import com.qr.dao.UserDao;
import com.qr.entity.User;
import com.qr.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Override
    public long insert(String name, String identity) {
        long id=userDao.getMax();
//        id+=10000;//最小id为10000
        id+=1;//自增的方式获取用户id
        if (userDao.insert(id,name,identity)==1)
            return  id;
        else
            return 0;
    }

    @Override
    public User getUserByName(String name) {
        return userDao.getUserByName(name);
    }

    @Override
    public User getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Override
    public long getUserIdBydentity(String identity) {
        return userDao.getUserIdByIdentity(identity);
    }

    @Override
    public List<User> getNotVerifiedUser(){
        return userDao.getNotVerifiedUser();
    }

    @Override
    public int authorizedUsers(long id,int verifyId){
        return userDao.updateVerifyById(id,verifyId);
    }
}
