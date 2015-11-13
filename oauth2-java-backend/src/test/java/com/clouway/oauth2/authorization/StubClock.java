package com.clouway.oauth2.authorization;

import com.clouway.oauth2.Clock;
import com.clouway.oauth2.Duration;

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
  public Date now() {
    return currentDate;
  }
}
