package com.catglo.taxidroid;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.Order;
import com.catglo.deliveryDatabase.TipTotalData;

import com.catglo.taxidroid.R;
import com.catglo.deliveryDatabase.StreetList;


public class MainScreenOrderAdapter extends ArrayAdapter<Order> {
	 private ArrayList<Order> items;
	private Context context;

     public MainScreenOrderAdapter(Context context, int textViewResourceId, ArrayList<Order> items) {
             super(context, textViewResourceId, items);
             this.items = items;
             this.context = context;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
             View v = convertView;
             if (v == null) {
                 LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 v = vi.inflate(R.layout.table_row, null);
             }
             Order order = items.get(position);
             if (order != null) {
            	 try {
                	 TextView addressView = (TextView)v.findViewById(R.id.addressSmall);   
            	 TextView timeView = (TextView)v.findViewById(R.id.textView1);
            	
            	 addressView.setText(order.address);
            	 timeView.setText(order.time.getHours()+":"+order.time.getMinutes());
            	 } catch(Exception e){e.printStackTrace();};
            	 //TextView tt = (TextView) v.findViewById(R.id.text1);
                    // TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                   //  if (tt != null) {
                     //      tt.setText("Name: "+o.address);                            }
                     //if(bt != null){
                      //     bt.setText("Status: "+ o.getOrderStatus());
                     //}
             }
             return v;
     }
}
