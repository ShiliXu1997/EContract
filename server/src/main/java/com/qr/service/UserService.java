package com.qr.service;

import com.qr.entity.User;

import java.util.List;

public interface UserService {
    public long insert(String name, String identity);
    public User getUserByName(String name);
    public User getUserById(long id);
    public long getUserIdBydentity(String identity);
    public List<User> getNotVerifiedUser();
    public int authorizedUsers(long id,int verifyId);
}
