package com.example.auth.core.authorization;

import com.example.auth.core.Clock;
import com.example.auth.core.Duration;

import java.util.Date;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class StubClock implements Clock {
  private Date currentDate;

  public StubClock(Date currentDate) {
    this.currentDate = currentDate;
  }

  @Override
  public Date nowPlus(Duration duration) {
    return new Date(currentDate.getTime() + duration.asMills());
  }

  @Override
  public Date now() {
    return currentDate;
  }
}
