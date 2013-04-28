package com.catglo.deliveryDatabase;

import java.util.Iterator;
import java.util.LinkedList;

public class ZipHash extends LinkedList<ZipCode> {
	public boolean contains(String zip){
		int i = new Integer(zip);
		return contains(i);
	}
	
	public boolean contains(int zipCode){
		Iterator<ZipCode> i = iterator();
		while (i.hasNext()){
			ZipCode z = i.next();
			if (z.zipCode==new Integer(zipCode)){
				return true;
			}
		}	
		return false;
	}
	
	public ZipHash(final int order) {
		super();
	}

	float	minDist	= Float.MAX_VALUE;
	ZipCode	center;
 
	public void insert(final ZipCode t) {
		if (t.distance < minDist) {
			minDist = t.distance;
			center = t;
		}
		Iterator<ZipCode> i = iterator();
		while (i.hasNext()){
			ZipCode z = i.next();
			if (z.zipCode==t.zipCode){
				z.state = t.state;
				return;
			}
		}
		add(t);
	}

	public ZipCode get(String zipCode) {
		Iterator<ZipCode> i = iterator();
		while (i.hasNext()){
			ZipCode z = i.next();
			if (z.zipCode==new Integer(zipCode)){
				return z;
			}
		}
		return null;
	}

	public void remove(String zipCode) {
		Iterator<ZipCode> i = iterator();
		while (i.hasNext()){
			ZipCode z = i.next();
			if (z.zipCode==new Integer(zipCode)){
				i.remove();
			}
		}
	}

}
