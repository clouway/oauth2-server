package com.clouway.oauth2.token;

import com.clouway.oauth2.common.DateTime;
import com.google.common.base.Optional;

public interface IdTokenBuilder {
  
  IdTokenBuilder issuer(String issuer);
  
  IdTokenBuilder audience(String audience);

  IdTokenBuilder issuedAt(DateTime instant);

  IdTokenBuilder ttl(Long ttlSeconds);

  IdTokenBuilder subjectUser(Identity identity);
  IdTokenBuilder subjectServiceAccount(ServiceAccount serviceAccount);

  IdTokenBuilder withAccessToken(String accessToken);
  IdTokenBuilder claim(String name, Object value);

  String build();
}
