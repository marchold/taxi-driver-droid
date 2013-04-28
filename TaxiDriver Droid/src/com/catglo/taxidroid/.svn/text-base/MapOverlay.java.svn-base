package com.catglo.taxidroid;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapOverlay extends ItemizedOverlay<OverlayItem> {

	public MapOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}

	@Override
	public int size() {
		return overlays.size();
	}
	ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	
	public void addOverlay(OverlayItem overlay) {
	    overlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return overlays.get(i);
	}
	
}
