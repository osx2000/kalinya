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
	 * Formats double as a string with 6 decimal points
	 * 
	 * @param dbl
	 * @return
	 */
	public static String formatDouble(double dbl, int precision) {
		return String.format("%1$,." + precision + "f", dbl);
	}
	
	/**
	 * Formats double as a string
	 * @param dbl
	 * @return
	 */
	public static String formatDouble(double dbl) {
		return String.format("%1$,.2f", dbl);
	}
	
	
	public static String formatDouble(BigDecimal bd) {
		return formatDouble(bd.doubleValue());
	}
	
	/**
	 * Formats double as a string with 8 decimal places
	 */
	public static String formatPrice(double dbl) {
		return String.format("%1$,.8f", dbl);
	}

	/**
	 * Formats BigDecimal as a string with 8 decimal places
	 */
	public static String formatPrice(BigDecimal bd) {
		return String.format("%1$,.8f", bd.doubleValue());
	}
	
	public static String toTitleCase(String input) {
		return WordUtils.capitalizeFully(input);
	    /*StringBuilder titleCase = new StringBuilder();
	    boolean nextTitleCase = true;
	    for (char c : input.toCharArray()) {
	        if (Character.isSpaceChar(c)) {
	            nextTitleCase = true;
	        } else if (nextTitleCase) {
	            c = Character.toTitleCase(c);
	            nextTitleCase = false;
	        } else {
	        	c = Character.toLowerCase(c);
	        }

	        titleCase.append(c);
	    }
	    return titleCase.toString();*/
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

}
