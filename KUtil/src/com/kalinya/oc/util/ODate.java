package com.kalinya.oc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.olf.openjvs.OException;
import com.olf.openrisk.calendar.CalendarFactory;
import com.olf.openrisk.calendar.EnumPaymentConvention;
import com.olf.openrisk.calendar.HolidaySchedules;
import com.olf.openrisk.calendar.SymbolicDate;

/**
 * A class that supports evaluating dates using optional symbolic date offsets
 * and tests of whether the date is the last good business date of a month, etc.
 * <p>
 * Example usage -
 * <p>
 * <code>return new ODate.Builder(session.getCalendarFactory(), currentDate)
		<br>.setSymbolicDateOffset(offset)
		<br>.build()
		<br>.getDate()</code>
 */
public class ODate {

	private CalendarFactory cf;
	private Date origin;
	private Date result;
	private HolidaySchedules holidays;
	private String symbolicDateOffset;
	private EnumPaymentConvention paymentConvention;

	/**
	 * A class to work with dates
	 * @param session
	 */
	private ODate(Builder builder) {
		this.cf = builder.cf;
		this.origin = builder.origin;
		this.result = builder.result;
		this.paymentConvention = builder.paymentConvention;
		this.holidays = builder.holidays;
		this.symbolicDateOffset = builder.symbolicDateOffset;
	}

	@Override
	public String toString() {
		return "<" + this.getClass() + "> "
				+ "Date origin [" + origin == null ? "null" : formatDate(origin) + "], "
				+ "Date result [" + result == null ? "null" : formatDate(result) + "], " 
				+ "Symbolic Date Offset [" + symbolicDateOffset + "], "
				+ "Holidays [" + holidays != null ? holidays.toString() : "null" + "] ";
	}

	/**
	 * Returns the date result after all symbolic date offset, holidays, and the
	 * payment convention have been applied
	 * 
	 * @return
	 */
	public Date getDate() {
		return result;
	}

	/**
	 * Returns true if the date is the last good business date of the month
	 * 
	 * @return
	 */
	public boolean isLastGoodBusinessDateOfMonth() {
		Calendar originCal = Calendar.getInstance();
		originCal.setTime(result);

		//Get the next good business day
		Date eom = getSymbolicDate(cf, result, "1d", holidays, paymentConvention);
		Calendar eomCal = Calendar.getInstance();
		eomCal.setTime(eom);
		
		//If the next good business day falls in the next month, today is the LGBD of the current month
		if (eomCal.get(Calendar.MONTH) != originCal.get(Calendar.MONTH)) {
			return true;
		}

		return false;
	}

	
	/**
	 * The last calendar day of the month prior to <code>origin</code>'s
	 * @return
	 */
	public Date getPriorMonthLastCalendarDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.result);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * The first calendar day of <code>origin</code>'s month
	 * @return
	 */
	public Date getCurrentMonthFirstCalendarDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.result);
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * The last calendar day of the month
	 * @return
	 */
	public Date getCurrentMonthLastCalendarDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.result);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * Returns the next month's first calendar date
	 */
	public Date getNextMonthFirstCalendarDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.origin);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * Gets a symbolic date relative to an origin using the default holidays
	 * @param cf
	 * @param origin
	 * @param symbolicDateStr
	 * @param holidays
	 * @param paymentConvention
	 * @return
	 */
	public static Date getSymbolicDate(CalendarFactory cf, Date origin, String symbolicDateStr, HolidaySchedules holidays, EnumPaymentConvention paymentConvention) {
		Date date = null;
		SymbolicDate sym = null;
		if(symbolicDateStr == null || 
				symbolicDateStr.length() < 1) {
			sym = cf.createSymbolicDate();
		}
		else {
			//Create a SymbolicDate using the symbolicDate string
			sym = cf.createSymbolicDate(symbolicDateStr);
		}

		if(holidays != null) {
			sym.setHolidaySchedules(holidays);
		} else {
			HolidaySchedules noHolidays = cf.createHolidaySchedules(false);
			sym.setHolidaySchedules(noHolidays);
			noHolidays.dispose();
		}
		
		if(paymentConvention != null) {
			sym.setPaymentConvention(paymentConvention);
		}

		//Evaluate the symbolic date representation
		date = sym.evaluate(origin, true);

		sym.dispose();

		return date;
	}

	/**
	 * The first good business day of the month
	 * @return
	 */
	public Date getCurrentMonthFirstGoodBusinessDate() {
		//E.g. origin = 8/1/14, "1d>-1lom" = 8/1
		//origin = 8/29/14, "1d>-1lom" = 8/1
		//origin = 9/2/14, "1d>-1lom" = 9/2
		return getSymbolicDate(cf, origin, "1d>-1lom", holidays, paymentConvention);
	}

	/**
	 * Returns the weekday before today
	 * @return
	 * @throws OException 
	 */
	public Date getPreviousWeekday() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(origin);
		
		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
			cal.add(Calendar.DATE, -3);
		} else {
			cal.add(Calendar.DATE, -1);
		}
		return cal.getTime();
	}
	
	/**
	 * Returns the next weekday 
	 * @return
	 */
	public Date getNextWeekday() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(origin);
		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			cal.add(Calendar.DATE, 3);
		} else {
			cal.add(Calendar.DATE, 1);
		}
		return cal.getTime();
	}
	
	/**
	 * Formats a date as MM/dd/yyyy
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
	
	public static class Builder {
		//Required parameters
		private CalendarFactory cf;
		private Date origin;
		
		//Optional parameters
		private HolidaySchedules holidays;
		private String symbolicDateOffset;
		private EnumPaymentConvention paymentConvention;
		
		//Calculated result
		private Date result;

		public Builder(CalendarFactory cf, Date origin) {
			this.cf = cf;
			this.origin = origin;
		}
		
		/**
		 * Call this method to return the ODate object after having applied the
		 * optional constructor parameters for holidays, date offsets and
		 * payment convention
		 * @return
		 */
		public ODate build() {
			result = getSymbolicDate(cf, origin, symbolicDateOffset, holidays, paymentConvention);
			return new ODate(this);
		}
		
		/**
		 * Sets the holiday schedule to use for the symbolic date representation
		 * @param holidays
		 * @return
		 */
		public Builder setHolidays(HolidaySchedules holidays) {
			this.holidays = holidays;
			return this;
		}
		
		/**
		 * Sets the symbolic date to apply to the origin
		 * @param symbolicDateOffset
		 * @return
		 */
		public Builder setSymbolicDateOffset(String symbolicDateOffset) {
			this.symbolicDateOffset = symbolicDateOffset;
			return this;
		}
		
		/**
		 * Sets the payment convention to use for the symbolic date
		 * representation.
		 * <p>
		 * This will have an impact for example if the symbolic date is "6m" and
		 * the symbolic date needs to know whether to apply the Modified
		 * Following convention
		 * 
		 * @param paymentConvention
		 * @return
		 */
		public Builder setPaymentConvention(EnumPaymentConvention paymentConvention) {
			this.paymentConvention = paymentConvention;
			return this;
		}
	}
}
