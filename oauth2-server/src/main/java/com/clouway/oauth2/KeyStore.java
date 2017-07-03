package com.clouway.oauth2;

import com.clouway.oauth2.client.IdentityKeyPair;

import java.util.List;

/**
 * KeyStore is store for keys used for signing and verifying of the signatures of the id_tokens.
 *
 * @author Ianislav Nachev <qnislav.nachev@clouway.com>
 */
public interface KeyStore {

  /**
   * Gets a list of available getKeys for signing the data and verifying it.
   *
   * @return a list of getKeys or an empty list if no getKeys are available
   */
  List<IdentityKeyPair> getKeys();

}
