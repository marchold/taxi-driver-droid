package com.catglo.taxidroid;

import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.DropOff;
import com.catglo.deliveryDatabase.Order;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.TextView;


public class PaymentDialogBase extends Dialog {
	 

	protected TextView extraLabel;
	protected EditText extraField;
	protected EditText paymentAmount;
	protected Runnable runOnDialogSave;
	protected int layoutId;
	protected DropOff dropOff;
	private Order order;

	public PaymentDialogBase(Context context, DropOff dropOff, Order order, Runnable runOnDialogSave,int layoutId) {
		super(context);
		this.dropOff = dropOff;
		this.order = order;
		this.runOnDialogSave = runOnDialogSave;
		this.layoutId = layoutId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(layoutId);
		
		
		paymentAmount = (EditText)findViewById(R.id.editText2);
		
		extraLabel = (TextView) findViewById(R.id.textView3);
		extraField = (EditText) findViewById(R.id.editText3);
		extraLabel.setVisibility(View.GONE);
		extraField.setVisibility(View.GONE);
		dropOff.paymentType = AddEditOrderBaseActivity.CASH_PAYMENT;

		
		RadioButton cashButton = (RadioButton) findViewById(R.id.radio0);
		cashButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked){
				extraLabel.setVisibility(View.GONE);
				extraField.setVisibility(View.GONE);
				dropOff.paymentType = AddEditOrderBaseActivity.CASH_PAYMENT;
			}
		}});
		
		RadioButton creditButton = (RadioButton) findViewById(R.id.radio1);
		creditButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked){
				extraLabel.setVisibility(View.VISIBLE);
				extraField.setVisibility(View.VISIBLE);
				extraLabel.setText(R.string.AuthorizationNumber);
				dropOff.paymentType = AddEditOrderBaseActivity.CREDIT_PAYMENT;
			}
		}});
		
		RadioButton accountButton = (RadioButton) findViewById(R.id.radio2);
		accountButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked){
				extraLabel.setVisibility(View.VISIBLE);
				extraField.setVisibility(View.VISIBLE);
				extraLabel.setText(R.string.account);
				dropOff.paymentType = AddEditOrderBaseActivity.ACCOUNT_PAYMENT;
			}
		}});
		
		
	}
	
	void formToDropOff(){
         try {
        	 dropOff.payment = new Float(paymentAmount.getEditableText().toString());
         } catch  (NumberFormatException e){
        	 dropOff.payment = 0f;
         }
        
	}
}
