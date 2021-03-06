package com.kalinya.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

public class StringUtil {
	/**
	 * Formats a date as MM/dd/yyyy
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		if(date == null) {
			return null;
		}
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		return df.format(date);
	}
	
	/**
	 * Formats a date as dd-MMM-yyyy
	 * @param date
	 * @return
	 */
	public static String formatInternationalDate(Date date) {
		if(date == null) {
			return "";
		}
		return new SimpleDateFormat("dd-MMM-yyyy").format(date);
	}
	
	public static String formatTime(Date date) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return df.format(date);
	}

	/**
	 * Formats double as a string with {@code precision} decimal places
	 * 
	 * @param d
	 * @param precision
	 * @return
	 */
	public static String formatDouble(double d, int precision) {
		return String.format("%1$,." + precision + "f", d);
	}

	/**
	 * Formats double as a string with {@code precision} decimal points
	 * 
	 * @param bd
	 * @param precision
	 * @return
	 */
	public static String formatDouble(BigDecimal bd, int precision) {
		return formatDouble(bd.doubleValue(), precision);
	}

	/**
	 * Formats double as a string with 2 decimal places
	 * 
	 * @param d
	 * @return
	 */
	public static String formatDouble(double d) {
		return formatDouble(d, 2);
	}
	
	
	public static String formatDouble(BigDecimal bd) {
		return formatDouble(bd.doubleValue());
	}

	/**
	 * Formats double as a string with 8 decimal places
	 */
	public static String formatPrice(double d) {
		return formatDouble(d, 8);
	}

	/**
	 * Formats BigDecimal as a string with 8 decimal places
	 */
	public static String formatPrice(BigDecimal bd) {
		return formatDouble(bd, 8);
	}
	
	/**
	 * Converts all the words in the parameter String into capitalized words.
	 * Each word is made up of an upper-case character and then a series of
	 * lower-case characters.
	 * 
	 * @param input
	 * @return
	 * @see org.apache.commons.lang3.text.WordUtils#capitalizeFully(String)
	 */
	public static String toTitleCase(String input) {
		return WordUtils.capitalizeFully(input);
	}
	
	public static <K,V> String getMapAsStringWithLineBreaks(Map<K, V> map) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(K key: map.keySet()) {
			if(i > 0) {
				sb.append("\n");
			}
			V value = map.get(key);
			sb.append(key.toString() + " = " + (value != null ? value.toString() : "(null)"));
			i++;
		}
		return sb.toString();
	}
	
	/**
	 * Returns a string of concatenated objects, separated by the delimiter
	 * 
	 * @param objects
	 * @param delimiter
	 * @return
	 */
	public static String join(Object[] objects, String delimiter) {
		return join(objects, delimiter, "");
	}
	
	/**
	 * Returns a string of concatenated objects, separated by the delimiter and
	 * surrounded by the quoteSymbol
	 * 
	 * @param objects
	 * @param delimiter
	 * @param quoteSymbol
	 * @return
	 */
	public static String join(Object[] objects, String delimiter, String quoteSymbol) {
		StringBuilder sb = new StringBuilder();
		String loopDelimiter = "";
		for(Object o: objects) {
			sb.append(loopDelimiter);
			sb.append(quoteSymbol + o.toString() + quoteSymbol);
			loopDelimiter = delimiter;
		}
		return sb.toString();
	}

	/**
	 * Returns a string of concatenated integers, separated by the delimiter
	 * 
	 * @param ints
	 * @param delimiter
	 * @return
	 */
	public static String join(int[] ints, String delimiter) {
		StringBuilder sb = new StringBuilder();
		String loopDelimiter = "";
		for(int i: ints) {
			sb.append(loopDelimiter);
			sb.append(String.valueOf(i));
			loopDelimiter = delimiter;
		}
		return sb.toString();
	}

	/**
	 * Prints the map to a map to the standard output stream
	 * 
	 * @param map
	 *            The map to print
	 * @param header
	 *            A string header that will be printed at the beginning (may be
	 *            {@code null})
	 */
	public static void printMapOfMap(Map<BigDecimal, Map<String, BigDecimal>> map, String header) {
		if(header != null) {
			System.out.println(header);
		}
		for(BigDecimal key: map.keySet()) {
			System.out.println(String.format("Key Value: [%s]", StringUtil.formatPrice(key)));
			Map<String, BigDecimal> values = map.get(key);
			Assertions.sumsToOne("portfolioWeights", values);
			printMap(values, null);
		}
	}
	
	/**
	 * Prints the map to the standard output stream
	 * 
	 * @param value
	 *            The map to print
	 * @param header
	 *            A string header that will be printed at the beginning (may be
	 *            {@code null})
	 */
	public static void printMap(Map<String, BigDecimal> value, String header) {
		if(header != null) {
			System.out.println(header);
		}
		for(String key: value.keySet()) {
			System.out.println(String.format("Key [%s] Value [%s]", key, StringUtil.formatPrice(value.get(key))));
		}
	}
	

}
