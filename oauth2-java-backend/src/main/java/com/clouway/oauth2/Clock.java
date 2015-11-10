package com.clouway.oauth2;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface Clock {

  Date nowPlus(Duration duration);

  Date now();
}