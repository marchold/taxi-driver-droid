package com.catglo.taxidroid;

import java.util.Calendar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.TipTotalData;


public class TipHistoryActivity extends TaxiDroidBaseActivity {
	private static final int	SETTINGS	= 0;

	boolean	userDates	= false;
	private Button	email;
	private EditText startDateField;
	private EditText endDateField;
	private TextView tipsMade;
	//private TextView driverEarnings;
	private TextView bestTip;
	private TextView averageTip;
	private TextView worstTip;
	private Button text;

	protected Calendar startDate;

	protected Calendar endDate;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_totals);
    
        final Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		startDate = (Calendar) now.clone();
		endDate = (Calendar) now.clone();
		
		
        String[] listValues = {
        		getString(R.string.today),
        		getString(R.string.thisWeek),
        		getString(R.string.ThisMonth),
        		getString(R.string.ThisYear),
        		getString(R.string.CustomDateRange)};
        Spinner dateRangeSpinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dateRangeSpinner.setAdapter(adapter);
		dateRangeSpinner.setSelection(1);
		dateRangeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				startDate = Calendar.getInstance();
				startDate.setTimeInMillis(System.currentTimeMillis());
				endDate = Calendar.getInstance();
				endDate.setTimeInMillis(System.currentTimeMillis());
					switch (arg2) {
		        case 0: startDate.set(Calendar.HOUR_OF_DAY, 0);
		                endDate.set(Calendar.HOUR_OF_DAY, 0);
		                endDate.add(Calendar.DAY_OF_YEAR, 1);
		                startDateField.setText(startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR));
		                endDateField.setText(endDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR));
		                updateUI();
		                break;
		        case 1: getWorkWeekDates(now,startDate,endDate);
				        startDateField.setText(startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR));
		                endDateField.setText(endDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR));
		                updateUI(); 
		        break;
		        case 2: startDate.set(Calendar.DATE, 0);
						endDate.add(Calendar.MONTH, 1);
						startDate.set(Calendar.DATE, 1);
						startDateField.setText(startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR));
		                endDateField.setText(endDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR));
		                updateUI();
		                break;
		        case 3: startDate.set(Calendar.DAY_OF_YEAR, 1);
						endDate.add(Calendar.YEAR, 1);
						endDate.set(Calendar.DAY_OF_YEAR, 0);
						startDateField.setText(startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR));
		                endDateField.setText(endDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR));
		                updateUI();
		                break;
		        case 4: 
		        		getDateRangeDialog(startDate,endDate,new OnDismissListener(){public void onDismiss(DialogInterface dialog) {
		        			startDateField.setText(startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR));
			                endDateField.setText(endDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR));
			                updateUI();
		        		}});
		        	break;
			    }
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
        
        startDateField = (EditText) findViewById(R.id.editText1);
        endDateField = (EditText) findViewById(R.id.editText2);
        
        getWorkWeekDates(now,startDate,endDate);
        startDateField.setText(startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR));
        endDateField.setText(endDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR));
      
        OnTouchListener dateTouchListener = new OnTouchListener(){public boolean onTouch(View v, MotionEvent event) {
        	if (event.getAction() == MotionEvent.ACTION_DOWN){
        		getDateRangeDialog(startDate,endDate,new OnDismissListener(){public void onDismiss(DialogInterface dialog) {
        			startDateField.setText(startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR));
	                endDateField.setText(endDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR));
	                updateUI();
        		}});
        		return true;
        	}
        	return false;
		}};
        
        startDateField.setOnTouchListener(dateTouchListener);
        endDateField.setOnTouchListener(dateTouchListener);
        
        tipsMade = (TextView) findViewById(R.id.textView5);
        //driverEarnings = (TextView) findViewById(R.id.textView7);
        bestTip = (TextView) findViewById(R.id.textView9);
        averageTip = (TextView) findViewById(R.id.textView11);
        worstTip = (TextView) findViewById(R.id.wordtTip);
         
        email = (Button) findViewById(R.id.button2);
        text = (Button) findViewById(R.id.button1);
        email.setOnClickListener(new View.OnClickListener() {public void onClick(final View v) {
        	TipTotalData tips = dataBase.getTipTotal(getApplicationContext(), " Payed >= 0 AND `"+ DataBase.Time + "` >= '"+String.format("%3$tY-%3$tm-%3$td", startDate, startDate, startDate) +
 	               "' AND `"+ DataBase.Time + "` <= '" + String.format("%3$tY-%3$tm-%3$td", endDate, endDate, endDate)+"'");
        	
        	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
    			
        	emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, ""+
        			getString(R.string.tipsHistoryExportEmailTop)+"\n"+
        			getString(R.string.startDate)+":"+startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR)+"  "+
        			getString(R.string.endDate)+":"+startDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR)+"  "+
        			getString(R.string.tipsMade) +":"+(tips.payed-tips.cost)+"\n"+
        			getString(R.string.DriverEarnings) +":"+(tips.total)+"\n"+
        			getString(R.string.bestTip) +":"+tips.bestTip+"\n"+
        			getString(R.string.averageTip) +":"+tips.averageTip+"\n"+
        			getString(R.string.worstTip) +":"+tips.worstTip+ "\n");
			emailIntent .setType("plain/text"); 
			emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{}); 
			emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT,  getString(R.string.app_name)+" "+getString(R.string.tipHistory) +
					startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR)+"..."+startDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR)); 
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}});
		text.setOnClickListener(new View.OnClickListener() {public void onClick(final View v) {
			TipTotalData tips = dataBase.getTipTotal(getApplicationContext(), " Payed >= 0 AND `"+ DataBase.Time + "` >= '"+String.format("%3$tY-%3$tm-%3$td", startDate, startDate, startDate) +
	 	               "' AND `"+ DataBase.Time + "` <= '" + String.format("%3$tY-%3$tm-%3$td", endDate, endDate, endDate)+"'");
	        	
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
			sendIntent.setData(Uri.parse("sms:"));
			sendIntent.putExtra("sms_body",  ""+
			getString(R.string.tipsHistoryExportEmailTop)+"\n"+
			getString(R.string.startDate)+":"+startDate.get(Calendar.MONTH)+"/"+startDate.get(Calendar.DAY_OF_MONTH)+"/"+startDate.get(Calendar.YEAR)+"  "+
			getString(R.string.endDate)+":"+startDate.get(Calendar.MONTH)+"/"+endDate.get(Calendar.DAY_OF_MONTH)+"/"+endDate.get(Calendar.YEAR)+"  "+
			getString(R.string.tipsMade) +":"+(tips.payed-tips.cost)+"\n"+
			getString(R.string.DriverEarnings) +":"+(tips.total)+"\n"+
			getString(R.string.bestTip) +":"+tips.bestTip+"\n"+
			getString(R.string.averageTip) +":"+tips.averageTip+"\n"+
			getString(R.string.worstTip) +":"+tips.worstTip+ "\n"); 
			startActivity(Intent.createChooser(sendIntent, "Send text..."));
		}});
		updateUI();
    }
    
    void updateUI(){
    	TipTotalData tips = dataBase.getTaxiTipTotal(getApplicationContext(), "  `"+ DataBase.Time + "` >= '"+String.format("%3$tY-%3$tm-%3$td", startDate, startDate, startDate) +
	               "' AND `"+ DataBase.Time + "` <= '" + String.format("%3$tY-%3$tm-%3$td", endDate, endDate, endDate)+"'");
    	
    	tipsMade.setText(""+currency.format(tips.payed-tips.cost));
    	bestTip.setText(""+currency.format(tips.bestTip));
    	if (Float.isNaN(tips.averageTip)==false) averageTip.setText(""+currency.format(tips.averageTip));
    	worstTip.setText(""+currency.format(tips.worstTip));
    }
    
 

}