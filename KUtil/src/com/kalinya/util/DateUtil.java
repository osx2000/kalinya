/*
 * 2/23/2017
 * Added getMinDate, getMaxDate
 */
package com.kalinya.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.Years;

import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OException;
import com.olf.openjvs.OLog;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.DAYS;

public class DateUtil {
	
	public static final Date MAXIMUM_DATE = new Date(Long.MAX_VALUE);
	public static final Date MINIMUM_DATE = new Date(0L);

	public static Date now() {
		return Calendar.getInstance().getTime();
	}

	public static Date today() {
		return getDate(now());
	}
	
	public static Date getDate(Date date) {
		Calendar instance = Calendar.getInstance();
		instance.setTime(date);
		int year = instance.get(Calendar.YEAR);
		int month = instance.get(Calendar.MONTH);
		int day = instance.get(Calendar.DAY_OF_MONTH);
		return new Calendar.Builder().setDate(year, month,day).build().getTime();
	}
	
	/**
	 * Creates a Date based on a given SimpleDateFormat string
	 * 
	 * @param simpleDateFormat
	 * @param dateString
	 * @return
	 */
	public static Date createDate(String simpleDateFormat, String dateString) {
		try {
			return new SimpleDateFormat(simpleDateFormat).parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates a Date from a given string. The method will attempt to identify
	 * the appropriate string SimpleDateFormat pattern.
	 * 
	 * @param s
	 * @return
	 */
	public static Date createDate(String s) {
		return parseDate(s);
	}
	
	/**
	 * Creates a java.util.Date
	 * 
	 * @param year
	 *            The YEAR value dayOfMonth
	 * @param month
	 *            The MONTH value (the month numbering is 1-based)
	 * @param day
	 *            The DAY_OF_MONTH value
	 * @return
	 */
	public static Date createDate(int year, int month, int day) {
		//Note month is 1-based, unlike 0-based java.util.Date 
		return new Calendar.Builder().setDate(year, month-1,day).build().getTime();
	}
	
	/**
	 * Gets an ordered set (TreeSet) of dates starting with startDate and ending
	 * with endDate, inclusive. All dates will be weekdays.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Set<Date> getWeekdayDates(Date startDate, Date endDate) {
		return getOrderedWeekdayDates(startDate, endDate, true);
	}

	/**
	 * Returns an ordered set of weekday dates between the {@code startDate} and
	 * the {@code endDate}
	 * 
	 * @param startDate
	 *            The earliest date in the sequence
	 * @param endDate
	 *            The last date in the sequence
	 * @param earliestToLatest
	 *            {@code earliestToLatest = true} will return a
	 *            {@code LinkedHashSet} from {@code startDate} to
	 *            {@code endDate} (oldest to newest), else the set will be from
	 *            latest date to the earliest
	 * @return
	 */
	public static Set<Date> getOrderedWeekdayDates(Date startDate, Date endDate, boolean earliestToLatest) {
		if(startDate.compareTo(endDate) > 0) {
			throw new IllegalArgumentException("StartDate [" + StringUtil.formatDate(startDate) + "] is after EndDate [" + StringUtil.formatDate(endDate) + "]");
		}
		Calendar c = Calendar.getInstance();
		
		//Check startDate argument is a weekday
		c.setTime(startDate);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
			throw new IllegalArgumentException("StartDate [" + StringUtil.formatDate(startDate) + "] is a [" + new SimpleDateFormat("EEEE").format(startDate) + "]");
		}
		//Check endDate argument is a weekday
		c.setTime(endDate);
		dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
			throw new IllegalArgumentException("EndDate [" + StringUtil.formatDate(endDate) + "] is a [" + new SimpleDateFormat("EEEE").format(endDate) + "]");
		}
		
		//LinkedHashSet is faster than TreeSet 
		Set<Date> dates = new LinkedHashSet<Date>();
		
		if (earliestToLatest) {
			c.setTime(startDate);
			while(c.getTime().compareTo(endDate) <= 0) {
				//Add the startDate to the collection
				dates.add(c.getTime());
				//Loop until the endDate and add each weekday to the collection
				while(c.getTime().compareTo(endDate) <= 0) {
					if(c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
						c.add(Calendar.DATE, 3);
					} else {
						c.add(Calendar.DATE, 1);
					}
					dates.add(c.getTime());
				}
			}
		} else {
			c.setTime(endDate);
			while(c.getTime().compareTo(startDate) >= 0) {
				dates.add(c.getTime());
				if(c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
					c.add(Calendar.DATE, -3);
				} else {
					c.add(Calendar.DATE, -1);
				}
				dates.add(c.getTime());
			}
		}
		return dates;
	}

