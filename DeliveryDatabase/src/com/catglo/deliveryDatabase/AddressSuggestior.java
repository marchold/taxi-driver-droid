package com.catglo.deliveryDatabase;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.maps.GeoPoint;
import com.catglo.deliveryDatabase.AddressSuggestiorGoogle.AddressSuggestionCommitor;


public class AddressSuggestior extends AddressSuggestiorGoogle {
	private String bounds;
	private Runnable commitLookup;
	private DataBase dataBase;
	float range;
	private LocationManager locationManager;
	private String bestProvider;
	private Location location;
	private double lat;
	private double lng;
	private StreetList streetList;
	private Runnable commitLookupReal;
	public ArrayList<AddressInfo> addressList;
		
	public AddressSuggestior(Context context, final Runnable commit, DataBase dataBase){
		super(context,null);
		this.dataBase = dataBase;
        streetList = StreetList.LoadState(context);	
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("generateDevLog", false)==true){
	        FileWriter f;
	        try {
				f = new FileWriter(Environment.getExternalStorageDirectory()+"/dr_log"+".txt",true);
				if (streetList!=null && streetList.addressList!=null){
					f.write("loaded streetList address with length of "+streetList.addressList.length+"\n");
				} else {
					if (streetList==null){
						f.write("Skipping streetList is null\n");
					} else {
						f.write("Skipping address list is null\n");
					}
				}
		        f.flush();
		        f.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        this.commitLookup = commit;
	}

	void init(){
		commitor = new AddressSuggestionCommitor(){public void commit(ArrayList<AddressInfo> addresses,  String originalSearchString) {
			Pattern streetNameAfterNumber = Pattern.compile("^([0-9]+\\s{0,2})(\\w+)");
			Matcher m = streetNameAfterNumber.matcher(originalSearchString);
			ArrayList<AddressInfo> list = null;
			if (m.find()){//If the address starts with a number and has street letters
				String numberPart = m.group(1);
				String streetPart = m.group(2);
					
				//Filter out addresses that do not have the same prefix
				list = new ArrayList<AddressInfo>();
	        	for (AddressInfo addressInfo : addresses){
	        		String address = addressInfo.address;
	        		if (address.toLowerCase().startsWith(originalSearchString.toLowerCase())){
	        			list.add(addressInfo);
	        		}
	        	}
	        	//If the google found at least 1 match with the same prefix, that is probably it, just use google
	        	//The google result did not find a good match, filter the local street list
	        	if (list.size()==0){
	        		int size = StreetList.parentList.size();
	    			if (size > 0) {
    					for (int i = 0; i < size; i++) {
        					//In this case the originalSearchString has a space so we need to parse out the street name 
        					//and filter by the letters
        					String s = new String(StreetList.parentList.get(i).name).toLowerCase();
        					
        					if (s.startsWith(streetPart.toLowerCase())){
        						s = s.substring(0, 1).toUpperCase() + s.substring(1);
        						AddressInfo addressInfo = new AddressInfo();
        						addressInfo.address = numberPart+s.replace('+', ' ');
        						list.add(addressInfo);
        					}
        				}					
	    			}
	        	}
			}
			else { //We never get here if its just a number so this is for text first search the notes and append to the google list
				list=new ArrayList<AddressInfo>();
				for (AddressInfo address : addresses){
					list.add(address);
				}
				ArrayList<String> resultsFromDB = new ArrayList<String>();
				dataBase.searchAddressSuggestionsFor(originalSearchString,resultsFromDB);	
				for (String address : resultsFromDB){
					AddressInfo ai = new AddressInfo();
					ai.address = address;
					list.add(ai);
				}
			}
		
			AddressSuggestior.this.addressList = list;
			if (commitLookup != null) {
				commitLookup.run();
			}
		}};
	}
	
	@Override
	public void lookup(final String addressSoFar) {
		if (commitor == null){
			init();
		}
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if (sharedPreferences.getBoolean("generateDevLog", false)==true){
			FileWriter f;
	        try {
				f = new FileWriter(Environment.getExternalStorageDirectory()+"/dr_log"+".txt",true);
				f.write("\nGoogle Lookup "+addressSoFar+"\n");
		        f.flush();
		        f.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
		super.lookup(addressSoFar);
	}
	

	protected boolean useAlternateLocalLookup(String addressSoFar) {
		Pattern streetNumberOnly = Pattern.compile("^[0-9]+\\s{0,2}$");
		if (streetNumberOnly.matcher(addressSoFar).find()){
			return true;
		} else {
			return false;
		}
	}
	protected void alternateLocalLookup(String addressSoFar)    {
		ArrayList<String> resultsFromDB = new ArrayList<String>();
		addressList = new ArrayList<AddressInfo>();
		Pattern streetNumberOnly = Pattern.compile("^[0-9]+$");
		if (streetNumberOnly.matcher(addressSoFar).find()){
			dataBase.searchAddressSuggestionsFor(addressSoFar,resultsFromDB);
			
			for (String address : resultsFromDB){
				AddressInfo ai = new AddressInfo();
				ai.address = address;
				addressList.add(ai);
			}
			if (commitLookup != null) {
				commitLookup.run();
			}
		} else 
		{
			int size = StreetList.parentList.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					String s = new String(StreetList.parentList.get(i).name).toLowerCase();
					s = s.substring(0, 1).toUpperCase() + s.substring(1);
					resultsFromDB.add(addressSoFar+s.replace('+', ' '));
				}				
			}
			for (String address : resultsFromDB){
				AddressInfo ai = new AddressInfo();
				ai.address = address;
				addressList.add(ai);
			}
			if (commitLookup != null) {
				commitLookup.run();
			}
		}
	}
}
