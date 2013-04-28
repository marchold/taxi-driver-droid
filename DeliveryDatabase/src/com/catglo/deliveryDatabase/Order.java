package com.catglo.deliveryDatabase;


import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.database.Cursor;

public class Order extends Object {
	public static final int	CASH	= 0;
	public static final int	CHECK	= 1;
	public static final int	CREDIT	= 2;
	public static final int	EBT	= 3;
	public static final int	NOT_PAID	= -1;
	
	public static final int PAYMENTSTATUS_NOTPAID  = -1; //For taxi droid the payment field is a status field
	public static final int PAYMENTSTATUS_PAID     =  0;
	public static final int PAYMENTSTATUS_NOSHOW   =  1;
	public static final int PAYMENTSTATUS_CANCELED =  2;
	
	public boolean	onHold;
	

	public static long GetTimeFromString(final String s) {
		try {
			return GetTimeFromString(s, "yyyy-MM-dd H:mm:ss");
		} catch (ParseException e) {
			try {
				return GetTimeFromString(s, "yyyy-MM-dd");
			} catch (ParseException e1) {
				throw new IllegalStateException();
			}
		}
	}
	
	public int getMinutesAgo(){
		return (int) ((System.currentTimeMillis() - time.getTime()) / 1000) / 60;
	}

	static long GetTimeFromString(final String s, final String format) throws ParseException {
		final SimpleDateFormat formatter = new SimpleDateFormat(format);
		long t;
		try {
			t = formatter.parse(s).getTime();
		} catch (NullPointerException e){
			e.printStackTrace();
			t = System.currentTimeMillis();
		}
		return t;
	}

	// Constructor for data from the sql database
	public Order(final Cursor c) {
		number = c.getString(c.getColumnIndex(DataBase.OrderNumber));
		cost = c.getFloat(c.getColumnIndex(DataBase.Cost));
		address = c.getString(c.getColumnIndex(DataBase.Address));
		notes = c.getString(c.getColumnIndex(DataBase.Notes));
		String s = c.getString(c.getColumnIndex(DataBase.Time));
		time = new Timestamp(GetTimeFromString(s));
		
		payed = c.getFloat(c.getColumnIndex(DataBase.Payed));
		payed2 = c.getFloat(c.getColumnIndex(DataBase.PayedSplit));
		deliveryOrder = c.getFloat(c.getColumnIndex(DataBase.DeliveryOrder));
		primaryKey = c.getInt(c.getColumnIndex("ID"));
		paymentType = c.getInt(c.getColumnIndex(DataBase.PaymentType));
		paymentType2 = c.getInt(c.getColumnIndex(DataBase.PaymentType2));
		
		apartmentNumber = c.getString(c.getColumnIndex(DataBase.AptNumber));
		if (apartmentNumber==null)
			apartmentNumber="";
		
		int bool = c.getInt(c.getColumnIndex(DataBase.OutOfTown));
		if (bool==0){
			outOfTown1=false;
		} else {
			outOfTown1=true;
		}
		
		bool = c.getInt(c.getColumnIndex(DataBase.OutOfTown2));
		if (bool==0){
			outOfTown2=false;
		} else {
			outOfTown2=true;
		}
		
		bool = c.getInt(c.getColumnIndex(DataBase.OutOfTown3));
		if (bool==0){
			outOfTown3=false;
		} else {
			outOfTown3=true;
		}
		
		bool = c.getInt(c.getColumnIndex(DataBase.OutOfTown4));
		if (bool==0){
			outOfTown4=false;
		} else {
			outOfTown4=true;
		}
			
		bool = c.getInt(c.getColumnIndex(DataBase.OnHold));
		if (bool==0){
			onHold=false;
		} else {
			onHold=true;
		}	
		
		bool = c.getInt(c.getColumnIndex(DataBase.StartsNewRun));
		if (bool==0){
			startsNewRun=false;
		} else {
			startsNewRun=true;
		}
		
		float lng = c.getFloat(c.getColumnIndex("GPSLng"));
		float lat = c.getFloat(c.getColumnIndex("GPSLat"));
		geoPoint =new GeoPoint(
				   (int)(lat*1000000),
				   (int)(lng*1000000));
		
		
		
		bool = c.getInt(c.getColumnIndex("validatedAddress"));
		if (bool==0){
			isValidated=false;
		} else {
			isValidated=true;
		}
		
		try {
			arivialTime = new Timestamp(GetTimeFromString(c.getString(c.getColumnIndex(DataBase.ArivalTime))));
			payedTime = new Timestamp(GetTimeFromString(c.getString(c.getColumnIndex(DataBase.PaymentTime))));
		} catch (final RuntimeException e) {
			arivialTime = new Timestamp(System.currentTimeMillis());
			payedTime = new Timestamp(System.currentTimeMillis());
		}
		
		bool = c.getInt(c.getColumnIndex("StreetHail"));
		if (bool==0){
			streetHail = false;
		} else {
			streetHail = true;
		}
		
	}

	public Order() {
		time =  new Timestamp(System.currentTimeMillis());
	}

	private final NumberFormat	format	= new DecimalFormat("00");

	public String getListText() {
		int hours = time.getHours();
		String amPm;
		if (hours > 12) {
			amPm = new String("pm");
			hours -= 12;
		} else {
			amPm = new String("am");
		}
		return String.format("%d:%s%s\t\t$%.2f\n%s", hours, format.format(time.getMinutes()), amPm, cost, address);
	}

	public float getTimeAsFloat() {
		float time = 0;
		time = this.time.getHours() + this.time.getMinutes() / 100f;
		return time;
	}

	public String		number;
	public Timestamp	time;
	public float		cost;
	public String		address;
	public String       apartmentNumber;
	public String		notes;
	public float		payed;
	public float		payed2;
	public float		deliveryOrder;
	public int			primaryKey;
	public int			paymentType;
	public int			paymentType2;
	public Timestamp	arivialTime;
	public Timestamp	payedTime;    //For taxi droid this is the time the coustomer got in the cab (or commited to paying)
	public boolean      outOfTown1;
	public boolean      outOfTown2;
	public boolean      outOfTown3;
	public boolean      outOfTown4;
	public boolean		startsNewRun;
	public GeoPoint		geoPoint;
	public boolean		isValidated;
	
	public String 	distance; //Not saved in db
	public String	travelTime;
	
	public boolean streetHail;
	
	boolean hasHistory=false;
	public TipTotalData tipTotalsForThisAddress = new TipTotalData();
	//float[] last5Tips= new float[5];
	
	public ArrayList<DropOff> dropOffs = new ArrayList<DropOff>();
	

}
