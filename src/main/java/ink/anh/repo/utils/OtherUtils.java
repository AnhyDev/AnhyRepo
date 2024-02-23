package ink.anh.repo.utils;

import java.util.Base64;

public class OtherUtils {

	public static String encryptString(String input, String key) {
	    byte[] inputBytes = input.getBytes();
	    byte[] keyBytes = key.getBytes();
	    byte[] encryptedBytes = new byte[inputBytes.length];

	    for (int i = 0; i < inputBytes.length; i++) {
	        encryptedBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
	    }

	    return Base64.getEncoder().encodeToString(encryptedBytes);
	}
	
	public static String decryptString(String input, String key) {
	    byte[] inputBytes = Base64.getDecoder().decode(input);
	    byte[] keyBytes = key.getBytes();
	    byte[] decryptedBytes = new byte[inputBytes.length];

	    for (int i = 0; i < inputBytes.length; i++) {
	        decryptedBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
	    }

	    return new String(decryptedBytes);
	}

	public static String encodeToBase64(String input) {
	    // Кодуємо рядок у Base64
	    return Base64.getEncoder().encodeToString(input.getBytes());
	}
	
	public static String decodeFromBase64(String input) {
	    // Декодуємо рядок з Base64
	    byte[] decodedBytes = Base64.getDecoder().decode(input);
	    return new String(decodedBytes);
	}
	
	public static String encryptAndEncodeBase64(String input, String key) {
	    // Шифруємо вхідний рядок
	    String encrypted = encryptString(input, key);
	    // Кодуємо зашифрований рядок у Base64
	    return encodeToBase64(encrypted);
	}
	
	public static String decodeAndDecryptBase64(String input, String key) {
	    // Декодуємо рядок з Base64
	    String decoded = decodeFromBase64(input);
	    // Дешифруємо декодований рядок
	    return decryptString(decoded, key);
	}
}
