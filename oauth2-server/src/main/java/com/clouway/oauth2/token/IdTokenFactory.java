package com.clouway.oauth2.token;

import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Optional;

/**
 * A class that builds JWT tokens using JJWT builder
 * <p>
 *   
 * @author Vasil Mitov <vasil.mitov@clouway.com>
 *
 */
public interface IdTokenFactory {
  IdTokenBuilder newBuilder();
}
