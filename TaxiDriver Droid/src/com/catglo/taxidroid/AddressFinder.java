package com.catglo.taxidroid;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.catglo.deliveryDatabase.WebServiceLookup;

public class AddressFinder extends WebServiceLookup {

	LocationManager locationManager;
	Location lastKnownLocation;
	String resultAddress;
	
	public interface OnAddressFound {
		void found(String result);
	};
	
	OnAddressFound commit;
	
	public void lookup(OnAddressFound commit) {
		this.commit = commit;
		super.lookup("");
	}
	
	public AddressFinder(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
	}
	
	@Override
	protected void handleJsonResponce(JSONObject jsonResponse, String addressSoFar) throws JSONException {
		JSONArray results = jsonResponse.getJSONArray("results");
		JSONObject a1=results.getJSONObject(0);
		resultAddress = a1.getString("formatted_address");
		commit.found(resultAddress);
	}

	@Override
	protected String getURL(String soFar) throws UnsupportedEncodingException {
		String URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+lastKnownLocation.getLatitude()+
		 			 ","+lastKnownLocation.getLongitude()+"&sensor=true";
		return URL;
	}	
}