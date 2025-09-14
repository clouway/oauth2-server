package com.clouway.oauth2.token

import com.clouway.oauth2.common.DateTime
import com.clouway.oauth2.keystore.IdentityKeyPair
import com.clouway.oauth2.keystore.KeyStore
import com.clouway.oauth2.util.Hashes
import io.jsonwebtoken.Jwts
import java.util.Date
import java.util.Random

/**
 * @author Vasil Mitov <vasil.mitov></vasil.mitov>@clouway.com>
 */
class JjwtIdTokenFactory(
    private val keyStore: KeyStore,
) : IdTokenFactory {
    override fun newBuilder(): IdTokenBuilder = DefaultIdTokenBuilder(keyStore)
}

private class DefaultIdTokenBuilder(
    private val keyStore: KeyStore,
) : IdTokenBuilder {
    private val claims: MutableMap<String, Any> = LinkedHashMap()
    private var issuedAt: DateTime? = null
    private var ttl: Long? = null

    override fun issuer(issuer: String): IdTokenBuilder {
        claims["iss"] = issuer
        return this
    }

    override fun audience(audience: String): IdTokenBuilder {
        claims["aud"] = audience
        return this
    }

    override fun issuedAt(instant: DateTime): IdTokenBuilder {
        this.issuedAt = instant
        return this
    }

    override fun ttl(ttlSeconds: Long): IdTokenBuilder {
        this.ttl = ttlSeconds
        return this
    }

    override fun subjectUser(identity: Identity): IdTokenBuilder {
        claims["sub"] = identity.id
        claims["subject_type"] = "user"
        claims["name"] = identity.name
        claims["email"] = identity.email
        claims["given_name"] = identity.givenName
        claims["family_name"] = identity.familyName
        claims.putAll(identity.claims)
        return this
    }

    override fun subjectServiceAccount(serviceAccount: ServiceAccount): IdTokenBuilder {
        claims["sub"] = serviceAccount.clientId
        claims["subject_type"] = "service_account"
        claims["name"] = serviceAccount.name
        claims["email"] = serviceAccount.clientEmail
        claims["given_name"] = ""
        claims["family_name"] = ""
        claims.putAll(serviceAccount.claims)
        return this
    }

    override fun withAccessToken(accessToken: String): IdTokenBuilder {
        claims["at_hash"] = Hashes.atHash(accessToken)
        return this
    }

    override fun claim(
        name: String,
        value: Any,
    ): IdTokenBuilder {
        claims[name] = value
        return this
    }

    override fun build(): String {
        val keys = keyStore.keys ?: throw IllegalStateException("No signing keys are configured")
        if (keys.isEmpty()) throw IllegalStateException("No signing keys are configured")

        val iat = issuedAt ?: throw IllegalStateException("issuedAt must be specified")
        val ttlSeconds = ttl ?: throw IllegalStateException("ttl must be specified")

        if (!claims.containsKey("sub")) {
            throw IllegalStateException("subject must be specified")
        }

        val signingKey = selectRandomKey(keys)
        val jwt =
            Jwts
                .builder()
                .header()
                .keyId(signingKey.keyId)
                .and()
                .claims(claims)
                .issuedAt(Date(iat.timestamp()))
                .expiration(Date(iat.timestamp() + ttlSeconds))
                .signWith(signingKey.privateKey, Jwts.SIG.RS256)
                .compact()
        return jwt
    }
}

private fun selectRandomKey(keys: List<IdentityKeyPair>): IdentityKeyPair {
    val random = Random()
    return keys[random.nextInt(keys.size)]
}
