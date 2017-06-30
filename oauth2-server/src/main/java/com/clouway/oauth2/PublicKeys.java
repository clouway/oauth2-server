package com.clouway.oauth2;

import com.google.common.base.Optional;

import java.security.Key;
import java.util.Map;

/**
 * PublicKeys is store for public keys, the implementation of this class
 * gives an access to retrieve public keys.
 *
 *
 * @author Ianislav Nachev <qnislav.nachev@clouway.com>
 */
public interface PublicKeys {

  /**
   * @param keyId the key id that is responsible for the the key
   * @return the key that is needed.
   */
  Optional<Key> getKey(String keyId);

  /**
   * Find  all public keys
   *
   * @return Map with keyid's and public keys
   */
  Map<String, String> getKeys();
}
