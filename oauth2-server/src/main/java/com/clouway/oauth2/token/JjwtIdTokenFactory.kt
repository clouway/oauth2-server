package com.clouway.oauth2.token

import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.keystore.IdentityKeyPair
import com.clouway.oauth2.keystore.KeyStore
import com.google.common.base.Optional
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.Date
import java.util.Random

/**
 * @author Vasil Mitov <vasil.mitov></vasil.mitov>@clouway.com>
 */
class JjwtIdTokenFactory(
    private val keyStore: KeyStore,
) : IdTokenFactory {
    override fun create(
        host: String,
        clientId: String,
        identity: Identity,
        ttl: Long,
        instant: DateTime,
    ): Optional<String> {
        val keys = keyStore.keys
		
        if (keys == null || keys.isEmpty()) {
            return Optional.absent()
        }
		
        val signingKey = randomKey(keys)
		
        val claims: MutableMap<String, Any> = LinkedHashMap()
        claims["iss"] = host
        claims["aud"] = clientId
        claims["sub"] = identity.id
        claims["name"] = identity.name
        claims["email"] = identity.email
        claims["given_name"] = identity.givenName
        claims["family_name"] = identity.familyName
        claims.putAll(identity.claims)
		
        return Optional.of(
            Jwts
                .builder()
                .setHeaderParam(
                    "cid",
                    signingKey.keyId,
                ) // CertificateId - the ID of the certificate that the token was signed with.
                .setClaims(claims)
                .setIssuedAt(Date(instant.timestamp()))
                .setExpiration(Date(instant.timestamp() + ttl))
                .signWith(SignatureAlgorithm.RS256, signingKey.privateKey)
                .compact(),
        )
    }

    override fun create(
        host: String,
        clientId: String,
        serviceAccount: ServiceAccount,
        ttl: Long,
        instant: DateTime,
    ): Optional<String> {
        val keys = keyStore.keys
		
        if (keys == null || keys.isEmpty()) {
            return Optional.absent()
        }

        val signingKey = randomKey(keys)
		
        val claims: MutableMap<String, Any> = LinkedHashMap()
        claims["iss"] = host
        claims["aud"] = clientId
        claims["sub"] = serviceAccount.clientId
        claims["name"] = serviceAccount.name
        claims["email"] = serviceAccount.clientEmail
        claims["given_name"] = ""
        claims["family_name"] = ""
        claims.putAll(serviceAccount.claims)
		
        return Optional.of(
            Jwts
                .builder()
                .setHeaderParam(
                    "cid",
                    signingKey.keyId,
                ) // CertificateId - the ID of the certificate that the token was signed with.
                .setClaims(claims)
                .setIssuedAt(Date(instant.timestamp()))
                .setExpiration(Date(instant.timestamp() + ttl))
                .signWith(SignatureAlgorithm.RS256, signingKey.privateKey)
                .compact(),
        )
    }

    private fun randomKey(keys: List<IdentityKeyPair>): IdentityKeyPair {
        val random = Random()
        return keys[random.nextInt(keys.size)]
    }
}
