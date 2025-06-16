package org.hl7.davinci.ehrserver.authproxy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for PKCE (Proof Key for Code Exchange) operations.
 * Provides methods to generate code verifier and code challenge as per RFC 7636.
 */
public class PKCEUtil {

    private static final String CODE_VERIFIER_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
    private static final int CODE_VERIFIER_MIN_LENGTH = 43;
    private static final int CODE_VERIFIER_MAX_LENGTH = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a cryptographically random code verifier string.
     * The code verifier is a URL-safe string with minimum length of 43 characters
     * and maximum length of 128 characters.
     * 
     * @return A URL-safe code verifier string
     */
    public static String generateCodeVerifier() {
        // Generate a code verifier with 64 characters (within the recommended range)
        int length = 64;
        StringBuilder codeVerifier = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(CODE_VERIFIER_CHARACTERS.length());
            codeVerifier.append(CODE_VERIFIER_CHARACTERS.charAt(randomIndex));
        }
        
        return codeVerifier.toString();
    }

    /**
     * Generates a code challenge from the given code verifier using SHA256 method.
     * The code challenge is the Base64-URL encoded SHA256 hash of the code verifier.
     * 
     * @param codeVerifier The code verifier string
     * @return The Base64-URL encoded SHA256 hash of the code verifier
     * @throws RuntimeException if SHA256 algorithm is not available
     */
    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Validates that a code verifier meets the RFC 7636 requirements.
     * 
     * @param codeVerifier The code verifier to validate
     * @return true if the code verifier is valid, false otherwise
     */
    public static boolean isValidCodeVerifier(String codeVerifier) {
        if (codeVerifier == null) {
            return false;
        }
        
        int length = codeVerifier.length();
        if (length < CODE_VERIFIER_MIN_LENGTH || length > CODE_VERIFIER_MAX_LENGTH) {
            return false;
        }
        
        // Check if all characters are valid
        for (char c : codeVerifier.toCharArray()) {
            if (CODE_VERIFIER_CHARACTERS.indexOf(c) == -1) {
                return false;
            }
        }
        
        return true;
    }
}
