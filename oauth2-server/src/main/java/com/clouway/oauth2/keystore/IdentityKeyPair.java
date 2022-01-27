package com.clouway.oauth2.keystore;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * IdentityKeyPair is a key pair which is used to represent identity, public and private keys that are used for signing
 * of id_token. The identity in the key is used as marker to mark the key that was used for signing and this ID is encoded
 * in the header of the signature to provide a clean way for safe verification of the signature.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class IdentityKeyPair {
  public final String keyId;
  public final PrivateKey privateKey;
  public final PublicKey publicKey;

  public IdentityKeyPair(String keyId, PrivateKey privateKey, PublicKey publicKey) {
    this.keyId = keyId;
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }
}
