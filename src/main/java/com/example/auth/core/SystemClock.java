package com.example.auth.core;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SystemClock implements Clock {

  @Override
  public Date nowPlus(Duration duration) {
    return new Date(System.currentTimeMillis() + duration.milliseconds);
  }
}