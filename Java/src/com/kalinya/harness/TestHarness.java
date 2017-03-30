package com.kalinya.harness;

import java.util.Date;

import com.kalinya.oc.util.MessageLog;
import com.kalinya.util.DateUtil;
import com.kalinya.util.StringUtil;

public class TestHarness {

	public static void main(String[] args) {
		System.out.println("Tests using 15-Mar");
		printDate("3/15/2017");
		printDate("15-Mar-2017");
		printDate("15-Mar-17");
		printDate("03/15/17");
		printDate("15 Mar 2017");
		
		System.out.println("\nTests using 3-Mar");
		printDate("3 Mar 2017");
		printDate("3 Mar 17");
		printDate("03/03/17");
		printDate("3/3/2017");
		printDate("3/3/17");
		
		System.out.println("\nTests using 13-Dec");
		printDate("12/13/16");
	}

	private static void printDate(String s) {
		Date date = DateUtil.parseDate(s);
		System.out.println("Input [" + s + "] Ouput [" + StringUtil.formatDate(date) + "]");
	}

}
