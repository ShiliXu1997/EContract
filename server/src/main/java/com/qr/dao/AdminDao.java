package com.qr.dao;

import com.qr.entity.Administrator;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminDao {

    @Select("select * from administrator")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "hash",column = "hash")
    })
    public Administrator getAdmin();

}
