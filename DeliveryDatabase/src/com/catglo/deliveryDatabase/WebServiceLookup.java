package com.catglo.deliveryDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.catglo.deliveryDatabase.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class WebServiceLookup  extends Object {
	Context context;
	private DefaultHttpClient httpclient;
	public WebServiceLookup(){
		httpclient = new DefaultHttpClient();	
	}
	
	
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		 BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		 StringBuilder sb = new StringBuilder();
		 
		 String line = null;
		 try {
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n");
			 }
		 } catch (IOException e) {
		    e.printStackTrace();
		 } finally {
			 try {
				 is.close();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 return sb.toString();
	}
	
	static long lastLookupTime;
	static final int JSON_LOOKUP_DELAY_TIME = 500;
	
	public void lookup(final String addressSoFar) {
		
		//Exit the function to prevent multiple requests from stacking up
		if (lastLookupTime > System.currentTimeMillis()-JSON_LOOKUP_DELAY_TIME){
			return; //TODO: Ideally I would keep the most recent request
		}
		lastLookupTime = System.currentTimeMillis();
		
		
		Thread thread = new Thread(new Runnable(){public void run() {
			synchronized (WebServiceLookup.this){
				if (useAlternateLocalLookup(addressSoFar)){
					alternateLocalLookup(addressSoFar);
				} 
				else try { 
					//Use Google Geocoding API to get the street name suggestions
					HttpGet httpget = new HttpGet(getURL(addressSoFar));
					HttpResponse response = httpclient.execute(httpget);
			         
					if(response.getStatusLine().getStatusCode() == 200){
			            HttpEntity entity = response.getEntity();
			            if (entity == null) 
			            	return;
			            InputStream instream = entity.getContent();
			            JSONObject jsonResponse = new JSONObject(convertStreamToString(instream));
			            
			            handleJsonResponce(jsonResponse,addressSoFar);
			        }
		
				}catch (IllegalArgumentException e){
		        	return; //We can't parse addressed that end in a space so just give up
		        } catch (ClientProtocolException e){
		        	e.printStackTrace();
		        } catch (IOException e){
		        	e.printStackTrace();
		        } catch (IllegalStateException e){
		        	e.printStackTrace();
		        } catch (JSONException e) {
		        	e.printStackTrace();
				}
			}
	    }});
		thread.start();
	}
	
	protected boolean useAlternateLocalLookup(String soFar) {return false;}
	protected void alternateLocalLookup(String soFar){};
	protected abstract void handleJsonResponce(JSONObject jsonResponse, String addressSoFar) throws JSONException;
	protected abstract String getURL(String soFar) throws UnsupportedEncodingException;
}
