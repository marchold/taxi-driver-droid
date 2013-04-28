package com.catglo.taxidroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class OrderListView extends ListView {
//	private static final int			EDIT_ID		= 0;
//	private static final int			DELETE_ID	= 1;
	private View						dragView;
	private int							dragPoint;
	private int							coordOffset;
	private final Context				context;
	private int							dragPos;
	private int							firstDragPos;
	private int							height;
	private int							upperBound;
	private int							lowerBound;
	private final int					touchSlop;
	private WindowManager				windowManager;
	private WindowManager.LayoutParams	windowParams;
	private final Rect					tempRect	= new Rect();
	private Bitmap						dragBitmap;
	@SuppressWarnings("unused")
	private DragListener				dragListener;
	private DropListener				dropListener;
	@SuppressWarnings("unused")
	private RemoveListener				removeListener;
	private final int					listItemsHeight;

	void edit(final long l) {

	}

	void delete(final long l) {

	}

	public void setDragListener(final DragListener l) {
		dragListener = l;
	}

	public void setDropListener(final DropListener l) {
		dropListener = l;
	}

	public void setRemoveListener(final RemoveListener l) {
		removeListener = l;
	}

	public interface DragListener {
		void drag(int from, int to);
	}

	public interface DropListener {
		void drop(int from, int to);
	}

	public interface RemoveListener {
		void remove(int which);
	}

	LinearLayout	tableRowLayout;

	public OrderListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// Distance a touch can wander before we think  the  user  is
		// scrolling in pixels
		this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop(); 
		
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
	
		if (display.getWidth() > 320 || display.getHeight() > 480){
			listItemsHeight = 80;//60;
		} else {
			listItemsHeight = 64;//40;
		}
		

	}

	private void adjustScrollBounds(final int y) {
		if (y >= height / 3) {
			upperBound = height / 3;
		}
		if (y <= height * 2 / 3) {
			lowerBound = height * 2 / 3;
		}
	}

	private void stopDragging() {
		final WindowManager wm = (WindowManager) context.getSystemService("window");
		wm.removeView(dragView);
		dragView = null;
		if (dragBitmap != null) {
			dragBitmap.recycle();
			dragBitmap = null;
		}
	}

	private void unExpandViews() {
		for (int i = 0;; i++) {
			View v = getChildAt(i);
			if (v == null) {
				/*
				 * if (deletion) { // HACK force update of mItemCount int position = getFirstVisiblePosition(); int y =
				 * getChildAt(0).getTop(); setAdapter(getAdapter()); setSelectionFromTop(position, y); // end hack }
				 */
				layoutChildren(); // force children to be recreated where needed
				v = getChildAt(i);
				if (v == null) {
					break;
				}
			}
			final ViewGroup.LayoutParams params = v.getLayoutParams();
			params.height = listItemsHeight;// 64;
			v.setLayoutParams(params);
			v.setVisibility(View.VISIBLE);
		}
	}  

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if (dragView != null) {
			final int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				// Rect r = tempRect;
				// dragView.getDrawingRect(r);
				stopDragging();

				// if (mRemoveMode == SLIDE && ev.getX() > r.right * 3 / 4) {
				// if (mRemoveListener != null) {
				// mRemoveListener.remove(mFirstDragPos);
				// }
				// unExpandViews(true);
				// } else {
				if (dropListener != null && dragPos >= 0 && dragPos < getCount()) {
					dropListener.drop(firstDragPos, dragPos);
				}
				unExpandViews();
				// }
				break;

			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				final int x = (int) event.getX();
				final int y = (int) event.getY();

				final int itemnum = getItemForPosition(y);

				doDragView(x, y);

				if (itemnum >= 0) {
				//	if (action == MotionEvent.ACTION_DOWN || itemnum != dragPos) {
				//		if (dragListener != null) {
				//			dragListener.drag(dragPos, itemnum);
				//		}
				//	}
					dragPos = itemnum;
					doExpansion();
				}
				int speed = 0;
				adjustScrollBounds(y);
				if (y > lowerBound) {
					// scroll the list up a bit
					speed = y > (height + lowerBound) / 2 ? listItemsHeight / 4 : 4;
				} else if (y < upperBound) {
					// scroll the list down a bit
					speed = y < upperBound / 2 ? -(listItemsHeight / 4)/* 16 */
					: -4;
				}
				if (speed != 0) {
					int ref = pointToPosition(0, height / 2);
					if (ref == AdapterView.INVALID_POSITION) {
						// we hit a divider or an invisible view, check
						// somewhere else
						ref = pointToPosition(0, height / 2 + getDividerHeight() + listItemsHeight);// 64);
					}
					final View v = getChildAt(ref - getFirstVisiblePosition());
					if (v != null) {
						final int pos = v.getTop();
						setSelectionFromTop(ref, pos - speed);
					}
				}
				// dragView=null;
				break;
			}
			return true;
		} else return super.onTouchEvent(event);
		// return true; //Otherwise we get multiple select for click on no list
		// item
	}

	private void doExpansion() {
		int childnum = dragPos - getFirstVisiblePosition();
		if (dragPos > firstDragPos) {
			childnum++;
		}

		final View first = getChildAt(firstDragPos - getFirstVisiblePosition());
		for (int i = 0;; i++) {
			final View vv = getChildAt(i);
			if (vv == null) {
				break;
			}
			int height = listItemsHeight;// 64; //why 64??
			int visibility = View.VISIBLE;
			if (vv.equals(first)) {
				// processing the item that is being dragged
				if (dragPos == firstDragPos) {
					// hovering over the original location
					visibility = View.INVISIBLE;
				} else {
					// not hovering over it
					height = 1;
				}
			} else if (i == childnum) {
				if (dragPos < getCount() - 1) {
					height = listItemsHeight * 2;// 128;//why 128??
				}
			}
			final ViewGroup.LayoutParams params = vv.getLayoutParams();
			params.height = height;
			vv.setLayoutParams(params);
			vv.setVisibility(visibility);
		}
	}

	private int myPointToPosition(final int x, final int y) {
		final Rect frame = tempRect;
		final int count = getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			final View child = getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y)) return getFirstVisiblePosition() + i;
		}
		return INVALID_POSITION;
	}

	private int getItemForPosition(final int y) {
		final int adjustedy = y - dragPoint - listItemsHeight / 2;// 32; //why 32??
		int pos = myPointToPosition(0, adjustedy);
		if (pos >= 0) {
			if (pos <= firstDragPos) {
				pos += 1;
			}
		} else if (adjustedy < 0) {
			pos = 0;
		}
		return pos;
	}

	private void doDragView(final int x, final int y) {

		// PizzaDilevery.text.setText("y="+(y- dragPoint +
		// coordOffset)+" "+dragPoint + " "+coordOffset);

		// Clip drawing of the dragged item at the top of the list, otherwise it
		// looks a bit weird as I can cover the buttons
		// at the top get covered by the drag box, maybe I will make it turn in
		// to a little cloud to indicate it's delete-able in the future
		if (y - dragPoint + coordOffset <= coordOffset) {
			windowParams.y = coordOffset;
		} else {
			windowParams.y = y - dragPoint + coordOffset;
		}

		windowManager.updateViewLayout(dragView, windowParams);
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			final int itemnum = pointToPosition(x, y);
			if (itemnum == AdapterView.INVALID_POSITION) {
				break;
			}

			if (x < listItemsHeight + 4) {
				// ViewGroup item = (ViewGroup) getChildAt(itemnum -
				// getFirstVisiblePosition());
				final View item = getChildAt(itemnum - getFirstVisiblePosition());

				dragPoint = y - item.getTop();
				coordOffset = (int) event.getRawY() - y;

				item.setDrawingCacheEnabled(true);
				// Create a copy of the drawing cache so that it does not get
				// recycled
				// by the framework when the list tries to clean up memory
				final Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
				startDragging(bitmap, y);
				dragPos = itemnum;
				firstDragPos = dragPos;
				height = getHeight();

				final int touchSlop = this.touchSlop;
				upperBound = Math.min(y - touchSlop, height / 3);
				lowerBound = Math.max(y + touchSlop, height * 2 / 3);
				break;
			}
		}
		return super.onInterceptTouchEvent(event);
	}

	private void startDragging(final Bitmap bm, final int y) {
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP;
		windowParams.x = 0;
		windowParams.y = y - dragPoint + coordOffset;

		windowParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		windowParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		final ImageView v = new ImageView(context);
		// Bitmap bbm = BitmapFactory.decodeResource(context.getResources(),
		// android.R.drawable.ic_menu_add);
		v.setBackgroundColor(Color.LTGRAY);

		v.setImageBitmap(bm);

		if (dragBitmap != null) {
			dragBitmap.recycle();
		}
		dragBitmap = bm;

		windowManager = (WindowManager) context.getSystemService("window");
		// windowManager.
		windowManager.addView(v, windowParams);
		dragView = v;
	}

}