/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


/**
 * This code is distributed by LiqPay and is taken from https://github.com/liqpay/sdk-java
 */

package com.liqpay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class LiqPayRequest {

    public static String post(String url, HashMap<String, String> list, LiqPay lp) throws Exception {


        String urlParameters = "";

        for (Map.Entry<String, String> entry: list.entrySet())
            urlParameters += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";

        URL obj = new URL(url);
        DataOutputStream wr;
        BufferedReader in;

        if(url.indexOf( "https:" ) != -1){
            HttpsURLConnection con;
            if(lp.getProxy() == null){
                con = (HttpsURLConnection) obj.openConnection();
            }else{
                con = (HttpsURLConnection) obj.openConnection(lp.getProxy());
                if(lp.getProxyUser() != null)
                    con.setRequestProperty("Proxy-Authorization", "Basic " + lp.getProxyUser());
            }
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            // Send post request
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            in = new BufferedReader(  new InputStreamReader(con.getInputStream()));
        }else{
            HttpURLConnection con;
            if(lp.getProxy() == null){
                con = (HttpURLConnection) obj.openConnection();
            }else{
                con = (HttpURLConnection) obj.openConnection(lp.getProxy());
                if(lp.getProxyUser() != null)
                    con.setRequestProperty("Proxy-Authorization", "Basic " + lp.getProxyUser());
            }
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            // Send post request
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            in = new BufferedReader(  new InputStreamReader(con.getInputStream()));
        }



        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}