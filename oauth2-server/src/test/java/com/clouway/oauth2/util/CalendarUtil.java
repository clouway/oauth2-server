package com.clouway.oauth2.util;

import com.clouway.oauth2.DateTime;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class CalendarUtil {

  public static Date newTime(int hour, int minute, int seconds) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, seconds);
    return calendar.getTime();
  }

  public static DateTime newDateTime(int year, int month, int day, int hour, int minute, int seconds) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, seconds);
    calendar.set(Calendar.MILLISECOND, 0);
    return new DateTime(calendar.getTime());
  }
}
