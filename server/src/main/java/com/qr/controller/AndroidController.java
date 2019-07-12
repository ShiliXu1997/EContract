package com.qr.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qr.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/app")
public class AndroidController {
    @Resource
    private UserContractService userContractService;
    //@Resource
    private SignCodeService signCodeService = new SignCodeService();
    @Resource
    private SignCodeStatusService signCodeStatusService;
    @Resource
    private UserService userService;
    @Resource
    private UserDeviceService userDeviceService;
    private ServerKeyService serverKeyService = new ServerKeyService();
    private EncDecService encDecService = new EncDecService();
    private QRCodeService qrCodeService = new QRCodeService();
    private QRCodeStatusService qrCodeStatusService = new QRCodeStatusService();
    //private TokenService tokenService = new TokenService();

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody

    public Map register(@RequestBody String requestParm){
        System.out.println("注册接口");
        System.out.println(requestParm);
        Gson gson = new Gson();
        JsonObject requestParmJson = gson.fromJson(requestParm, JsonObject.class);
        String ks = requestParmJson.get("encrypted_key").toString();
        String ed = requestParmJson.get("data").toString();
        System.out.println("密钥："+ks);
        System.out.println("数据："+ed);

        String secKey = serverKeyService.decodeEncData(ks);
        System.out.println("解出来的密钥:"+secKey);
        String data = encDecService.decDES(ed, secKey);
        System.out.println(data);

        Map<String,String> map = new HashMap<>();
        map = gson.fromJson(data, map.getClass());

        Map<String,Object> response = new HashMap<>();
        //获取用户数据，验签
        String dataBeforeSign = map.get("user_name")+map.get("card_id")+map.get("phone_id")+map.get("public_key")+map.get("time");
        System.out.println("注册参数：");
        System.out.println("user_name："+map.get("user_name"));
        System.out.println("card_id："+map.get("card_id"));
        System.out.println("phone_id："+map.get("phone_id"));
        System.out.println("public_key："+map.get("public_key"));
        System.out.println("time："+map.get("time"));

        if(encDecService.verifySignture(dataBeforeSign, map.get("signed_hash"),map.get("public_key"))){
            //向dao层请求生成一个uid
            //插入数据
            String uid = Long.toString(userService.insert(map.get("user_name"),map.get("card_id")));
            userDeviceService.insert(Long.parseLong(uid),map.get("phone_id"),map.get("public_key"));
            String serverSign = serverKeyService.signatureMess(uid);
            response.put("status_code", 200);
            JsonObject dataJson = new JsonObject();
            String symKey = EncDecService.createSymKey();
            if(symKey == null)
                System.out.println("产生密钥失败");
            dataJson.addProperty("user_id",uid);
            dataJson.addProperty("signed_hash",serverSign);
            String encData = encDecService.encDES(dataJson.toString(),symKey);
            String encSymKey = encDecService.encRSA(symKey,map.get("public_key"));
            System.out.println("加密后的对称密钥"+encSymKey);
            response.put("encrypted_key",encSymKey);
            response.put("data",encData);
            System.out.println("encData:"+encData);
            System.out.println("user_id"+uid);
            System.out.println("signed_hash:"+serverSign);
        }else{
            response.put("status_code", 400);
            String serverSign = "null";
            JsonObject dataJson = new JsonObject();
            String symKey = EncDecService.createSymKey();
            dataJson.addProperty("uid","null");
            dataJson.addProperty("signed_hash",serverSign);
            String encData = encDecService.encDES(dataJson.toString(),symKey);
            String encSymKey = encDecService.encRSA(symKey,map.get("public_key"));
            response.put("encrypted_key",encSymKey);
            response.put("data",encData);
        }

        return response;
    }

    @RequestMapping("/key")
    @ResponseBody
    public Map getPublicKey(){
        String key = serverKeyService.getPublicKey();
//        String sign = serverKeyService.signatureMess(key);
        if(key == "" )
            return null;
        Map<String,String> map = new HashMap<>();
        map.put("public_key_of_server",key);
//        map.put("sign",sign);
        return map;
    }

