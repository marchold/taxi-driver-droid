package com.catglo.taxidroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.catglo.deliveryDatabase.Shift;
import com.catglo.widgets.ButtonPadView;

public class OdometerEntryActivity extends TaxiDroidBaseActivity {
	private ButtonPadView odoPad;
	private int dataBasePrimaryKey;
	private boolean isStartShift;
	private Shift shift;
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        odoPad = new ButtonPadView(this, null);
		setContentView(odoPad);

        final Intent i = getIntent();
		dataBasePrimaryKey = i.getIntExtra("ID", -1);
		isStartShift = i.getBooleanExtra("startValue", true);
		
		dataBase.createShiftRecordIfNonExists();
		shift = dataBase.getShift(dataBasePrimaryKey);
		
        odoPad = new ButtonPadView(this, null);
		setContentView(odoPad);

		odoPad.setText("Odometer Reading");

		ArrayAdapter<String> adapter = dataBase.getOdometerPredtion();
		if (adapter!=null)
			odoPad.list.setAdapter(adapter);
		
		odoPad.next.setOnClickListener(new OnClickListener(){public void onClick(View v) {
			try {
				int value = new Integer(odoPad.edit.getEditableText().toString());
				if (isStartShift){
					shift.odometerAtShiftStart = value;
				} else {
					shift.odometerAtShiftEnd = value;
				}
				dataBase.saveShift(shift);
				finish();
			} catch (NumberFormatException e){
				e.printStackTrace();
			};
		}});
		
		
		/*
		startNew.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (end.isChecked()){
						dataBase.setThisShiftOdometerEnd(odoPad.edit.getText().toString());
					}
					if (start.isChecked()){
						dataBase.setThisShiftOdometerStart(odoPad.edit.getText().toString());
					}
					
					odoPad.edit.setText(new String(""));
				}
			}}
		);
	
		start.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (end.isChecked()){
						dataBase.setThisShiftOdometerEnd(odoPad.edit.getText().toString());
					}
					
					long odo = dataBase.getThisShiftOdomenterStart();
					odoPad.edit.setText(new String(""+odo));
				}
			}}
		);
		
		end.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (start.isChecked()){
						dataBase.setThisShiftOdometerStart(odoPad.edit.getText().toString());
					}
					long odo = dataBase.getThisShiftOdomenterEnd();
					odoPad.edit.setText(new String(""+odo));
				}
			}}
		);
		*/
	

      
    }


   /* protected void setOdometerValue() {
		if (startNew.isChecked()){
			dataBase.setNextShift();
			dataBase.setThisShiftOdometerStart(odoPad.edit.getText().toString());
		} else if (start.isChecked()) {
			dataBase.setThisShiftOdometerStart(odoPad.edit.getText().toString());
		} else if (end.isChecked()){
			dataBase.setThisShiftOdometerEnd(odoPad.edit.getText().toString());
		}
	}*/
}