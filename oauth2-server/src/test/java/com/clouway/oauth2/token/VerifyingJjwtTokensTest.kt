package com.clouway.oauth2.token

import com.clouway.oauth2.jws.Pem
import com.clouway.oauth2.jws.RsaJwsSignature
import com.google.common.collect.ImmutableMap
import com.google.common.io.BaseEncoding
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Date

private val SECRET_KEY: String =
    """
	 	-----BEGIN PRIVATE KEY-----
	 	MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCIga1VBV5S+diJ
	 	2abh4YVHAWb7rlM9pnK1GVZOYE6xheJ1jsoxh03lUHjw8HtBR2USIAWG1a/kAAdz
	 	fngrFFudpOO5RU1yEFnz2WNwy7gj3xu1erqtxmctOkyGA2hfATzHUfStSl8gyMXK
	 	f3GURe4m+Y/qa1nI0ClGrJWbFdM9gz9YjRf3rAJsYZR17dGyBRIdlMeKF/QudP68
	 	Dv/2/fssOhrIz5RZl2pkovWYidZxeRBl9XS9naoUZM8HvqoyUTE7ByjvDBR9qBkS
	 	mCXJAUb82/LkQFLhufhnHCSC8b+kHWpBhiMqslVnR00mhiQHCW7PNtLfb6ugTZNi
	 	cfBIaI1DAgMBAAECggEAEwXnTtrhqzSQPZ2sSPwxo5SJcnd3uDay85PlWCTJsqmS
	 	xokwmjhd3aAaSpFoy88UQbNescyjp2VtpGWyf2Zl4hExfwcuZL/smTPpTLXHIpCb
	 	/u1siH0GseHW+jINYHf+rVQ5gdDEcwAnuDGMdXpNVvceXC+7omWH6wZwDt26w26L
	 	J31/aHwvjrlIbQq4PAS0gAh6CHUgyeOv8MkCR5rVhS9Db03U6QFRc79CMMghl906
	 	C9rqRjDmIX3MEh/IiW/hJzmz8kPNL1RiUb5OI24ceDtWa2AzvGalwDS0SvtS8SBT
	 	9jI0htKl8NGY6oknfkopw6LuVjZ2hOdgV3z26DJ2YQKBgQDbUlAnqdSLrtzepKZN
	 	PJQqNgC2ATUVvpcE64ze48t8sV5lVsdceLllzVxD0hjh6ngqynDGJjzp+AEcS5LQ
	 	nCYinVb0x8iIZlC5dIPDP85mIfYARFFHF20DD0HoMJfcxqeeJsaU24Pkh7Y8V/BC
	 	as4DM8ByOreBk2a/VZaMWA9X8wKBgQCfVdqHBeZDOSoHPHZ9ApMsHAYJmp/0MBzv
	 	vQda4lZehmJ9Hefr5J3tk4c4xH+FODAEqaWHoFe+Y95XuR69xkGiBiIHlhMK1xSA
	 	1Gmn+WWPTjhPcNqGWXfeTnMZNWgZguo7e10fHNWQT2RklegJfB593E4K83Y+pG+b
	 	4adiHMQZcQKBgCuI9lI5OvCTQFKNmllAiiSq3Y9DRBdR4sZeP3NLAmx5BMTW6fHo
	 	IN0dW5A21yuZEEtmLeaXVoYW7ZmBQt5X8JX0Z3tlYN/6d1Go2DLcqorJePxqkzuq
	 	YcA2uh1t7+cqI8GX7tlDjbXCXqExz4ZPjx9BmZTTJPP6n22hfqXTIRCTAoGAC498
	 	Gn3YFhqIrRu68RkFupaR7ZJ1do8jGlXZucNgRt1zOea4lAnzV3BzyC+hnPXVrhDs
	 	/KkqlJrEYBMDYvuGeY3+XBSMbyXpy+sde12B++LN/R2QDV1icBO7ECIq2mcAPa6W
	 	tBIwgJbyDsY9nqqNv84DL5I4ixT9MA8wSNMTe1ECgYB329OIU8nIpn4KQDiFlNdg
	 	VE+F2I7f4zNtM/NAeKoG7WGY6CO2sGmKKvT7g5Ql5/mbzsowkzaLNrzR0RQNEy5r
	 	IsCHV4InIHYnIPxuxT2nWP33x1rlso7pB5xTf5q0ee+6ps25zWmBfEYlM72dPFCi
	 	e16DdKBIzOaDt8ShlblC9g==
	 	-----END PRIVATE KEY-----
	 	""".trimIndent()

