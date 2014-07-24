package com.example.auth.core;

import java.util.Date;

/**
 * @author Ivan Stefanov <ivan.stefanov@clouway.com>
 */
public interface Clock {

  Date nowPlus(Interval interval);
}