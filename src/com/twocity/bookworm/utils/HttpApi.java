package com.twocity.bookworm.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpApi {
    
    private static HttpClient customHttpClient;

    private static synchronized HttpClient getHttpClient() {
        if (customHttpClient == null) {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            
            ConnManagerParams.setTimeout(params, 1000);

            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", 
                            PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", 
                            SSLSocketFactory.getSocketFactory(), 443));
            ClientConnectionManager conMgr = new 
                            ThreadSafeClientConnManager(params,schReg);
            
            customHttpClient = new DefaultHttpClient(conMgr, params);
        }
        return customHttpClient;
    }
	
	public static InputStream getStream(String url) throws Exception {
	    HttpClient client = getHttpClient();
	    HttpGet request = new HttpGet(url);
	    InputStream inputstream = null;
	    try{
	        HttpResponse response = client.execute(request);
	        StatusLine status = response.getStatusLine();
	        if(status.getStatusCode() != HttpStatus.SC_OK){
	            throw new Exception("Invalid response from server: " + status.toString());
	        }
	        HttpEntity entity = response.getEntity();
	        if(entity != null){
	            inputstream = entity.getContent();
	        }
	    }catch(Exception e){
	        throw new Exception("Problem communicating with API", e);
	    }
	    return inputstream;
	}
	
	public static String getStrings(String url) throws Exception {
	    InputStream inputstream = null;
	    inputstream = getStream(url);
	    if(inputstream == null){
	        throw new Exception("inputstream is null");
	    }
	    try {
	        ByteArrayOutputStream content = new ByteArrayOutputStream();
	        // Read response into a buffered stream
	        int readBytes = 0;
	        byte[] sBuffer = new byte[12048];
	        while ((readBytes = inputstream.read(sBuffer)) != -1) {
	            content.write(sBuffer, 0, readBytes);
	        }
	        return new String(content.toByteArray());
	     } catch (IOException e) {
	         Log.e("Http-api", e.getMessage());
	         throw new Exception("Problem communicating with API", e);
	     }
	}

}
