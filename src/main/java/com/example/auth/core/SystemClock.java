package com.example.auth.core;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
class SystemClock implements Clock {
  @Override
  public Date now() {
    return new Date();
  }

  @Override
  public Date nowPlus(Interval interval) {
    return new Date(System.currentTimeMillis() + interval.milliseconds);
  }
}