    @RequestMapping("normal_login")
    @ResponseBody
    public Map normalLogin(@RequestBody String requestParm){

        Gson gson = new Gson();
        JsonObject requestParmJson = gson.fromJson(requestParm, JsonObject.class);
        String encryptedKey = requestParmJson.get("encrypted_key").toString();
        String ciphertext = requestParmJson.get("data").toString();
        //获得对称密钥
        String key = serverKeyService.decodeEncData(encryptedKey);
        System.out.println(key);
        //解密密文
        String plainText = encDecService.decDES(ciphertext, key);
        System.out.println("口令登陆json数据:"+plainText);
        Map<String,String> plainTextJson = new HashMap<>();
        plainTextJson = gson.fromJson(plainText, plainTextJson.getClass());
        //获取公钥
        //验证签名
        String userId = plainTextJson.get("user_id");
        System.out.println("口令登陆user_id:"+userId);
        String sign = plainTextJson.get("signed_hash");
        System.out.println("口令登陆signed_hash:"+sign);
        String phoneId = plainTextJson.get("phone_id");
        System.out.println("口令登陆phone_id"+phoneId);
        String publicKey = userDeviceService.getPublicKey(Long.parseLong(userId),plainTextJson.get("phone_id"));
        System.out.println("-------用户公约："+publicKey);
        String dataBeforSign = userId+phoneId;

        Map<String,Object> response = new HashMap<>();

        if(encDecService.verifySignture(dataBeforSign, sign, publicKey)){
            String token = TokenService.createToken("ANDROID_CODE", userId);
            System.out.println("口令登陆产生token"+token);
            String tokenSign = serverKeyService.signatureMess(token);
            System.out.println("口令登陆token的签名"+tokenSign);
            String symKey = EncDecService.createSymKey();
            System.out.println("口令登陆对称密钥:"+symKey);
            String encSymKey = encDecService.encRSA(symKey,publicKey);
            System.out.println("口令登陆对称密钥加密后:"+encSymKey);
            JsonObject data = new JsonObject();
            data.addProperty("token", token);
            data.addProperty("signed_hash", tokenSign);
            String desData = encDecService.encDES(data.toString(), symKey);
            System.out.println("口令登陆的加密数据："+desData);
            response.put("encrypted_key", encSymKey);
            response.put("status_code", 200);
            response.put("data", desData); }
        else
            response.put("status_code", 400);

        System.out.println("返回口令登陆结果");
        return response;

        //将uid绑定
//        qrCodeService.scanCode(code, uid);
//        qrCodeStatusService.sendMessage(code);
//        return 200;
    }

    @RequestMapping("/help_login")
    @ResponseBody
    public Map help_login(@RequestBody String requestParm){
        System.out.println("------------------------------安卓扫码登陆----------------------------");
        Gson gson = new Gson();
        JsonObject requestParmJson = gson.fromJson(requestParm, JsonObject.class);
        String encryptedKey = requestParmJson.get("encrypted_key").toString();
        String ciphertext = requestParmJson.get("data").toString();
        //获得对称密钥
        String key = serverKeyService.decodeEncData(encryptedKey);
        //解密密文
        String plainText = encDecService.decDES(ciphertext, key);
        Map<String,String> plainTextJson = new HashMap<>();
        plainTextJson= gson.fromJson(plainText, plainTextJson.getClass());

        String userId = plainTextJson.get("user_id");
        String randomStr = plainTextJson.get("random_str");
        String time = plainTextJson.get("time");
        String signHash = plainTextJson.get("signed_hash");
        String phoneId = plainTextJson.get("phone_id");

        System.out.println("user_id : "+userId);
        System.out.println("time : "+time);
        System.out.println("signed_hash : "+signHash);
        System.out.println("phone_id : "+phoneId);
        System.out.println("安卓扫码字符串："+randomStr);

        String publicKey = userDeviceService.getPublicKey(Long.parseLong(userId),phoneId);

        Map<String,Object> response = new HashMap<>();
        if(encDecService.verifySignture(time+userId+phoneId+randomStr, signHash, publicKey)) {
            boolean scanCodeResult = qrCodeService.scanCode(randomStr, userId);
            if(scanCodeResult)
                System.out.println("二维码绑定uid");
            boolean sendMessageResult = qrCodeStatusService.sendMessage(randomStr);
            if(scanCodeResult)
                System.out.println("");
            if (scanCodeResult && sendMessageResult) {
                response.put("status_code", 200);
                return response;
            }
        }

        response.put("status_code", 400);
        return response;
    }

    @RequestMapping("test")
    public void test(){
        String str = "sdfndsklvnfkdlbmldfmbldfmbldf";
        String end = serverKeyService.encodeData(str);
        System.out.println("加密的数据："+end);
        String dec = serverKeyService.decodeEncData(end);
        System.out.println("解密的数据："+dec);
    }
    @RequestMapping("get_userid")
    @ResponseBody
    public Map getUserId(@RequestParam("token") String token){
        Map<String,String> response = new HashMap<>();
        String idInToken = TokenService.parseToken(token);
        if(idInToken == ""){
            response.put("user_id", "null");
            return response;
        }
        response.put("user_id", idInToken);
        return response;
    }
    @RequestMapping("qrcode_login")
    @ResponseBody
    public Map registerNewDivice(@RequestBody String requestParm){
        System.out.println("添加新设备信息");
        Gson gson = new Gson();
        JsonObject requestParmJson = gson.fromJson(requestParm,JsonObject.class);
        String ks = requestParmJson.get("encrypted_key").toString();
        String ed = requestParmJson.get("data").toString();
        System.out.println("密钥："+ks);
        System.out.println("数据："+ed);

        String secKey = serverKeyService.decodeEncData(ks);
        String data = encDecService.decDES(ed, secKey);

        Map<String,String> map = new HashMap<>();
        map = gson.fromJson(data, map.getClass());

        Map<String,Object> response = new HashMap<>();
        //获取用户数据，验签
        String dataBeforeSign = map.get("user_id")+map.get("phone_id")+map.get("public_key")+map.get("time");
        System.out.println("参数：");
        System.out.println("user_id："+map.get("user_id"));
        System.out.println("phone_id："+map.get("phone_id"));
        System.out.println("public_key："+map.get("public_key"));
        System.out.println("time："+map.get("time"));

        System.out.println(dataBeforeSign);

        if(encDecService.verifySignture(dataBeforeSign, map.get("hash"),map.get("public_key"))){
            //插入数据
            String uid = map.get("user_id");
            userDeviceService.insert(Long.parseLong(uid),map.get("phone_id"),map.get("public_key"));
            response.put("status_code", 200);
        }else{
            response.put("status_code", 400);
        }

        return response;
    }

