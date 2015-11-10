package com.clouway.oauth2;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public class SystemClock implements Clock {

  @Override
  public Date nowPlus(Duration duration) {
    return new Date(System.currentTimeMillis() + duration.asMills());
  }

  @Override
  public Date now() {
    return new Date(System.currentTimeMillis());
  }
}