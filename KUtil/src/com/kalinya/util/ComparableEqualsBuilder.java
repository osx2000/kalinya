package com.kalinya.util;

public class ComparableEqualsBuilder<T> {
	Comparable<T> lhs;
	Object rhs;
	
	private ComparableEqualsBuilder(){
	}
	
	public ComparableEqualsBuilder(Comparable<T> comparable, Object rhs) {
		this();
		this.lhs = comparable;
		this.rhs = rhs;
	}

	@SuppressWarnings("unchecked")
	public boolean build() {
		if (rhs == null) {
			return false;
		}
		if (rhs == this) {
			return true;
		}
		if (rhs.getClass() != lhs.getClass()) {
			return false;
		}
		return lhs.compareTo((T) rhs) == 0;
	}
}
