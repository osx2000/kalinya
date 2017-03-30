package com.kalinya.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public final class Assertions {

    private Assertions() {
        // can not be instantiated
    }

    public static void notNull(final String parameterName, final Object parameter) {
    	notNull(parameterName, "", parameter);
    }
    
    public static void notNull(final String parameterName, final String message, final Object parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be null! %s", parameterName, message));
        }
    }
    
    public static void notNullOrEmpty(final String parameterName, final Collection<?> parameter) {
        notNull(parameterName, parameter);
    	if (parameter.size() == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty!", parameterName));
        }
    }
    
    public static void notNullOrEmpty(final String parameterName, final String message, final Collection<?> parameter) {
        notNull(parameterName, message, parameter);
    	if (parameter.size() == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty! %s", parameterName, message));
        }
    }
    
    public static <K, V> void notNullOrEmpty(final String parameterName, final Map<K,V> parameter) {
        notNull(parameterName, parameter);
    	if (parameter.size() == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty!", parameterName));
        }
    }
    
    public static <K, V> void notNullOrEmpty(final String parameterName, final String message, final Map<K,V> parameter) {
        notNull(parameterName, message, parameter);
    	if (parameter.size() == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty! %s", parameterName, message));
        }
    }

	public static void notZero(final String parameterName, final BigDecimal bd) {
		notNull(parameterName, bd);
		if(bd.compareTo(BigDecimal.ZERO) == 0) {
			throw new IllegalArgumentException(String.format("Parameter [%s] must not be zero!", parameterName));
		}
	}
	
	public static void notZero(final String parameterName, final double d) {
		notZero(parameterName, NumberUtil.newBigDecimal(d));
	}
}
