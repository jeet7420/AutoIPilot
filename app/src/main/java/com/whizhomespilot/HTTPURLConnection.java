package com.whizhomespilot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by smarhas on 12/16/2017.
 */

public class HTTPURLConnection {

    JSONObject json;
    String input;
    String url1=" http://www.whizindia.com/rest";

    public JSONObject invokeService(String path, HashMap<String, String> params) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            input = getPostDataString(params);

            System.out.println("HTTP Data : " + input);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() == 200) {
                System.out.println("success");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("Output from Server .... \n");
            String jsonText = readAll(br);
            json = new JSONObject(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject invokeServiceForMap(String path, HashMap<String, HashMap<String,String>> params) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            input = getPostDataStringForMap(params);

            System.out.println("HTTP Data : " + input);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() == 200) {
                System.out.println("success");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("Output from Server .... \n");
            String jsonText = readAll(br);
            json = new JSONObject(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject getMetrics(String path, HashMap<String, String> params) {
        JSONArray al=null;
        int response=0;
        URL url=null;
        JSONObject json = null;
        try {

            url= new URL(path);

            // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("www-proxy.idc.oracle.com", 8080));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            System.out.println("Serial : 5");
            //String input = "{\"startLat\":\"startLat\",\"startLng\":\"startLng\",\"endLat\":\"endLat\",\"endLng\":\"endLng\"}";

            input = getPostDataString(params);

            System.out.println("HTTP Data : " + input);

            //  ServerLog.logHashMap.put(String.valueOf(ServerLog.key), "source and destination as posted to server -> " + input);ServerLog.key++;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if(conn.getResponseCode()==200)
            {
                System.out.println("success");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            String jsonText = readAll(br);
            JSONObject js1=null;
            json = new JSONObject(jsonText);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            // return "error";
        }
        return json;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        JSONObject dataAsJson = new JSONObject(params);
        return dataAsJson.toString();
    }

    private String getPostDataStringForMap(HashMap<String, HashMap<String,String>> params) throws UnsupportedEncodingException {
        JSONObject dataAsJson = new JSONObject(params);
        return dataAsJson.toString();
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
