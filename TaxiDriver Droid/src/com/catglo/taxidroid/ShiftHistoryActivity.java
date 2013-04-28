package com.catglo.taxidroid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.DropOff;
import com.catglo.deliveryDatabase.Order;
import com.catglo.deliveryDatabase.TipTotalData;
import com.catglo.taxidroid.R;


public class ShiftHistoryActivity extends TaxiDroidBaseActivity  implements android.view.GestureDetector.OnGestureListener {
	private static final int		SETTINGS						= 0;
	private static final int		TIP_STATS						= 1;
	private static final int		SHOW_ADDRESS					= 2;
	private static final int		SHOW_NOTES						= 3;
	private static final int        DELETE_SHIFT                    = 4;
	private static final int		DIALOG_CONFIRM_DELETE_RECORD	= 5;
	private static final int        DIALOG_CONFIRM_DELETE_SHIFT     = 6;
	private static final int		EDIT_ID							= 7;
	private static final int		DELETE_ID						= 8;
	private static final int        GOTO_DATE                       = 9;
	private static final int        GOTO_DATE_DIALOG                = 10;
	private static final int		EXPORT_DATA						= 11;
	private static final int		EXPORT_DATA_DIALOG				= 12;
	private static final int 		EXPORT_WHERE      				= 13;
	private static final int   	    EMAIL_LOG          				= 14;
	private static final int        EXPORT_WHAT_DIALOG              = 15;
	private static final int        EMAIL_OR_FILE_DIALOG            = 16;
		
	ListView						orderList;
	int[]							listOrderKeys= new int[1000];
	private long					recordToDelete= -1;
	private boolean					showNotes;
//	private TextView				collumHeadings;
	private ViewFlipper				viewFlipper;
	private GestureDetector			gestureDector;
	private OnTouchListener			gestureListener;
	private boolean					use2ndView;
	private int						viewingShift;
	private TextView				moneyCollected;
	private TextView				tipsMade;
//	private TextView				mileageEarned;
//	private TextView				driverEarnings;
	private TextView				moneyCollectedA;
	private TextView				tipsMadeA;
	//private TextView				collumHeadingsA;
	private ListView				orderListA;
	private TextView				moneyCollectedB;
	private TextView				tipsMadeB;
//	private TextView				collumHeadingsB;
	private ListView				orderListB;
	private TextView				numberOfOrders;
	private TextView				numberOfOrdersA;
	private TextView				numberOfOrdersB;
	private TextView				shiftA;
	private TextView				shiftB;
	private TextView				shift;
	private TextView				osDateA;
	private TextView				osDateB;
	private TextView				osDate;
	private TextView				cashCollectedA;
	private TextView				cashCollectedB;
	private TextView				cashCollected;
	private TextView				cashTipsA;
	private TextView				cardCheckTipsA;
	private TextView				cashTipsB;
	private TextView				cardCheckTipsB;
	private TextView				cashTips;
	private TextView				cardCheckTips;
	
	private TextView				commision;
	private TextView				commisionA;
	private TextView				commisionB;
	
	private TextView				expenses;
	private TextView				expensesA;
	private TextView				expensesB;
	
	//For CSV Export
	private Calendar startDate;
	private Calendar endDate;
	protected String fileName;
    
	
	@Override
	public void onResume(){
		super.onResume();
		updateUI();
	}
	
