package com.catglo.taxidroid;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import com.catglo.deliveryDatabase.*;

import com.catglo.taxidroid.AddressFinder.OnAddressFound;
import com.catglo.taxidroid.R;
import com.catglo.widgets.AddressAutocomplete;
import com.catglo.widgets.AddressEntryView;
import com.catglo.deliveryDatabase.AddressSuggestior;
import com.catglo.widgets.ButtonPadView;
import com.catglo.deliveryDatabase.StreetList;


class FixedFlipper extends ViewFlipper{
	public FixedFlipper(Context context){
	    super(context);
	}
	
	public FixedFlipper(Context context, AttributeSet attrs){
	    super(context, attrs);
	}
	
	@Override
	protected void onDetachedFromWindow(){
	    try{
	        super.onDetachedFromWindow();
	    }catch(Exception e){
	        super.stopFlipping();
	    }
	}
}


public class NewOrderActivity extends AddEditOrderBaseActivity implements OnGestureListener, OnClickListener {
	protected static final int	VOICE_RECOGNITION_REQUEST_CODE	= 1;
	EditText					orderNumber;
	EditText					orderTime;
	AddressAutocomplete 		orderAddress;
	EditText					orderNotes;
	Button						addOrder;
	int							dataBasePrimaryKey;
	float						deliveryOrder;
	
	int							costDropDownCount;
	private FixedFlipper		vs0;
	public int					viewStage		= 0;
	private ButtonPadView		buttonPad1;

	int							flipStage		= 0;
	private GestureDetector		gestureScanner;
	private OnTouchListener		gestureListener;

	private AddressEntryView	buttonPad2;

	private AddressEntryView	buttonPad3;

	
	boolean						skipAddrScreen	= false;

	ArrayAdapter<String>		costAdapter;
	private StreetList			streetList;
	
	StreetNameInformation streetToEdit;
	private TextWatcher textWatcher;
	private Order order;
	
	static final int EDIT_STREET = 1;
	static final int DELETE_STREET = 2;
	static final int ADD_NEW_STREET = 3;
	static final int DIALOG_EDIT_STREET = 4;
	static final int DIALOG_ADD_STREET = 5;
	

	@Override
	public void onDestroy(){
		
		super.onDestroy();
	}
	
    /** Called when the activity is first created. */
	int repeatPreventCounter1=0;
	private AddressFinder addressFinder;
	private AutoCompleteTextView tripType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
  
       
        
        order = new Order();
    	boolean skipOrderNumberScreen = sharedPreferences.getBoolean("noOrderNumberScreen", true);
		boolean skipPickupAddressScreen = sharedPreferences.getBoolean("noAddressScreen", false);
		boolean skipDropoffAddressScreen = sharedPreferences.getBoolean("noDropoffAddressScreen", false);
		
        
        final FrameLayout root = new FrameLayout(getApplicationContext());
    	root.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	root.setBackgroundResource(R.drawable.blue_gradent_background);// .setBackgroundDrawable(R.drawable.blue_gradent_background);

    	vs0 = new FixedFlipper(getApplicationContext());
    	vs0.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	root.addView(vs0);

    	buttonPad1 = new ButtonPadView(this, null);
    	buttonPad1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	
     	
    	buttonPad2 = new AddressEntryView(this, null, dataBase);
    	buttonPad2.setText(getString(R.string.pickupAddress));
    	buttonPad2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	buttonPad2.setOnNextScreenAction(new Runnable(){public void run() {
    		if (repeatPreventCounter1++==0){
    			showTimeSliderDialog(orderTime,order.time);	
    		}
		}});
    	
    	addressFinder = new AddressFinder(getApplicationContext());
        buttonPad2.customButton.setText(getString(R.string.currentAddress));
        buttonPad2.customButton.setOnClickListener(new OnClickListener(){public void onClick(View v) {
        	buttonPad2.edit.setHint(R.string.lookingUpCurrentAddress);
        	buttonPad2.progressSpinnner.setVisibility(View.VISIBLE);
    		addressFinder.lookup(new OnAddressFound(){public void found(final String result) {
        		runOnUiThread(new Runnable(){public void run() {
                	buttonPad2.edit.setText(result);	
                	buttonPad2.progressSpinnner.setVisibility(View.GONE);
    			}});
            }});
        }});
        buttonPad2.customButton.setVisibility(View.VISIBLE);
        

		
        
         
    	
