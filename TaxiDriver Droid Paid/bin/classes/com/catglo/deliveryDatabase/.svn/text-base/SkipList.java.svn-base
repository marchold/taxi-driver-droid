package com.catglo.deliveryDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;

import android.util.Log;

class SkipNode implements Comparable<String> {
	public static final int	MAX_LEVELS	= 12;
	Street[]				nodes;

	public String getKey() {
		return new String("");
	}

	public int compareTo(final String arg0) {
		// TODO Auto-generated method stub
		return 0;
	};
}

class NumberRange {
	int	start;
	int	stop;

	public NumberRange(final int start, final int stop) {
		this.start = start;
		this.stop = stop;
	}
}

class Street extends SkipNode {
	public Street(final String name) {
		super();
		this.name = name;
	}

	void update(final int start, final int stop) {
		int length = 0;
		if (range != null) {
			length = range.length;
		}

		if (numberRangeCount >= length) {
			final NumberRange[] temp = new NumberRange[length + 5];
			for (int i = 0; i < length; i++) {
				temp[i] = range[i];
			}
			range = temp;
		}
		range[numberRangeCount] = new NumberRange(start, stop);
		numberRangeCount++;
	}

	public Street(final String name, final int start, final int stop) {
		super();
		this.name = name;
		update(start, stop);
	}

	String			name;
	NumberRange[]	range;
	int				numberRangeCount	= 0;

	@Override
	public int compareTo(final String another) {
		return name.compareTo(another);
	}

	@Override
	public String getKey() {
		return name;
	}

	public void write(final DataOutputStream objStream) throws IOException {
		final int l = name.length();
		objStream.writeByte(l);
		final char[] c = name.toCharArray();
		for (int i = 0; i < l; i++) {
			objStream.writeByte(c[i]);
		}
		if (numberRangeCount > 255) {
			numberRangeCount = 255;
		}
		objStream.writeByte(numberRangeCount);
		for (int i = 0; i < numberRangeCount; i++) {
			objStream.writeInt(range[i].start);
			objStream.writeInt(range[i].stop);
		}
	}

	static public Street read(final DataInputStream objStream) throws IOException {
		Street s;
		int l = objStream.readByte();
		final char[] c = new char[l];
		for (int i = 0; i < l; i++) {
			c[i] = (char) objStream.readByte();
		}
		s = new Street(new String(c));

		l = objStream.readByte();
		s.range = new NumberRange[l];
		for (int i = 0; i < l; i++) {
			s.range[i] = new NumberRange(objStream.readInt(), objStream.readInt());
		}
		return s;
	}
}

public class SkipList implements Enumeration<Street> {

	Street			head;
	Street			tail;
	int				currentLevels;
	Random			randomGenerator;
	int				maxLevels	= 12;
	private int		size		= 0;
	private int		Street;
	private Street	it;

	public int size() {
		return size;
	}

	public SkipList() {
		this.head = new Street(null);
		this.head.nodes = new Street[SkipNode.MAX_LEVELS];
		this.tail = new Street(null);
		this.tail.nodes = new Street[SkipNode.MAX_LEVELS];
		currentLevels = SkipNode.MAX_LEVELS;
		randomGenerator = new Random(1);
		for (int i = 0; i < SkipNode.MAX_LEVELS; i++) {
			this.head.nodes[i] = tail;
		}
	}

	synchronized public void insert(final Street newNode) {
		if (newNode == null) return;

		final Street[] update = new Street[SkipNode.MAX_LEVELS];

		size++;

		final float p = 50; // 50% prob
		int level = 1;
		while (Math.abs(randomGenerator.nextInt()) % 100 > p && level < SkipNode.MAX_LEVELS) {
			level++;
		}

		newNode.nodes = new Street[level];

		Street current = head;

		for (int i = current.nodes.length - 1; i >= 0; i--) {
			while (current.nodes[i] != tail && ((Comparable) current.nodes[i]).compareTo(newNode.getKey()) < 0) {
				current = current.nodes[i];
			}
			update[i] = current;
		}

		for (int i = 0; i < newNode.nodes.length; i++) {
			newNode.nodes[i] = update[i].nodes[i];
			update[i].nodes[i] = newNode;
		}
	}

	synchronized public boolean remove(final String key) {
		Street current = head;
		boolean retVal = false;

		if (key.startsWith("190")) {
			Log.i("Removing", key);
		}

		int i = current.nodes.length;
		do { // set current to our items parent,
			i--;
			while (current.nodes[i] != tail && key.compareTo(current.nodes[i].getKey()) > 0) {
				if (current.nodes[i] != null && key.compareTo(current.nodes[i].getKey()) == 0) {
					current.nodes[i] = current.nodes[i].nodes[i];
					retVal = true;
				}
				current = current.nodes[i];
			}
		} while (i > 0);
		return retVal;
	}

	public boolean contains(final String key) {
		if (find(key) == null) return false;
		else return true;
	}

	synchronized public Street find(final String key) {
		Street current = head;

		int i = current.nodes.length;
		do {
			i--;
			while (current.nodes[i] != tail && key.compareTo(current.nodes[i].getKey()) > 0) {
				current = current.nodes[i];
				// TODO: I need to break here for perf maybe?
			}
		} while (i > 0);

		if (current.nodes[i] != tail && key.compareTo(current.nodes[i].getKey()) == 0) return current.nodes[i];
		return null;
	}

	synchronized public String[] getList() {
		final String[] list = new String[size];
		if (size > 0) {
			int i = 0;
			SkipNode cur = head.nodes[0];
			do {
				list[i++] = new String(cur.getKey());
				cur = cur.nodes[0];
			} while (cur.nodes[0] != null && cur.nodes[0] != tail);

			list[i] = new String(cur.getKey());
		}
		return list;
	}

	public boolean hasMoreElements() {
		if (it.nodes[0] != null && it.nodes[0] != tail) return true;
		return false;
	}

	public Street nextElement() {
		final Street s = it.nodes[0];
		it = s;
		return s;
	}

	Enumeration<Street> enumerate() {
		it = head;
		return this;
	}
}
