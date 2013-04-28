package com.catglo.taxidroid;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import android.database.DatabaseUtils;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.Order;


import com.catglo.taxidroid.OrderListView.DragListener;
import com.catglo.deliveryDatabase.StreetList;
import com.catglo.deliveryDatabase.ZipCode;

import com.google.android.maps.GeoPoint;

public class TaxiDriverMainActivity extends TaxiDriverMainBaseActivity {
	
	OrderListView						orderList;
	MainScreenOrderAdapter				orderListText;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
		
		orderListText = new MainScreenOrderAdapter(this, R.layout.table_row, new ArrayList<Order>());

    	newOrder = (Button) findViewById(R.id.AddDelevery);
    	orderList = (OrderListView) findViewById(R.id.orderList);
    	
    	orderList.setDropListener(dropListener);
    	orderList.setDragListener(new DragListener(){public void drag(int from, int to) {
    		//helpDrag.setVisibility(View.GONE);
    	}});
    	
    	orderList.setOnItemClickListener(new OnItemClickListener(){public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    		Intent intent = new Intent(getApplicationContext(), DroppingOffActivity.class);
    		intent.putExtra("DB Key", listOrderKeys[arg2]);
    		startActivity(intent);	
		}});
    	
    	startShift();

    	((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener(){public void onClick(View v){
    		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    		emailIntent.setType("plain/text");
    		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "goblets@gmail.com" });
    		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Taxi Droid Feedback");
    		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
    		startActivity(Intent.createChooser(emailIntent, "Send mail..."));	
    	}});
    	
    	//Street Hire
    	((Button)findViewById(R.id.button2)).setOnClickListener(new OnClickListener(){public void onClick(View v){
    		Intent intent = new Intent(getApplicationContext(), ImmediatePickupAcitivty.class);
    		startActivity(intent);
    	}});
    
    	registerForContextMenu(orderList);
    }
    
    private void startShift() {
      	final String odometerPay = sharedPreferences.getString("odometer_per_mile", "");
		Float odoPay = 0f;
		
		final int lastOrderDelta  = dataBase.getHoursSinceLastOrder();
		final int openOrders      = dataBase.getUndeliveredOrderCount();
		final int ordersThisShift = dataBase.getNumberOfOrdersThisShift();
		
		if (lastOrderDelta > 12 && ordersThisShift > 0 && ordersThisShift > openOrders) {
			dataBase.setNextShift();
			startActivity(new Intent(getApplicationContext(),ShiftStartEndActivity.class));
		}
		
		final int extraTime = sharedPreferences.getInt("extraDays", 0);
		
		/*if ((dataBase.getCurShift() > 7) && zzz_version.isFree==true){
			if (extraTime == 0) {
				showDialog(REVIEW_FOR_MORE);
			} else {
				showDialog(TRIAL_OVER);
			}
		}*/
	}
   
	/******************************************************************
	 * ACTIVITY UI DEFINITION
	 * 
	 * - onActivityResult - rebuildList - dropListener - onCreateContextMenu - onContextItemSelected -
	 * commitChangeFromOrderEditScreen - onCreateOptionsMenu - onOptionsItemSelected
	 ******************************************************************/

	
	static final int DISPLAY_ALL = 0;
	static final int DISPLAY_ADDRESS = 1;
	static final int DISPLAY_ORDER_NUMBER = 2;
	static final int DISPLAY_PAST_TIPS = 3;
	int displayMode = DISPLAY_ADDRESS;
	
	public String getListText(Order order) {
		switch (displayMode){
		case DISPLAY_ADDRESS:{
			return String.format("%s",order.address);
		}
		case DISPLAY_ALL: {
			NumberFormat format	= new DecimalFormat("00");
			int hours = order.time.getHours();
			String amPm;
			if (hours > 12) {
				amPm = new String("pm");
				hours -= 12;
			} else {
				amPm = new String("am");
			}
			return String.format("%d:%s%s\t\t$%.2f\n%s", hours, format.format(order.time.getMinutes()), amPm, order.cost, order.address);
		}
		case DISPLAY_ORDER_NUMBER: 
			return String.format("%s",order.number);
		}
		return null;
		
	}
	
	protected void rebuildList() {
		orderListText.clear();
		final Cursor c = dataBase.getUndeliveredOrders();
		count = 0;

		if (c != null) {
			if (c.moveToFirst()) {
				do {
					final Order order = new Order(c);
					String escapedAddress = "'null'";
					try {
						escapedAddress = DatabaseUtils.sqlEscapeString(order.address);
					} catch (NullPointerException e){
						e.printStackTrace();
					}
					order.tipTotalsForThisAddress = dataBase.getTipTotal(getApplicationContext(), " `"+DataBase.Address+"` = "+escapedAddress+" AND Payed != -1");
					
					orderListText.add(order);

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
		//Log.i("Delivery Driver", "Order List Has " + count + " lines");


		if (count>1 && dragAndDropCount<20){
		//	helpDrag.setVisibility(View.VISIBLE);
		} else {
		//	helpDrag.setVisibility(View.GONE);
		}
		//Display display = ((WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		//int bot = (int) (count*(64));
		//if (display.getWidth() > 320 || display.getHeight() > 480){
		//	bot = (int) (count*(80));
		//} 
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) orderList.getLayoutParams();
		params.height=LayoutParams.WRAP_CONTENT;
		orderList.setLayoutParams(params);
		
		orderList.setAdapter(orderListText);
	}

	int dragAndDropCount=0;
	
	private final OrderListView.DropListener dropListener = new OrderListView.DropListener() { public void drop(final int from, final int to) {
		prefEditor.putInt("dragAndDropCount", ++dragAndDropCount);
		
		//Log.i("Delivery Driver", "Drag n Drop from = "+ from + " to= " + to);

		float order;
		if (to == 0) {
			order = listOrderValues[0] + 1;
		} else {
			if (to < from) {
				order = (listOrderValues[to] + listOrderValues[to - 1]) / 2;
			} else {
				order = (listOrderValues[to] + listOrderValues[to + 1]) / 2;
			}
		}
		dataBase.changeOrder(listOrderKeys[from], order);
		rebuildList();
	}};

	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, "View Details/Edit");
		menu.add(0, DELETE_ID, 0, "Delete");
	}
	    
	public boolean onContextItemSelected(final MenuItem item) {

		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		info.id = listOrderKeys[info.position];
		
		switch (item.getItemId()) {
		case EDIT_ID:
			Intent editOrderIntent = new Intent(getApplicationContext(), EditOrderActivity.class);
			editOrderIntent.putExtra("DB Key", (int)info.id);
			startActivityForResult(editOrderIntent, 0);
			return true;
		case DELETE_ID:
			recordToDelete = (int) info.id;
			showDialog(DIALOG_CONFIRM_DELETE_RECORD);
			return true;
		default:
			return false;// super.onContextItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SHOW_MAP_ACTIVITY, 0, "Show Map").setIcon(android.R.drawable.ic_menu_mapmode);
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case SHOW_MAP_ACTIVITY:
			startActivity(new Intent(getApplicationContext(),TaxiDriverMapActivity.class));	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}