	public static Date getMinDate(Collection<Date> dates) {
		return getMinMaxDate(dates, true);
	}
	
	public static Date getMaxDate(Collection<Date> dates) {
		return getMinMaxDate(dates, false);
	}
	
	public static Date getMinMaxDate(Collection<Date> dates, boolean returnMinDate) {
		Date boundaryDate = null;
		int i = 0;
		for(Date date: dates) {
			if(boundaryDate == null) {
				boundaryDate = date;
				continue;
			}
			if(date != null) {
				i = (returnMinDate ? 1 : -1) * date.compareTo(boundaryDate);
				if(i < 0) {
					boundaryDate = date;
				}
			}
		}
		return boundaryDate;
	}
	
	/**
	 * Creates a Date based on a given string. Uses Apache Commons DateUtils.
	 * 
	 * @param s
	 * @return
	 * @see org.apache.commons.lang3.time.DateUtils#parseDateStrictly(String, String...)
	 * @see #createDate(String)
	 */
	public static Date parseDate(String s) {
		Date date = null;
		try {
			if(s == null || s.length() == 0) {
				return null;
			}
			date = DateUtils.parseDateStrictly(s, getDateParsePatterns());
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		return date;
	}

	private static String[] getDateParsePatterns() {
		List<String> parsePatterns = getDateParsePatternsAsList();
		return parsePatterns.toArray(new String[parsePatterns.size()]);
	}
	
	private static final List<String> getDateParsePatternsAsList() {
		List<String> parsePatterns = new ArrayList<>();
		//US formats
		parsePatterns.add("MM/dd/yy");
		parsePatterns.add("MM/dd/yyyy");
		parsePatterns.add("MM/d/yy");
		parsePatterns.add("MM/d/yyyy");
		parsePatterns.add("M/dd/yy");
		parsePatterns.add("M/dd/yyyy");
		parsePatterns.add("M/d/yy");
		parsePatterns.add("M/d/yyyy");

		//International patterns
		// with hyphens
		parsePatterns.add("dd-MM-yy");
		parsePatterns.add("dd-MM-yyyy");
		parsePatterns.add("d-MM-yy");
		parsePatterns.add("d-MM-yyyy");
		parsePatterns.add("dd-MMM-yy");
		parsePatterns.add("dd-MMM-yyyy");
		parsePatterns.add("d-MMM-yy");
		parsePatterns.add("d-MMM-yyyy");

		// with spaces
		parsePatterns.add("dd MM yy");
		parsePatterns.add("dd MM yyyy");
		parsePatterns.add("d MM yy");
		parsePatterns.add("d MM yyyy");
		parsePatterns.add("dd MMM yy");
		parsePatterns.add("dd MMM yyyy");
		parsePatterns.add("d MMM yy");
		parsePatterns.add("d MMM yyyy");
		
		//Unexamined examples from StackOverflow
		parsePatterns.add("yyyyMMdd");
		parsePatterns.add("yyyy-MM-dd");
		parsePatterns.add("yyyy/MM/dd");
		parsePatterns.add("dd MMM yyyy");
		parsePatterns.add("dd MMMM yyyy");
		parsePatterns.add("yyyyMMddHHmm");
		parsePatterns.add("yyyyMMdd HHmm");
	    parsePatterns.add("dd-MM-yyyy HH:mm");
	    parsePatterns.add("yyyy-MM-dd HH:mm");
	    parsePatterns.add("MM/dd/yyyy HH:mm");
	    parsePatterns.add("yyyy/MM/dd HH:mm");
	    parsePatterns.add("dd MMM yyyy HH:mm");
	    parsePatterns.add("dd MMMM yyyy HH:mm");
	    parsePatterns.add("yyyyMMddHHmmss");
	    parsePatterns.add("yyyyMMdd HHmmss");
	    parsePatterns.add("dd-MM-yyyy HH:mm:ss");
	    parsePatterns.add("yyyy-MM-dd HH:mm:ss");
	    parsePatterns.add("MM/dd/yyyy HH:mm:ss");
	    parsePatterns.add("yyyy/MM/dd HH:mm:ss");
	    parsePatterns.add("dd MMM yyyy HH:mm:ss");
	    parsePatterns.add("dd MMMM yyyy HH:mm:ss");
	    
	    return parsePatterns;
	}

	/**
	 * Returns the julian date representation of the weekday before today
	 * @return
	 * @throws OException 
	 */
	public static int getPreviousWeekday(int origin) throws OException {
		int weekday = OCalendar.getDayOfWeek(origin);
		if(weekday == DAYS.MONDAY.toInt()) {
			return origin - 3;
		}
		if(weekday == DAYS.SUNDAY.toInt()) {
			return origin - 2;
		}

		return origin - 1;
	}
	
	/**
	 * Returns the julian date representation of the weekday after today
	 * @param origin 
	 * @return
	 * @throws OException 
	 */
	public static int getNextWeekday(int origin) throws OException {
		int weekday = OCalendar.getDayOfWeek(origin);
		if(weekday == DAYS.FRIDAY.toInt()) {
			return origin + 3;
		} else {
			return origin + 1;
		}
	}

	/**
	 * Returns a String representation of the time as 'hhmmss'. 
	 * <p>Useful for log file names that must distinguish between distinct executions.
	 * 
	 * @return
	 * @throws OException 
	 */
	public static String getServerTimeString() {
		String time = "";
		try {
			time = Util.timeGetServerTimeHMS().replaceAll(":", "");
		} catch (OException e) {
			OLog.logError(0, "Failed to parse server time using com.olf.openjvs.Util.timeGetServerTimeHMS()");
			throw new RuntimeException(e);
		}
		return time;
	}

	/**
	 * Returns an ArrayList containing consecutive weekday Julian dates from
	 * startDate to endDate, inclusive
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws OException
	 */
	public static ArrayList<Integer> getDateRangeList(int startDate, int endDate) throws OException {
		ArrayList<Integer> dates = new ArrayList<Integer>();
		int currentDate = startDate;
		while(currentDate <= endDate) {
			dates.add(currentDate);
			currentDate = getNextWeekday(currentDate);
		}
		return dates;
	}

	public static Date parseSymbolicDate(String string) {
		return parseSymbolicDate(now(), string);
	}
	
	public static Date parseSymbolicDate(Date origin, String string) {
		String regex = "[^\\d-]";
		/*
		 * https://stackoverflow.com/questions/24186801/java-time-expression-evaluation
		 * input: e.g. P1Y1M1WT2H10M15S
		 */
		String[] periodSuffixes = new String[]{"Y","M","W","D"};
		for(String periodSuffix: periodSuffixes) {
			int index = string.toUpperCase().indexOf(periodSuffix);
			if (index >= 0) {
				String input = "P" + string.substring(0, index).replaceAll(regex , "") + periodSuffix;
				Period period = new Period(input);
				DateTime result = new DateTime(origin).plus(period);
				return createDate(result.getYear(), result.getMonthOfYear(), result.getDayOfMonth());
			}
		}
		throw new IllegalArgumentException(String.format("Unable to parse string [%s]", string));
	}
	
	public static BigDecimal getDateDifferenceInYears(Date d1, Date d2) {
		Date earlyDate = d1;
		Date lateDate = d2;
		if(earlyDate.compareTo(lateDate) > 0) {
			earlyDate = d2;
			lateDate = d1;
		}
		LocalDate earlyLocalDate = LocalDate.fromDateFields(earlyDate);
		LocalDate lateLocalDate = LocalDate.fromDateFields(lateDate);
		int years = Years.yearsBetween(earlyLocalDate, lateLocalDate).getYears();
		int days = Days.daysBetween(earlyLocalDate.plusYears(years), lateLocalDate).getDays();
		return NumberUtil.newBigDecimal(years + (days/365.0), 3);
	}
}
