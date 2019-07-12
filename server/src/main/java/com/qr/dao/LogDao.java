package com.qr.dao;

import com.qr.entity.Log;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LogDao {

    @Insert("insert into log values(#{serial},#{detials},#{time})")
    public int insertLog(String serial,String detials,String time);

    @Select("select * from log where serial=#{serial}")
    @Results({
            @Result(property = "serial",column = "serial"),
            @Result(property = "detials",column = "detials"),
            @Result(property = "time",column = "time")
    })
    public Log getLogBySerial(String serial);

}