    @RequestMapping("scanContractsSignCode")
    @ResponseBody
    public Map scanContractsSignCode(@RequestBody String requestParm,@RequestParam("token")String token){
        System.out.println("-----------------------合同扫码------------------------------");
        Gson gson = new Gson();

        JsonObject requestParmJson = gson.fromJson(requestParm,JsonObject.class);
        String ks = requestParmJson.get("encrypted_key").toString();
        System.out.println("未解密的密钥："+ks);
        String ed = requestParmJson.get("data").toString();
        System.out.println("未解密的数据："+ks);

        String secKey = serverKeyService.decodeEncData(ks);
        System.out.println("解密得到的对称密钥："+ks);
        String data = encDecService.decDES(ed, secKey);
        System.out.println("解密得到的数据："+ed);
        Map<String,String> map = new HashMap<>();
        map = gson.fromJson(data,map.getClass());

        String userId = map.get("user_id");
        String sign = map.get("signed_hash");
        String phone_id = map.get("phone_id");
        String hash = map.get("hash");
        System.out.println("--------合同数据：");
        System.out.println("----user_id : "+userId);;
        System.out.println("----signed_hash : "+sign);
        System.out.println("---phone_id : "+phone_id);
        System.out.println("---contract hash : "+hash);

        Map<String,Object> response = new HashMap<>();
        //验证token
        String idInToken = TokenService.parseToken(token);
        System.out.println("idInToken  : "+idInToken);
        if(idInToken.compareTo(userId) != 0){
            System.out.println("token里的userID与数据里的uid不一致");
            response.put("status_code",400);
            return response;
        }
        //验证user签名
        String publicKey = userDeviceService.getPublicKey(Long.parseLong(userId),phone_id);
        System.out.println("获得用户公钥："+publicKey);
        if(encDecService.verifySignture(hash,sign,publicKey)){
            //保存签名文件
            //记录签名状态
            String contractId = signCodeStatusService.getContractId(hash);

            if (contractId == null){
                System.out.println("合同id为空");
                response.put("status_code",400);
                return response;
            }
            if(!saveSign(sign,userId,contractId)){
                response.put("status_code",400);
                return response;
            }
            System.out.println("合同保存成功");
            boolean isSign = signCodeStatusService.userSign(userId,contractId);
            if(!isSign){
                System.out.println("列表签名状态保存失败");
                response.put("status_code",400);
                return response;
            }
            System.out.println("列表签名状态改变成功");
            //jsdfhsdlgvls
            if (userContractService.getUserContract(contractId).getUserId1()==Long.parseLong(userId)){
                userContractService.signContract(contractId,Long.parseLong(userId));
            }else {
                userContractService.signContract(contractId,Long.parseLong(userId));
            }

            System.out.println("数据库合同状态保存成功");
            response.put("status_code",200);

            Session session = signCodeService.getSession(hash);
            if(session == null)
                System.out.println("session为空");
            JsonObject massage = new JsonObject();
            massage.addProperty("status",200);
            massage.addProperty("massage","success");
            signCodeService.sendMessage(session,massage.toString());
            System.out.println("消息发送");

//            if(signCodeStatusService.signBoth(contractId)){
//                System.out.println("两个用户都签名了");
//                Session session = signCodeService.getSession(hash);
//                if(session == null)
//                    System.out.println("session为空");
//                JsonObject massage = new JsonObject();
//                massage.addProperty("status",200);
//                massage.addProperty("massage","success");
//                signCodeService.sendMessage(session,massage.toString());
//                System.out.println("消息发送");
//            }

        }else{
            response.put("status_code",400);
        }
        return response;
    }

    public boolean saveSign(String sign,String userId,String contractId){
        System.out.println("-------------------保存合同文件---------------------");
        String path= userContractService.getContractByContractId(contractId).getPath();
        String party = "";
        if(userContractService.getUserContract(contractId).getUserId1() == Long.parseLong(userId))
            party = "-AParty";
        else
            party = "-BParty";
        int end = path.lastIndexOf(".")+1;
        String newPath = path.substring(0,end);
        newPath = newPath+party+".txt";
        File file = new File(newPath);
        try {
            Writer writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(sign,0,sign.length());
            bufferedWriter.close();
            writer.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;

    }
}
