package com.kalinya.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.kalinya.enums.RoundingType;

public class NumberUtil {

	private static final int SCALE = 12;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	
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
}
