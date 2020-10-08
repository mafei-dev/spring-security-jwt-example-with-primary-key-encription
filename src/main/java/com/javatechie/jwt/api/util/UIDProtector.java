package com.javatechie.jwt.api.util;


import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/*
  @Author kalhara@bowsin
  @Created 9/30/2020 11:49 PM  
*/
@Component
public class UIDProtector {


    public static final String algorithm = "AES";

    private SecretKeySpec prepareSecreteKey(String myKey) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
        key = sha.digest(key);
        key = Arrays.copyOf(key, 32);
        SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
        return secretKey;
    }

    private String encrypt(String strToEncrypt, String secret) {
        try {
            SecretKeySpec secretKeySpec = prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting {} " + e.toString());
        }
        return null;
    }

    /**
     * @param toEncryptValue
     * @param key
     * @return encrypted hash
     * @throws IllegalAccessException
     */
    public String encryptSingle(String toEncryptValue, String key) throws IllegalAccessException {
        return encrypt(toEncryptValue, key);
    }


    /**
     * @param dataObject
     * @param key
     * @param <T>
     * @throws IllegalAccessException
     * @apiNote encrypt all UIDs from the given object of annotated UID()
     */
    public <T> void encryptAll(T dataObject, String key) throws IllegalAccessException {
        Class<?> aClass = dataObject.getClass();
        for (Field declaredField : aClass.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(UID.class)) {
                if (declaredField.getAnnotation(UID.class).encrypt()) {
                    declaredField.setAccessible(true);
                    String encryptedVal = encrypt(declaredField.get(dataObject).toString(), key);
                    declaredField.set(dataObject, encryptedVal);
                }
            }

        }
    }


    private String decrypt(String encryptedString, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec secretKeySpec = prepareSecreteKey(secretKey);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            String stringUUID = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedString)));
            //check uid is equal to UUID format
            UUID.fromString(stringUUID);
            return stringUUID;
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    /**
     * @param encryptedString
     * @param key
     * @return decrypted UID
     */
    public String decryptSingle(String encryptedString, String key) {
        return decrypt(encryptedString, key);
    }


    /**
     * @param dataObject
     * @param key
     * @param <T>
     * @throws IllegalAccessException
     */
    public <T> void decryptAll(T dataObject, String key) throws IllegalAccessException {
        Class<?> aClass = dataObject.getClass();

        for (Field declaredField : aClass.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(UID.class)) {
                if (declaredField.getAnnotation(UID.class).encrypt()) {
                    declaredField.setAccessible(true);
                    String encryptedVal = decrypt(declaredField.get(dataObject).toString(), key);
                    declaredField.set(dataObject, encryptedVal);
                }
            }
        }

    }

}
