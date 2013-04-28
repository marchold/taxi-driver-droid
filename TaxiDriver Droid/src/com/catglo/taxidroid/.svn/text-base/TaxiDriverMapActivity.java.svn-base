package com.catglo.taxidroid;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.catglo.deliveryDatabase.AddressSuggestior;
import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.Order;


import com.catglo.taxidroid.OrderListView.DragListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TaxiDriverMapActivity extends TaxiDriverMainBaseActivity {
	private MapView mapView;
	private MapController mapController;
	List<Overlay> startPoints;
	Drawable drawable;
	MapOverlay itemizedOverlay;
	//private List<Overlay> endPoints;
	private AddressSuggestior addressValidator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_map);
        init();
        
        mapView = (MapView) findViewById(R.id.mapview);
    	mapView.setBuiltInZoomControls(true);
    	mapController = mapView.getController();
    	mapController.setZoom(13);
    	
    	startPoints = mapView.getOverlays();
    	itemizedOverlay = new MapOverlay(getResources().getDrawable(R.drawable.marker));
    	
    	rebuildList();
    }
    
    @Override
    public void onLocationChanged(Location location) {
    	super.onLocationChanged(location);
    	if (mapController!=null){
    		mapController.setCenter(geoPoint);
    	}
    }

   
    
	@Override
	synchronized protected void rebuildList() {
		
		
		final Cursor c = dataBase.getUndeliveredOrders();
		count = 0;
		orders.clear();
		
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					final Order order = new Order(c);
					orders.add(order);
	
					if (order.isValidated==true){
						float lat = (float)order.geoPoint.getLatitudeE6()/(float)1E6;
						float lng = (float)order.geoPoint.getLongitudeE6()/(float)1E6;
						Log.i("Taxi","Validated = Added Overlay item at lat="+lat+"  lng="+lng);
						OverlayItem overlayitem = new OverlayItem(order.geoPoint, order.address, "snippet");
    					itemizedOverlay.addOverlay(overlayitem);
    					startPoints.add(itemizedOverlay);
					} else {
						Log.i("Taxi","Validated = Skipped overlay");
						
					}
					
					listOrderValues[count] = order.deliveryOrder;
					listOrderKeys[count] = order.primaryKey;
					count++;
					if (count > 99) {
						break;
					}

				} while (c.moveToNext());
				listOrderValues[count] = listOrderValues[count - 1] - 2;
			}
		}
		c.close();	
	}

}


/* void lookupOrderGeoPoint(final Order order){
addressValidator = new AddressSuggestior(getApplicationContext(),new Runnable(){public void run(){
runOnUiThread(new Runnable(){public void run(){
	synchronized(TaxiDriverMapActivity.this){
	try {
    
		if (addressValidator.addressLocations.size()>0 &&
				addressValidator.addressLocations.get(0) != null){
			
			Log.d("Taxi","Found "+order.address);
			
			order.isValidated=true;
			order.geoPoint = addressValidator.addressLocations.get(0);
			
			OverlayItem overlayitem = new OverlayItem(order.geoPoint, order.address, "snippet");
			itemizedOverlay.addOverlay(overlayitem);
			startPoints.add(itemizedOverlay);
			
	
		} else {
			Log.d("Taxi","Failed "+order.address);
			
		}
  	} catch (NullPointerException e){e.printStackTrace();} Log.d("Taxi",order.address);}
	
}});

}},dataBase);
addressValidator.lookup(order.address);
Log.d("Taxi","lookup "+order.address);
}*/