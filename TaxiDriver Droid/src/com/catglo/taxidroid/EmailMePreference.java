package com.catglo.taxidroid;

import android.content.Context;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class EmailMePreference extends EditTextPreference {

	Context	context;

	public EmailMePreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onClick() {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "goblets@gmail.com" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Taxi Droid Feedback");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

}