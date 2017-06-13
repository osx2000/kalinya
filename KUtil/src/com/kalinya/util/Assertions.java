package com.kalinya.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Map;

public final class Assertions {

	private static final int SCALE = 6;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	private static final BigDecimal ONE = BigDecimal.ONE.setScale(SCALE, ROUNDING_MODE);
	private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);
	
    private Assertions() {
        // can not be instantiated
    }

    /**
	 * Throws an IllegalArgumentException if the parameter is null
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
    public static void notNull(final String parameterName, final Object parameter) {
    	notNull(parameterName, "", parameter);
    }
    
    /**
	 * Throws an IllegalArgumentException if the parameter is null
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter
	 *            The parameter to test
	 */
    public static void notNull(final String parameterName, final String message, final Object parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be null! %s", parameterName, message));
        }
    }
    
    /**
     * Throws an IllegalArgumentException if the parameter is null or the Collection is empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
     */
    public static void notNullOrEmpty(final String parameterName, final Collection<?> parameter) {
    	notNullOrEmpty(parameterName, "", parameter);
    }
    
    /**
	 * Throws an IllegalArgumentException if the parameter is null or the
	 * Collection is empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter
	 *            The parameter to test
	 */
	public static void notNullOrEmpty(final String parameterName, final String message, final Collection<?> parameter) {
		notNull(parameterName, message, parameter);
    	if (parameter.size() == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty! %s", parameterName, message));
        }
    }
    
    /**
	 * Throws an IllegalArgumentException if the parameter is null or the Map is
	 * empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
    public static <K, V> void notNullOrEmpty(final String parameterName, final Map<K,V> parameter) {
    	notNullOrEmpty(parameterName, "", parameter);
    }
    
    /**
	 * Throws an IllegalArgumentException if the parameter is null or the Map is
	 * empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter
	 *            The parameter to test
	 */
    public static <K, V> void notNullOrEmpty(final String parameterName, final String message, final Map<K,V> parameter) {
        notNull(parameterName, message, parameter);
    	if (parameter.size() == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty! %s", parameterName, message));
        }
    }

    /**
	 * Throws an IllegalArgumentException if the parameter is null or the array is
	 * empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
	public static void notNullOrEmpty(String parameterName, Object[] parameter) {
		notNullOrEmpty(parameterName, "", parameter);
	}
	
	/**
	 * Throws an IllegalArgumentException if the parameter is null or the array
	 * is empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter
	 *            The parameter to test
	 */
	public static void notNullOrEmpty(String parameterName, final String message, Object[] parameter) {
		notNull(parameterName, parameter);
    	if (parameter.length == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty! %s", parameterName, message));
        }
	}
	
    /**
	 * Throws an IllegalArgumentException if the parameter is null or the array is
	 * empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
	public static void notNullOrEmpty(final String parameterName, final int[] parameter) {
		notNullOrEmpty(parameterName, "", parameter);
	}
	
	/**
	 * Throws an IllegalArgumentException if the parameter is null or the array
	 * is empty
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter
	 *            The parameter to test
	 */
	public static void notNullOrEmpty(String parameterName, final String message, int[] parameter) {
		notNull(parameterName, parameter);
    	if (parameter.length == 0) {
            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty! %s", parameterName, message));
        }
	}
	
	 /**
		 * Throws an IllegalArgumentException if the parameter is null or the array is
		 * empty
		 * 
		 * @param parameterName
		 *            The name of the parameter
		 * @param parameter
		 *            The parameter to test
		 */
		public static void notNullOrEmpty(final String parameterName, final double[] parameter) {
			notNullOrEmpty(parameterName, "", parameter);
		}
		
		/**
		 * Throws an IllegalArgumentException if the parameter is null or the array
		 * is empty
		 * 
		 * @param parameterName
		 *            The name of the parameter
		 * @param message
		 *            The message to accompany the IllegalArgumentException
		 * @param parameter
		 *            The parameter to test
		 */
		public static void notNullOrEmpty(String parameterName, final String message, double[] parameter) {
			notNull(parameterName, parameter);
	    	if (parameter.length == 0) {
	            throw new IllegalArgumentException(String.format("Parameter [%s] must not be empty! %s", parameterName, message));
	        }
		}
	
	/**
	 * Throws an IllegalArgumentException if the parameter is zero. Use this
	 * prior to dividing by zero.
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
	public static void notZero(final String parameterName, final BigDecimal parameter) {
		notNull(parameterName, parameter);
		if(parameter.compareTo(BigDecimal.ZERO) == 0) {
			throw new IllegalArgumentException(String.format("Parameter [%s] must not be zero!", parameterName));
		}
	}
	
	/**
	 * Throws an IllegalArgumentException if the parameter is zero. Use this
	 * prior to dividing by zero.
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
	public static void notZero(final String parameterName, final double parameter) {
		notZero(parameterName, NumberUtil.newBigDecimal(parameter));
	}

	/**
	 * Throws an IllegalStateException if the parameter map's values do not sum
	 * to 1.000000
	 * 
	 * @param <K>
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
	public static <K> void sumsToOne(final String parameterName, final Map<K, BigDecimal> parameter) {
		BigDecimal sum = ZERO;
		for(BigDecimal value: parameter.values()) {
			sum = sum.add(value);
		}
		if(sum.setScale(SCALE, ROUNDING_MODE).compareTo(ONE) != 0) {
			throw new IllegalStateException(String.format("Parameter [%s], value [%s] must sum to one!", 
					parameterName, StringUtil.formatPrice(sum)));
		}
	}

	/**
	 * Throws an IllegalStateException if the parameter map's values do not
	 * sum to 1.000000
	 * 
	 * @param <K>
	 * @param <L>
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter
	 *            The parameter to test
	 */
	public static <K, L> void sumsToOneMapOfMap(final String parameterName, final Map<K, Map<L, BigDecimal>> parameter) {
		for(K key: parameter.keySet()) {
			Assertions.sumsToOne(parameterName, parameter.get(key));
		}
	}
	
	/**
	 * Throws an IllegalArgumentException if the parameters are equal.
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter1
	 *            The first parameter to test
	 * @param parameter2
	 *            The second parameter to test
	 */
	public static void isNotEqual(final String parameterName, final Object parameter1, final Object parameter2) {
		isNotEqual(parameterName, "", parameter1, parameter2);
	}
	
	/**
	 * Throws an IllegalArgumentException if the parameters are equal.
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter1
	 *            The first parameter to test
	 * @param parameter2
	 *            The second parameter to test
	 */
	public static void isNotEqual(final String parameterName, final String message, final Object parameter1, final Object parameter2) {
		if (parameter1.equals(parameter2)) {
			throw new IllegalArgumentException(String.format("Parameters [%s] and [%s] must not be equal! %s", 
					parameterName, parameter1, parameter2, message));
		}
	}
	
	/**
	 * Throws an IllegalArgumentException if the parameters are not equal. E.g.
	 * use this to check that array lengths are equal.
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param parameter1
	 *            The first parameter to test
	 * @param parameter2
	 *            The second parameter to test
	 */
	public static void isEqual(final String parameterName, final int parameter1, final int parameter2) {
		isEqual(parameterName, "", parameter1, parameter2);
	}
	
	/**
	 * Throws an IllegalArgumentException if the parameters are not equal. E.g.
	 * use this to check that array lengths are equal.
	 * 
	 * @param parameterName
	 *            The name of the parameter
	 * @param message
	 *            The message to accompany the IllegalArgumentException
	 * @param parameter1
	 *            The first parameter to test
	 * @param parameter2
	 *            The second parameter to test
	 */
	public static void isEqual(final String parameterName, final String message, final int parameter1, final int parameter2) {
		if (parameter1 != parameter2) {
			throw new IllegalArgumentException(String.format("Parameters [%s] and [%s] must be equal! %s", 
					parameterName, parameter1, parameter2, message));
		}
	}
}
