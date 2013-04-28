package com.catglo.taxidroid;

import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.DropOff;
import com.catglo.deliveryDatabase.Order;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class PaymentDialogNoShow extends PaymentDialogBase {
	 
	
	private int primaryKey;

	Context context;

	public PaymentDialogNoShow(Context context,int key, DropOff dropOff, Order order, Runnable runOnDialogSave) {
		super(context,dropOff,order,runOnDialogSave,R.layout.payment_noshow);
		this.context = context;
		primaryKey = key;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	
		Spinner chargeNoShowSpinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> spinnerValues = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,new String[] 
		                                         {"Charge No Show",
		                                         "Dont Charge No Show Fee"});
		chargeNoShowSpinner.setAdapter(spinnerValues);
		spinnerValues.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
		setTitle(R.string.Payment);
		
		((Button) findViewById(R.id.button1)).setOnClickListener(new Button.OnClickListener(){public void onClick(View v) {
			formToDropOff();
			dismiss();
			if (runOnDialogSave != null){
				runOnDialogSave.run();
			}
		}});
		
		
	}
}
