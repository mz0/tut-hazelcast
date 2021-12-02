package com.exactpro.compat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Produces a hash in a manner compatible<><br>
 * with the default shsha-web implementation:<br>
 * SHA-512 with salt, 1024 iterations
 */
public class PasswordHash {
	public final static int SHSHA_DEFAULT_ITERATIONS = 1024;

	public static String bytes2string(byte[] ba) {
		StringBuilder sb = new StringBuilder();
		for (byte b : ba) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public static String getSha512Hash(String message, byte[] salt, int iterations) {
        String hash;
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            digester.reset();
            digester.update(salt);
            byte[] hashed = digester.digest(message.getBytes(StandardCharsets.UTF_8));
            int toDo = iterations - 1;
            for (int i = 0; i < toDo; i++) {
                digester.reset();
                hashed = digester.digest(hashed);
            }
            hash = bytes2string(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hash;
    }

	public static String getShshaHash(String message, String salt) {
		return getSha512Hash(message, salt.getBytes(StandardCharsets.UTF_8), SHSHA_DEFAULT_ITERATIONS);
	}

	@SuppressWarnings("unused") // TODO move to src/test/
	public static boolean isShshaWebCompatible() {
		String adminHash =
			"5ad5220c22d98de88893cdca9cb620343404c9ef0d3f58db48160a954dead0bd" +
			"2321e2ae0de22b18df8e7e853638b9e68ad0d959452243c88a30c9585b01d4e1";
		String adminSalt = "43e7d427e16e5dad9668b02e51227a12";
		return getShshaHash("admin", adminSalt).equals(adminHash);
	}
}
