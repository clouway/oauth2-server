package com.clouway.oauth2.util;

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
}
