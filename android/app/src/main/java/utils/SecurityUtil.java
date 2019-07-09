package utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import android.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class SecurityUtil {

    public static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes,Base64.NO_WRAP);
    }

    public static byte[] base64Decode(String str) {
        return Base64.decode(str, Base64.NO_WRAP);
    }

    public static String getDESKeyString() {
        String keyString = "";
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            keyString = base64Encode(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }
        return keyString;
    }

    public static String encryptStringByDESKeyString(String str, String keyString) {
        String ans = "";
        try {
            DESKeySpec desKeySpec = new DESKeySpec(base64Decode(keyString));
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] strBytes = str.getBytes();
            ans = base64Encode(cipher.doFinal(strBytes));
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (InvalidKeySpecException invalidKeySpecException) {
            invalidKeySpecException.printStackTrace();
        } catch (NoSuchPaddingException noSuchPaddingException) {
            noSuchPaddingException.printStackTrace();
        } catch (BadPaddingException badPaddingException) {
            badPaddingException.printStackTrace();
        } catch (IllegalBlockSizeException illegalBlockSizeException) {
            illegalBlockSizeException.printStackTrace();
        }
        return ans;
    }

    public static String decryptStringByDESKeyString(String str, String keyString) {
        String ans = "";
        try {
            DESKeySpec desKeySpec = new DESKeySpec(base64Decode(keyString));
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] strBytes = base64Decode(str);
            ans = new String(cipher.doFinal(strBytes));
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (InvalidKeySpecException invalidKeySpecException) {
            invalidKeySpecException.printStackTrace();
        } catch (NoSuchPaddingException noSuchPaddingException) {
            noSuchPaddingException.printStackTrace();
        } catch (BadPaddingException badPaddingException) {
            badPaddingException.printStackTrace();
        } catch (IllegalBlockSizeException illegalBlockSizeException) {
            illegalBlockSizeException.printStackTrace();
        }
        return ans;
    }

    public static Map<String, String> getRSAKeyPair() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//            map.put("public_key", base64Encode(publicKey.getEncoded()));
//            map.put("private_key", base64Encode(privateKey.getEncoded()));

            String temp_pubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0VFArVu0yFuns+jHeHQQ49M+bclVFoHjhBJlMNNQXSyDEA2Gj2BWId1sz+3Sm/kLqFSI45oW6IMf6FRYdEYEz2nQZUWIXRZKwccwcOkDT7X2s7ewYaVnMo5hOBZe/Dr7I7wrfktjwBb4euSpCpSoSF1ReGEMJ3uWAolseRYF+uwIDAQAB";
            String temp_privkey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALRUUCtW7TIW6ez6Md4dBDj0z5tyVUWgeOEEmUw01BdLIMQDYaPYFYh3WzP7dKb+QuoVIjjmhbogx/oVFh0RgTPadBlRYhdFkrBxzBw6QNPtfazt7BhpWcyjmE4Fl78OvsjvCt+S2PAFvh65KkKlKhIXVF4YQwne5YCiWx5FgX67AgMBAAECgYADA0S+MTHQBVogKcyB6rMwSdh/iEke21Ii1EfEz/TaUDQV3bRPjRqVR7wSRnluYjz3PqYlOMsF4Wp4LxPcbpvvwZeJW5TxkgWjuGH2ju7aTRmWJ0reQtxLVfEDZMnKTsPTT6evwIql/YVoly+bJYMC9DTwnoKx91qpb2JTsya6iQJBANn02BZjLFJj8HukH4AVpoGaDpQwNJEGkA5Ry8PaKKJtvKjYV7sWw5l0k+axi8gdujYt1mD69ZGalGXCv0KlXfkCQQDTziR2IMz2zr/juemDPsdtN9ALtGmUBjxHCT2DrkSC9u6AhAAD9EisfY302FRnaEDh0569mrOUGw8Hj3NB0f9TAkASkvQLTF5Nkyi+UKlCkBXFe6x8YnNIXKfQIJZd1WybEwD93pnzXqhCnpWwFjdUUXw5+8QGNbzRsLuuxF7qPg95AkEAuJl/H9HAsg9KIVFkmzX5HFC7q6fnKNMyb/s2uPEG11oeTrY9STT8rhGTyuVM0v+DJQ+K19fgUeIvfDhjjOdY7wJAU3qlqDrA4kUooOT6dxfyhUi4oWg6xNq1cBs01UgjNPGk6BxwQA9G+32rNA/Gl4KZVNo3/o/0757u+CSOygE8Hg==";

            map.put("public_key", temp_pubkey);
            map.put("private_key", temp_privkey);

            System.out.println("pub:\n"+map.get("public_key"));
            System.out.println("pri:\n"+map.get("private_key"));

        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }
        return map;
    }

    public static String encryptStringByRSAPublicKeyString(String str, String keyString) {
        String ans = "";
        try {
            byte[] keyBytes = base64Decode(keyString);
            RSAPublicKey key = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] strBytes = str.getBytes("UTF-8");
            ans = base64Encode(cipher.doFinal(strBytes));
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (InvalidKeySpecException invalidKeySpecException) {
            invalidKeySpecException.printStackTrace();
        } catch (NoSuchPaddingException noSuchPaddingException) {
            noSuchPaddingException.printStackTrace();
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        } catch (BadPaddingException badPaddingException) {
            badPaddingException.printStackTrace();
        } catch (IllegalBlockSizeException illegalBlockSizeException) {
            illegalBlockSizeException.printStackTrace();
        }
        return ans;
    }

    public static String decryptStringByRSAPrivateKeyString(String str, String keyString) {
        String ans = "";
        try {
            byte[] keyBytes = base64Decode(keyString);
            RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] strBytes = base64Decode(str);
            ans = new String(cipher.doFinal(strBytes));
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (InvalidKeySpecException invalidKeySpecException) {
            invalidKeySpecException.printStackTrace();
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
        } catch (IllegalBlockSizeException illegalBlockSizeException) {
            illegalBlockSizeException.printStackTrace();
        } catch (BadPaddingException badPaddingException) {
            badPaddingException.printStackTrace();
        } catch (NoSuchPaddingException noSuchPaddingException) {
            noSuchPaddingException.printStackTrace();
        }
        return ans;
    }

    public static String signStringByRSAPrivateKeyString(String str, String keyString) {
        String ans = "";
        try {
            byte[] keyBytes = base64Decode(keyString);
            RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(key);
            signature.update(str.getBytes());
            ans = base64Encode(signature.sign());
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (InvalidKeySpecException invalidKeySpecException) {
            invalidKeySpecException.printStackTrace();
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
        } catch (SignatureException signatureException) {
            signatureException.printStackTrace();
        }
        return ans;
    }

    public static boolean verifyStringByRSAPublicKeyString(String str, String signedHash, String keyString) {
        boolean ans = false;
        try {
            byte[] keyBytes = base64Decode(keyString);
            RSAPublicKey key = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(key);
            signature.update(str.getBytes());
            ans = signature.verify(base64Decode(signedHash));
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (InvalidKeySpecException invalidKeySpecException) {
            invalidKeySpecException.printStackTrace();
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
        } catch (SignatureException signatureException) {
            signatureException.printStackTrace();
        }
        return ans;
    }
}
