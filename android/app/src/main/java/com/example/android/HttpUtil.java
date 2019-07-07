package com.example.android;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class HttpUtil {

    private static HttpUtil mHttpUtil = new HttpUtil();
    private static String mBaseAddress = "http://47.95.214.69";
    private static String mServerPublicKey = "";

    public static HttpUtil getInstance() {
        return mHttpUtil;
    }

    public static void initServerPublicKey() {
//        RequestForKey requestForKey = new RequestForKey();
//        requestForKey.execute(mBaseAddress + "/app/key");

        Map<String, String> serverKeyPair = SecurityUtil.getRSAKeyPair();
        mServerPublicKey = serverKeyPair.get("public_key");
    }

    public static String getServerPublicKey() {
        if (mServerPublicKey.isEmpty())
            initServerPublicKey();
        return mServerPublicKey;
    }

    public void register(String userName, String cardId) {
        String serverPublicKey = getServerPublicKey();
        RequestToRegister requestToRegister = new RequestToRegister();
        JSONArray requests = new JSONArray();
        try {
            JSONObject address = new JSONObject();
            address.put("address", mBaseAddress + "/app/register");

            Map<String, String> userKeyPair = SecurityUtil.getRSAKeyPair();
            String userPublicKey = userKeyPair.get("public_key");
            String userPrivateKey = userKeyPair.get("private_key");

            String key = SecurityUtil.getDESKeyString();
            String encryptedKey = SecurityUtil.encryptStringByRSAPublicKeyString(key, serverPublicKey);

            String phoneId = "Xiaomi 9 SE";
            String time = "2019 07 06";

            JSONObject data = new JSONObject();
            data.put("user_name", userName);
            data.put("card_id", cardId);
            data.put("phone_id", phoneId);
            data.put("public_key", userPublicKey);
            data.put("time", time);
            data.put("signed_hash", SecurityUtil.hashBySHA1(userName + cardId + phoneId + userPublicKey + time));

            JSONObject body = new JSONObject();
            body.put("encrypted_key", encryptedKey);
            body.put("data", data);

            requests.put(address);
            requests.put(body);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        requestToRegister.execute(requests);
    }

    public void login(String userId) {
        String serverPublicKey = getServerPublicKey();

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
                }
                else
                    return "GET the URL successfully but the public_key_of_server is wrong!";
            } catch (MalformedURLException malformedURLExeption) {
                malformedURLExeption.printStackTrace();
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            HttpUtil.mServerPublicKey = result;

            System.out.println("Public Key of Server:");
            System.out.println(HttpUtil.mServerPublicKey);
        }
    }

    private class RequestToRegister extends AsyncTask<JSONArray, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONArray... requests) {
            try {
                JSONObject address = requests[0].getJSONObject(0);
                JSONObject body = requests[0].getJSONObject(1);

                URL url = new URL((String) address.get("address"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);

//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
//                outputStreamWriter.write(body.toString());
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(body.toString().getBytes());

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
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
        }
    }
}
