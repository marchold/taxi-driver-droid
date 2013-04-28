package com.catglo.deliveryDatabase;

import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;


import com.google.android.maps.GeoPoint;



import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class DataBase extends Object  {
	// public static final String KEY_ROWID = "_id";
	// public static final String KEY_ISBN = "isbn";
	// public static final String KEY_TITLE = "title";
	// public static final String KEY_PUBLISHER = "publisher";
	//private static final String			TAG					= "DataBase";

	public static final String			OrderNumber			= "OrderNumber";
	public static final String			Address				= "Address";
	public static final String			Cost				= "Cost";
	public static final String			Time				= "Time";
	public static final String			Notes				= "Notes";
	public static final String			Payed				= "Payed";
	public static final String			PayedSplit			= "PayedSplit";
	public static final String			DeliveryOrder		= "DeliveryOrder";
	public static final String			Shift				= "Shift";
	public static final String			PaymentType			= "PaymentType";
	public static final String			PaymentType2		= "PaymentType2";
	public static final String			ArivalTime			= "ArivialTime";
	public static final String			PaymentTime			= "PaymentTime";
	public static final String			RunNumber			= "RunNumber";
	public static final String			OutOfTown			= "OutOfTown";
	public static final String			OnHold   			= "OnHold";
	public static final String			OutOfTown2          = "OutOfTown2"; 
	public static final String			OutOfTown3          = "OutOfTown3"; 
	public static final String			OutOfTown4          = "OutOfTown4";
	public static final String			AptNumber           = "AptNumber";
	
	
	public static final String			ODO_START			= "OdomoterStart";
	public static final String			ODO_END				= "OdomoterEnd";
	public static final String			TIME_START			= "TimeStart";
	public static final String			TIME_END			= "TimeEnd";
	public static final String			StartsNewRun        = "StartsNewRun";
	public static final String			PAY_RATE            = "PAY_RATE";
	public static final String			PAY_RATE_ON_RUN     = "PAY_RATE_ON_RUN";
	

	public static final String			DATABASE_NAME		= "DeliveryData.SQLite";
	private static final String			DATABASE_TABLE		= "orders";
	private static final int			DATABASE_VERSION	= 7;

	private static int					TodaysShiftCount	= -1;
	
	static boolean justCreated=false;
	
	private static final String			DATABASE_CREATE		= 
		"CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + " ("
			+ "ID integer primary key autoincrement,"
			+ OrderNumber + "   VARCHAR, " 
			+ Address     + "   VARCHAR, " 
			+ Cost   + "        FLOAT, "
			+ Time + "          TIMESTAMP, " 
			+ Notes+ "          VARCHAR, " 
			+ Payed + "         FLOAT,"
			+ PayedSplit + "    FLOAT,"
			+ DeliveryOrder + " FLOAT,"
			+ Shift + "         INT," 
			+ PaymentType + "   INT,"
			+ PaymentType2 + "  INT,"
			+ ArivalTime + "    TIMESTAMP," 
			+ PaymentTime + "   TIMESTAMP," 
			+ RunNumber + "     INT,"
			+ OutOfTown +"      BOOLEAN,"
			+ StartsNewRun +"   BOOLEAN," 
		    + OutOfTown2+"      BOOLEAN," 
		    + OutOfTown3+"      BOOLEAN," 
		    + OutOfTown4+"      BOOLEAN,"
		    + AptNumber+"       VARCHAR,"
			+"GPSLat            FLOAT, "
			+"GPSLng            FLOAT, "
		    +"validatedAddress  BOOLEAN,"
		    +"StreetHail        BOOLEAN,"
		    + OnHold +"         BOOLEAN);"
			
			//Things to add to orders
			//Apartment #
			//Alt Tip 2
			//Alt Tip 3
			//Alt Tip 4
			
			
			//Things to add to shifts
			//Pay rate that shift
			//On delivery pay rate that shift
			
			/* +

			"CREATE TABLE IF NOT EXISTS shifts (ID integer primary key autoincrement,"
			+ ODO_START + "  INTEGER, " 
			+ ODO_END + "    INTEGER, " 
			+ TIME_START + " INTEGER, "
			+ TIME_END + "   INTEGER);"*/;

	private final Context				context;

//	private final DatabaseHelper		databaseHelper;
	private SQLiteDatabase				db = null;

	private SharedPreferences	prefs;

	private static int					dataBaseInitLock	= 0;

	static File							path;

	public void init(File path){
		try {
			db = SQLiteDatabase.openDatabase(path.toString(), null, SQLiteDatabase.OPEN_READWRITE);
			justCreated=false;
		} catch (final SQLiteException ex) {
			db = SQLiteDatabase.openDatabase(path.toString(), null, SQLiteDatabase.CREATE_IF_NECESSARY
					| SQLiteDatabase.OPEN_READWRITE);
			onCreate(db);
		} finally {
			if (db != null) {
				db.close();
				db=null;
			}
		}
	}
	
	
	public DataBase(final Context ctx) {
		this.context = ctx;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		if (prefs.getBoolean("DatabaseOnSdcard", false) == true) {
			path = Environment.getExternalStorageDirectory();
		} else {
			path = context.getFilesDir();
		}

		path = new File(path, DATABASE_NAME);
		
		init(path);
		
	}
	

	public DataBase(final Context context,String file){
		this.context=context;
		path = Environment.getExternalStorageDirectory();
		path = new File(path, file);
		init(path);
	}
	
	public void onCreate(final SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL("CREATE TABLE IF NOT EXISTS shifts (ID integer primary key autoincrement,"
				+ ODO_START + "  INTEGER, " 
				+ ODO_END + "    INTEGER, "
				+ PAY_RATE + "   FLOAT,"
				+ PAY_RATE_ON_RUN+" FLOAT,"
				+ TIME_START + " INTEGER, "
				+ TIME_END + "   INTEGER);");
		
		database.execSQL("CREATE TABLE IF NOT EXISTS dropOffs (ID integer primary key autoincrement,"
				+ "dropOffAddress	VARCHAR, " 
				+ "pickupId         INTEGER, "
				+ "payment          FLOAT, "
				+ "paymentType      INT,"
				+ "meterAmount      FLOAT,"
				+ "account          VARCHAR,"  //Account or credit card authorization
				+ "authorization    VARCHAR,"
				+ "dropOffTime      TIMESTAMP);");
		
		database.execSQL("CREATE TABLE IF NOT EXISTS streetNames (ID integer primary key autoincrement,"
				+ "streetName		VARCHAR);");
		
		database.execSQL("CREATE TABLE IF NOT EXISTS expenses (ID integer primary key autoincrement,"
				+ "description   	VARCHAR, " 
				+ "category         VARCHAR, "
				+ "amount           FLOAT, "
				+ "reimbursable     BOOLEAN,"
				+ "reimbursed       BOOLEAN,"
				+ "shiftId          INT,"
				+ "expenseTime      TIMESTAMP);");
		
		
		justCreated=true;
		database.setVersion(DATABASE_VERSION);
	}
	
	public void MergeDatabase(DataBase otherDatabase){
		//Next import all the Shift records as is and record the 1st id number to offset the Orders shift ID
		String query = "SELECT * FROM shifts";
		Cursor c = otherDatabase.db.rawQuery(query, null);
		final int NOTHING=-1;
		int firstInsertedShift=NOTHING;
		int firstInsertedOrder=NOTHING;
		if (c != null && c.moveToFirst()) {
			do {
				final ContentValues init = new ContentValues();
				
				init.put(ODO_START,c.getInt(c.getColumnIndex(ODO_START)));
				init.put(ODO_END  ,c.getInt(c.getColumnIndex(ODO_END)));
				init.put(TIME_END ,c.getInt(c.getColumnIndex(TIME_END)));
				init.put(TIME_START,c.getInt(c.getColumnIndex(TIME_START)));
				if (c.getColumnIndex(PAY_RATE)!=-1) init.put(PAY_RATE ,c.getFloat(c.getColumnIndex(PAY_RATE)));
				if (c.getColumnIndex(PAY_RATE_ON_RUN)!=-1) init.put(PAY_RATE_ON_RUN, c.getFloat(c.getColumnIndex(PAY_RATE_ON_RUN)));
				
				int insertedKey = (int) db.insertOrThrow("shifts", null, init);
				if (firstInsertedShift==NOTHING){
					firstInsertedShift=insertedKey;
				}
				
			} while (c.moveToNext());
		}
		c.close();
		if (firstInsertedShift==NOTHING){
			return; //TODO: Toast the user about the error
		}
		
		//Next import all the Orders offsetting the Shift id
		query = "SELECT * FROM "+DATABASE_TABLE;
		c = otherDatabase.db.rawQuery(query, null);
		if (c != null && c.moveToFirst()) {
			do {
				final ContentValues init = new ContentValues();
				init.put(OrderNumber,c.getString(c.getColumnIndex(OrderNumber)));
				init.put(Address,c.getString(c.getColumnIndex(Address)));
				init.put(Cost,c.getFloat(c.getColumnIndex(Cost)));
				init.put(Time,c.getString(c.getColumnIndex(Time)));
				init.put(Notes,c.getString(c.getColumnIndex(Notes)));
				init.put(Payed, c.getFloat(c.getColumnIndex(Payed)));
				if (c.getColumnIndex(PayedSplit)!=-1) init.put(PayedSplit, c.getFloat(c.getColumnIndex(PayedSplit)));
				init.put(DeliveryOrder, c.getFloat(c.getColumnIndex(DeliveryOrder)));
				int shift = c.getInt(c.getColumnIndex(Shift));
				shift+=firstInsertedShift;
				init.put(Shift, shift);
				init.put(PaymentType, c.getInt(c.getColumnIndex(PaymentType)));
				if (c.getColumnIndex(PaymentType2)!=-1) init.put(PaymentType2, c.getInt(c.getColumnIndex(PaymentType2)));
				init.put(ArivalTime,c.getString(c.getColumnIndex(ArivalTime)));
				init.put(PaymentTime,c.getString(c.getColumnIndex(PaymentTime)));
				if(c.getColumnIndex(RunNumber)!=-1) init.put(RunNumber,c.getString(c.getColumnIndex(RunNumber)));
				if(c.getColumnIndex(OutOfTown)!=-1) init.put(OutOfTown, c.getInt(c.getColumnIndex(OutOfTown)));
				if(c.getColumnIndex(StartsNewRun)!=-1) init.put(StartsNewRun, c.getInt(c.getColumnIndex(StartsNewRun)));
				if(c.getColumnIndex(OutOfTown2)!=-1) init.put(OutOfTown2, c.getInt(c.getColumnIndex(OutOfTown2)));
				if(c.getColumnIndex(OutOfTown3)!=-1) init.put(OutOfTown3, c.getInt(c.getColumnIndex(OutOfTown3)));
				if(c.getColumnIndex(OutOfTown4)!=-1) init.put(OutOfTown4, c.getInt(c.getColumnIndex(OutOfTown4)));
				if(c.getColumnIndex(AptNumber)!=-1) init.put(AptNumber,c.getString(c.getColumnIndex(AptNumber)));
				if(c.getColumnIndex("GPSLat")!=-1) init.put("GPSLat",c.getFloat(c.getColumnIndex("GPSLat")));
				if(c.getColumnIndex("GPSLat")!=-1) init.put("GPSLat",c.getFloat(c.getColumnIndex("GPSLat")));
				if(c.getColumnIndex("StreetHail")!=-1) init.put("StreetHail",c.getFloat(c.getColumnIndex("StreetHail")));
				if (c.getColumnIndex("validatedAddress")!=-1) init.put("validatedAddress",  c.getInt(c.getColumnIndex("validatedAddress")));
				if (c.getColumnIndex(OnHold)!=-1) init.put(OnHold,  c.getInt(c.getColumnIndex(OnHold)));
				
				int insertedKey = (int) db.insertOrThrow(DATABASE_TABLE, null, init);
				if (firstInsertedOrder==NOTHING){
					firstInsertedOrder=insertedKey;
				}
				
			} while (c.moveToNext());		
		}
		c.close();
	}
	
	
	
	private static String[]	orderCosts		= null;
	//private static int		orderCostLock	= 0;
	private static String[]	orderNumbers	= null;
	public static String	orderNumberPrefix;

	private void generateOrderNumbers() {
		String nnn = prefs.getString("lastGeneratedOrderNumberString", "1");
		int number=0;
		try {
			number= new Integer(nnn);
		} catch (NumberFormatException e){
			
		}
		
		orderNumbers = new String[50];
		synchronized (this) {
			for (int j = 0; j < 50; j++) {
				orderNumbers[j] = String.format("%d", number + j + 1);
			}
		}
	}


	
	public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
		if (oldVersion<2 && oldVersion!=0){ //Version 3 is sometimes called version 0
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+PayedSplit+" FLOAT");
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+PaymentType2+" INT");	
		}
		if (oldVersion<3){
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+ OutOfTown +"      BOOLEAN");
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+ OnHold +"         BOOLEAN");
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+ StartsNewRun +"   BOOLEAN");
		}
		if (oldVersion<4){
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+ OutOfTown2+"      BOOLEAN");
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+ OutOfTown3+"      BOOLEAN"); 
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+ OutOfTown4+"      BOOLEAN");
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD "+ AptNumber+"       VARCHAR");
			
			database.execSQL("ALTER TABLE shifts ADD "+ PAY_RATE + "   FLOAT");
			database.execSQL("ALTER TABLE shifts ADD "+ PAY_RATE_ON_RUN+" FLOAT");	
		}
		if (oldVersion<5){
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD GPSLat            FLOAT");
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " Add GPSLng            FLOAT"); 
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD validatedAddress  BOOLEAN");
		}
		if (oldVersion<6){
			database.execSQL("ALTER TABLE  " + DATABASE_TABLE + " ADD StreetHail         BOOLEAN");
		}
		if (oldVersion<7){
			database.execSQL("CREATE TABLE IF NOT EXISTS expenses (ID integer primary key autoincrement,"
					+ "description   	VARCHAR, " 
					+ "category         VARCHAR, "
					+ "amount           FLOAT, "
					+ "reimbursable     BOOLEAN,"
					+ "reimbursed       BOOLEAN,"
					+ "shiftId          INT,"
					+ "expenseTime      TIMESTAMP);");
		}
		
		database.setVersion(newVersion);
	}
	
	

	// ---opens the database---
	public DataBase open() throws SQLException {
		if (db==null){
			db = SQLiteDatabase.openDatabase(path.toString(), null, SQLiteDatabase.OPEN_READWRITE);
			if (db.getVersion() < DATABASE_VERSION) {
				onUpgrade(db, db.getVersion(), DATABASE_VERSION);
			}
			if (justCreated){ 
				TodaysShiftCount=1;
				return this;
			}
			if (TodaysShiftCount == -1) {
				getCurShift();
				if (TodaysShiftCount==0){
					createShiftRecordIfNonExists();
				}
			}
		}
		return this;
	}

	public synchronized ArrayAdapter<String> getOrderNumberAdapter(final Context context) {
		if (orderNumbers == null) 
			generateOrderNumbers();
		return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, orderNumbers);
	}
	

	
	synchronized String getOrderNumberPrefix() {
		if (orderNumbers[0] == null) return new String("");
		String prefix = new String(orderNumbers[0]);
		if (prefix.length() > 2) {
			prefix = prefix.substring(0, prefix.length() / 2);
		}
		return prefix;
	}

	// ---closes the database---
	public void close() {
		if (db != null) {
			db.close();
			db=null;
		}
	}

	public long add(Expense expense){
		final ContentValues initialValues = new ContentValues();
		initialValues.put("description",  expense.description);
		initialValues.put("category",     expense.category);
		initialValues.put("amount",       expense.amount);
		initialValues.put("reimbursable", expense.reimbursable);
		initialValues.put("reimbursed",   expense.reimbursed);
		initialValues.put("shiftId",      expense.shiftId);
		initialValues.put("expenseTime",  GetDateString(expense.expenseTime));
		final long addedRow = db.insertOrThrow("expenses", null, initialValues);
		return addedRow;
	}
	
	public boolean update(Expense expense){
		final ContentValues args = new ContentValues();
		args.put("description",  expense.description);
		args.put("category",     expense.category);
		args.put("amount",       expense.amount);
		args.put("reimbursable", expense.reimbursable);
		args.put("reimbursed",   expense.reimbursed);
		args.put("shiftId",      expense.shiftId);
		args.put("expenseTime",  GetDateString(expense.expenseTime));
		final boolean retVal = db.update("expenses", args, expense.ID + "= ID", null) > 0;
		return retVal;
	}
	
	public boolean delete(Expense expense){
		return db.delete(DATABASE_TABLE, "ID" + "=" + expense.ID, null) > 0;
	}
	

	
	public ArrayList<Expense> getShiftExpenses(int shiftId){
		String query = "SELECT * FROM expenses WHERE shiftId = "+shiftId+" ORDER BY expenseTime";
		Log.d("DRIVER",query);
		final Cursor c = db.rawQuery(query, null);
		ArrayList<Expense> returnList = new ArrayList<Expense>();
		if (c.moveToFirst()) {
			do {
				Expense e = new Expense();
				e.description = c.getString(c.getColumnIndex("description"));
				e.category = c.getString(c.getColumnIndex("category"));
				e.amount = c.getFloat(c.getColumnIndex("amount"));
				
				int r = c.getInt(c.getColumnIndex("reimbursable"));
				if (r==0){
					e.reimbursable = false;
				} else {
					e.reimbursable = true;
				}
				
				r = c.getInt(c.getColumnIndex("reimbursed"));
				if (r==0){
					e.reimbursed = false;
				} else {
					e.reimbursed = true;
				}
				e.shiftId = c.getInt(c.getColumnIndex("shiftId"));
				e.expenseTime.setTimeInMillis(Order.GetTimeFromString(c.getString(c.getColumnIndex("expenseTime"))));
				returnList.add(e);
			} while (c.moveToNext());
		}
		c.close();
		return returnList;
	}
	
	public ArrayList<String> getExpensCategories(){
		String query = "SELECT DISTINCT category FROM expenses ORDER BY category";
		Log.d("DRIVER",query);
		final Cursor c = db.rawQuery(query, null);
		ArrayList<String> returnList = new ArrayList<String>();
		if (c.moveToFirst()) {
			do {
				returnList.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
		return returnList;
	}

	public float getTotalExpensesForShift(int shiftId){
		String query = "SELECT SUM(amount) FROM expenses WHERE shiftId = "+shiftId;
		final Cursor c = db.rawQuery(query, null);
		float retVal = 0f;
		if (c.moveToFirst()){
			retVal = c.getFloat(0);
		}
		c.close();
		return retVal;
	}
	
	public long addDropoff(int pickupId, String address, Calendar time){
		final ContentValues initialValues = new ContentValues();
		initialValues.put("pickupId", 		pickupId);
		initialValues.put("dropOffAddress", address);
		initialValues.put("dropOffTime", 	GetDateString(time));
		final long addedRow = db.insertOrThrow("dropOffs", null, initialValues);
		return addedRow;
	}
	
	// Modifies the fields in the order
	public boolean editDropOff(int orderId, String address) {
		final ContentValues args = new ContentValues();
		args.put("dropOffAddress", address);
		final boolean retVal = db.update("dropOffs", args, orderId + "= ID", null) > 0;
		return retVal;
	}
	
	
	public boolean updateDropOff(int primaryKey, int paymentType, String meter,String payment, String extra, Order order) {
		final ContentValues args = new ContentValues();
		float paymentValue=0f;
		float meterValue=0;
		try {
			paymentValue = new Float(payment);
		} catch(NumberFormatException e){e.printStackTrace();};
		try {
			meterValue =  new Float(meter);
		} catch(NumberFormatException e){e.printStackTrace();}; 
		
		args.put("meterAmount",meterValue);
		args.put("payment", paymentValue);		
		args.put("paymentType", paymentType);
		args.put("account", extra);
		
		final boolean retVal = db.update("dropOffs", args, primaryKey + "= ID", null) > 0;
		return retVal;		
	}
	
	public boolean updateDropOff(DropOff dropOff, Order order) {
		final ContentValues args = new ContentValues();
		float meterValue=0;
		try {
		args.put("payment", new Float(dropOff.payment));
		} catch(NumberFormatException e){e.printStackTrace();};
		args.put("paymentType", dropOff.paymentType);
		try {
			meterValue =  new Float(dropOff.meterAmount);
		} catch(NumberFormatException e){e.printStackTrace();}; 
		
		args.put("account", dropOff.account);
		args.put("dropOffAddress", dropOff.address);
		args.put("meterAmount",meterValue);
		
		final boolean retVal = db.update("dropOffs", args, dropOff.id + "= ID", null) > 0;
		return retVal;		
	}
	
	// Add a new delivery order to the database
	// - It is necessary to determine the sort order when we add it
	public long add(final Order order) {
		final Cursor c = db.query(DATABASE_TABLE, new String[] { Time, DeliveryOrder }, "payed='-1'", null, null, null,
				Time);
		float orderOfNextSmallest = -1;
		long timeOfNextSmallest = Long.MAX_VALUE;

		float orderOfNextBiggest = -1;
		long timeOfNextBiggest = Long.MIN_VALUE;

		float orderOfSmallest = -1;
		long timeOfSmallest = Long.MAX_VALUE;

		float orderOfBiggest = -1;
		long timeOfBiggest = Long.MIN_VALUE;

		float myListOrder = -1;

		if (c != null) {
			if (c.moveToFirst()) {
				do {
					final long t = Order.GetTimeFromString(c.getString(c.getColumnIndex(DataBase.Time)));
					final float o = c.getFloat(c.getColumnIndex(DataBase.DeliveryOrder));
					if (order.time.getTime() > t) // if the new order time is
					// bigger than the order we
					// are looking at
					{
						if (timeOfNextBiggest < t) { // if the last biggest is
							// smaller than the time
							// we are looking at
							timeOfNextBiggest = t;
							orderOfNextBiggest = o;
						}
					}

					if (order.time.getTime() < t) {
						if (timeOfNextSmallest > t) {
							timeOfNextSmallest = t;
							orderOfNextSmallest = o;
						}
					}

					if (t < timeOfSmallest) {
						timeOfSmallest = t;
						orderOfSmallest = o;
					}

					if (t > timeOfBiggest) {
						timeOfBiggest = t;
						orderOfBiggest = o;
					}

				} while (c.moveToNext());
			}
		}
		c.close();

		// Here we are determining a value for the list order we start with 1000
		// and then add 1
		// each time, we use float so we can stick numbers in the middel.
		if (timeOfNextBiggest == Long.MIN_VALUE && timeOfNextSmallest == Long.MAX_VALUE) { // This is the first
			// one in the list
			myListOrder = 1000;
		} else {
			if (timeOfNextBiggest == Long.MIN_VALUE) {// This is the last one in
				// the list
				myListOrder = orderOfSmallest - 1;
			} else if (timeOfNextSmallest == Long.MAX_VALUE) {// This is the
				// first one in
				// the list
				myListOrder = orderOfBiggest + 1;
			} else { // somewhere in the middle of the list
				myListOrder = (orderOfNextBiggest + orderOfNextSmallest) / 2;
			}
		}

		final ContentValues initialValues = new ContentValues();
		initialValues.put(OrderNumber, order.number);
		initialValues.put(Address, order.address);
		initialValues.put(AptNumber, order.apartmentNumber.toUpperCase());
		initialValues.put(Cost, order.cost);
		initialValues.put(Time, GetDateString(order.time));
		initialValues.put(Notes, order.notes);
		initialValues.put(DeliveryOrder, myListOrder);
		initialValues.put(Payed, order.payed);
		initialValues.put(Shift, TodaysShiftCount);
		initialValues.put(PaymentType, order.paymentType);
		initialValues.put(ArivalTime, GetDateString(order.arivialTime));
		initialValues.put(PaymentTime, GetDateString(order.payedTime));
		initialValues.put(RunNumber, 0); // TODO: We don't use run numbers yet

		if (order.outOfTown1){
			initialValues.put(OutOfTown, "1");
		} else{
			initialValues.put(OutOfTown, "0");
		}
		
		if (order.outOfTown2){
			initialValues.put(OutOfTown2, "1");
		} else{
			initialValues.put(OutOfTown2, "0");
		}
		
		if (order.outOfTown3){
			initialValues.put(OutOfTown3, "1");
		} else{
			initialValues.put(OutOfTown3, "0");
		}
		
		if (order.outOfTown4){
			initialValues.put(OutOfTown4, "1");
		} else{
			initialValues.put(OutOfTown4, "0");
		}
		
		if (order.onHold){
			initialValues.put(OnHold, "1");
		} else{
			initialValues.put(OnHold, "0");
		}
		
		if (order.startsNewRun){
			initialValues.put(StartsNewRun, "1");
		} else{
			initialValues.put(StartsNewRun, "0");
		}
		if (order.geoPoint!=null){
			initialValues.put("GPSLng", (float)order.geoPoint.getLongitudeE6()/(float)1000000);
			initialValues.put("GPSLat", (float)order.geoPoint.getLatitudeE6()/(float)1000000);
		}
		
		
		//For taxi droid 
		if (order.streetHail){
			initialValues.put("StreetHail", "1");
		} else{
			initialValues.put("StreetHail", "0");		
		}
		
		//final String[] o = { new String(order.number) };
		generateOrderNumbers();

		final long addedRow = db.insertOrThrow(DATABASE_TABLE, null, initialValues);
	
		return addedRow;
	}

	// ---retrieves all the titles---
	public Cursor getUndeliveredOrders() {
		return db.query(DATABASE_TABLE, // table The table name to compile the
				// query against.
				null, // fields array or null for all
				// selection A filter declaring which rows to return, formatted as an SQL WHERE clause
				//(excluding the WHERE itself). Passing null will return all rows for the given table.
				Payed + "='-1' AND " + "Shift='" + TodaysShiftCount + "'",
				null, // selectionArgs You may include ?s in selection, which
				// will be replaced by the values from selectionArgs, in
				// order that they appear in the selection. The values
				// will be bound as Strings.
				null, // groupBy A filter declaring how to group rows, formatted
				// as an SQL GROUP BY clause (excluding the GROUP BY
				// itself). Passing null will cause the rows to not be
				// grouped.
				null, // having A filter declare which row groups to include in
				// the cursor, if row grouping is being used, formatted
				// as an SQL HAVING clause (excluding the HAVING
				// itself). Passing null will cause all row groups to be
				// included, and is required when row grouping is not
				// being used.
				DeliveryOrder + "+0 DESC"); // orderBy How to order the rows,
		// formatted as an SQL ORDER BY
		// clause (excluding the ORDER BY
		// itself). Passing null will use
		// the default sort order, which may
		// be unordered.

	}


	public int getUndeliveredOrderCount() {
		final Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE + " WHERE " + Payed +"='-1' AND Shift='"+TodaysShiftCount+"'", null);
		int retVal=0;
		if (c.moveToFirst()) {
			retVal = c.getInt(0);
		}
		c.close();
		return retVal;
	}

	public ArrayList<Order> searchForOrders(String searchFor){
		searchFor = DatabaseUtils.sqlEscapeString("%"+searchFor+"%");
		Cursor c = db.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE address LIKE "+searchFor+" OR Notes LIKE"+searchFor+" ORDER BY Time DESC LIMIT 80", null);
		ArrayList<Order> orders = new ArrayList<Order>();
		Order o = null;
		if (c != null && c.moveToFirst()) {
			do {
				o = new Order(c);
				orders.add(o);
			} while (c.moveToNext());
		}
		c.close();
		return orders;
	}
	
	// ---retrieves all the titles---
	public Cursor getShiftOrders(final int shift) {
		return db.query(DATABASE_TABLE, // table The table name to compile the
				// query against.
				null, // fields array or null for all
				"Shift='" + shift + "'", // selection A filter declaring which
				// rows to return, formatted as an
				// SQL WHERE clause (excluding the
				// WHERE itself). Passing null will
				// return all rows for the given
				// table.
				null, // selectionArgs You may include ?s in selection, which
				// will be replaced by the values from selectionArgs, in
				// order that they appear in the selection. The values
				// will be bound as Strings.
				null, // groupBy A filter declaring how to group rows, formatted
				// as an SQL GROUP BY clause (excluding the GROUP BY
				// itself). Passing null will cause the rows to not be
				// grouped.
				null, // having A filter declare which row groups to include in
				// the cursor, if row grouping is being used, formatted
				// as an SQL HAVING clause (excluding the HAVING
				// itself). Passing null will cause all row groups to be
				// included, and is required when row grouping is not
				// being used.
				OrderNumber + "+0 ASC"); // orderBy How to order the rows,
		// formatted as an SQL ORDER BY clause
		// (excluding the ORDER BY itself).
		// Passing null will use the default
		// sort order, which may be unordered.
	}

	public Order getOrder(final int key) {
		Cursor c = db.query(DATABASE_TABLE, null, /* WHERE */"ID='" + key + "'", null, null, null, null);
		Order o = null;
		if (c != null && c.moveToFirst()) {
			o = new Order(c);
		}
		c.close();
		
		return o;
	}
	
	//Taxi Droid Only
	public void loadOrderDropOffs(Order order){
		Cursor c = db.rawQuery("SELECT * FROM dropOffs WHERE pickupId = "+order.primaryKey, null);
		if (c!=null && c.moveToFirst()){
			do {
				DropOff d = new DropOff();
				d.id = c.getInt(c.getColumnIndex("ID"));
				d.pickupId = c.getInt(c.getColumnIndex("pickupId"));
				d.time.setTime(Order.GetTimeFromString( c.getString(c.getColumnIndex("dropOffTime"))));
				d.address = c.getString(c.getColumnIndex("dropOffAddress"));
				d.payment = c.getFloat(c.getColumnIndex("payment"));
				d.meterAmount = c.getFloat(c.getColumnIndex("meterAmount"));
				d.account = c.getString(c.getColumnIndex("account"));
				d.authorization = c.getString(c.getColumnIndex("authorization"));
				d.paymentType = c.getInt(c.getColumnIndex("paymentType"));
				order.dropOffs.add(d);
			} while (c.moveToNext());
		}
		c.close();
	}
	
	public Order getOrder(String where){
		final Cursor c = db.query(DATABASE_TABLE, null, where, null, null, null, null);
		Order o = null;
		if (c != null && c.moveToFirst()) {
			o = new Order(c);
		}
		c.close();
		return o;
	}
	
	

	public Shift getShift(int shiftID){
		Cursor c = db.rawQuery("SELECT * FROM shifts WHERE ID = "+shiftID, null);
		Shift shift = new Shift();
		if (c!=null){
			if (c.moveToFirst()) {
				long t1 = c.getLong(c.getColumnIndex(TIME_START));
				long t2 = c.getLong(c.getColumnIndex(TIME_END));
				shift.startTime.setTimeInMillis(t1);
				shift.endTime.setTimeInMillis(t2);
				shift.odometerAtShiftStart  = c.getInt(c.getColumnIndex(ODO_START));
				shift.odometerAtShiftEnd  = c.getInt(c.getColumnIndex(ODO_END));
				shift.payRate = c.getFloat(c.getColumnIndex(PAY_RATE));
				shift.payRateOnRun = c.getFloat(c.getColumnIndex(PAY_RATE_ON_RUN));
				shift.primaryKey = c.getInt(c.getColumnIndex("ID"));
				
				if (shift.endTime.getTimeInMillis() < shift.startTime.getTimeInMillis()){
					shift.endTime=shift.startTime;
				}
			}
			c.close();
			
		}
		
		
		return shift;
	}
	
	public void saveShift(Shift shift){
		final ContentValues args = new ContentValues();
		args.put(TIME_START, shift.startTime.getTimeInMillis());
		args.put(TIME_END  , shift.endTime.getTimeInMillis());
		args.put(ODO_START , shift.odometerAtShiftStart);
		args.put(ODO_END   , shift.odometerAtShiftEnd);
		args.put(ODO_START  , shift.odometerAtShiftStart);
		args.put(PAY_RATE, shift.payRate);
		args.put(PAY_RATE_ON_RUN, shift.payRateOnRun);
		db.update("shifts", args, shift.primaryKey + "= ID", null);
	}
	
	public float getTotalMoneyCollectedForShift(final int shift) {
		float retVal = -1;
		final Cursor c = db.rawQuery("SELECT SUM(" + Payed + ") FROM " + DATABASE_TABLE + " WHERE " + Payed
				+ "!=-1 AND Shift='" + shift + "'", null);
		if (c.moveToFirst()) {
			retVal = c.getFloat(0);
		}
		c.close();
		return retVal;
	}

	public float getTotalCostForShift(final int shift) {
		float retVal = -1;
		final String q = new String("SELECT SUM(" + Cost + ") FROM " + DATABASE_TABLE + " WHERE " + Payed
				+ "!=-1 AND Shift='" + shift + "'");
		final Cursor c = db.rawQuery(q, null);
		if (c.moveToFirst()) {
			retVal = c.getFloat(0);
		}
		c.close();
		return retVal;
	}
	
	public class ShiftCounts{
		public int prev;
		public int next;
		public int cur;
	}
	
	public ShiftCounts getShiftCounts(int shiftId){
		ShiftCounts counts=new ShiftCounts();
		counts.prev=0;
		counts.cur=1;
		counts.next=0;
		Cursor c = db.rawQuery("SELECT count(*) FROM shifts WHERE ID < "+shiftId, null);
		if (c.moveToFirst()) {
			counts.prev = c.getInt(0);
	        counts.cur = counts.prev+1;
		}
		c = db.rawQuery("SELECT count(*) FROM shifts WHERE ID > "+shiftId, null);
		int nextCount = 0;
		if (c.moveToFirst()) {
			nextCount = c.getInt(0);
		}
		if (nextCount>0){
			counts.next = counts.cur+1;
		}
		return counts;
	}

	
	public TipTotalData getTipTotal(final Context context, String where){
		TipTotalData ret=new TipTotalData();
		Cursor c;

		//public float bestTip;
		//public float worstTip;
		//public float averageTip;
		
		
		try {
			c = db.rawQuery("SELECT *,strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " + where, null);
			ret.cashTips = 0;
			ret.reportableTips = 0;
			
			ret.bestTip = 0;
			ret.worstTip = Float.MAX_VALUE;
			
			if (c.moveToFirst()) {
				do {
					int paymentType1 = c.getInt(c.getColumnIndex(PaymentType));				
					int paymentType2 = c.getInt(c.getColumnIndex(PaymentType2));
					float cost = c.getFloat(c.getColumnIndex(Cost));
					float payed1 = c.getFloat(c.getColumnIndex(Payed));
					float payed2 = c.getFloat(c.getColumnIndex(PayedSplit));
					float thisTip;
					
					Log.i("weekday","weekday ="+c.getInt(c.getColumnIndex("weekday")));
					
					if (payed2 != 0){ //Split orders
						float tip = cost;
						if (paymentType1 != Order.CASH || paymentType1 == Order.NOT_PAID){
							tip -= payed1;
						}
						if (paymentType2 != Order.CASH){
							tip -= payed2;
						}
						if (tip < 0) {
							//then we have part of the tip as non cash
							ret.reportableTips -= tip;
							if (paymentType1 == Order.CASH || paymentType1 == Order.NOT_PAID){
								ret.cashTips+=payed1;
							}
							if (paymentType2 == Order.CASH){
								ret.cashTips+=payed2;
							}
						} else {
							//none of the tip came from non-cash payment
							if (paymentType1 == Order.CASH || paymentType1 == Order.NOT_PAID){
								tip -= payed1;
							}
							if (paymentType2 == Order.CASH){
								tip -= payed2;
							}	
							if (tip < 0){
								ret.cashTips -= tip;
							}
						}
						thisTip = (payed1+payed2-cost);
						
					}else { //single payment orders
						if (paymentType1 == Order.CASH || paymentType1 == Order.NOT_PAID){
							ret.cashTips += (payed1-cost);
						} else {
							ret.reportableTips += (payed1-cost);
						}
						thisTip = (payed1-cost);
					}
					
					if (thisTip > ret.bestTip)
						ret.bestTip = (payed1-cost);
					if (thisTip < ret.worstTip)
						ret.worstTip = (payed1-cost);
					
				} while (c.moveToNext());
			}
			c.close();
			if (Float.isNaN(ret.worstTip) || ret.worstTip == Float.MAX_VALUE){
				ret.worstTip = 0;
			}
	
			
			c = db.rawQuery("SELECT Sum(" + Cost + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " + where, null);
			ret.cost = -1;
			if (c.moveToFirst()) {
				ret.cost = c.getFloat(0);
			}
			c.close();
	
			c = db.rawQuery("SELECT Sum(" + Payed + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " + where, null);
			ret.payed = -1;
			if (c.moveToFirst()) {
				ret.payed = c.getFloat(0);
			}
			c.close();
	
			c = db.rawQuery("SELECT Sum(" + PayedSplit + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " + where, null);
			if (c.moveToFirst()) {
				ret.payed += c.getFloat(0);
			}
			c.close();
	
			c = db.rawQuery("SELECT Sum(" + Payed + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " +PaymentType+" = "+Order.CASH+" AND "+where, null);
			ret.payedCash = 0;
			if (c.moveToFirst()) {
				ret.payedCash = c.getFloat(0);
			}
			c.close();
	
			c = db.rawQuery("SELECT Sum(" + PayedSplit + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " +PaymentType2+" = "+Order.CASH+" AND "+where, null);
			if (c.moveToFirst()) {
				ret.payedCash += c.getFloat(0);
			}
			c.close();
	
			c = db.rawQuery("SELECT Sum(" + OutOfTown + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE "+where, null);
			if (c.moveToFirst()) {
				ret.outOfTownOrders += c.getInt(0);
			}
			c.close();
			
			c = db.rawQuery("SELECT Sum(" + OutOfTown2 + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE "+where, null);
			if (c.moveToFirst()) {
				ret.outOfTownOrders2 += c.getInt(0);
			}
			c.close();
			
			c = db.rawQuery("SELECT Sum(" + OutOfTown3 + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE "+where, null);
			if (c.moveToFirst()) {
				ret.outOfTownOrders3 += c.getInt(0);
			}
			c.close();
			
			c = db.rawQuery("SELECT Sum(" + OutOfTown4 + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE "+where, null);
			if (c.moveToFirst()) {
				ret.outOfTownOrders4 += c.getInt(0);
			}
			c.close();
	
			c = db.rawQuery("SELECT Sum(" + StartsNewRun + "),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE "+where, null);
			if (c.moveToFirst()) {
				ret.runs += c.getInt(0);
			}
			c.close();
	
			c = db.rawQuery("SELECT COUNT(*),strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " + where, null);
			ret.deliveries = -1;
			if (c.moveToFirst()) {
				ret.deliveries = c.getInt(0);
			}
			c.close();
	
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			
			ret.total = 0;
	
			final String MilagePayPerTrip = prefs.getString("per_delivery_pay", "0");
			final String MilagePayPercent = prefs.getString("percent_order_price", "0");
			final String MilagePayPerMile = prefs.getString("odometer_per_mile", "0");
			final String MilagePayPerOutOfTownDelivery = prefs.getString("per_out_of_town_delivery", "0");
			final String MilagePayPerOutOfTownDelivery2 = prefs.getString("per_out_of_town_delivery2", "0");
			final String MilagePayPerOutOfTownDelivery3 = prefs.getString("per_out_of_town_delivery3", "0");
			final String MilagePayPerOutOfTownDelivery4 = prefs.getString("per_out_of_town_delivery4", "0");
			final String MilagePayPerRun = prefs.getString("per_run_pay", "0");
			
			//Calculate fixed mileage pay per trip
			ret.mileageEarned=0;
			try {
				float f = new Float(MilagePayPerTrip);
				//if (f>0){
					f = f * (float) ret.deliveries;
					ret.mileageEarned += f;
				//}
			} catch (final NumberFormatException e) {}
			
			//Calculate mileage pay as % of order total
			try {
				float f = new Float(MilagePayPercent);
				if (f>0){
					f = (f/100)* (ret.cost);
					ret.mileageEarned += f;
				}
			} catch (final NumberFormatException e) {}
			
			
			try {
				float outOfTowns = new Float(MilagePayPerOutOfTownDelivery);
				float outOfTownMileage = ret.outOfTownOrders * outOfTowns;
				//if (outOfTownMileage>0){
					ret.mileageEarned += outOfTownMileage;
				//}
			} catch (final NumberFormatException e) {}
			
			try {
				float outOfTowns = new Float(MilagePayPerOutOfTownDelivery2);
				float outOfTownMileage = ret.outOfTownOrders2 * outOfTowns;
				//if (outOfTownMileage>0){
					ret.mileageEarned += outOfTownMileage;
				//}
			} catch (final NumberFormatException e) {}
			
			try {
				float outOfTowns = new Float(MilagePayPerOutOfTownDelivery3);
				float outOfTownMileage = ret.outOfTownOrders3 * outOfTowns;
				//if (outOfTownMileage>0){
					ret.mileageEarned += outOfTownMileage;
				//}
			} catch (final NumberFormatException e) {}
			
			try {
				float outOfTowns = new Float(MilagePayPerOutOfTownDelivery4);
				float outOfTownMileage = ret.outOfTownOrders4 * outOfTowns;
				//if (outOfTownMileage>0){
					ret.mileageEarned += outOfTownMileage;
				//}
			} catch (final NumberFormatException e) {}
			
			try {
				float milagePayPerRun = new Float(MilagePayPerRun);
				float runMileagePay = ret.runs * milagePayPerRun;
				//if (runMileagePay>0){
					ret.mileageEarned += runMileagePay;
				//}
			} catch (final NumberFormatException e) {}
			
			c = db.rawQuery("SELECT shift,strftime('%w',`"+ DataBase.Time + "`) AS `weekday` FROM " + DATABASE_TABLE + " WHERE " + where, null);
		    ArrayList<Integer> list = new ArrayList<Integer>(100);
			if (c!=null) {
				if (c.moveToFirst()){
					do {
						Integer s = c.getInt(0);
						if (!list.contains(s)){
							list.add(s);
						}
					} while (c.moveToNext());
				}
			}
			c.close();
			
			long hoursInMills = 0;
			for (int i = 0; i < list.size(); i++){
				c = db.rawQuery("SELECT "+TIME_START+","+TIME_END+" FROM shifts WHERE id = "+list.get(i), null);
				if (c.moveToFirst()) {
					do {
						long timeStart = c.getLong(c.getColumnIndex(TIME_START));
						long timeEnd = c.getLong(c.getColumnIndex(TIME_END));
						if (timeEnd > timeStart) {
							hoursInMills += timeEnd-timeStart;
						}
					}while (c.moveToNext());
				}
				c.close();
			}
			ret.hours = (float)hoursInMills/3600000.0f;
			
			try {
				float f = new Float(MilagePayPerMile);
				
					
					for (int i = 0; i < list.size(); i++){
						c = db.rawQuery("SELECT "+ODO_START+","+ODO_END+" FROM shifts WHERE id = "+list.get(i), null);
						long odoStart=0;
						long odoEnd=0;
						if (c.moveToFirst()) {
							do {
								odoStart = c.getLong(c.getColumnIndex(ODO_START));
								odoEnd = c.getLong(c.getColumnIndex(ODO_END));
								if (odoEnd-odoStart>0){
									ret.mileageEarned +=  (float)(odoEnd-odoStart)*f;
									ret.odometerTotal += (odoEnd-odoStart);
								}
							}while (c.moveToNext());
						}
						c.close();  
				//	}
					
					f = (f/100)* (ret.payed - ret.cost);
					ret.mileageEarned += f;
				}
			} catch (final NumberFormatException e) {}	
			
			ret.total = ret.mileageEarned;
			ret.total += ret.payed - ret.cost;
			
			ret.averageTip = ((ret.payed - ret.cost)/ret.deliveries);
		} catch (NullPointerException e){
			e.printStackTrace(); //It would be better to return bogus info than crash. I was able to repro by clicking text message.
		}
		return ret;
	}
	
	/*public String getTodaysTipTotalString(final Context context, float currentTip) {
		final int shiftNumber = TodaysShiftCount;
		final DecimalFormat currency = new DecimalFormat("$#0.00");
		
		TipTotalData x  = getTipTotal(context,Shift + " = "+shiftNumber+" AND "+Payed+">0");
		
		String mps="";
		if (x.mileageEarned>0)
			mps = new String("\nMileage Earned:" + currency.format(x.mileageEarned));
		return new String("\nTips Made:" + currency.format((x.payed - x.cost) + currentTip) + mps + "\nDriver Earnings:"
				+ currency.format(x.total));		
	}*/

	static String GetDateString(Timestamp time){
		Calendar t = CalendarFromTimestamp(time);
		return GetDateString(t);
	}
	
	static String GetDateString(Calendar t){
		String dateString = String.format("%1$tY-%1$tm-%1$td %1$tH:%1tM:%1$tS.%1$tL", t);
		return dateString;
	}
	
	
	static Calendar CalendarFromTimestamp(Timestamp time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		int year =  c.get(Calendar.YEAR);
		if (year > 2050){
			year -= 1900;
		}
		c.set(Calendar.YEAR,year);
		return c;
	}
	
	public static String GetHumanReadableDateString(Timestamp time){  
		Calendar c = CalendarFromTimestamp(time);
		return String.format("%3$tm/%3$td/%3$tY", c,c,c);
	}
	
	

	public int getShiftUndiliveredOrderCount(final int shift) {
		int retVal = -1;
		final Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE + " WHERE " + Payed + "!=-1 AND Shift='"
				+ shift + "'", null);
		if (c.moveToFirst()) {
			retVal = c.getInt(0);
		}
		c.close();
		return retVal;
	}

	public int getThisShiftTotalOrderCount() {
		int retVal = -1;
		final Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE + " WHERE Shift='" + TodaysShiftCount
				+ "'", null);
		if (c.moveToFirst()) {
			retVal = c.getInt(0);
		}
		c.close();
		return retVal;
	}

	public int getHoursSinceLastOrder() {
		long retVal = -1;
		String timeString = new String();
		final Cursor c = db.rawQuery("SELECT MAX(" + Time + ") FROM " + DATABASE_TABLE, null);
		if (c.moveToFirst()) {
			timeString = c.getString(0);
			try {
				retVal = Order.GetTimeFromString(timeString);
			} catch (final RuntimeException e) {
				retVal = System.currentTimeMillis();
			}
		} else {
			retVal = 0;
		}
		c.close();

		return (int) ((System.currentTimeMillis() - retVal) / 3600000);
	}

	public String[] getLast10OrderNumbers() {
		final String retVal[] = new String[10];// rowid (select max(rowid) from
		// employee).
		int id = -1;
		Cursor c = db.rawQuery("SELECT MAX(ID) FROM " + DATABASE_TABLE, null);
		if (c.moveToFirst()) {
			id = c.getInt(0);
		}
		c.close();
		c = db.rawQuery("SELECT " + OrderNumber + " FROM " + DATABASE_TABLE + " WHERE ID > " + (id - 10) + " AND "
				+ OrderNumber + "!=''", null);
		if (c.moveToFirst()) {
			int i = 0;
			do {
				retVal[i++] = c.getString(c.getColumnIndex(OrderNumber));
			} while (c.moveToNext());
		}
		c.close();
		return retVal;
	}

	// select distinct firstname from employee;
	// select count(*) from (select distinct datesend from mailings)
	synchronized public ArrayAdapter<String> getCostAdapter(final Context that) {
		String query = "SELECT "+Cost+" FROM " + DATABASE_TABLE + " GROUP BY "+Cost+" ORDER BY count(*) DESC LIMIT 0,100";
		ArrayList<String> arrayList = new ArrayList<String>();
		final Cursor c = db.rawQuery(query, null);
		if (c != null && c.moveToFirst()) {
			do {
				String address = c.getString(0);
				arrayList.add(address);
			} while (c.moveToNext());
		}
		c.close();
		
		return new ArrayAdapter<String>(that, android.R.layout.simple_dropdown_item_1line, arrayList);
	}

	synchronized public int getCostScroll() {
		if (orderCosts == null) return 0;
		return orderCosts.length / 2;
	}

	// Modifies an orders delivery order value
	public boolean changeOrder(final int key, final float order) {
		final ContentValues args = new ContentValues();
		args.put(DeliveryOrder, order);
		return db.update(DATABASE_TABLE, args, key + "= ID", null) > 0;
	}

	// Modifies an orders delivery payment value
	public boolean setOrderPayment(final int key, final float payment, final int paymentType, float payment2, int paymentType2, boolean startNewRun, String notes) {
		final ContentValues args = new ContentValues();
		args.put(Payed, payment);
		args.put(PayedSplit, payment2);
		args.put(PaymentType, paymentType);
		args.put(PaymentType2, paymentType2);
		args.put(Notes, notes);
		args.put(PaymentTime, GetDateString(new Timestamp(System.currentTimeMillis())));
		args.put(ArivalTime, GetDateString(new Timestamp(System.currentTimeMillis())));// TODO:remove and set with GPS
		if (startNewRun){
			args.put(StartsNewRun, "1");
		} else {
			args.put(StartsNewRun, "0");
		}
		
		return db.update(DATABASE_TABLE, args, key + "= ID", null) > 0;
	}

	// Modifies the fields in the order
	public boolean edit(final Order order) {
		final ContentValues args = new ContentValues();
		args.put(OrderNumber, order.number);
		args.put(Address, order.address);
		args.put(AptNumber, order.apartmentNumber.toUpperCase());
		args.put(Cost, order.cost);
		args.put(Time, GetDateString(order.time));
		args.put(Notes, order.notes);
		args.put(Payed, order.payed);
		args.put(PaymentType, order.paymentType);
		if (order.arivialTime != null) {
			args.put(ArivalTime, GetDateString(order.arivialTime));
		}
		if (order.payedTime != null) {
			args.put(PaymentTime, GetDateString(order.payedTime));
		}
		if (order.outOfTown1){
			args.put(OutOfTown, "1");
		} else{
			args.put(OutOfTown, "0");
		}
		if (order.outOfTown2){
			args.put(OutOfTown2, "1");
		} else{
			args.put(OutOfTown2, "0");
		}
		if (order.outOfTown3){
			args.put(OutOfTown3, "1");
		} else{
			args.put(OutOfTown3, "0");
		}
		if (order.outOfTown4){
			args.put(OutOfTown4, "1");
		} else{
			args.put(OutOfTown4, "0");
		}
		
		args.put(PaymentType2, order.paymentType2);
		args.put(PayedSplit, order.payed2);
		
		args.put("GPSLng", (float)order.geoPoint.getLongitudeE6()/(float)1000000);
		args.put("GPSLat", (float)order.geoPoint.getLatitudeE6()/(float)1000000);
		
		Log.i("address","edit addressLocation "+order.geoPoint.getLatitudeE6()+order.geoPoint.getLongitudeE6());
		
		args.put("validatedAddress", order.isValidated);
		
		//For taxi droid 
		if (order.streetHail){
			args.put("StreetHail", "1");
		} else{
			args.put("StreetHail", "0");		
		}
		
		final boolean retVal = db.update(DATABASE_TABLE, args, order.primaryKey + "= ID", null) > 0;
		
		for (int i=0; i < order.dropOffs.size();i++){
			updateDropOff(order.dropOffs.get(i),order);
		}
		return retVal;
	}

	public boolean delete(final long rowId) {
		return db.delete(DATABASE_TABLE, "ID" + "=" + rowId, null) > 0;
	}

	
	public int getCurShift() {
		try {
			final Cursor c = db.rawQuery("SELECT MAX(ID) FROM shifts", null);

			if (c != null && c.moveToFirst()) {
				TodaysShiftCount = (int) c.getLong(0);
			}
			c.close();
		} catch (NullPointerException e){
			e.printStackTrace();
			return 1;
		}
		return TodaysShiftCount;
	}

	public boolean updateAddress(final int updateAddressKey, final String newOrderAddress) {
		final ContentValues args = new ContentValues();
		args.put(Address, newOrderAddress);
		final boolean retVal = db.update(DATABASE_TABLE, args, updateAddressKey + "= ID", null) > 0;
		return retVal;
	}

	public long getThisShiftOdomenterStart() {
		final Cursor c = db.rawQuery("SELECT "+ODO_START+" FROM shifts WHERE ID ="+TodaysShiftCount, null);
		long odoVal=0;
		if (c != null && c.moveToFirst()) {
			odoVal =  c.getLong(0);
		}
		c.close();
		return odoVal;
	}
	
	public long getThisShiftOdomenterEnd() {
		final Cursor c = db.rawQuery("SELECT "+ODO_END+" FROM shifts WHERE ID ="+TodaysShiftCount, null);
		long odoVal=0;
		if (c != null && c.moveToFirst()) {
			odoVal =  c.getLong(0);
		}
		c.close();
		return odoVal;
	}
	

	public int getNumberOfOrdersThisShift() {
		final Cursor c = db.rawQuery("SELECT count(*) FROM "+DATABASE_TABLE+" WHERE Shift ="+TodaysShiftCount, null);
		int totalOrdersThisShift=0;
		if (c != null && c.moveToFirst()) {
			totalOrdersThisShift =  c.getInt(0);
		}
		c.close();
		return totalOrdersThisShift;
	}


	public void setThisShiftOdometerStart(String string) {
		final ContentValues args = new ContentValues();
		long val = 0;
		try {
			val = new Long(string);
		} catch(Exception e){};
		args.put(ODO_START, val );
		//final boolean retVal = db.update("shifts", args, TodaysShiftCount + "= ID", null) > 0;	
	}

	public boolean setThisShiftOdometerEnd(String string) {
		final ContentValues args = new ContentValues();
		long val = 0;
		try {
			val = new Long(string);
		} catch(Exception e){};
		args.put(ODO_END, val);
		return  db.update("shifts", args, TodaysShiftCount + "= ID", null) > 0;		
	}

	public void createShiftRecordIfNonExists() {
		int maxShiftFromOrders=0;
		int maxShiftFromShifts=-1;
		
		//For upgrade from before we had a shift table we need to check and see if there is
		//any existing shift records in the order table and create dummy shifts to match
		Cursor c = db.rawQuery("SELECT MAX(" + Shift + ") FROM " + DATABASE_TABLE, null);
		if (c != null && c.moveToFirst()) {
			maxShiftFromOrders = (int) c.getLong(0);
		}
		c.close();
		
		c = db.rawQuery("SELECT MAX(ID) FROM shifts", null);
		if (c != null && c.moveToFirst()) {
			maxShiftFromShifts = (int) c.getLong(0);
		}
		c.close();
		
		//This is because the 1st shift needs to be one instead of 0
		//and is really a startup task
		if (maxShiftFromShifts == 0 && maxShiftFromOrders==0){
			maxShiftFromOrders=1;
		}
		
		while (maxShiftFromShifts<maxShiftFromOrders){
			final ContentValues init = new ContentValues();
			init.put(TIME_START, 0);
			init.put(TIME_END,0);
			TodaysShiftCount = (int) db.insertOrThrow("shifts", null, init);
			maxShiftFromShifts=TodaysShiftCount;
		};	
	}
	
	public void setNextShift() {
		// First we check that there are orders in this shift, if not we do not incrment the shift
		// count. Instead we just update the current shift
		final Cursor cc = db.rawQuery("SELECT ID FROM " + DATABASE_TABLE + " WHERE Shift='"
				+ TodaysShiftCount + "'", null);
		
		if (!(cc!=null && cc.moveToFirst())){
			final ContentValues args = new ContentValues();
			args.put(TIME_START, 0);
			args.put(TIME_END,0);
			db.update("shifts", args, TodaysShiftCount + "= ID", null);
			return;
		}
		cc.close();
		
		
		// Next - We query all the records that are not payed and 
		//         are in todays shift
		final Cursor c = db.rawQuery("SELECT ID FROM " + DATABASE_TABLE + " WHERE " + Payed + "=-1 AND Shift='"
				+ TodaysShiftCount + "'", null);
		
		// Next - We set 0 timestamps for a new new shift record and create it to update our shift count
		final ContentValues init = new ContentValues();
		init.put(TIME_START, 0);
		init.put(TIME_END,0);
		TodaysShiftCount = (int) db.insertOrThrow("shifts", null, init);

		// Last - We update all the orders that we queried with the new shift number
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					final long key = c.getLong(0);
					final ContentValues args = new ContentValues();
					args.put(Shift, TodaysShiftCount);
					db.update(DATABASE_TABLE, args, key + "= ID", null);
				} while (c.moveToNext());
			}
		}
		c.close();
	}

	public ArrayAdapter<String> getOdometerPredtion() {
		Cursor c;
		long lastOdometer=0;
		
		c = db.rawQuery("SELECT "+ODO_END+" FROM shifts WHERE id = "+(TodaysShiftCount-1), null);
		if (c != null && c.moveToFirst()) {
			lastOdometer = c.getLong(0);
		}
		c.close();
		
		c = db.rawQuery("SELECT "+ODO_START+" FROM shifts WHERE id = "+(TodaysShiftCount), null);
		if (c != null && c.moveToFirst()) {
			lastOdometer = c.getLong(0);
		}
		c.close();	
		
		if (lastOdometer==0){
			c = db.rawQuery("SELECT MAX("+ODO_END+") FROM shifts", null);
			if (c != null && c.moveToFirst()) {
				lastOdometer = c.getLong(0);
			}
			c.close();	
		}
		
		String[] odometerPrediction = new String[200];
		String s = new String(""+(lastOdometer+1));
		try {
			s = s.substring(0, s.length()-1);
			long l = new Long(s);
			
			for (int i= 0; i < 200; i++){
				odometerPrediction[i]=new String(""+(l+i));
			}
		} catch(Exception e){
			return null;
		}
		return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, odometerPrediction);

	}

	public void deleteShift(int viewingShift) {
		int previousShift = getPrevoiusShiftNumber(viewingShift);
		if (previousShift < 1) {
			final Cursor c = db.rawQuery("SELECT ID FROM " + DATABASE_TABLE + " WHERE Shift='"+ viewingShift + "'", null);
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						final long key = c.getLong(0);
						delete(key);
					} while (c.moveToNext());
				}
			}
			c.close();
		} else {
			final ContentValues args = new ContentValues();
			args.put("Shift", previousShift);
			db.update(DATABASE_TABLE, args, "Shift = '"+viewingShift+"'", null);
		}
		db.delete("shifts", "ID" + "=" + viewingShift, null);
	}

	public int getPrevoiusShiftNumber(int viewingShift) {
		while (viewingShift>0){
			viewingShift--;
			final Cursor c = db.rawQuery("SELECT ID FROM shifts WHERE ID='"+ viewingShift + "'", null);
			if (c != null && c.moveToFirst()) {
				c.close();
				return viewingShift;
			}	
		}
		return 0;
	}

	public int getNextShiftNumber(int viewingShift) {
		long max=0;
		final Cursor cm = db.rawQuery("SELECT MAX(ID) FROM shifts", null);
		if (cm != null && cm.moveToFirst()) {
			max = cm.getLong(0);
			cm.close();
		}	
		while (viewingShift<=max){
			viewingShift++;
			final Cursor c = db.rawQuery("SELECT ID FROM shifts WHERE ID='"+ viewingShift + "'", null);
			if (c != null && c.moveToFirst()) {
				c.close();
				return viewingShift;
			}	
		}
		return getPrevoiusShiftNumber(viewingShift);
	}

	public int findShiftForTime(Calendar calendar) {
		String dateString = String.format("%3$tY-%3$tm-%3$td", calendar, calendar, calendar);//""+time2.getYear()+"-0"+time2.getMonth()+"-"+time2.getDate();
		String sql = "SELECT "+Shift+" FROM " + DATABASE_TABLE + "  WHERE "+Time+" >= '"+dateString+"'";
		final Cursor c = db.rawQuery(sql, null);
		if (c != null && c.moveToFirst()) {
			int newShift = c.getInt(0);
			c.close();
			return newShift;
		}	
		c.close();
		return -1;//error
	}

	public String getCSVData(Calendar startDate, Calendar endDate) {
		String query = "SELECT * FROM " + DATABASE_TABLE + " WHERE `" + Time + "` >= '"+String.format("%3$tY-%3$tm-%3$td", startDate, startDate, startDate) +
		               "' AND `"+ Time + "` <= '" + String.format("%3$tY-%3$tm-%3$td", endDate, endDate, endDate)+"'";
		String csvData = new String();
		final Cursor c = db.rawQuery(query, null);
		if (c != null && c.moveToFirst()) {
			int colCount = c.getColumnCount();
			csvData += ""+ c.getColumnName(0);
			for (int i = 1; i < colCount; i++){
				csvData += ","+ c.getColumnName(i);
			}
			csvData += "\n";
			
			do {
				csvData += c.getString(0);
				for (int i = 1; i < colCount; i++){
					String s = c.getString(i);
					try {
						s.replaceAll("\"", "\"\"");
					} catch (NullPointerException e){
						e.printStackTrace();
						s = "null";
					}
					csvData += ",\""+ s +"\"";
				}
				csvData += "\n";
				
			} while (c.moveToNext());
		}
		c.close();
		return csvData;
	}
	
	public String getCSVTaxiData(Calendar startDate, Calendar endDate) {
		String query = "SELECT * FROM " + DATABASE_TABLE + " " +
				"JOIN dropOffs ON dropOffs.pickupId = orders.ID " +
				"WHERE `" + Time + "` >= '"+String.format("%3$tY-%3$tm-%3$td", startDate, startDate, startDate) +
        "' AND `"+ Time + "` <= '" + String.format("%3$tY-%3$tm-%3$td", endDate, endDate, endDate)+"'";
		String csvData = new String();
		final Cursor c = db.rawQuery(query, null);
		if (c != null && c.moveToFirst()) {
		int colCount = c.getColumnCount();
		csvData += ""+ c.getColumnName(0);
		for (int i = 1; i < colCount; i++){
			csvData += ","+ c.getColumnName(i);
		}
		csvData += "\n";
		
		do {
			csvData += c.getString(0);
			for (int i = 1; i < colCount; i++){
				String s = c.getString(i);
				try {
					s.replaceAll("\"", "\"\"");
				} catch (NullPointerException e){
					e.printStackTrace();
					s = "null";
				}
				csvData += ",\""+ s +"\"";
			}
			csvData += "\n";
			
		} while (c.moveToNext());
		}
		c.close();
		return csvData;
	}
	
	public String getTaxiLogSheet(int shiftId) {
		String result = 
		"<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />";
		
		result += "\n<title></title>\n";
		result += "<style type='text/css'>\n";
		result += "body {font: 100%/1.4 Verdana, Arial, Helvetica, sans-serif;background: #FFF;margin: 0;padding: 0;color: #000;}ul, ol, dl {padding: 0;margin: 0;}h1, h2, h3, h4, h5, h6, p {margin-top: 0;padding-right: 15px;padding-left: 15px;}a img {border: none;}.container {width: 960px;margin: 0 auto;}.content {padding: 10px 0;}.label {float:left;width:100px;font-size:12px}.smallInput{ float:left;width:125px;}.mediumInput{ float:left;width:200px;}.cleanTable {border-collapse:collapse;}"+
		".expGroup {display:inline-block;width:150px;}"+
		"\n</style>";
		result += "</head><body><div class='container'><div class='content'>"+
		"<div class='label'>Driver #:</div><div class='smallInput'>"+prefs.getString("DriverNumber", "___________")+"</div>";
		
		Shift shift = getShift(shiftId);
		result += "<div class='label'>Date:</div><div class='smallInput'>"+shift.startTime.get(Calendar.MONTH)+"/"+shift.startTime.get(Calendar.DAY_OF_MONTH)+"/"+shift.startTime.get(Calendar.YEAR)+"</div>"; 
		result += "<div class='label'>Cab #:</div><div class='smallInput'>"+prefs.getString("CabNumber", "___________")+"</div>";
		result += "<div class='label'>Start Mileage:</div><div class='smallInput'>"+shift.odometerAtShiftStart+"</div>";
		result += "<div style='clear:both'></div>";
		result += "<div class='label'>Shift Start:</div><div class='smallInput'>"+String.format("%tl:%tM %tp", shift.startTime,shift.startTime,shift.startTime)+"</div>";
		
		long a = shift.startTime.getTimeInMillis();
		long b = shift.endTime.getTimeInMillis();
		float hours = (float)(b-a)/3600000f;
		result += "<div class='label'>Shift Length:</div><div class='smallInput'>"+String.format("%.2f", hours)+"</div>";
		result += "<div class='label'>Radio Presets:</div><div class='smallInput'>"+prefs.getString("radioPresets", "100.7, 94.5, 95.5, 89.5,")+"</div>";
		result += "<div class='label'>Lease Cost:</div><div class='smallInput'>"+prefs.getString("leaseCost","_______")+"</div>";
	    
		result += "<table class='cleanTable' width='100%' border='1'><tr><th width='8%' scope='col'>Trip ID</th><th width='16%' scope='col'>Trip Type</th><th width='32%' scope='col'>Pick Up Address</th><th width='32%' scope='col'>Drop Off Address</th><th width='12%' scope='col'>Paid</th></tr>";
			
		ArrayList<Order> orders = getShiftOrderList(shiftId);  //TODO: accuratly reflect multiple drop offs
		for (int i = 0; i < orders.size();i++){
			Order order = orders.get(i);
			
			result += "<tr><td>";
			result += ""+order.number;
			result += "</td>";
			result += "<td>"+order.apartmentNumber+"</td>";
			result += "<td>"+order.address+"</span></p></td>";
			result += "<td>"+order.dropOffs.get(0).address+"</td>";
			result += "<td>"+order.dropOffs.get(0).payment+"</td>";
			result += "</tr>";
		}
		
		result += "</table>";
		
		
		
		String query = "SELECT category,SUM(amount) FROM expenses WHERE shiftId = "+shiftId+" GROUP BY category";
		final Cursor c = db.rawQuery(query, null);
		float total=0;
		if (c.moveToFirst()) {
			do {
				float amount = c.getFloat(1);
				total+=amount;
				result += "<span class='expGroup'>"+c.getString(0)+"</span>"+getFormattedCurrency(amount)+"<br/>";
			} while (c.moveToNext());
		}
		c.close();
		result += "<p><b><span class='expGroup'>Total:</span>"+getFormattedCurrency(total)+"</b></p>";
		
		result += "</body></html>";
		
		return result;
		
		
		
	}
	
	static String getFormattedCurrency(Float f){
		String currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();
		DecimalFormat currency = new DecimalFormat("#0.00");
		currency.setMaximumFractionDigits(Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits());
		currency.setMinimumFractionDigits(Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits());
		return currencySymbol+currency.format(f);	
	}
	

	private ArrayList<Order> getShiftOrderList(int shiftId) {
		Cursor c = getShiftOrders(shiftId);
		ArrayList<Order> orders = new ArrayList<Order>();
		if (c.moveToFirst()){
			do {
				orders.add(new Order(c));
			} while (c.moveToNext());
		}
		c.close();
		for (int i = 0; i < orders.size();i++){
			loadOrderDropOffs(orders.get(i));
		}
		return orders;
	}

	public void searchAddressSuggestionsFor(String addressOrNotes, ArrayList<String> resultsFromDB) {
		String addressSoFarPlusSpace = DatabaseUtils.sqlEscapeString(addressOrNotes+" %");
		String notesSoFar = DatabaseUtils.sqlEscapeString("%"+addressOrNotes+"%");
		addressOrNotes = DatabaseUtils.sqlEscapeString(addressOrNotes+"%");
		
		try {
			String query = "SELECT "+Address+" FROM " + DATABASE_TABLE + " WHERE "+Address+" LIKE "+addressOrNotes+" OR Notes LIKE "+notesSoFar+" GROUP BY "+Address+" ORDER BY "+Address+" LIKE "+addressSoFarPlusSpace+" DESC,count(*) DESC LIMIT 0,100";
			final Cursor c = db.rawQuery(query, null);
			if (c != null && c.moveToFirst()) {
				do {
					String address = c.getString(0);
					resultsFromDB.add(address);
				} while (c.moveToNext());
			}
			c.close();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	public void getAddressSuggestionsFor(String addressSoFar, ArrayList<String> resultsFromDB) {
		String addressSoFarPlusSpace = DatabaseUtils.sqlEscapeString(addressSoFar+" %");
		addressSoFar = DatabaseUtils.sqlEscapeString(addressSoFar+"%");
		
		
		try {
			String query = "SELECT "+Address+" FROM " + DATABASE_TABLE + " WHERE "+Address+" LIKE "+addressSoFar+" GROUP BY "+Address+" ORDER BY "+Address+" LIKE "+addressSoFarPlusSpace+" DESC,count(*) DESC LIMIT 0,100";
			final Cursor c = db.rawQuery(query, null);
			if (c != null && c.moveToFirst()) {
				do {
					String address = c.getString(0);
					resultsFromDB.add(address);
				} while (c.moveToNext());
			}
			c.close();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	public static final int NO_PAYMENT = 0;        //HArdcoded elsewhere these values can not change
	public static final int CASH_PAYMENT = 1;
	public static final int CREDIT_PAYMENT = 2;
	public static final int ACCOUNT_PAYMENT = 3;

	public TipTotalData getTaxiTipTotal(Context applicationContext, String where) {
		TipTotalData ret=new TipTotalData();
		Cursor c;
		
		c = db.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + where , null);
		ret.cashTips = 0;
		ret.reportableTips = 0;
		
		ret.bestTip = 0;
		ret.worstTip = Float.MAX_VALUE;
		ret.cost=0;
		ret.payed=0;
		ret.cashTips=0;
		ret.reportableTips=0;
		ret.payedCash=0;
		ret.bestTip=0;
		ret.worstTip=Float.MAX_VALUE;
		if (c.moveToFirst()) {
			do {
				int pickupid = (int)c.getLong(c.getColumnIndex("ID"));
				
				//Totals
				Cursor d = db.rawQuery("SELECT SUM(payment) FROM dropOffs WHERE pickupId="+pickupid, null);
				if (d.moveToFirst()) {
					ret.payed += d.getFloat(0);
				}
				d.close();
				d = db.rawQuery("SELECT SUM(meterAmount) FROM dropOffs WHERE pickupId="+pickupid, null);
				if (d.moveToFirst()) {
					ret.cost += d.getFloat(0);
				}
				d.close();
				
				//Cash tip
				d = db.rawQuery("SELECT SUM(payment) FROM dropOffs WHERE pickupId="+pickupid+
						" AND paymentType="+CASH_PAYMENT, null);
				if (d.moveToFirst()) {
					float f = d.getFloat(0);
					ret.cashTips += f;
					ret.payedCash += f;
				}
				d.close();
				d = db.rawQuery("SELECT SUM(meterAmount) FROM dropOffs WHERE pickupId="+pickupid+
						" AND paymentType="+CASH_PAYMENT, null);
				if (d.moveToFirst()) {
					ret.cashTips -= d.getFloat(0);
				}
				d.close();
				
				//Non cash tip
				d = db.rawQuery("SELECT SUM(payment) FROM dropOffs WHERE pickupId="+pickupid+
						" AND paymentType!="+CASH_PAYMENT, null);
				if (d.moveToFirst()) {
					ret.reportableTips += d.getFloat(0);
				}
				d.close();
				d = db.rawQuery("SELECT SUM(meterAmount) FROM dropOffs WHERE pickupId="+pickupid+
						" AND paymentType!="+CASH_PAYMENT, null);
				if (d.moveToFirst()) {
					ret.reportableTips -= d.getFloat(0);
				}
				d.close();
				
				d = db.rawQuery("SELECT MAX(payment-meterAmount) FROM dropOffs WHERE pickupId="+pickupid, null);
				if (d.moveToFirst()) {
					Float f = d.getFloat(0);
					if (ret.bestTip < f){
						ret.bestTip=f;
					}
				}
				d.close();
				d = db.rawQuery("SELECT MIN(payment-meterAmount) FROM dropOffs WHERE pickupId="+pickupid, null);
				if (d.moveToFirst()) {
					Float f = d.getFloat(0);
					if (ret.bestTip < f){
						ret.worstTip=f;
					}
				}
				d.close();
				
			} while (c.moveToNext());
		}
		c.close();
		if (Float.isNaN(ret.worstTip) || ret.worstTip == Float.MAX_VALUE){
			ret.worstTip = 0;
		}
		
		c = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE + " WHERE " + where, null);
		ret.deliveries = -1;
		if (c.moveToFirst()) {
			ret.deliveries = c.getInt(0);
		}
		c.close();

		
		ret.total = ret.payed - ret.cost;
		
		ret.averageTip = ((ret.payed - ret.cost)/ret.deliveries);
		
		return ret;
	}

	public void estimateShiftTimes(Shift shift) {
		long firstTime = System.currentTimeMillis();
		long lastTime = System.currentTimeMillis();
		int successes=0;
		Cursor c;
		try {
			c = db.rawQuery("SELECT MAX(Time) FROM "+DATABASE_TABLE +" WHERE Shift="+shift.primaryKey, null);
			if (c != null && c.moveToFirst()) {
				lastTime = Order.GetTimeFromString(c.getString(0));	
				successes++;
			}
			c.close();
			c = db.rawQuery("SELECT MIN(Time) FROM "+DATABASE_TABLE +" WHERE Shift="+shift.primaryKey, null);
			if (c != null && c.moveToFirst()) {
				firstTime = Order.GetTimeFromString(c.getString(0));
				successes++;
			}
			c.close();
			if (successes<2) { //Then its a new empty shift set the start time to now
				shift.startTime.setTimeInMillis(System.currentTimeMillis());
				shift.endTime.setTimeInMillis(System.currentTimeMillis());
				shift.noEndTime=true;
			} else {  
				if (shift.startTime.getTimeInMillis() > firstTime || shift.startTime.getTimeInMillis()==0) {
					shift.startTime.setTimeInMillis(firstTime);
				}
				if (shift.endTime.getTimeInMillis() < lastTime){
					shift.endTime.setTimeInMillis(lastTime);
				}
			}
		} catch (NullPointerException e){
			e.printStackTrace();
			shift.endTime.setTimeInMillis(lastTime);
			shift.startTime.setTimeInMillis(firstTime);	
		}
	}

	//The apartment number field from the order is used as the order type field in taxi droid
	public ArrayAdapter<String> getOrderTypeAdapter() {
		ArrayList<String> tripTypeList = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT DISTINCT AptNumber FROM "+DATABASE_TABLE, null);
		if (c.moveToFirst()){
			do {
				String s = c.getString(0);
				if (s!=null && s.length()>1){
					tripTypeList.add(s);
				}
			} while (c.moveToNext());
		}
		c.close();
		return new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,tripTypeList);
	}

	//This is a terrible hack, getPastNotesForAddressAndAptNo gets called first and then
	//sets a global variable. 
	//TODO: I need to merge this and have a single function that returns an array list or something
	String currentAptNo="";
	public String getPastNotesForAddress(String address) {
		String retVal = "";
		try {
			address = DatabaseUtils.sqlEscapeString(address);
			String query;
			if (currentAptNo.length() > 1){
				query = "SELECT "+Notes+",Time FROM " + DATABASE_TABLE + " WHERE "+Address+" LIKE "+address+" AND "+AptNumber+" NOT LIKE '"+currentAptNo+"' ORDER BY Time DESC LIMIT 0,25";
			} else {
				//TODO: Fix/Text seems wrong
				query = "SELECT "+Notes+",Time FROM " + DATABASE_TABLE + " WHERE "+Address+" LIKE "+address+" ORDER BY Time DESC LIMIT 0,25";		
			}
			final Cursor c = db.rawQuery(query, null);
			if (c != null && c.moveToFirst()) {
				do {
					String note = c.getString(0);
					Timestamp time = new Timestamp(Order.GetTimeFromString(c.getString(1)));
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time.getTime());
					if (note.length()>0){
						retVal += String.format("%tb %te", cal,cal) + ":" +note +"\n";
					}
				} while (c.moveToNext());
			}
			c.close();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return retVal;
	}
	
	public String getPastNotesForAddressAndAptNo(String address,String aptNo) {
		currentAptNo=aptNo;
		String retVal = "";
		//if (aptNo.length() < 1) return retVal;
		try {
			address = DatabaseUtils.sqlEscapeString(address);
			String query = "SELECT "+Notes+",Time FROM " + DATABASE_TABLE + " WHERE "+Address+" LIKE "+address+" AND "+AptNumber+" LIKE '"+aptNo+"' ORDER BY Time DESC LIMIT 0,25";
			final Cursor c = db.rawQuery(query, null);
			if (c != null && c.moveToFirst()) {
				do {
					String note = c.getString(0);
					Timestamp time = new Timestamp(Order.GetTimeFromString(c.getString(1)));
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time.getTime());
					if (note.length()>0){
						retVal += String.format("%tb %te", cal,cal) + ":" +note +"\n";
					}
				} while (c.moveToNext());
			}
			c.close();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return retVal;
	}

	
}