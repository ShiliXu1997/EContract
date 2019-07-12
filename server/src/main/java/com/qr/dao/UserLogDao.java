package com.qr.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserLogDao {

    @Insert("insert into user_log values(#{userId},#{logSerial})")
    public int insert(long userId, String logSerial);

    @Select("select logserial from user_log where userid=#{id}")
    public List<String> getLogSerialByUserId(long id);
}
