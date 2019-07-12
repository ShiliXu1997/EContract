package com.qr.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDeviceDao {

    @Insert("insert into user_device values(#{userId},#{deviceId},#{publicKey})")
    public int insert(long userId, String deviceId, String publicKey);

    @Select("select public_key from user_device where userid=#{userId} and deviceid=#{deviceId}")
    public String getPublicKeyByUserIdAndDeviceId(long userId,String deviceId);

}
