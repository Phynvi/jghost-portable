package org.whired.ghostclient.awt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class SortedListModel extends AbstractListModel {

	// Define a SortedSet
	final SortedSet<Object> model;

	public SortedListModel() {
		// Create a TreeSet
		// Store it in SortedSet variable
		model = new TreeSet<Object>(new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				final String o1v = o1.toString().toLowerCase();
				final String o2v = o2.toString().toLowerCase();
				int max = o1v.length() > o2v.length() ? o2v.length() : o1v.length();
				for (int i = 0; i < max; i++) {
					if (o1v.charAt(i) == o2v.charAt(i)) {
						continue;
					}
					else if (o1v.charAt(i) < o2v.charAt(i)) {
						return -1;
					}
					else {
						return 1;
					}
				}
				return o1v.length() > o2v.length() ? 1 : o1v.length() < o2v.length() ? -1 : 0;
			}
		});
	}

	// ListModel methods
	@Override
	public int getSize() {
		return model.size();
	}

	public int size() {
		return model.size();
	}

	@Override
	public Object getElementAt(int index) {
		return model.toArray()[index];
	}

	// Other methods
	public void addElement(Object element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public void addAll(Object elements[]) {
		int h = size();
		int t = h + elements.length;
		Collection<Object> c = Arrays.asList(elements);
		model.addAll(c);
		fireIntervalAdded(this, h, t);
		fireContentsChanged(this, h, t);
	}

	public void copyInto(int startIdx, Object[] elems) {
		System.arraycopy(model.toArray(), startIdx, elems, 0, elems.length);
	}

	public void clear() {
		model.clear();
		fireIntervalRemoved(this, 0, getSize());
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

	public Object firstElement() {
		return model.first();
	}

	public Iterator<Object> iterator() {
		return model.iterator();
	}

	public Object lastElement() {
		return model.last();
	}

	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireIntervalRemoved(this, 0, getSize());
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}