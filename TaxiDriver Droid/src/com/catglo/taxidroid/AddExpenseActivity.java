package com.catglo.taxidroid;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.catglo.deliveryDatabase.DataBase;
import com.catglo.deliveryDatabase.DropOff;
import com.catglo.deliveryDatabase.Expense;
import com.catglo.deliveryDatabase.Order;
import com.catglo.taxidroid.R;

import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AddExpenseActivity extends TaxiDroidBaseActivity {
	
	
	private ListView expenseList;
	private EditText description;
	private EditText amount;
	private AutoCompleteTextView category;
	private Button addButton;
	private int viewingShift;
	private ArrayList<Expense> list;
	private TextView total;


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses);
        
        expenseList = (ListView)findViewById(R.id.listView1);
        description = (EditText)findViewById(R.id.editText2);
        amount = (EditText)findViewById(R.id.editText1);
        total = (TextView)findViewById(R.id.textView2);
        category = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        addButton = (Button)findViewById(R.id.button1);
        addButton.setOnClickListener(new OnClickListener(){public void onClick(View v) {
        	Expense e = new Expense();
        	e.category = category.getEditableText().toString();
        	e.description = description.getEditableText().toString();
        	try {
        		e.amount = new Float(amount.getEditableText().toString());
        	} catch (NumberFormatException exception){
        		e.amount = 0;
        	}
        	e.shiftId = viewingShift;
        	dataBase.add(e);
        	updateUI();
        	description.setText("");
        	amount.setText("");
        	category.setText("");
		}});
        viewingShift = dataBase.getCurShift(); 
        
        updateUI();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, dataBase.getExpensCategories());
        category.setAdapter(adapter);
        category.setThreshold(1);
        
	}
	
	
	private void updateUI() {
		final ArrayList<HashMap<String, String>> displayValues = new ArrayList<HashMap<String, String>>();
		list = dataBase.getShiftExpenses(viewingShift);
		
		for (int i =0; i<list.size();i++){
			Expense e = list.get(i);
			final HashMap<String, String> map = new HashMap<String, String>();
			
			map.put("category", e.category);
			map.put("description", e.description);
			map.put("amount", TaxiDroidBaseActivity.getFormattedCurrency(e.amount));
			map.put("expenseTime", TaxiDroidBaseActivity.getFormattedTime(e.expenseTime));
			map.put("ID", ""+e.ID);
			
			displayValues.add(map);
		}
		
		
		final SimpleAdapter adapter = new SimpleAdapter(this, displayValues, R.layout.expense_row, 
				new String[] { "category",     "description", "amount",        "ID"}, 
				new int[] {    R.id.textView1, R.id.textView3, R.id.textView2 , R.id.databasePrimaryKey});

		expenseList.setAdapter(adapter);
		
		total.setText(TaxiDroidBaseActivity.getFormattedCurrency(dataBase.getTotalExpensesForShift(viewingShift)));

	}
	
	
	String formatMoney(Float value){
		if (value == 0)
			return "";
		return currency.format(value);
	}
	
	
	@Override
	public void onPause(){
	    super.onPause();
	}
	
	
	
	
}
