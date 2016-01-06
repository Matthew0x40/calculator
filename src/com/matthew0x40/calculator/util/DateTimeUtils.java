package com.matthew0x40.calculator.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {
    /**
     * Number of milliseconds in a standard second.
     */
    public static final long MILLIS_PER_SECOND = 1000;
    /**
     * Number of milliseconds in a standard minute.
     */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    /**
     * Number of milliseconds in a standard hour.
     */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    /**
     * Number of milliseconds in a standard day.
     */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    /**
     * Number of milliseconds in a standard day.
     */
    public static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;
    
    public static Date add(Date date, CalendarField field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field.field, amount);
		return cal.getTime();
    }
    
    public static enum CalendarField {
    	MILLISECOND(Calendar.MILLISECOND),
    	SECOND(Calendar.SECOND),
    	MINUTE(Calendar.MINUTE),
    	HOUR(Calendar.HOUR_OF_DAY),
    	DAY(Calendar.DAY_OF_YEAR),
    	MONTH(Calendar.MONTH),
    	YEAR(Calendar.YEAR),
    	;
    	
    	public final int field;
    	
    	CalendarField(int field) {
    		this.field = field;
    	}
    }
    
    public static Date set(Date date, CalendarField field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(field.field, amount);
		return cal.getTime();
    }
    
    public static int get(Date date, CalendarField field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(field.field);
    }
    
	public static long getTimeBetween(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillis = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillis, timeUnit);
	}
	
	public static Date dateFromNow(int years, int months, int days, int hours, int minutes, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, years);
		cal.add(Calendar.MONTH, months);
		cal.add(Calendar.DAY_OF_YEAR, days);
		cal.add(Calendar.HOUR, hours);
		cal.add(Calendar.MINUTE, minutes);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}
	
	/**
	 * Returns whether or not the supplied year is a leap year or not.
	 * 
	 * @param year
	 * @return true if leap year, false otherwise
	 */
	public static boolean isLeapYear(int year) {
	  Calendar cal = Calendar.getInstance();
	  cal.set(Calendar.YEAR, year);
	  return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
	}
}
