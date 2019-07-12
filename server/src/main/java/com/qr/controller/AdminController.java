package com.qr.controller;

import com.qr.entity.Administrator;
import com.qr.entity.User;
import com.qr.service.AdminService;
import com.qr.service.TokenService;
import com.qr.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;

    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    @ResponseBody
    public Map admin(@RequestParam("id") String id, @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<>();
        Administrator admin = adminService.getAdmin();
        String hash = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("提交的admin："+admin);
        System.out.println("password"+password);
        if (admin.getId().equals(id) && admin.getHash().equals(hash)) {
            String token = TokenService.createToken("CONTRACT_SIGN_CODE", "admin");
            map.put("status", 200);
            map.put("message", "success");
            map.put("data", token);
        } else {
            map.put("status", 400);
            map.put("message", "ID或密码错误");
        }
        return map;
    }

    @RequestMapping(value = "/admin/unauthorizedUsers", method = RequestMethod.GET)
    @ResponseBody
    public Map getUnauthorizedUsers(@PathParam("token") String token) {
        Map<String, Object> map = new HashMap<>();
        String admin = TokenService.parseToken(token);
        System.out.println(admin);
        if (admin == null) {
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        } else if (admin.equals("admin")) {
            List<User> users = userService.getNotVerifiedUser();
            map.put("status", 200);
            map.put("message", "success");
            List<Map> list = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("id", user.getId());
                map1.put("name", user.getName());
                map1.put("cardID", user.getIdentity());
                list.add(map1);
            }
            map.put("data", list);
        } else {
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }
        return map;
    }

    @RequestMapping(value = "/admin/unauthorizedUsers/{userId}/accept", method = RequestMethod.POST)
    @ResponseBody
    public Map acceptUnauthorizedUsers(@PathVariable("userId") String userId, @PathParam("token") String token) {
        Map<String, Object> map = new HashMap<>();
        String admin = TokenService.parseToken(token);
        if (admin == null) {
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        } else if (admin.equals("admin")) {
            userService.authorizedUsers(Long.parseLong(userId),1);
            map.put("status",200);
            map.put("message","success");
        } else {
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }
        return map;
    }

    @RequestMapping(value = "/admin/unauthorizedUsers/{userId}/decline", method = RequestMethod.POST)
    @ResponseBody
    public Map declineUnauthorizedUsers(@PathVariable("userId") String userId, @PathParam("token") String token) {
        Map<String, Object> map = new HashMap<>();
        String admin = TokenService.parseToken(token);
        if (admin == null) {
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        } else if (admin.equals("admin")) {
            userService.authorizedUsers(Long.parseLong(userId),-1);
            map.put("status",200);
            map.put("message","success");
        } else {
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }
        return map;
    }
}