/**
 * @author Vasil Mitov <v.mitov.clouway></v.mitov.clouway>@gmail.com>
 */
class VerifyingJjwtTokensTest {
    private val pem = Pem()

    @Test
    @Throws(Exception::class)
    fun tokenIsValidTest() {
        val block = pem.parse(ByteArrayInputStream(SECRET_KEY.toByteArray(StandardCharsets.UTF_8)))
		
        val identity =
            Identity(
                "123",
                "Pojo",
                "Foo",
                "Bar",
                "example@email.com",
                "",
                ImmutableMap.of<String, Any>(
                    "customerId",
                    "::customer Id::",
                    "customerName",
                    "::customer name::",
                    "isAdmin",
                    false,
                ),
            )
		
        val claims: Map<String, Any> =
            ImmutableMap
                .builder<String, Any>()
                .put("iss", "example.host")
                .put("aud", "123")
                .put("sub", identity.id)
                .put("iat", Date())
                .put("exp", Date())
                .put("name", identity.name)
                .put("email", identity.email)
                .put("given_name", identity.givenName)
                .put("family_name", identity.familyName)
                .putAll(identity.claims)
                .build()
		
        val idToken =
            Jwts
                .builder()
                .setHeaderParam("cid", "certKeyIdentifier")
                .setClaims(claims)
                .signWith(SignatureAlgorithm.RS256, getPrivateKey(block!!))
                .compact()

        val parts = idToken.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val tokenWithoutSignature = String.format("%s.%s", parts[0], parts[1])
        val rsaJwsSignature = RsaJwsSignature(BaseEncoding.base64Url().decode(parts[2]))
		
        val tokenIsValid = rsaJwsSignature.verifyWithPrivateKey(tokenWithoutSignature.toByteArray(), block)
        Assert.assertTrue(tokenIsValid)
    }

    @Test
    @Throws(Exception::class)
    fun tokenIsNotValidTest() {
        val pem = Pem()
        val block = pem.parse(ByteArrayInputStream(SECRET_KEY.toByteArray(StandardCharsets.UTF_8)))
        val claims: Map<String, Any> =
            ImmutableMap
                .builder<String, Any>()
                .put("iss", "example.host")
                .put("aud", "123")
                .put("sub", "::subject::")
                .put("iat", Date())
                .put("exp", Date())
                .build()
		
        val idToken =
            Jwts
                .builder()
                .setHeaderParam("cid", "certKeyIdentifiers")
                .setClaims(claims)
                .signWith(getPrivateKey(block!!), SignatureAlgorithm.RS256)
                .compact()
		
        val tokenHasBeenTemperedWithSignature =
            "mhkSKK6lcwNZ085PYfUc8uQbynCnMd25IcomnQ9Tkbxb9FvhA51Klgu_nmyQtvsqytYM0HfDCUA8jf2MprQ79xiu7DVMWQ9bS0W18PpUN6KNi5_L6KMxzNrS8DxRUliYYIcRYDh5xWfzXrDGpnw208vSfH1GD4PGY2z6JhlFkf0"
		
        val parts = idToken.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val tokenWithoutSignature = String.format("%s.%s", parts[0], parts[1])
        val rsaJwsSignature = RsaJwsSignature(BaseEncoding.base64Url().decode(tokenHasBeenTemperedWithSignature))
		
        val tokenIsValid = rsaJwsSignature.verifyWithPrivateKey(tokenWithoutSignature.toByteArray(), block)
        Assert.assertFalse(tokenIsValid)
    }

    private fun getPrivateKey(block: Pem.Block): PrivateKey? {
        val keySpec = PKCS8EncodedKeySpec(block.bytes)
        val kf: KeyFactory
        try {
            kf = KeyFactory.getInstance("RSA")
            return kf.generatePrivate(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        }
        return null
    }
}
