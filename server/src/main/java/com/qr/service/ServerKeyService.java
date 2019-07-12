package com.qr.service;


import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class ServerKeyService {
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    //获得公钥字符串
    public String getPublicKey(){
//        return publicKey.toString();
    return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDhIL26DLsuxaHgxGeQNfAEMdKk" +
            "l3z1RQgVwLbzrqXFFgVn1/oGM4hQhvHhc8cP0FmY66aA8o7kDzmdKn6qN6TV2weS" +
            "MzuED9+XcR+80GCabHycn7bt4o+A61nnPvezO9rGuoKUz1B+Z4PXLMtp0A4TWRxV" +
            "GH5NkkN5e9jtcq9UwQIDAQAB";
    }

    public ServerKeyService() {
        String priKeyFile = "key/private_key.pem";
        String pubKeyFile = "key/rsa_public_key.pem";
        privateKey = getPriKey(priKeyFile);
        publicKey = getPubKey(pubKeyFile);
    }


    private PrivateKey getPriKey(String path){
        privateKey = null;
        try {
//            File file = ResourceUtils.getFile("classpath:"+path);
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String s = br.readLine();
//            StringBuffer pkStr = new StringBuffer();
//            s = br.readLine();
//            while (s.charAt(0) != '-') {
//                pkStr.append(s + "\r");
//                s = br.readLine();
//            }
                String pkStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOEgvboMuy7FoeDE" +
                        "Z5A18AQx0qSXfPVFCBXAtvOupcUWBWfX+gYziFCG8eFzxw/QWZjrpoDyjuQPOZ0q" +
                        "fqo3pNXbB5IzO4QP35dxH7zQYJpsfJyftu3ij4DrWec+97M72sa6gpTPUH5ng9cs" +
                        "y2nQDhNZHFUYfk2SQ3l72O1yr1TBAgMBAAECgYBRylVjvLBcw8yWHoUJra7vtzIy" +
                        "Ph9V9KiFTqipS7BKND/uhFb/3cUOjJhgMnIF2spSdnrdqkIjtSxXX1L5gJHPucZA" +
                        "3jrhsqE6F8dRgq+uAqV5KdA0O+WLVaJ1o/f3SBwDrvBB1e9L7i5wTEFFq42Z9//E" +
                        "NzEVVoNYjaVkhDn/gQJBAPxJozKuNyHtWhLnlSpEq8tfV9mH1wqkjIUyUjq6JqMr" +
                        "Xnkd5oivE/UyHa55nNR8TGqqCaJ57I4E5tXDITZ5LIkCQQDkcMsbhSbEEaCNnpsD" +
                        "9Zr0mMQEuR7Bcu4V+FM2BoIUkydecfuvytVIKxPYDv1r3nFD+BDwgkxVoMopCt9K" +
                        "nQh5AkBZy2PYwAVDgBVVMTP4TWTQB+letWimkxaoudZmrKbf4KnJdgj9kUMLPIEv" +
                        "/n0BbBROyqKPP9IgYkI+xyrlFo/xAkEAhyApJFg4vBXpMJw2+bqYNEMBAAI4rRk8" +
                        "uAYxwm1LGLyKtxUZWbzTOGMy08TaJqpnuVrNOlb4rFX1/x0NQ+drkQJBAJrVKk/e" +
                        "pJmOVu4gm7SM7/qMd9H/ZqWOxJb3kM0X3FN4EKYFuv9sRFu0mJoWUWugq1hELf89" +
                        "KefwCdzsIylDoP0=";
//            BASE64Decoder decoder = new BASE64Decoder();
            byte[] keyByte = Base64.decodeBase64(pkStr);
            System.out.println("密钥文件：");
            System.out.println(pkStr);

            //获取KeyFactory，指定RSA算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");


            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyByte));
            if (privateKey == null)
                System.out.println("私钥为空");
            return privateKey;
        }catch (Exception e){
            e.printStackTrace();
        }
        return privateKey;

    }

    private PublicKey getPubKey(String path){
        publicKey = null;
        try{
//            File file = ResourceUtils.getFile("classpath:"+path);
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String s = br.readLine();
//            StringBuffer pkStr = new StringBuffer();
//            s = br.readLine();
//            while (s.charAt(0) != '-') {
//                pkStr.append(s + "\r");
//                s = br.readLine();
//            }
//            BASE64Decoder decoder = new BASE64Decoder();
            String pkStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDhIL26DLsuxaHgxGeQNfAEMdKk" +
                    "l3z1RQgVwLbzrqXFFgVn1/oGM4hQhvHhc8cP0FmY66aA8o7kDzmdKn6qN6TV2weS" +
                    "MzuED9+XcR+80GCabHycn7bt4o+A61nnPvezO9rGuoKUz1B+Z4PXLMtp0A4TWRxV" +
                    "GH5NkkN5e9jtcq9UwQIDAQAB";
//            byte[] keyByte = Base64.decodeBase64(pkStr);
            //System.out.println("密钥文件：");
            //System.out.println(pkStr);
            byte[] keyByte = Base64.decodeBase64(pkStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyByte));
            if (publicKey == null)
                System.out.println("公钥为空");
        }catch(Exception e){
            e.printStackTrace();
        }
        return publicKey;

    }

    public String signatureMess(String str){
//        if(privateKey == null)
//            System.out.println("签名时密钥为空");
//        System.out.println("待签名数据："+str);
        String signMess = "";
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            //加载私钥
            signature.initSign(privateKey);
            //更新待签名的数据
            signature.update(str.getBytes());
            //进行签名
            byte[] signed = signature.sign();

            //将加密后的字节数组，转换成BASE64编码的字符串，作为最终的签名数据
//            BASE64Encoder encoder = new BASE64Encoder();
            signMess = Base64.encodeBase64String(signed);
        }catch (Exception e){
            e.printStackTrace();
        }
        return signMess;
    }

    public boolean verifySignture(String str,String sign){
        boolean result = false;
        try {
            //获取Signature实例，指定签名算法(与之前一致)
            Signature signature = Signature.getInstance("SHA1WithRSA");
            //加载公钥
            signature.initVerify(publicKey);
            //更新原数据
            signature.update(str.getBytes());
            //公钥验签（true-验签通过；false-验签失败）
//            BASE64Decoder decoder = new BASE64Decoder();
            result = signature.verify(Base64.decodeBase64(sign));
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String decodeEncData(String data){
//        Cipher cipher = null;
//        byte[] decryptedData = null;
//        try {
//            cipher = Cipher.getInstance("RSA/None/NoPadding");
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);
////        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] dataBytes = Base64.decodeBase64(data);
//        decryptedData = cipher.doFinal(dataBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // 解密后的内容
//        return new String(decryptedData);
        String ans = "";
        String priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOEgvboMuy7FoeDE" +
                "Z5A18AQx0qSXfPVFCBXAtvOupcUWBWfX+gYziFCG8eFzxw/QWZjrpoDyjuQPOZ0q" +
                "fqo3pNXbB5IzO4QP35dxH7zQYJpsfJyftu3ij4DrWec+97M72sa6gpTPUH5ng9cs" +
                "y2nQDhNZHFUYfk2SQ3l72O1yr1TBAgMBAAECgYBRylVjvLBcw8yWHoUJra7vtzIy" +
                "Ph9V9KiFTqipS7BKND/uhFb/3cUOjJhgMnIF2spSdnrdqkIjtSxXX1L5gJHPucZA" +
                "3jrhsqE6F8dRgq+uAqV5KdA0O+WLVaJ1o/f3SBwDrvBB1e9L7i5wTEFFq42Z9//E" +
                "NzEVVoNYjaVkhDn/gQJBAPxJozKuNyHtWhLnlSpEq8tfV9mH1wqkjIUyUjq6JqMr" +
                "Xnkd5oivE/UyHa55nNR8TGqqCaJ57I4E5tXDITZ5LIkCQQDkcMsbhSbEEaCNnpsD" +
                "9Zr0mMQEuR7Bcu4V+FM2BoIUkydecfuvytVIKxPYDv1r3nFD+BDwgkxVoMopCt9K" +
                "nQh5AkBZy2PYwAVDgBVVMTP4TWTQB+letWimkxaoudZmrKbf4KnJdgj9kUMLPIEv" +
                "/n0BbBROyqKPP9IgYkI+xyrlFo/xAkEAhyApJFg4vBXpMJw2+bqYNEMBAAI4rRk8" +
                "uAYxwm1LGLyKtxUZWbzTOGMy08TaJqpnuVrNOlb4rFX1/x0NQ+drkQJBAJrVKk/e" +
                "pJmOVu4gm7SM7/qMd9H/ZqWOxJb3kM0X3FN4EKYFuv9sRFu0mJoWUWugq1hELf89" +
                "KefwCdzsIylDoP0=";
        try {
            byte[] keyBytes = Base64.decodeBase64(priKey);
            RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] strBytes = Base64.decodeBase64(data);
            ans = new String(cipher.doFinal(strBytes));
        }catch (Exception e){
            e.printStackTrace();
        }
        return ans;
    }

    public String encodeData(String data){
        Cipher cipher = null;
        byte[] decryptedData = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        BASE64Decoder decoder = new BASE64Decoder();
            byte[] dataBytes = Base64.encodeBase64(cipher.doFinal(data.getBytes()));
            decryptedData = dataBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 解密后的内容
        return new String(decryptedData);
    }
}
