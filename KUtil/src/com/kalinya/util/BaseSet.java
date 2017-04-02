package com.kalinya.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BaseSet<E extends Comparable<E>> implements Set<E>, Serializable {
	private static final long serialVersionUID = -8390644896151388826L;
	private Set<E> set;
	
	public BaseSet() {
		set = createSet();
		
	}
	
	protected Set<E> createSet() {
		return new TreeSet<E>();
	}

	@Override 
	public String toString() {
		return toVerboseString();
	}
	
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder("<" + this.getClass().getSimpleName() + ">");
		sb.append(" Size [" + getCount() + "]");
		return sb.toString();
	}
	
	public String toVerboseString() {
		StringBuilder sb = new StringBuilder("<" + getClass().getSimpleName() + ">");
		sb.append(" Size [" + size() + "]");
		sb.append(getSetStringWithLineBreaks());
		return sb.toString();
	}
	
	public String toVerboseStringWithCap(int elementSizeToPrintVerbose) {
		return getCollectionElementsAsString(getSet(), elementSizeToPrintVerbose);
	}
	
	protected String getSetStringWithLineBreaks() {
		StringBuilder sb = new StringBuilder();
		for(E e: getSet()) {
			sb.append("\n" + e.toString());
		}
		return sb.toString();
	}

	public final Set<E> getSet() {
		return set;
	}
	
	@Override
	public boolean add(E arg0) {
		return getSet().add(arg0);
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		return getSet().addAll(arg0);
	}
	
	@Override
	public void clear() {
		getSet().clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return getSet().contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return getSet().containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return getSet().isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return getSet().iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return getSet().remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return getSet().removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return getSet().retainAll(arg0);
	}

	@Override
	public int size() {
		return getSet().size();
	}

	@Override
	public Object[] toArray() {
		return getSet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return getSet().toArray(arg0);
	}

	public int getCount() {
		return getSet().size();
	}

	public static <T> String getCollectionElementsAsString(Collection<T> collection) {
		return getCollectionElementsAsString(collection, 10);
	}
	
	public static <T> String getCollectionElementsAsString(Collection<T> collection, int elementSizeToPrintVerbose) {
		StringBuilder sb = new StringBuilder();
		if(collection == null || collection.size() == 0) {
			return "";
		}
		int i = 0;
		for(T t: collection) {
			if(t != null) {
				if(i > 0) {
					sb.append("\n ");
				}
				sb.append(t.toString());
				if(i >= elementSizeToPrintVerbose) {
					sb.append("\n   " + (collection.size() - i) + " more... ");
					break;
				}
				i++;
			}
		}
		return sb.toString();
	}
	
	public static <K, V> String getCollectionElementsAsString(Map<K, V> map) {
		return getCollectionElementsAsString(map, 10);
	}
	
	public static <K, V> String getCollectionElementsAsString(Map<K, V> map, int elementSizeToPrintVerbose) {
		StringBuilder sb = new StringBuilder();
		if(map == null || map.size() == 0) {
			return "";
		}
		int i = 1;
		for(K k: map.keySet()) {
			if(i > 1) {
				sb.append("\n ");
			}
			sb.append(k.toString() + " = " + map.get(k).toString());
			if(i >= elementSizeToPrintVerbose) {
				sb.append("\n   " + (map.size() - i) + " more... \n");
				break;
			}
			i++;
		}
		return sb.toString();
	}
	
	/**
	 * Returns a Set that is the intersection of {@code this} Set and
	 * {@code that} Set
	 * 
	 * @param that
	 * @return
	 */
	public BaseSet<E> intersection(BaseSet<E> that) {
		BaseSet<E> intersection = new BaseSet<E>();
		for(E e: getSet()) {
			if(that.contains(e)) {
				intersection.add(e);
			}
		}
		return intersection;
	}
	
	/**
	 * Returns a Set that is the union of {@code this} Set and
	 * {@code that} Set
	 * 
	 * @param that
	 * @return
	 */
	public BaseSet<E> union(BaseSet<E> that) {
		BaseSet<E> union = new BaseSet<E>();
		union.addAll(getSet());
		union.addAll(that);
		return union;
	}
	
	/**
	 * Returns a Set that is a member of {@code that} Set and not a member of
	 * {@code this} Set
	 * 
	 * @param that
	 * @return
	 */
	public BaseSet<E> complement(BaseSet<E> that) {
		BaseSet<E> complement = new BaseSet<E>();
		complement.addAll(that);
		complement.removeAll(getSet());
		return complement;
	}
}
