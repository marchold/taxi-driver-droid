package com.catglo.deliveryDatabase;

import com.google.android.maps.GeoPoint;

public class AddressInfo {
	public String address;
	public GeoPoint location;
	
	@Override
	public String toString(){
		return address;
	}
}