/*
 * Copyright 2009 Inspire-Software.com
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.impl.LiqPayPaymentGatewayImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class LiqPayRequest {

    private static final Logger LOG = LoggerFactory.getLogger(LiqPayPaymentGatewayImpl.class);

    private static final int CONNECT_TIMEOUT = 60000;
    private static final int READ_TIMEOUT = 60000;

    public static String post(String url, HashMap<String, String> list, LiqPay lp) throws Exception {


        String urlParameters = "";

        for (Map.Entry<String, String> entry: list.entrySet())
            urlParameters += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";

        LOG.debug("LiqPay request: {}", urlParameters);

        URL obj = new URL(url);
        OutputStream wr;
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
            con.setConnectTimeout(CONNECT_TIMEOUT);
            con.setReadTimeout(READ_TIMEOUT);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setDoOutput(true);
            wr = con.getOutputStream();
            // Send post request
            wr.write(urlParameters.getBytes(StandardCharsets.UTF_8));
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
            con.setConnectTimeout(CONNECT_TIMEOUT);
            con.setReadTimeout(READ_TIMEOUT);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setDoOutput(true);
            wr = con.getOutputStream();
            // Send post request
            wr.write(urlParameters.getBytes(StandardCharsets.UTF_8));
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

        LOG.debug("LiqPay response: {}", response);

        return response.toString();
    }
}