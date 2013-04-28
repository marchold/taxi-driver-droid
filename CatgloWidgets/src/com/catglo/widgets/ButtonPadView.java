package com.catglo.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewFlipper;


public class ButtonPadView extends TableLayout implements OnItemClickListener, OnEditorActionListener{

	public EditText				edit;
	public ListView				list;
	protected EditText			forignEditor;
	protected Context						context;

	ViewFlipper					parentViewSwitcher;
	protected TextView			text;
	protected ImageButton		seven;
	protected ImageButton		eight;
	protected ImageButton		nine;
	protected ImageButton		four;
	protected ImageButton		five;
	protected ImageButton		six;
	protected ImageButton		one;
	protected ImageButton		two;
	protected ImageButton		three;
	protected ImageButton		dot;
	protected ImageButton		zero;
	protected ImageButton		del;
	public ImageButton			next;
	public ImageButton		    speakButton;
	
	public RelativeLayout	numbers;

	Runnable callback = null;
	public ProgressBar progressSpinnner;
	public Button customButton;
	private RelativeLayout buttons;
	public View numberLine;
	public View priceLine;
	public View addressLine;
	public View backButton;
	
	
	public void press(final int keyVal) {
		// Debug.stopMethodTracing();

		if (keyVal == -1) {
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD));
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_PERIOD));
		}
		if (keyVal == -2) {
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
		}
		if (keyVal == -3) {
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));
		} else {
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0 + keyVal));
			edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_0 + keyVal));
		}
	}

	public void setAdapter(final ArrayAdapter<String> adapter) {
		list.setAdapter(adapter);
	}

	public void setParentViewSwitcher(final ViewFlipper v) {
		parentViewSwitcher = v;
	}

	public void setEditor(final EditText t) {
		forignEditor = t;
	}

	public void setText(final CharSequence s) {
		text.setText(s);
		text.setVisibility(View.VISIBLE);
		((TextView)numbers.findViewById(R.id.textView1)).setVisibility(View.GONE);
		((TextView)numbers.findViewById(R.id.textView3)).setVisibility(View.GONE);
		((TextView)numbers.findViewById(R.id.textView4)).setVisibility(View.GONE);
		((TextView)numbers.findViewById(R.id.textView5)).setVisibility(View.GONE);
		
	}

	public ButtonPadView(Context context, final AttributeSet attrs) {
		super(context, attrs);
		Activity a = (Activity) context;
		this.context = a.getBaseContext();
		a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		numbers = (RelativeLayout) a.getLayoutInflater().inflate(R.layout.button_pad, null);
		a=null;
		
		numberLine = (View)numbers.findViewById(R.id.numberSelectedLine);
		priceLine = (View)numbers.findViewById(R.id.priceSelectedLine);
		addressLine = (View)numbers.findViewById(R.id.addressSelectedLine);
		
		one = (ImageButton) numbers.findViewById(R.id.Button01);
		one.requestFocus();
		
		two = (ImageButton) numbers.findViewById(R.id.Button02);
		three = (ImageButton) numbers.findViewById(R.id.Button03);
		four = (ImageButton) numbers.findViewById(R.id.Button04);
		five = (ImageButton) numbers.findViewById(R.id.Button05);
		six = (ImageButton) numbers.findViewById(R.id.Button06);
		seven = (ImageButton) numbers.findViewById(R.id.Button07);
		eight = (ImageButton) numbers.findViewById(R.id.Button08);
		nine = (ImageButton) numbers.findViewById(R.id.Button09);

		dot = (ImageButton) numbers.findViewById(R.id.ButtonDot);
		zero = (ImageButton) numbers.findViewById(R.id.ButtonZero);
		del = (ImageButton) numbers.findViewById(R.id.ButtonDel);
		next = (ImageButton) numbers.findViewById(R.id.ButtonNext);
		
		edit = (EditText) numbers.findViewById(R.id.buttonPadEdit);
		text = (TextView) numbers.findViewById(R.id.buttonPadText);
	
		list = (ListView) numbers.findViewById(R.id.buttonPadList);
		list.setFastScrollEnabled(true);
		
		progressSpinnner = (ProgressBar) numbers.findViewById(R.id.progressBar1);
		progressSpinnner.setVisibility(View.GONE);
		
		speakButton = (ImageButton) numbers.findViewById(R.id.ButtonSpeech);
		customButton = (Button) numbers.findViewById(R.id.button1);
		
		backButton = (View) numbers.findViewById(R.id.backButton);

		
		zero.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(0);
			}
		});
		one.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(1);
			}
		});
		two.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(2);
			}
		});
		three.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(3);
			}
		});
		four.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(4);
			}
		});
		five.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(5);
			}
		});
		six.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(6);
			}
		});
		seven.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(7);
			}
		});
		eight.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(8);
			}
		});
		nine.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(9);
			}
		});
		dot.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(-1);
			}
		});

		del.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				press(-2);
			}
		});

		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				nextView();
			}
		});

		addView(numbers);

		list.setCacheColorHint(0xeeeeeeFF);
		list.setBackgroundColor(0xeeeeeeFF);

		list.setOnItemClickListener(this);
		edit.clearFocus();
		list.setFocusable(true);
		list.setFocusableInTouchMode(true);
		list.requestFocus();
		
		edit.setOnEditorActionListener(this);
		
		//Detect on screen keyboard show/hide
		buttons = (RelativeLayout)numbers.findViewById(R.id.buttonPadButtonLayout);
		numbers.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {public void onGlobalLayout() {
	        int heightDiff = numbers.getRootView().getHeight() - numbers.getMeasuredHeight();
	        int threshold = 100;
	     //  Log.i("keyboard","keyboardHidden="+getResources().getConfiguration().+"Configuration.KEYBOARDHIDDEN_YES="+Configuration.KEYBOARDHIDDEN_YES);
	        	
	        
	        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
	        //	threshold=24; //bummer in landscape its 24 for up and down
	        }
	        Log.i("keyboard","diff = "+heightDiff+" threshold = "+threshold);
	        
	        if (heightDiff > threshold) { // if more than 100 pixels, its probably a keyboard...
	        	//onKeyboardShowen();
	        	if (buttons!=null) buttons.setVisibility(View.GONE);
	        } 
	        if (heightDiff < threshold) {
	        	//onKeyboardHidden();
	        	if (buttons!=null) buttons.setVisibility(View.VISIBLE);
	        }
		}});

	}


	public void setGestureListener(final GestureDetector gestureScanner, final OnTouchListener gestureListener,
			final OnClickListener newOrder) {
		// TODO Auto-generated method stub
		setOnTouchListener(gestureListener);

		numbers.setOnTouchListener(gestureListener);

		seven.setOnTouchListener(gestureListener);
		eight.setOnTouchListener(gestureListener);
		nine.setOnTouchListener(gestureListener);

		one.setOnTouchListener(gestureListener);
		two.setOnTouchListener(gestureListener);
		three.setOnTouchListener(gestureListener);

		four.setOnTouchListener(gestureListener);
		five.setOnTouchListener(gestureListener);
		six.setOnTouchListener(gestureListener);

		zero.setOnTouchListener(gestureListener);
		dot.setOnTouchListener(gestureListener);
		del.setOnTouchListener(gestureListener);

		edit.setOnTouchListener(gestureListener);
		list.setOnTouchListener(gestureListener);

	}

	public void setOnNextScreenAction(Runnable r){
		callback = r;
	}
	
	public void nextView() {
		if (parentViewSwitcher==null){
			
		} else {
			parentViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_left_in));
			parentViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_left_out));
			parentViewSwitcher.showNext();
			try {
				forignEditor.setText(edit.getText());
			} catch (final NullPointerException e) {
			}
		}
		if (callback != null)
			callback.run();
	}

	public void destroy() {
		parentViewSwitcher=null;
	}


	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		edit.setText(((TextView) arg1).getText());
		edit.setSelection(((TextView) arg1).getText().length(), ((TextView) arg1).getText().length());
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE){
			parentViewSwitcher.showNext();
			return true;
		}
		return false;
	}

}