	/** Called when the activity is first created. */
    /** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		showNotes = false;

		use2ndView = false;

		viewingShift = dataBase.getCurShift();

		viewFlipper = new ViewFlipper(this);
		viewFlipper.setLayoutParams(new android.view.ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layoutA = (RelativeLayout)inflater.inflate(R.layout.order_summary, null);
		RelativeLayout layoutB = (RelativeLayout)inflater.inflate(R.layout.order_summary, null);
		viewFlipper.addView(layoutA, 0);
		viewFlipper.addView(layoutB, 1);
		setContentView(viewFlipper);

		
		gestureDector = new GestureDetector(this);
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(final View v, final MotionEvent event) {
				if (gestureDector.onTouchEvent(event)) return true;
				return false;
			}
		};

		moneyCollectedA = (TextView) layoutA.findViewById(R.id.osMoneyCollected);
		cashCollectedA = (TextView) layoutA.findViewById(R.id.osCashCollected);
		tipsMadeA = (TextView) layoutA.findViewById(R.id.osTipsMade);
	//	mileageEarnedA = (TextView) layoutA.findViewById(R.id.osMileageEarned);
	//	driverEarningsA = (TextView) layoutA.findViewById(R.id.osDriverEarnings);
//		collumHeadingsA = (TextView) layoutA.findViewById(R.id.Label04);
		orderListA = (ListView) layoutA.findViewById(R.id.DeliveriesSummaryList);
		numberOfOrdersA = (TextView)layoutA.findViewById(R.id.osNumberOfDeliveries);
		osDateA = (TextView)layoutA.findViewById(R.id.osDate);
		shiftA = (TextView)layoutA.findViewById(R.id.osShift);
		cashTipsA = (TextView)layoutA.findViewById(R.id.CashTips);
		cardCheckTipsA = (TextView)layoutA.findViewById(R.id.osCreditTips);
		commisionA =  (TextView)layoutA.findViewById(R.id.textView2);
		expensesA = (TextView)layoutA.findViewById(R.id.textView4);
		
		moneyCollectedB = (TextView) layoutB.findViewById(R.id.osMoneyCollected);
		cashCollectedB = (TextView) layoutA.findViewById(R.id.osCashCollected);
		tipsMadeB = (TextView) layoutB.findViewById(R.id.osTipsMade);
	//	mileageEarnedB = (TextView) layoutB.findViewById(R.id.osMileageEarned);
	//	driverEarningsB = (TextView) layoutB.findViewById(R.id.osDriverEarnings);
	//	collumHeadingsB = (TextView) layoutB.findViewById(R.id.Label04);
		orderListB = (ListView) layoutB.findViewById(R.id.DeliveriesSummaryList);
		numberOfOrdersB = (TextView)layoutB.findViewById(R.id.osNumberOfDeliveries);
		osDateB = (TextView)layoutB.findViewById(R.id.osDate);
		shiftB = (TextView)layoutB.findViewById(R.id.osShift);
		cashTipsB = (TextView)layoutB.findViewById(R.id.CashTips);
		cardCheckTipsB = (TextView)layoutB.findViewById(R.id.osCreditTips);
		commisionB =  (TextView)layoutB.findViewById(R.id.textView2);
		expensesB = (TextView)layoutB.findViewById(R.id.textView4);
			
		osDate = osDateA;
		moneyCollected = moneyCollectedA;
		cashCollected = cashCollectedA;
		tipsMade = tipsMadeA;
//		mileageEarned = mileageEarnedA;
//		driverEarnings = driverEarningsA;
		//collumHeadings = collumHeadingsA;
		orderList = orderListA;
		numberOfOrders = numberOfOrdersA;
		shift = shiftA;
		cashTips = cashTipsA;
		cardCheckTips = cardCheckTipsA;
		commision = commisionA;
		expenses = expensesA;
		
		moneyCollectedA.setOnTouchListener(gestureListener);
		cashCollectedA.setOnTouchListener(gestureListener);
		tipsMadeA.setOnTouchListener(gestureListener);
	//	collumHeadingsA.setOnTouchListener(gestureListener);
		orderListA.setOnTouchListener(gestureListener);
		numberOfOrdersA.setOnTouchListener(gestureListener);
		cashTipsA.setOnTouchListener(gestureListener);
		cardCheckTipsA.setOnTouchListener(gestureListener);
		commisionA.setOnTouchListener(gestureListener);
		expensesA.setOnTouchListener(gestureListener);
		
		moneyCollectedB.setOnTouchListener(gestureListener);
		cashCollectedB.setOnTouchListener(gestureListener);
		tipsMadeB.setOnTouchListener(gestureListener);
		//collumHeadingsB.setOnTouchListener(gestureListener);
		orderListB.setOnTouchListener(gestureListener);
		numberOfOrdersB.setOnTouchListener(gestureListener);
		cashTipsB.setOnTouchListener(gestureListener);
		cardCheckTipsB.setOnTouchListener(gestureListener);
		commisionB.setOnTouchListener(gestureListener);
		expensesB.setOnTouchListener(gestureListener);
			
		
		registerForContextMenu(orderListA);
		registerForContextMenu(orderListB);

		updateUI();

	}
	
	public float calculateCommision(int paymentType,float meterValue,Order order){
		float rate;
		try {
			switch (paymentType) {
			default:
			case /*NO_PAYMENT*/0:
				rate=0;
				break;
			case /*CASH_PAYMET*/1:
				if (order.streetHail){
					
					rate = new Float(sharedPreferences.getString("Commission_for_street_hire", "0"));
				} else {
					rate = new Float(sharedPreferences.getString("Commission_for_scheduled_jobs", "0"));
				}
				break;
			case /*CREDIT_PAYMENT*/2:
				if (order.streetHail){
					rate = new Float(sharedPreferences.getString("Commission_for_street_hire_credit", "0"));
				} else {
					rate = new Float(sharedPreferences.getString("Commission_for_scheduled_jobs_credit", "0"));
				}
				break;
			case /*ACCOUNT_PAYMENT*/3:
				if (order.streetHail){
					rate = new Float(sharedPreferences.getString("Commission_for_street_hire_account", "0"));
				} else {
					rate = new Float(sharedPreferences.getString("Commission_for_scheduled_jobs_account", "0"));
				}
				break;
			}
		} catch (NumberFormatException e){
			rate=0;
		}
		float retVal = meterValue*rate;
		return retVal;
	}

	
	private void updateUI() {
		final ArrayList<HashMap<String, String>> listText = new ArrayList<HashMap<String, String>>();
		final Cursor c = dataBase.getShiftOrders(viewingShift);
		int count = 0;
		String time = new String("");
		//int _shift = 0;
		osDate.setText("");
		final DecimalFormat currency = new DecimalFormat("#0.00");

		final ArrayList<Integer> pickupIds = new ArrayList<Integer>();
		
		if (c != null) {
			if (c.moveToFirst()) {
				final Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(Order.GetTimeFromString(c.getString(c.getColumnIndex(DataBase.Time))));
			//	_shift = c.getInt(c.getColumnIndex(DataBase.Shift));
				time = String.format("%tA %tb %te %tY", calendar, calendar, calendar, calendar);
				osDate.setText(time);
				do {
					final HashMap<String, String> map = new HashMap<String, String>();
					map.put("number", c.getString(c.getColumnIndex(DataBase.OrderNumber)));
					
					String st = c.getString(c.getColumnIndex(DataBase.Cost));
					map.put("cost", "" + currency.format(new Float(st)));
					
					pickupIds.add(new Integer(c.getInt(c.getColumnIndex("ID"))));
					
					if (showNotes) {
						map.put("address", c.getString(c.getColumnIndex(DataBase.Notes)));
				//		collumHeadings.setText(R.string.notes);
					} else {
						map.put("address", c.getString(c.getColumnIndex(DataBase.Address)));
				//		collumHeadings.setText(R.string.pickupAddress);
					}

					listText.add(map);

					listOrderKeys[count] = c.getInt(c.getColumnIndex("ID"));
					count++;
					if (count > 999) {
						break;
					}

					// listText.add(String.format("%1$-8s$%2$-8s%s",
					// c.getString(c.getColumnIndex(DataBase.OrderNumber)),c.getString(c.getColumnIndex(DataBase.Cost)),c.getString(c.getColumnIndex(DataBase.Address))));
				} while (c.moveToNext());
			}
		}
		c.close();

		float cashTotal = 0;
		float creditTotal = 0;
		float accountTotal = 0;
		float meterTotal = 0;
		float meterCashTotal = 0; 
		float paymentTotal = 0;
		float commisionTotal = 0;
		
		int totalOrders = pickupIds.size();
		
		for (int i = 0; i < totalOrders;i++){
			Order order = dataBase.getOrder(pickupIds.get(i));
			dataBase.loadOrderDropOffs(order);
			float _meterTotal = 0;
			float _paymentTotal = 0;
			float _commisionTotal = 0;
			//boolean calncled=false;
			//boolean noshow=false;
			String dropOffAddress = "";
			
			for (int j = 0; j < order.dropOffs.size(); j++){
				DropOff d = order.dropOffs.get(j);
				_meterTotal += d.meterAmount;
				_paymentTotal += d.payment;
				_commisionTotal += calculateCommision(d.paymentType,d.meterAmount,order);
				if (j==0){
					dropOffAddress = d.address;
				}
				switch (d.paymentType){
				case AddEditOrderBaseActivity.CASH_PAYMENT: cashTotal += d.payment;
														    meterCashTotal += d.meterAmount;
				break;
				case AddEditOrderBaseActivity.CREDIT_PAYMENT: creditTotal += d.payment;
				break;
				case AddEditOrderBaseActivity.ACCOUNT_PAYMENT: accountTotal += d.payment;
				break;
				}
			}
			
			
			final HashMap<String, String> map = listText.get(i);
			if (order.payed == Order.PAYMENTSTATUS_CANCELED){
				map.put("cost",getString(R.string.cancled));
			} else if (order.payed == Order.PAYMENTSTATUS_NOSHOW){
				map.put("cost","noshow");
			} else {
				map.put("cost",currency.format(_meterTotal));
			}
			map.put("payed",currency.format(_paymentTotal));
			
			map.put("type",order.apartmentNumber);
			
			map.put("dropOffAddress",dropOffAddress);
			
			
			meterTotal += _meterTotal;
			paymentTotal += _paymentTotal;
			commisionTotal += _commisionTotal;
		}
		
		final SimpleAdapter adapter = new SimpleAdapter(this, listText, R.layout.check_out_list, new String[] {
				"number", "type", "address","cost","payed","dropOffAddress" }, new int[] { 
				R.id.check_out_order_number, R.id.checkout_trip_type, R.id.textView2 ,
				R.id.textView3,R.id.textView4,R.id.textView5});

		orderList.setAdapter(adapter);

		
		numberOfOrders.setText(""+totalOrders);
		
		moneyCollected.setText(currency.format(paymentTotal));
		cashCollected.setText(currency.format(cashTotal));
		
		float totalTipsMade = paymentTotal-meterTotal;
		tipsMade.setText(currency.format(totalTipsMade));

		float cashTipsAmount = cashTotal - meterCashTotal; 
		cashTips.setText(currency.format(cashTipsAmount));
		
		float reportableTips = totalTipsMade-cashTipsAmount;
		cardCheckTips.setText(currency.format(reportableTips));
		
		//driverEarnings.setText(currency.format((totalTipsMade + tip.mileageEarned)));
		shift.setText(" "+viewingShift);
		
		commision.setText(TaxiDroidBaseActivity.getFormattedCurrency(commisionTotal));
		
		expenses.setText(TaxiDroidBaseActivity.getFormattedCurrency(dataBase.getTotalExpensesForShift(viewingShift)));
		
	}
	

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case EDIT_ID:
			edit(info.id);
			return true;
		case DELETE_ID:
			recordToDelete = listOrderKeys[(int) info.id];
			showDialog(DIALOG_CONFIRM_DELETE_RECORD);
			return true;
		default:
			return false;// super.onContextItemSelected(item);
		}
	}

	void edit(final long l) {
		final Context context = getApplicationContext();
		final Intent myIntent = new Intent(context, EditOrderActivity.class);
		myIntent.putExtra("DB Key", listOrderKeys[(int) l]);
		startActivityForResult(myIntent, 0);
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, "View Order Details");
		menu.add(0, DELETE_ID, 0, "Delete Order");
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
		if (resultCode == 400) {
			setResult(400);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
	//	menu.add(0, SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, EMAIL_LOG, 0, "Export").setIcon(android.R.drawable.ic_menu_send);
		menu.add(0, SHOW_NOTES, 0, "Toggle Notes/Address");
		menu.add(0, GOTO_DATE, 0, "Go To Day").setIcon(android.R.drawable.ic_menu_day);
		if (zzz_version.isFree==false)
			menu.add(0, DELETE_SHIFT, 0, "Delete This Shift").setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS: {
			final Intent myIntent = new Intent(getApplicationContext(), TaxiSettingsActivity.class);
			startActivityForResult(myIntent, 0);
			return true;
		}
		case TIP_STATS: {
			startActivityForResult(new Intent(getApplicationContext(), TipHistoryActivity.class), 0);
			return true;
		}
		case SHOW_NOTES: {
			showNotes = !showNotes;
			updateUI();
			return true;
		}
		case DELETE_SHIFT: {
			showDialog(DIALOG_CONFIRM_DELETE_SHIFT);
			return true;
		}
		case GOTO_DATE: 
			showDialog(GOTO_DATE_DIALOG);
			return true;
		case EMAIL_LOG: /*Export*/
			showDialog(EXPORT_WHAT_DIALOG);
			return true;
		/*case EXPORT_DATA:
			showDialog(EXPORT_DATA_DIALOG);
			return true;
		case EMAIL_LOG:
			Thread thread = new Thread(new Runnable(){public void run()
			{
				String driverLog = dataBase.getTaxiLogSheet(viewingShift);
				String fileName = "logSheet.html";
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath());
				File file = new File(dir, fileName);
				try {
					FileWriter fstream = new FileWriter(file);
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(driverLog);
					out.flush();
					out.close();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "ERROR:"+e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				
				final Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.logSheet));
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
				emailIntent.setType("text/html");
				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+fileName));	
				emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ShiftHistoryActivity.this.runOnUiThread(new Runnable(){public void run(){
					getApplicationContext().startActivity(emailIntent);		
				}});
				
			}});
			thread.start();
			return true;
		*/
		}
			
		return false;
	}

	public boolean onDown(final MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private void flipViews() {
		if (use2ndView) {
			use2ndView = false;
			moneyCollected = moneyCollectedA;
			cashCollected = cashCollectedA;
			tipsMade = tipsMadeA;
		//	mileageEarned = mileageEarnedA;
		//	driverEarnings = driverEarningsA;
			numberOfOrders = numberOfOrdersA;
			osDate=osDateA;
			shift = shiftA;
			cashTips = cashTipsA;
			cardCheckTips = cardCheckTipsA;
			commision = commisionA;
			expenses=expensesA;
			
			orderList = orderListA;
		} else {
			use2ndView = true;
			moneyCollected = moneyCollectedB;
			cashCollected = cashCollectedB;
			tipsMade = tipsMadeB;
		//	mileageEarned = mileageEarnedB;
	//		driverEarnings = driverEarningsB;
			numberOfOrders = numberOfOrdersB;
			shift = shiftB;
			osDate=osDateB;
			orderList = orderListB;
			cashTips = cashTipsB;
			cardCheckTips = cardCheckTipsB;
			commision = commisionB;
			expenses = expensesB;
			
		}
	}

	public boolean onFling(final MotionEvent arg0, final MotionEvent arg1, final float velocityX, final float arg3) {
		if (velocityX > 400) { // left fling
			viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_in));
			viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_out));
			if (viewingShift > 0) {
				flipViews();
				viewingShift = dataBase.getPrevoiusShiftNumber(viewingShift);
				updateUI();
				viewFlipper.showNext();
				return true;
			}
		}
		if (velocityX < 400) { // right fling
			viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_in));
			viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_out));
			if (viewingShift < dataBase.getCurShift()) {
				flipViews();
				viewingShift=dataBase.getNextShiftNumber(viewingShift);
				updateUI();
				viewFlipper.showPrevious();
				return true;
			}
		}

		return false;
	}

	public void onLongPress(final MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onScroll(final MotionEvent arg0, final MotionEvent arg1, final float arg2, final float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(final MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(final MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent me) {
		return gestureDector.onTouchEvent(me);
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case EXPORT_WHAT_DIALOG: {
			final CharSequence[] items = {
					  getString(R.string.rawCSVData), 
					  getString(R.string.printableHTML)};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.exportWhereDialogTitle));
			builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					showDialog(EXPORT_DATA_DIALOG);
				break;
				case 1:
					showDialog(EMAIL_OR_FILE_DIALOG);
				break;
				}
			}});
			builder.setIcon(R.drawable.icon);
			return builder.create();
		}
		case EMAIL_OR_FILE_DIALOG:{
			
			AlertDialog.Builder builder;
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.export_csv_how_dialog, (ViewGroup) findViewById(R.id.linearLayout1));
			final Spinner exportTo = (Spinner) layout.findViewById(R.id.spinner1);
			final EditText fileNameEditor = (EditText) layout.findViewById(R.id.editText1);
			fileNameEditor.setText("LogSheet"+""+".html");
			exportTo.setOnItemSelectedListener(new OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					switch (arg2){
					case 0: fileNameEditor.setText("LogSheet"+""+".html");
					        fileNameEditor.setEnabled(false);
						break;
					case 1:  fileNameEditor.setEnabled(true);
						break;
					}
				}
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
			String[] items = new String[] { 
					getString(R.string.email),     //0
					getString(R.string.sdcard)};	//1
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			exportTo.setAdapter(adapter);
			Button exportButton = (Button) layout.findViewById(R.id.button1);
			builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			final AlertDialog dialog = builder.create();
			
			exportButton.setOnClickListener(new OnClickListener(){public void onClick(View v) {
				
				String fileName = "logSheet.html";
				
				if (exportTo.getSelectedItemPosition()==1){
					fileName = fileNameEditor.getEditableText().toString()+".html";
				} else {
					fileName = "LogSheet"+""+".html";
				}
				
				String driverLog = dataBase.getTaxiLogSheet(viewingShift);
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath());
				File file = new File(dir, fileName);
				try {
					FileWriter fstream = new FileWriter(file);
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(driverLog);
					out.flush();
					out.close();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "ERROR:"+e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				if (exportTo.getSelectedItemPosition()==0){
					final Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.cvs_email_subject));
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.email_save_html_warning));
					emailIntent.setType("text/csv");
					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+fileName));	
					emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					dialog.getOwnerActivity().runOnUiThread(new Runnable(){public void run(){
						getApplicationContext().startActivity(emailIntent);	
					}});
				}
				
				
				dialog.dismiss();
			}});
			
			return dialog;
			
		}
		case EXPORT_DATA_DIALOG: {
			final CharSequence[] items = {getString(R.string.today), 
										  getString(R.string.thisWeek), 
										  getString(R.string.ThisMonth), 
										  getString(R.string.ThisYear),
										  getString(R.string.CustomDateRange)};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.selectDateRange));
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	startDate = Calendar.getInstance();
			    	endDate = Calendar.getInstance();
			        Calendar now = Calendar.getInstance();
			        now.setTimeInMillis(System.currentTimeMillis());
			        startDate.setTimeInMillis(System.currentTimeMillis());
			        endDate.setTimeInMillis(System.currentTimeMillis());
			    	switch (item) {
			        case 0: startDate.set(Calendar.HOUR_OF_DAY, 0);
			                endDate.set(Calendar.HOUR_OF_DAY, 0);
			                endDate.add(Calendar.DAY_OF_YEAR, 1);
			                showDialog(EXPORT_WHERE);
			                break;
			        case 1: getWorkWeekDates(now,startDate,endDate);
			        		showDialog(EXPORT_WHERE);
			        break;
			        case 2: startDate.set(Calendar.DATE, 0);
							endDate.add(Calendar.MONTH, 1);
							startDate.set(Calendar.DATE, 1);
							showDialog(EXPORT_WHERE);
							break;
			        case 3: startDate.set(Calendar.DAY_OF_YEAR, 1);
							endDate.add(Calendar.YEAR, 1);
							endDate.set(Calendar.DAY_OF_YEAR, 0);
							showDialog(EXPORT_WHERE);
							break;
			        case 4: 
			        		getDateRangeDialog(startDate,endDate,new OnDismissListener(){public void onDismiss(DialogInterface dialog) {
			        			showDialog(EXPORT_WHERE);
			        		}});
			        	break;
				    }
			    	
			    }
			});
			builder.setIcon(R.drawable.icon);
			return builder.create();
		}
		case EXPORT_WHERE:{
		
			AlertDialog.Builder builder;
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.export_csv_how_dialog, (ViewGroup) findViewById(R.id.linearLayout1));
			final Spinner exportTo = (Spinner) layout.findViewById(R.id.spinner1);
			final EditText fileNameEditor = (EditText) layout.findViewById(R.id.editText1);
			fileNameEditor.setText("deliveryData.csv");
			exportTo.setOnItemSelectedListener(new OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					switch (arg2){
					case 0: fileNameEditor.setText("deliveryData.csv");
					        fileNameEditor.setEnabled(false);
						break;
					case 1:  fileNameEditor.setEnabled(true);
						break;
					}
				}
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
			String[] items = new String[] { 
					getString(R.string.email),     //0
					getString(R.string.sdcard)};	//1
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			exportTo.setAdapter(adapter);
			Button exportButton = (Button) layout.findViewById(R.id.button1);
			builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			final AlertDialog dialog = builder.create();;
			
			exportButton.setOnClickListener(new OnClickListener(){public void onClick(View v) {
				String csvData = dataBase.getCSVTaxiData(startDate,endDate);
				
				if (exportTo.getSelectedItemPosition()==1){
					fileName = fileNameEditor.getEditableText().toString()+".csv";
				} else {
					fileName = "deliveryData.csv";
				}
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath());
				File file = new File(dir, fileName);
				try {
					FileWriter fstream = new FileWriter(file);
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(csvData);
					out.flush();
					out.close();
					Toast.makeText(getApplicationContext(), getString(R.string.wrote)+" "+fileName+" to SD-Card", Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "ERROR:"+e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				if (exportTo.getSelectedItemPosition()==0){
					final Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.cvs_email_subject));
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
					emailIntent.setType("text/csv");
					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+fileName));	
					emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					dialog.getOwnerActivity().runOnUiThread(new Runnable(){public void run(){
						getApplicationContext().startActivity(emailIntent);	
					}});
				}
				
				
				dialog.dismiss();
			}});
			
			return dialog;
		}
	
		case DIALOG_CONFIRM_DELETE_RECORD: {
			if (recordToDelete > -1) return new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(
					"Delete this record?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int whichButton) {
					//Log.i("Delivery Driver", "User Y/N Delete Order");
					dataBase.delete(recordToDelete);
					updateUI();
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int whichButton) {

					/* User clicked Cancel so do some stuff */
				}
			}).create();
		}
		break;
		case DIALOG_CONFIRM_DELETE_SHIFT: {
			TipTotalData tip = dataBase.getTipTotal(this,DataBase.Shift+"="+viewingShift+" AND "+DataBase.Payed+" >= 0");
			
			return new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(
					"Delete this shift and all "+tip.deliveries+" order records?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int whichButton) {
					dataBase.deleteShift(viewingShift);
					viewingShift=dataBase.getNextShiftNumber(viewingShift);
					//flipViews();
					updateUI();
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int whichButton) {

					/* User clicked Cancel so do some stuff */
				}
			}).create();
		}	
		case GOTO_DATE_DIALOG:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);  
			  
			alert.setTitle("Go To Day");  
			//alert.setMessage("Enter the date you want to show");  
			  
			// Set an EditText view to get user input   
			final DatePicker input = new DatePicker(this);  
			alert.setView(input);  
			  
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
			
				Calendar c = Calendar.getInstance();
				c.set(input.getYear(), input.getMonth(), input.getDayOfMonth());
				
				int shift = dataBase.findShiftForTime(c);
				if (shift>=0){
					viewingShift=shift;
					updateUI();
				}
			  }  
			});  
			  
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
			  public void onClick(DialogInterface dialog, int whichButton) {  
			    // Canceled.  
			  }  
			});  
			  
			return alert.create();
		}
		return null;
	}

	

}