package com.catglo.taxidroid;

import java.sql.Timestamp;
import java.util.Calendar;

import com.catglo.deliveryDatabase.Shift;
import com.catglo.deliveryDatabase.TipTotalData;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ShiftStartEndActivity extends TaxiDroidBaseActivity implements Dialog.OnDismissListener {
    
	private static final int SETTINGS = 1;
	private static final int DELETE_SHIFT = 2;
	private EditText startTime;
	private EditText endTime;
	private EditText startODO;
	private EditText endODO;
	private Shift shift;
	private InputMethodManager imm;
	int whichShift;
	private Button endThisShift;
	private TipTotalData tips;
	private TextView deliveries;
	private TextView moneyCollected;
	private TextView hoursWorked;
	private TextView odoTotal;
	
	@Override
	public void onPause(){
		dataBase.saveShift(shift);
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		updateUI();
	}
	
	void updateUI(){
		shift = dataBase.getShift(whichShift);
		
		dataBase.estimateShiftTimes(shift);
		
		tips = dataBase.getTaxiTipTotal(getApplicationContext(), "Payed != -1 AND Shift="+whichShift);
		
		startTime.setText(getFormattedTimeDay(shift.startTime));
		endTime.setText(getFormattedTimeDay(shift.endTime));
		startODO.setText(shift.odometerAtShiftStart+"");
		endODO.setText(shift.odometerAtShiftEnd+"");
		
		deliveries.setText(tips.deliveries+"");
		moneyCollected.setText(super.getFormattedCurrency(tips.payed));
		hoursWorked.setText("---");
		
		float t1 =  shift.endTime.getTimeInMillis();
		float t2 = shift.startTime.getTimeInMillis();
		//if (t1>t2) {
			float total = t1-t2;
			total = total/3600000f;
			hoursWorked.setText(String.format("%.2f",total));
		//} else {
		//	endTime.setText("0");
		//}
		
		odoTotal.setText(""+(shift.odometerAtShiftEnd-shift.odometerAtShiftStart));
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_end_shift);
		
		
		Intent intent = getIntent();
		int id = intent.getIntExtra("ID", -1);
		if (id == -1){
			whichShift = dataBase.getCurShift();
		} else {
			whichShift = id;
		}
		
		startTime = (EditText) findViewById(R.id.editText1);
		endTime = (EditText) findViewById(R.id.editText2);
		startODO = (EditText) findViewById(R.id.editText3);
		endODO = (EditText) findViewById(R.id.editText4);
	
		deliveries = (TextView) findViewById(R.id.textView6);
		moneyCollected = (TextView)findViewById(R.id.textView8);
		hoursWorked = (TextView)findViewById(R.id.textView10);
		odoTotal = (TextView)findViewById(R.id.textView12);
		
		
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(startTime.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(endTime.getWindowToken(), 0);
		
		
		imm.hideSoftInputFromWindow(startODO.getWindowToken(), 0);
		
		
		startTime.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() != MotionEvent.ACTION_UP) return false;
				showTimeSliderDialog(startTime,shift.startTime,ShiftStartEndActivity.this);	
				return true;
		}});
		endTime.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() != MotionEvent.ACTION_UP) return false;
				showTimeSliderDialog(endTime,shift.endTime,ShiftStartEndActivity.this);
				return true;
		}});
		
		startODO.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				imm.hideSoftInputFromWindow(startODO.getWindowToken(), 0);
				if (event.getAction() != MotionEvent.ACTION_UP) return true;
				Intent i = new Intent(getApplicationContext(),OdometerEntryActivity.class);
				i.putExtra("startValue", true);
				i.putExtra("ID", shift.primaryKey);
				startActivity(i);
				return false;
			}});
		endODO.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				imm.hideSoftInputFromWindow(endODO.getWindowToken(), 0);
				if (event.getAction() != MotionEvent.ACTION_UP) return true;
				Intent i = new Intent(getApplicationContext(),OdometerEntryActivity.class);
				i.putExtra("startValue", false);
				i.putExtra("ID", shift.primaryKey);
				startActivity(i);
				return false;
			}});
		
		//OK Button
		((Button) findViewById(R.id.button2)).setOnClickListener(new OnClickListener(){public void onClick(View v) {
			finish();
		}});
		
	//	((Button) findViewById(R.id.button3)).setOnClickListener(new OnClickListener(){public void onClick(View v) {
	//		dataBase.deleteShift(whichShift);
	//		finish();
	//	}});
		
		endThisShift = (Button) findViewById(R.id.button1);
		if (id != -1) {
			endThisShift.setVisibility(View.GONE);
		}
		endThisShift.setOnClickListener(new OnClickListener(){public void onClick(View v) {
			dataBase.setNextShift();
			finish();
		}});
		
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case DELETE_SHIFT:
			return new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(
					R.string.deleteThisShift).setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int whichButton) {
						//Log.i("Delivery Driver", "User Y/N Delete Order");
						int newShift = dataBase.getPrevoiusShiftNumber(whichShift);
						dataBase.deleteShift(whichShift);
						whichShift=newShift;
						updateUI();
					}
				}).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int whichButton) {
	
						/* User clicked Cancel so do some stuff */
					}
				}).create(); 
		}
		return null;
	}
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, SETTINGS, 0, getString(R.string.settings)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, DELETE_SHIFT, 0, getString(R.string.deleteThisShift)).setIcon(android.R.drawable.ic_menu_delete);
		return true;
	} 

	/* Handles item selections */
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS: {
			startActivityForResult(new Intent(getApplicationContext(), TaxiSettingsActivity.class), 0);
			return true;
		}
		case DELETE_SHIFT: {
				showDialog(DELETE_SHIFT);
			return true;
		}
		}
		return false;
	}

	public void onDismiss(DialogInterface dialog) {
		dataBase.saveShift(shift);
		updateUI();
	}
	
}
