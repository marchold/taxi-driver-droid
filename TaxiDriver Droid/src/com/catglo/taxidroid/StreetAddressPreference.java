package com.catglo.taxidroid;

import com.catglo.widgets.AddressAutocomplete;

import android.content.Context;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class StreetAddressPreference extends EditTextPreference {

	Context	context;
	public AddressAutocomplete autocomplete;

	public StreetAddressPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	
		autocomplete = new AddressAutocomplete(context, attrs);
	}
	
	@Override
	protected void onBindDialogView(View view) {
	    AddressAutocomplete editText = autocomplete;
	    editText.setText(getText());

	    ViewParent oldParent = editText.getParent();
	    if (oldParent != view) {
	        if (oldParent != null) {
	            ((ViewGroup) oldParent).removeView(editText);
	        }
	        onAddEditTextToDialogView(view, editText);
	    } 
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    if (positiveResult) {
	        String value = autocomplete.getText().toString();
	        if (callChangeListener(value)) {
	            setText(value);
	        }
	    }
	}

}