package com.clouway.oauth2.codechallenge

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Base64

/**
 * @author Vasil Mitov <vasil.mitov></vasil.mitov>@clouway.com>
 */
class AuthorizationCodeVerifier : CodeVerifier {
	override fun verify(codeChallenge: CodeChallenge, providedCodeVerifier: String): Boolean {
		try {
			//no code verifier was provided and no code challenge was saved so this is a normal OAuth2 code flow
			if (providedCodeVerifier.isEmpty() && !codeChallenge.isProvided) {
				return true
			}
			//there is a saved code challenge but provided providedCodeVerifier is empty.
			if (providedCodeVerifier.isEmpty() && codeChallenge.isProvided) {
				return false
			}
			//providedCodeVerifier was provided but no codeChallenge was saved.
			if (providedCodeVerifier.isNotEmpty() && !codeChallenge.isProvided) {
				return false
			}
			
			if (codeChallenge.method == "plain") {
				return codeChallenge.transformedCodeChallenge == providedCodeVerifier
			}
			
			if (codeChallenge.method == "S256") {
				val hashed = MessageDigest.getInstance("SHA-256").digest(
					providedCodeVerifier.toByteArray(
						StandardCharsets.UTF_8
					)
				)
				
				val codeChallengeValue = Base64.getUrlEncoder().withoutPadding().encodeToString(hashed)
				
				return codeChallengeValue == codeChallenge.transformedCodeChallenge
			}
			return false
		} catch (e: NoSuchAlgorithmException) {
			return false
		}
	}
}