    	buttonPad3 = new AddressEntryView(this, null, dataBase);
    	buttonPad3.setText(getString(R.string.dropOffAddress));
    	buttonPad3.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        buttonPad3.edit.setHint(getString(R.string.leaveBlankForGPSAddress));
        
    	final LinearLayout lastScreen = (LinearLayout) getLayoutInflater()
    			.inflate(R.layout.new_order_last_screen, null);
    	
    	gestureScanner = new GestureDetector(this);

    	gestureListener = new View.OnTouchListener() {
    		public boolean onTouch(final View v, final MotionEvent event) {
    			if (gestureScanner.onTouchEvent(event)) return true;
    			return false;
    		}
    	};

    	
    	orderNumber = (EditText) lastScreen.findViewById(R.id.OrderNumber);
    	orderTime = (EditText) lastScreen.findViewById(R.id.OrderTime);
    	Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(order.time.getTime());
		orderTime.setText(String.format("%tl:%tM %tp %tA", calendar, calendar, calendar, calendar));
    
		orderAddress = (AddressAutocomplete) lastScreen.findViewById(R.id.OrderAddress);
    	orderAddress.startSuggestor(dataBase);
    	
    	tripType = (AutoCompleteTextView) lastScreen.findViewById(R.id.autoCompleteTextView1);
    	tripType.setAdapter(dataBase.getOrderTypeAdapter());
    	tripType.setOnFocusChangeListener(new OnFocusChangeListener(){public void onFocusChange(View v, boolean hasFocus) {
    		if (hasFocus) {
    			tripType.showDropDown();
    		}
		}});
    	tripType.setOnClickListener(new OnClickListener(){public void onClick(View v) {
    		tripType.showDropDown();
		}});
    	
    	orderNotes = (EditText) lastScreen.findViewById(R.id.OrderNotes);
    	addOrder = (Button) lastScreen.findViewById(R.id.AddOrder);
    	dropOffContainer = (LinearLayout) lastScreen.findViewById(R.id.dropOffContainer);
    	addDropoffRow(R.layout.dropoff_input_row);
    	
    	if (skipOrderNumberScreen){
    		String nnn = sharedPreferences.getString("lastGeneratedOrderNumberString", "1");
    		int num = 0;
    		try {
    			num = new Integer(nnn);
    		} catch (NumberFormatException e){};
    		num++;
    		Editor editor = sharedPreferences.edit();
    		editor.putString("lastGeneratedOrderNumberString", ""+num);
    		editor.commit();
    		orderNumber.setText(""+num);
    	} else {
    		vs0.addView(buttonPad1);
    		buttonPad1.setText(getString(R.string.order_number));
    		final ArrayAdapter<String> adapter = dataBase.getOrderNumberAdapter(getApplicationContext());
    		if (adapter != null) {
    			buttonPad1.setAdapter(adapter);
    		}
    		buttonPad1.setGestureListener(gestureScanner, gestureListener, this);
    		buttonPad1.setParentViewSwitcher(vs0);
    		buttonPad1.setEditor(orderNumber);
    	}
    	
    
    	buttonPad2.setEditor(orderAddress);
    	if (skipPickupAddressScreen==false) {
    		buttonPad2.setParentViewSwitcher(vs0);
    		vs0.addView(buttonPad2);
    		buttonPad2.setGestureListener(gestureScanner, gestureListener, this);

    	}
    	
    	buttonPad3.setEditor(dropOffRows.get(0).address);
    	if (skipDropoffAddressScreen==false) {
    		buttonPad3.setParentViewSwitcher(vs0);
    		vs0.addView(buttonPad3);
    		buttonPad3.setGestureListener(gestureScanner, gestureListener, this);
    	}
	
    	
    	
    	vs0.addView(lastScreen);
    	

    	setContentView(root);
    
    	
    	//if (skipAddrScreen) {
    	//	vs2.showNext();
    	//}

    	// Set the order time to 5 minutes ago
/*    	int min = orderTime.getCurrentMinute();
    	int hr = orderTime.getCurrentHour();
    	if (min < 4) {
    		hr -= 1;
    		if (hr<0)//FIX FOR CRASH 
    			hr=0;//TODO: MAKE it pick yesterday date 
    		min += 55;
    	} else if (min > 5){
    		min -= 5;
    	}
  //  	orderTime.setCurrentMinute(min);
//    	.setCurrentHour(hr);
*/
    	orderTime.setOnTouchListener(new OnTouchListener(){ public boolean onTouch(View arg0, MotionEvent arg1) {
          	showTimeSliderDialog(orderTime,order.time);
  		    return true;
  		}});
          
    	
    	// Edit orders come with the primary key for the database, load the old
    	// record
    	
