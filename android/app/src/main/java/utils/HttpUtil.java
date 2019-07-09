package utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.android.ConfirmActivity;
import com.example.android.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HttpUtil {

    public static final int MESSAGE_REGISTER_SUCCESS_RESPONSE = 0x00005000;
    public static final int MESSAGE_REGISTER_FAIL_RESPONSE = 0x00005001;
    public static final int MESSAGE_LOGIN_SUCCESS_RESPONSE = 0x00005002;
    public static final int MESSAGE_LOGIN_FAIL_RESPONSE = 0x00005003;

    private static HttpUtil mHttpUtil = new HttpUtil();
//    private static String mBaseAddress = "http://121.250.213.89:8080";
    private static String mBaseAddress = "http://47.95.214.69:1002";
    private static String mServerPublicKey = "";
    private String token = "";
//    private static String mServerPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOEgvboMuy7FoeDEZ5A18AQx0qSXfPVFCBXAtvOupcUWBWfX+gYziFCG8eFzxw/QWZjrpoDyjuQPOZ0qfqo3pNXbB5IzO4QP35dxH7zQYJpsfJyftu3ij4DrWec+97M72sa6gpTPUH5ng9csy2nQDhNZHFUYfk2SQ3l72O1yr1TBAgMBAAECgYBRylVjvLBcw8yWHoUJra7vtzIyPh9V9KiFTqipS7BKND/uhFb/3cUOjJhgMnIF2spSdnrdqkIjtSxXX1L5gJHPucZA3jrhsqE6F8dRgq+uAqV5KdA0O+WLVaJ1o/f3SBwDrvBB1e9L7i5wTEFFq42Z9//ENzEVVoNYjaVkhDn/gQJBAPxJozKuNyHtWhLnlSpEq8tfV9mH1wqkjIUyUjq6JqMrXnkd5oivE/UyHa55nNR8TGqqCaJ57I4E5tXDITZ5LIkCQQDkcMsbhSbEEaCNnpsD9Zr0mMQEuR7Bcu4V+FM2BoIUkydecfuvytVIKxPYDv1r3nFD+BDwgkxVoMopCt9KnQh5AkBZy2PYwAVDgBVVMTP4TWTQB+letWimkxaoudZmrKbf4KnJdgj9kUMLPIEv/n0BbBROyqKPP9IgYkI+xyrlFo/xAkEAhyApJFg4vBXpMJw2+bqYNEMBAAI4rRk8uAYxwm1LGLyKtxUZWbzTOGMy08TaJqpnuVrNOlb4rFX1/x0NQ+drkQJBAJrVKk/epJmOVu4gm7SM7/qMd9H/ZqWOxJb3kM0X3FN4EKYFuv9sRFu0mJoWUWugq1hELf89KefwCdzsIylDoP0=";

    public static HttpUtil getInstance() {
        return mHttpUtil;
    }

    public static void initServerPublicKey() {
        RequestForKey requestForKey = new RequestForKey();
        Log.v(TAG, "准备向服务器获取公钥");
        requestForKey.execute(mBaseAddress + "/app/key");

//        Map<String, String> serverKeyPair = SecurityUtil.getRSAKeyPair();
//        mServerPublicKey = serverKeyPair.get("public_key");
    }

    public static String getServerPublicKey() {
        if (mServerPublicKey.isEmpty())
            initServerPublicKey();
        return mServerPublicKey;
    }



    public void confirm(String qrCode,String userid, String phoneId, Handler handler) {
        String serverPublicKey = getServerPublicKey();
        String userPrivateKey = "";
        RequestToConfirm requestToConfirm = new RequestToConfirm(handler);
        JSONArray requests = new JSONArray();
        try {
            String key = SecurityUtil.getDESKeyString();
            String encryptedKey = SecurityUtil.encryptStringByRSAPublicKeyString(key, serverPublicKey);

            JSONObject data= new JSONObject();
            Date date = new Date();
            String time = String.valueOf(date.getTime());
            String signedHash = SecurityUtil.signStringByRSAPrivateKeyString(userid+ phoneId+ qrCode+ time, userPrivateKey);

            data.put("user_id",userid);
            data.put("phone_id",phoneId);
            data.put("random_str",qrCode);
            data.put("time",time);
            data.put("signed_hash",signedHash);

            String desData = SecurityUtil.encryptStringByDESKeyString(data.toString(), key);

            JSONObject body= new JSONObject();
            body.put("encrypted_key",encryptedKey);
            body.put("data",desData);

            JSONObject addr= new JSONObject();
            addr.put("address",mBaseAddress + "/app/help_login");

            requests.put(addr);
            requests.put(body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestToConfirm.execute(requests);
    }

    public void register(String userName, String cardId, Handler handler) {
        String serverPublicKey = getServerPublicKey();
        RequestToRegister requestToRegister = new RequestToRegister(handler);
        JSONArray requests = new JSONArray();
        try {
            JSONObject address = new JSONObject();
            address.put("address", mBaseAddress + "/app/register");

            Map<String, String> userKeyPair = SecurityUtil.getRSAKeyPair();
            String userPublicKey = userKeyPair.get("public_key");
            String userPrivateKey = userKeyPair.get("private_key");

            String phoneId = "Xiaomi 9 SE";

            String key = SecurityUtil.getDESKeyString();
            Log.v(TAG, "随机生成的对称密钥是：" + key);
            String encryptedKey = SecurityUtil.encryptStringByRSAPublicKeyString(key, serverPublicKey);
            Log.v(TAG, "已成功使用公钥加密对称钥");

            Date date = new Date();
            String time = String.valueOf(date.getTime());
            String signedHash = SecurityUtil.signStringByRSAPrivateKeyString(userName + cardId + phoneId + userPublicKey + time, userPrivateKey);

            JSONObject data = new JSONObject();

            data.put("user_name", userName);
            data.put("card_id", cardId);
            data.put("phone_id", phoneId);
            data.put("public_key", userPublicKey);
            data.put("time", time);
            data.put("signed_hash", signedHash);
            String desData = SecurityUtil.encryptStringByDESKeyString(data.toString(), key);

            JSONObject body = new JSONObject();
            body.put("encrypted_key", encryptedKey);
            body.put("data", desData);

            JSONObject info = new JSONObject();
            info.put("user_name", userName);
            info.put("private_key", userPrivateKey);

            requests.put(address);
            requests.put(body);
            requests.put(info);

            Log.v(TAG, "已构造好注册请求");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        requestToRegister.execute(requests);
    }

    public void login(String userId,String pin,Handler handler) {
        String serverPublicKey = getServerPublicKey();
        System.out.println("登录时的服务器公钥:"+serverPublicKey);
        RequestToLogin requestToLogin = new RequestToLogin(handler);
        JSONArray requests = new JSONArray();

        try {
            JSONObject address = new JSONObject();
            address.put("address", mBaseAddress + "/app/normal_login");

            Map<String, String> userKeyPair = SecurityUtil.getRSAKeyPair();
            String userPublicKey = userKeyPair.get("public_key");
            String userPrivateKey = userKeyPair.get("private_key");

            String phoneId = "Xiaomi 9 SE";

            String key = SecurityUtil.getDESKeyString();
            String encryptedKey = SecurityUtil.encryptStringByRSAPublicKeyString(key, serverPublicKey);

            Log.v(TAG, "已成功使用公钥加密对称钥");

            String signedHash = SecurityUtil.signStringByRSAPrivateKeyString(userId+phoneId, userPrivateKey);

            System.out.println("用户私钥:"+userPrivateKey);


            JSONObject data = new JSONObject();
            data.put("user_id", userId);
            data.put("phone_id",phoneId);
            data.put("signed_hash", signedHash);
            String desData = SecurityUtil.encryptStringByDESKeyString(data.toString(), key);

            JSONObject body = new JSONObject();
            body.put("encrypted_key", encryptedKey);
            body.put("data", desData);

            JSONObject info = new JSONObject();
            info.put("private_key", userPrivateKey);

            requests.put(address);
            requests.put(body);
            requests.put(info);

            Log.v(TAG, "已构造好口令登录请求");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        requestToLogin.execute(requests);

    }

    public void getQrCode(Handler handler) {
        RequestForQrcode requestForQrcode = new RequestForQrcode(handler);
        requestForQrcode.execute(mBaseAddress + "/app/qrCode");
    }



    private static String getStringFromInputStream(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String temp;
            while ((temp = bufferedReader.readLine()) != null)
                stringBuilder.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private static JSONObject getJSONObjectFromInputStream(InputStream inputStream) {
        String string = getStringFromInputStream(inputStream);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(string);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    private static class RequestForKey extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    return getJSONObjectFromInputStream(inputStream).get("public_key_of_server").toString();
                } else
                    return "GET the URL successfully but the public_key_of_server is wrong!";
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
                return "URL is wrong!";
            } catch (ProtocolException protocolException) {
                protocolException.printStackTrace();
                return "Property setting is wrong!";
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return "Input is wrong!";
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                return "Public key of server is wrong!";
            }
        }
    }

    private void setToken(String token) {
        this.token = token;
    }

    private static class RequestForQrcode extends AsyncTask<String, Integer, String> {

        private Handler mHander;

        public RequestForQrcode(Handler handler) {
            this.mHander = handler;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String qrCode;
                    InputStream inputStream = connection.getInputStream();
                    qrCode = getJSONObjectFromInputStream(inputStream).get("qr_code").toString();

                    Message message = mHander.obtainMessage();
                    message.what = LoginActivity.GET_QR_CODE_SUCCESS;
                    message.obj = qrCode;

                    return qrCode;
                }
                else
                    return "GET the URL successfully but the qr_code is wrong!";
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
                return "URL is wrong!";
            } catch (ProtocolException protocolException) {
                protocolException.printStackTrace();
                return "Property setting is wrong!";
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return "Input is wrong!";
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                return "Public qr_code of server is wrong!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            HttpUtil.mServerPublicKey = result;
            Log.v(TAG, "服务器公钥是：" + HttpUtil.mServerPublicKey);
        }
    }

    private class RequestToLogin extends AsyncTask<JSONArray,Integer,JSONObject> {

        private Handler mhandler;

        public RequestToLogin(Handler handler) {
            mhandler = handler;
        }

        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(JSONArray... requests) {
            try {

                JSONObject address = requests[0].getJSONObject(0);
                JSONObject body = requests[0].getJSONObject(1);
                JSONObject info = requests[0].getJSONObject(2);

                String userPrivateKey = (String) info.get("private_key");

                URL url = new URL((String) address.get("address"));

                System.out.println(url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-type", "application/x-java-serialized-object");

                PrintStream printStream = new PrintStream(connection.getOutputStream());
                printStream.print(body.toString());
                System.out.println("body:\n"+body.toString());
                printStream.flush();
                printStream.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.v(TAG, "响应码为200");
                    InputStream inputStream = connection.getInputStream();
                    Message message = mhandler.obtainMessage();
                    Log.v(TAG, "已获取响应的输入流");
                    JSONObject response = getJSONObjectFromInputStream(inputStream);
                    String encryptedKey = (String) response.get("encrypted_key");
                    String key = SecurityUtil.decryptStringByRSAPrivateKeyString(encryptedKey, userPrivateKey);

                    Log.v(TAG, encryptedKey);
                    Log.v(TAG, key);

                    int statusCode = (int) response.get("status_code");

                    JSONObject mesObj = new JSONObject();
                    switch (statusCode) {
                        case 200:
                            message.what = HttpUtil.MESSAGE_REGISTER_FAIL_RESPONSE;

                            String desData = (String) response.get("data");
                            JSONObject data = new JSONObject(SecurityUtil.decryptStringByDESKeyString(desData, key));
                            String token = (String) data.get("token");
                            String signedHash = (String) data.get("signed_hash");

                            if (SecurityUtil.verifyStringByRSAPublicKeyString(token, signedHash, mServerPublicKey)) {
                                Log.v(TAG, "服务器的响应验签通过");
                                mesObj.put("token", token);
                                mesObj.put("error", null);
                                Log.v(TAG, "获取到的token：" + token);
                                getInstance().setToken(token);
                            } else {
                                mesObj.put("error", "Got malicious message!");
                            }
                            message.obj = mesObj;
                            break;
                        case 400:
                            message.what = HttpUtil.MESSAGE_REGISTER_FAIL_RESPONSE;

                            mesObj.put("error", "Server doesn't distribute any ID!");
                            message.obj = mesObj;
                            break;
                        default:
                            break;
                    }
                    message.sendToTarget();
                }
                else {
                    System.out.println("POST to register failed!");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    private class RequestToRegister extends AsyncTask<JSONArray, Integer, JSONObject> {

        private Handler mHandler;

        public RequestToRegister(Handler handler) {
            mHandler = handler;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONArray... requests) {
            try {
                JSONObject address = requests[0].getJSONObject(0);
                JSONObject body = requests[0].getJSONObject(1);
                JSONObject info = requests[0].getJSONObject(2);

                String userName = (String) info.get("user_name");
                String userPrivateKey = (String) info.get("private_key");

                URL url = new URL((String) address.get("address"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-type", "application/x-java-serialized-object");

                PrintStream printStream = new PrintStream(connection.getOutputStream());
                printStream.print(body.toString());
                printStream.flush();
                printStream.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.v(TAG, "响应码为200");
                    InputStream inputStream = connection.getInputStream();
                    Message message = mHandler.obtainMessage();
                    Log.v(TAG, "已获取响应的输入流");
                    JSONObject response = getJSONObjectFromInputStream(inputStream);
                    String encryptedKey = (String) response.get("encrypted_key");
                    String key = SecurityUtil.decryptStringByRSAPrivateKeyString(encryptedKey, userPrivateKey);

                    Log.v(TAG, encryptedKey);
                    Log.v(TAG, key);

                    int statusCode = (int) response.get("status_code");

                    JSONObject mesObj = new JSONObject();
                    switch (statusCode) {
                        case 200:
                            message.what = HttpUtil.MESSAGE_REGISTER_FAIL_RESPONSE;

                            String desData = (String) response.get("data");
                            JSONObject data = new JSONObject(SecurityUtil.decryptStringByDESKeyString(desData, key));
                            String userId = (String) data.get("user_id");
                            String signedHash = (String) data.get("signed_hash");

                            if (SecurityUtil.verifyStringByRSAPublicKeyString(userId, signedHash, mServerPublicKey)) {
                                Log.v(TAG, "服务器的响应验签通过");
                                mesObj.put("user_name", userName);
                                mesObj.put("user_id", userId);
                                mesObj.put("error", null);
                                Log.v(TAG, "获取到的用户ID是：" + userId);
                            } else {
                                mesObj.put("error", "Got malicious message!");
                            }
                            message.obj = mesObj;
                            break;
                        case 400:
                            message.what = HttpUtil.MESSAGE_REGISTER_FAIL_RESPONSE;

                            mesObj.put("error", "Server doesn't distribute any ID!");
                            message.obj = mesObj;
                            break;
                        default:
                            break;
                    }
                    message.sendToTarget();
                }
                else {
                    System.out.println("POST to register failed!");
                }
            } catch (MalformedURLException malformedUrlException) {
                malformedUrlException.printStackTrace();
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
            JSONObject response = new JSONObject();
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            Log.v(TAG, "申请注册已结束");
        }
    }

    private class RequestToConfirm extends AsyncTask<JSONArray,Integer,String> {

        private Handler mHandler;

        public RequestToConfirm(Handler handler) {
            this.mHandler= handler;
        }


        @Override
        protected String doInBackground(JSONArray... jsonArrays) {
            try {
                JSONObject addr = jsonArrays[0].getJSONObject(0);
                JSONObject body = jsonArrays[0].getJSONObject(1);
                String address = (String) addr.get("address");

                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-type", "application/x-java-serialized-object");

                PrintStream printStream = new PrintStream(connection.getOutputStream());
                printStream.print(body.toString());
                printStream.flush();
                printStream.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.v(TAG, "响应码为200");
                    InputStream inputStream = connection.getInputStream();
                    Message message = mHandler.obtainMessage();
                    Log.v(TAG, "已获取响应的输入流");
                    JSONObject response = getJSONObjectFromInputStream(inputStream);

                    int statusCode = (int) response.get("status_code");

                    JSONObject mesObj = new JSONObject();
                    switch (statusCode) {
                        case 200:
                            message.what = ConfirmActivity.CONFIRM_SUCCESS;
                            break;
                        case 400:
                            message.what = ConfirmActivity.CONFIRM_FAIL;
                            break;
                        default:
                            break;
                    }
                    message.sendToTarget();
                }
                else {
                    System.out.println("POST to confirm failed!");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
