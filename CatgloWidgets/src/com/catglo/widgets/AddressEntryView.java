package com.catglo.widgets;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.catglo.deliveryDatabase.AddressInfo;
import com.catglo.deliveryDatabase.AddressSuggestior;

import com.catglo.deliveryDatabase.DataBase;
import com.google.android.maps.GeoPoint;


public class AddressEntryView extends ButtonPadView implements OnItemClickListener {
	AddressSuggestior addressSuggestior;
	Pattern			pattern;
	private int		inputStage;
	ArrayList<AddressInfo> addressList;
	public GeoPoint addressLocation;
	
	
	private void onSpace(){
		switch (inputStage) {
		case 0:
			list.setVisibility(View.VISIBLE);
			//text.setText("Address - Street Name");
			
			break;
		case 1:
			list.setVisibility(View.VISIBLE);
			//text.setText("Address - Suffix");
			final String[] sufixList = 
			{ 
				"Apt. ", "Suite.", "Ave", "St", "Pl", "Dr",
				"N", "S","E", "W", "NW", "NE", "SW", "SE" 
			};
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					R.layout.address_list_item, sufixList);
			setAdapter(adapter);
			
			inputStage = 2;
			break;
		}	
	}

	public AddressEntryView(final Context context, final AttributeSet attrs, DataBase dataBase) {
		super(context, attrs);
		inputStage = 0;
		
		list.setOnItemClickListener(new OnItemClickListener(){public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			edit.setText(addressList.get(arg2).address); //TODO: FIX crash when selecting address from address history
			edit.setSelection(((TextView) arg1).getText().length(), ((TextView) arg1).getText().length());
			addressLocation = addressList.get(arg2).location;
			if (addressLocation!=null) Log.i("address","addressLocation "+addressLocation.getLatitudeE6()+addressLocation.getLongitudeE6());
		}});
		
		addressSuggestior = new AddressSuggestior(context.getApplicationContext(),new Runnable(){public void run(){
			list.post(new Runnable(){public void run(){
				ArrayAdapter<AddressInfo> streets = new ArrayAdapter<AddressInfo>(context, R.layout.address_list_item, addressSuggestior.addressList);
				list.setAdapter(streets);
				addressList = addressSuggestior.addressList;
			}});
		}},dataBase);
		pattern = Pattern.compile("([0-9\\-\\#\\@\\*_]*\\s)(.*)");

		//setText("Address - House Number"/*context.getString(R.string.AddressHouseNumber)*/);
		
		edit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(final Editable s) {
				addressLocation=null;
				Log.i("address","addressLocation is null");
				addressSuggestior.lookup(s.toString());
			}
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			}

		});
		
		
		
		setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction()==KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_SPACE){
						onSpace();
					}
				}
				return false;
			}}
		);
		
		final ImageButton space = (ImageButton) numbers.findViewById(R.id.ButtonSpace);
		space.setVisibility(View.VISIBLE);
		space.setBackgroundColor(0);
		space.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(-3);
				onSpace();
			}
		});

	
		final ImageButton abc = (ImageButton) numbers.findViewById(R.id.ButtonAbc);
		abc.setVisibility(View.VISIBLE);
		abc.setBackgroundColor(0);
		abc.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				final InputMethodManager mgr = (InputMethodManager) ((Activity) context)
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				// only will trigger it if no physical keyboard is open
				mgr.showSoftInput(edit, InputMethodManager.SHOW_FORCED);// .SHOW_IMPLICIT)
				
			}
		});
		
		ArrayList<String> addressStrings = new ArrayList<String>();
		dataBase.getAddressSuggestionsFor("",addressStrings);
		addressList = new ArrayList<AddressInfo>();
		for (String address:addressStrings){
			AddressInfo i = new AddressInfo();
			i.address = address;
			addressList.add(i);
		}
		ArrayAdapter<AddressInfo> streets = new ArrayAdapter<AddressInfo>(context, R.layout.address_list_item, addressList);
		list.setAdapter(streets);
		edit.clearFocus();
		one.requestFocus();
		list.requestFocus();
		
		edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS); 
		
				
	}
	


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);
		//selectedPoint = addressLocations.get(arg2);
	}

}