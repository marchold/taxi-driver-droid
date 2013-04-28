package com.catglo.taxidroid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.catglo.deliveryDatabase.*;


import com.catglo.taxidroid.R;
import com.catglo.deliveryDatabase.StreetList;
import com.catglo.deliveryDatabase.ZipCode;



public class TaxiSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	EditTextPreference			databaseFileCopy;
	CheckBoxPreference			databaseOnSdcard;

	SharedPreferences			prefs;
	private PreferenceScreen	zipCodePrefs;
	private EditTextPreference	newZipCode;
	Pattern						pattern;
	private StreetList	streetList;
	DataBase dataBase;

	@Override
	protected void onDestroy() {
		dataBase.close();
		super.onDestroy();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);

	    if (dataBase == null) {
        	dataBase = new DataBase(getApplicationContext());
        	dataBase.open();
        }
    	
	    
//		InstructionsPreference instructionsAltPay = (InstructionsPreference) getPreferenceScreen().findPreference("instructionsAltPay");
//		instructionsAltPay.
		
		streetList = StreetList.LoadState(getApplicationContext());
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
 	
		if (!zzz_version.isFree) {
			databaseFileCopy = (EditTextPreference) getPreferenceScreen().findPreference("DatabaseFileCopy");
			databaseOnSdcard = (CheckBoxPreference) getPreferenceScreen().findPreference("DatabaseOnSdcard");
	
			databaseFileCopy.setPositiveButtonText("Copy it!");
			databaseFileCopy.setNegativeButtonText("Don't Copy");
			
			/*
			 * databaseFileCopy.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			 * 
			 * public boolean onPreferenceClick(Preference arg0) { // TODO Auto-generated method stub
			 * //arg0.setDefaultValue("/sdcard/DeliveriesDatabase");
			 * 
			 * return false; }
			 * 
			 * });
			 */

			databaseOnSdcard.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				public boolean onPreferenceChange(final Preference preference, final Object newValue) {
					try {
						if ((Boolean) newValue == true) {
							//copy to sdcard
							copyDatabse(getFilesDir() + "/" + DataBase.DATABASE_NAME,
									Environment.getExternalStorageDirectory() + "/" + DataBase.DATABASE_NAME);

						} else {
							//copy from sdcard
							copyDatabse(Environment.getExternalStorageDirectory() + "/" + DataBase.DATABASE_NAME,
									getFilesDir() + DataBase.DATABASE_NAME);

						}
						final Toast toast = Toast.makeText(TaxiSettingsActivity.this.getApplicationContext(),
								"Database file moved! You must restart this app.", Toast.LENGTH_LONG);
						toast.show();
						setResult(400);
						finish();

					} catch (final FileNotFoundException e) {
						final Toast toast = Toast.makeText(TaxiSettingsActivity.this.getApplicationContext(),
								"DID NOT MOVE DATABASE FILE! (file not found error)"+getFilesDir() + "/" + DataBase.DATABASE_NAME+"   "+Environment.getExternalStorageDirectory() + "/" + DataBase.DATABASE_NAME,
								Toast.LENGTH_LONG);
						toast.show();
						return false;
					} catch (final IOException e) {
						final Toast toast = Toast.makeText(TaxiSettingsActivity.this.getApplicationContext(),
								"DID NOT MOVE DATABASE FILE! You may have insufficent space on your sdcard.",
								Toast.LENGTH_LONG);
						toast.show();
						return false;
					}
					return true;
				}

			});

			databaseFileCopy.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				public boolean onPreferenceChange(final Preference preference, final Object newValue) {
					if (prefs.getBoolean("DatabaseOnSdcard", false) == true) {
						final Toast toast = Toast.makeText(TaxiSettingsActivity.this.getApplicationContext(),
								"Can not copy databse already on sdcard", Toast.LENGTH_LONG);
						toast.show();
						return false;
					} else {
						final String val = newValue.toString();
						try {
							copyDatabse(getFilesDir() + "/" + DataBase.DATABASE_NAME,
									Environment.getExternalStorageDirectory() + "/" + val);
							final Toast toast = Toast.makeText(TaxiSettingsActivity.this.getApplicationContext(),
									"successfully copied database to "+Environment.getExternalStorageDirectory() +"/"+ val, Toast.LENGTH_LONG);
							toast.show();
						} catch (final FileNotFoundException e) {
							final Toast toast = Toast.makeText(TaxiSettingsActivity.this.getApplicationContext(),
									"DID NOT BACKUP DATABASE FILE! You may have specified an invalid file name",
									Toast.LENGTH_LONG);
							toast.show();
							return false;
						} catch (final IOException e) {
							final Toast toast = Toast.makeText(TaxiSettingsActivity.this.getApplicationContext(),
									"DID NOT BACKUP DATABASE FILE! You may have insufficent space on your sdcard.",
									Toast.LENGTH_LONG);
							toast.show();
							return false;
						}
					}
					return true;
				}
			});
		}
			



		
	}
	
	void copyDatabse(final String to, final String from) throws FileNotFoundException, IOException {
		final FileInputStream input = new FileInputStream(to);
		final OutputStream myOutput = new FileOutputStream(from);

		// transfer bytes from the inputfile to the outputfile
		final byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		input.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}

}