package com.qr.controller;

import com.qr.service.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TestMain {

    @RequestMapping("admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("upload")
    public String upload(){
        return "upload";
    }

    @GetMapping("update")
    public String update(){return "update";}

    public static void main(String[] args) {
//        String hash = DigestUtils.md5DigestAsHex("admin".getBytes());
//        System.out.println(hash);
//        long currentTime=System.currentTimeMillis();
//        String date = new SimpleDateFormat("YYYY-MM-DD HH:MM:ss").format(currentTime);
//        System.out.println(date);
//        String path="adad/afaefadaf/agrafaf";
//        String[] a=path.split("/");
//        System.out.println(a[1]);
        System.out.println(TokenService.createToken("","admin"));
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        String userId = "40022";
//        System.out.println(userId);
//        long l = Long.parseLong(userId);
//        System.out.println(l);
//        String path= "jsdfngksfgpoekf[pwr/rwegrg/efgrop.txt";
//        String newPath = path.substring(0,path.length()-4);
//        System.out.println(newPath);

//        System
    }
}
