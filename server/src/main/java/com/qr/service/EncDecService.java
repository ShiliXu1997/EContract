package com.qr.service;

import org.springframework.stereotype.Service;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

@Service
public class EncDecService {

    public static String createSymKey(){
        try {
            System.out.println("产生对称密钥");
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            SecureRandom rand = new SecureRandom();
            System.out.println("------------------随机："+rand.toString());
            kg.init(new SecureRandom());
            SecretKey k = kg.generateKey();
//            BASE64Encoder encoder = new BASE64Encoder();
            String key = Base64.encodeBase64String(k.getEncoded());
            System.out.println("密钥："+key);
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("产生密钥失败");
            return null;
        }

    }

    //解密DES对称加密数据
    public String decDES(String data,String key){
        System.out.println("解密DES加密数据");
        try {
//            BASE64Decoder decoder = new BASE64Decoder();
//            BASE64Encoder encoder = new BASE64Encoder();
            SecureRandom random = new SecureRandom();
            DESKeySpec desKeySpec = new DESKeySpec(Base64.decodeBase64(key));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] databyte = cipher.doFinal(Base64.decodeBase64(data));
            return new String(databyte);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String encDES(String data,String key){
//        BASE64Decoder decoder = new BASE64Decoder();
//        BASE64Encoder encoder = new BASE64Encoder();
        SecureRandom random = new SecureRandom();
        DESKeySpec desKeySpec = null;
        try {
            desKeySpec = new DESKeySpec(Base64.decodeBase64(key));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] databyte = cipher.doFinal(data.getBytes());
            return Base64.encodeBase64String(databyte);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //客户端签名验签
    public boolean verifySignture(String data,String sign,String pkStr){
        try {
//            BASE64Decoder decoder = new BASE64Decoder();
            System.out.println("客户端签名验签：");
            System.out.println("公钥："+pkStr);
            byte[] keyByte = Base64.decodeBase64(pkStr);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyByte));

            Signature signature = Signature.getInstance("SHA1WithRSA");
            //加载公钥
            signature.initVerify(publicKey);
            //更新原数据
            signature.update(data.getBytes());
            //公钥验签（true-验签通过；false-验签失败）
            boolean result = signature.verify(Base64.decodeBase64(sign));
            if(result)
                System.out.println("验签成功");
            else
                System.out.println("验签失败");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String encRSA(String data,String pubKey){
        String res =  "";
        try {
            byte[] keyBytes = Base64.decodeBase64(pubKey);
            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            res = Base64.encodeBase64String(cipher.doFinal(data.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }




}
