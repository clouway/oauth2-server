package com.clouway.oauth2;

import java.util.Date;

/**
 * SystemClock is a {@link Clock } implementation which is using the system time.
 *
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SystemClock implements Clock {

  @Override
  public Date now() {
    return new Date(System.currentTimeMillis());
  }

}