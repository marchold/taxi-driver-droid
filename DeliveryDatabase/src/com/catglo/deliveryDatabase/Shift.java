package com.catglo.deliveryDatabase;

import java.util.Calendar;

public class Shift {
	public Calendar startTime;
	public Calendar endTime;
	public Float payRate;
	public Float payRateOnRun;
	public int odometerAtShiftStart;
	public int odometerAtShiftEnd;
	public int primaryKey;
	public boolean noEndTime=false;
	public Shift(){
		startTime=Calendar.getInstance();
		endTime=Calendar.getInstance();
	}

}
