package com.clouway.oauth2;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface Clock {
  /**
   * Returns the current time.
   * @return the current time
   */
  Date now();

}