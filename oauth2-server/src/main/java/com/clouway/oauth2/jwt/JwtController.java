package com.clouway.oauth2.jwt;

import com.clouway.friendlyserve.Request;
import com.clouway.friendlyserve.Response;
import com.clouway.oauth2.BearerTokenResponse;
import com.clouway.oauth2.DateTime;
import com.clouway.oauth2.InstantaneousRequest;
import com.clouway.oauth2.OAuthError;
import com.clouway.oauth2.client.Client;
import com.clouway.oauth2.client.ClientKeyStore;
import com.clouway.oauth2.jws.Pem;
import com.clouway.oauth2.jws.Signature;
import com.clouway.oauth2.jws.SignatureFactory;
import com.clouway.oauth2.token.GrantType;
import com.clouway.oauth2.token.TokenResponse;
import com.clouway.oauth2.token.Tokens;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class JwtController implements InstantaneousRequest {
  private final Gson gson = new Gson();

  private final SignatureFactory signatureFactory;
  private final Tokens tokens;
  private final ClientKeyStore keyStore;

  public JwtController(SignatureFactory signatureFactory, Tokens tokens, ClientKeyStore keyStore) {
    this.signatureFactory = signatureFactory;
    this.tokens = tokens;
    this.keyStore = keyStore;
  }

  @Override
  public Response handleAsOf(Request request, DateTime instant) {
    String assertion = request.param("assertion");
    String scope = request.param("scope") == null ? "" : request.param("scope");

    List<String> parts = Lists.newArrayList(Splitter.on(".").split(assertion));

    // Error should be returned if any of the header parts is missing
    if (parts.size() != 3) {
      return OAuthError.invalidRequest();
    }

    String headerContent = parts.get(0);
    String headerValue = new String(BaseEncoding.base64Url().decode(headerContent));
    String content = new String(BaseEncoding.base64Url().decode(parts.get(1)));

    byte[] signatureValue = BaseEncoding.base64Url().decode(parts.get(2));

    Jwt.Header header = gson.fromJson(headerValue, Jwt.Header.class);
    Jwt.ClaimSet claimSet = gson.fromJson(content, Jwt.ClaimSet.class);

    Optional<Pem.Block> possibleResponse = keyStore.findKey(header, claimSet);

    if (!possibleResponse.isPresent()) {
      return OAuthError.invalidGrant();
    }

    Pem.Block serviceAccountKey = possibleResponse.get();

    Optional<Signature> optSignature = signatureFactory.createSignature(signatureValue, header);

    // Unknown signture was provided, so we are returning request as invalid.
    if (!optSignature.isPresent()) {
      return OAuthError.invalidRequest();
    }

    byte[] headerAndContentAsBytes = String.format("%s.%s", parts.get(0), parts.get(1)).getBytes();

    if (!optSignature.get().verify(headerAndContentAsBytes, serviceAccountKey)) {
      return OAuthError.invalidGrant();
    }

    Set<String> scopes = Sets.newTreeSet(Splitter.on(" ").omitEmptyStrings().split(scope));
    Client client = new Client(claimSet.iss, "", "", Collections.<String>emptySet());
    TokenResponse response = tokens.issueToken(GrantType.JWT, client, claimSet.iss, scopes, instant);

    return new BearerTokenResponse(response.accessToken, response.ttlInSeconds, response.refreshToken);
  }

}
