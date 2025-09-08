package demo.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;

public class Utils {

    public static KeyPair generateKey() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            // SECURITY FIX: Use secure key length (2048 bits minimum)
            keyPairGen.initialize(2048);
            return keyPairGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static void deleteFile(String fileName) throws IOException {
        // SECURITY FIX: Prevent path traversal attacks
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        // Sanitize the filename to prevent path traversal
        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "");
        if (!sanitizedFileName.equals(fileName)) {
            throw new IllegalArgumentException("Invalid characters in filename");
        }
        
        // Ensure the file is within a safe directory
        File safeBaseDir = new File("/tmp/safe_uploads/");
        File file = new File(safeBaseDir, sanitizedFileName);
        
        // Verify the file is still within the safe directory after resolution
        String canonicalBasePath = safeBaseDir.getCanonicalPath();
        String canonicalFilePath = file.getCanonicalPath();
        if (!canonicalFilePath.startsWith(canonicalBasePath)) {
            throw new IllegalArgumentException("Path traversal attempt detected");
        }
        
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }

    public static void executeJs(String input) throws ScriptException {
        // SECURITY FIX: Removed dynamic code execution vulnerability
        // Validate input against a whitelist of allowed operations
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        
        // Only allow specific predefined operations instead of arbitrary code execution
        String sanitizedInput = input.trim().toLowerCase();
        switch (sanitizedInput) {
            case "hello":
                System.out.println("Hello World");
                break;
            case "version":
                System.out.println("Version 1.0");
                break;
            default:
                throw new IllegalArgumentException("Operation not allowed: " + sanitizedInput);
        }
    }

    public static void encrypt(byte[] key, byte[] ptxt) throws Exception {
        // SECURITY FIX: Use cryptographically secure random IV instead of static nonce
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[12]; // 96-bit IV for GCM mode
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        
        // Note: In production, you would need to return both the encrypted data and the IV
        // so that decryption can use the same IV
    }
}