    	vs0.setAnimateFirstView(true);
    
			
		
		addOrder.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				
				
				order.number = orderNumber.getEditableText().toString();
				order.payed = -1;
				order.payed2 = 0;
				order.address = orderAddress.getEditableText().toString();
				order.apartmentNumber = tripType.getEditableText().toString();
				order.notes = orderNotes.getEditableText().toString();
				
				order.onHold=false;
				order.startsNewRun=false;
				order.arivialTime = new Timestamp(System.currentTimeMillis());
				order.payedTime = new Timestamp(System.currentTimeMillis());
				int pickupPrimaryKey = (int) dataBase.add(order);
				order.primaryKey = pickupPrimaryKey;
				
				for (int i = 0; i < dropOffRows.size(); i++){
					DropOffRow screen = dropOffRows.get(i);
					Calendar now = Calendar.getInstance();
					now.setTimeInMillis(System.currentTimeMillis());
					dataBase.addDropoff(pickupPrimaryKey,screen.address.getEditableText().toString(),now);
				}
				
				//Start a validation thread on the addresses
				SilentAddressValidator addressValidator = new SilentAddressValidator(getApplicationContext());
				addressValidator.lookup(order);
				
				Editor editor = sharedPreferences.edit();
				editor.putString("lastGeneratedOrderNumberString", orderNumber.getEditableText().toString());
				editor.commit();
				setResult(100);
				finish();
			}
		});	
    }
    
    
	
  
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, ADD_NEW_STREET, 0, R.string.add_new_street).setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case ADD_NEW_STREET: 
			showDialog(DIALOG_ADD_STREET);
			return true;
		}
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
			case DIALOG_EDIT_STREET:
				return null;
			case DIALOG_ADD_STREET:
				return null;
		}
		return null;
	}	
	
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
		switch (requestCode) {
		case VOICE_RECOGNITION_REQUEST_CODE:
		       if (intent!=null && resultCode==Activity.RESULT_OK){
		    	   ArrayList<String> matches = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			       Iterator<String> i = matches.iterator();
			       if (i.hasNext()){
			    	   String s = i.next();
			    	   s = buttonPad2.edit.getText().toString()+" "+s;
			    	   buttonPad2.edit.setText(s);
			    	   orderAddress.setText(s);
			       }
		       }
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	// OnGestureListener::
	public boolean onDown(final MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	// OnGestureListener::
	public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
		
		// volocityX is positive for right fling across the portrait screen
		// volocityX is negative for left fling (about -2000)
		if (velocityX < -800) {// Left fling

			vs0.setInAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left_in));
			vs0.setOutAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left_out));
			
			vs0.showNext();
			
			return true;
		}

		if (velocityX > 800) {// Left fling

			vs0.setInAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_right_in));
			vs0.setOutAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_right_out));
			vs0.showPrevious();
		
			return true;
		}
		return false;
	}

	// OnGestureListener::
	public void onLongPress(final MotionEvent e) {
		// TODO Auto-generated method stub

	}

	// OnGestureListener::
	public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	// OnGestureListener::
	public void onShowPress(final MotionEvent e) {
		// TODO Auto-generated method stub

	}

	// OnGestureListener::
	public boolean onSingleTapUp(final MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onClick(final View v) {
		// TODO Auto-generated method stub

	}
	
	protected void onSaveInstanceState (Bundle outState){
		super.onSaveInstanceState(outState);
		String number = buttonPad1.edit.getText().toString();
		String address = buttonPad2.edit.getText().toString();
		String dropOff= buttonPad3.edit.getText().toString();
		
		int displayFrame = vs0.getDisplayedChild();
		outState.putInt("frame", displayFrame);
		
		
		if (displayFrame == 3) {
			number = orderNumber.getText().toString();
	
			address = orderAddress.getText().toString();
			
		}
		
		outState.putString("number", number);
		outState.putString("address", address);
		outState.putString("dropoff", dropOff);
	}
	
	protected void onRestoreInstanceState (Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		int frame = savedInstanceState.getInt("frame");
		for (int i = 0; i < frame; i++){
			vs0.showNext();
		}
		
		buttonPad1.edit.setText(savedInstanceState.getString("number"));
		buttonPad2.edit.setText(savedInstanceState.getString("address"));
		buttonPad3.edit.setTag(savedInstanceState.get("dropoff"));
		
	}
	

	
}