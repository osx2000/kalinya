package com.kalinya.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.kalinya.enums.RoundingType;

public class NumberUtil {

	public static final int SCALE = 12;
	public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	/**
	 * Uses the parameter rounding type to return the value rounded or truncated
	 * to the parameter number of decimal places
	 * 
	 * @param d
	 * @param roundingType
	 * @param places
	 * @return
	 */
	public static double roundPayment(double d, RoundingType roundingType, int places) {
		if(roundingType == null) {
			return d;
		}
		switch(roundingType) {
		case NONE:
			return d;
		case ROUND:
			return round(d, places);
		case TRUNCATE:
			return truncate(d, places);
		default:
			throw new UnsupportedOperationException("Rounding type [" + roundingType.name() + "] is not supported");
		}
	}

	/**
	 * Rounds a value to a given number of decimal places
	 * 
	 * @param d
	 * @param roundingType
	 * @param places
	 * @return
	 */
	public static double round(double d, int places) {
		if (places < 0) {
			throw new IllegalArgumentException("Number of places to round [" + places + "] cannot be less than zero");
		}
		if (d > 0) {
			return new BigDecimal(String.valueOf(d)).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			return new BigDecimal(String.valueOf(d)).setScale(places, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		}
	}

	/**
	 * Truncates a value to a given number of decimal places
	 * @param d
	 * @param places
	 * @return
	 */
	public static double truncate(double d, int places) {
		if (places < 0) {
			throw new IllegalArgumentException("Number of places to truncate [" + places + "] cannot be less than zero");
		}
		if (d > 0) {
			return new BigDecimal(String.valueOf(d)).setScale(places, BigDecimal.ROUND_FLOOR).doubleValue();
		} else {
			return new BigDecimal(String.valueOf(d)).setScale(places, BigDecimal.ROUND_CEILING).doubleValue();
		}
	}
	
	public static BigDecimal newBigDecimal(int i) {
		return newBigDecimal(String.valueOf(i));
	}
	
	public static BigDecimal newBigDecimal(double d) {
		return newBigDecimal(d, SCALE);
	}

	public static BigDecimal newBigDecimal(double d, int scale) {
		return new BigDecimal(d).setScale(scale, ROUNDING_MODE);
	}

	public static BigDecimal newBigDecimal(String s) {
		return newBigDecimal(Double.valueOf(s.replace(",","")));
	}

	public static BigDecimal newBigDecimal(String s, int scale) {
		return newBigDecimal(Double.valueOf(s.replace(",","")), scale);
	}

	public static BigDecimal getStandardDeviation(Collection<? extends BigDecimal> bds) {
		return NumberUtil.newBigDecimal(
				createStatisticsDataSet(bds)
				.getStandardDeviation());
	}
	
	private static DescriptiveStatistics createStatisticsDataSet(Collection<? extends BigDecimal> bds) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(BigDecimal bd: bds) {
			stats.addValue(bd.doubleValue());
		}
		return stats;
	}

	/**
	 * Returns the sum of the values in the input array, or
	 * <code>Double.NaN</code> if the array is empty. Rounds to
	 * <code>scale</code> decimal places
	 * <p>
	 * Throws <code>IllegalArgumentException</code> if the input array is null.
	 * </p>
	 * 
	 * @param numbers
	 *            The array of doubles
	 * @param scale
	 *            The number of decimal points to round
	 * @return
	 * @see org.apache.commons.math3.stat.StatUtils#sum(double[])
	 */
	public static BigDecimal sum(double[] numbers, int scale) {
		return newBigDecimal(StatUtils.sum(numbers), scale);
	}

	/**
	 * Returns the sum of the values in the input array, or
	 * <code>Double.NaN</code> if the array is empty.
	 * <p>
	 * Throws <code>IllegalArgumentException</code> if the input array is null.
	 * </p>
	 * 
	 * @param numbers
	 *            The array of doubles
	 * @return
	 * @see org.apache.commons.math3.stat.StatUtils#sum(double[])
	 */
	public static BigDecimal sum(double[] numbers) {
		return new BigDecimal(StatUtils.sum(numbers));
	}

	/**
	 * Returns the sum of the values in the input collection
	 * 
	 * @param values
	 * @return
	 */
	public static BigDecimal sum(Collection<BigDecimal> values) {
		BigDecimal sum = BigDecimal.ZERO;
		for(BigDecimal value: values) {
			sum = sum.add(value);
		}
		return sum;
	}

	/**
	 * Returns the array of Doubles as a List
	 * 
	 * @param dbls
	 * @return
	 */
	public static List<BigDecimal> getDoubleArrayAsListBigDecimal(double[] dbls) {
		List<BigDecimal> list = new ArrayList<>();
		for(double dbl: dbls) {
			list.add(newBigDecimal(dbl));
		}
		return list;
	}
}
