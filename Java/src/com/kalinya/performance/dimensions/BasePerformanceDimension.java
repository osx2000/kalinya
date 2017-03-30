package com.kalinya.performance.dimensions;

import java.io.Serializable;

public class BasePerformanceDimension implements Comparable<BasePerformanceDimension>, Serializable {
	public BasePerformanceDimension() {
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	@Override
	public boolean equals(Object obj) {
        if(!(obj instanceof BasePerformanceDimension)) {
            return false;
        }
        return this.compareTo((BasePerformanceDimension) obj) == 0;
    }
	
	@Override
	public int compareTo(BasePerformanceDimension that) {
		if (this == that) {
			return 0;
		}
		
		int i;
		
		i = getName().compareTo(that.getName());
		if(i != 0) return i;
		
		return 0;
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}
}
