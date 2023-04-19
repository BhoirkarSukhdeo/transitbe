package com.axisbank.transit.core.shared.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class EncryptionUtil {
    /**
     * This Function takes in plain string and encrypts it with given algorithm and secreteKey
     * and return encrpted text (base 64)
     * @param plainText
     * @param secreteKey
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static String encrypt(String plainText, String secreteKey,  String algorithm) throws Exception
    {
        Key key = generateKey(secreteKey.getBytes(), algorithm);
        Cipher chiper = Cipher.getInstance(algorithm);
        chiper.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = chiper.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    /**
     * generates Key with given secreteKey and Algorithm.
     * @param keyVal
     * @param algorithm
     * @return
     * @throws Exception
     */
    private static Key generateKey(byte[] keyVal, String algorithm) throws Exception
    {
        return new SecretKeySpec(keyVal, algorithm);
    }

    /**
     * This Function takes in encrypted Text(Base 64) and decrypts it with given algorithm and secreteKey
     * @param encryptedText
     * @param secreteKey
     * @param algorithm
     * @return
     * @throws Exception
     */

    public static String decrypt(String encryptedText, String secreteKey,  String algorithm) throws Exception
    {
        Key key = generateKey(secreteKey.getBytes(), algorithm);
        Cipher chiper = Cipher.getInstance(algorithm);
        chiper.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedText);
        byte[] decValue = chiper.doFinal(decordedValue);
        return new String(decValue);
    }


    @SuppressWarnings("null")
    public static String aesEncrypt(final String word, final String password){
        try{
            byte[] ivBytes;
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            byte[] saltBytes = bytes;
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2withHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 50,
                    128);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] encryptedTextBytes = cipher.doFinal(word.getBytes("UTF-8"));
//prepend salt and vi
            byte[] buffer = new byte[saltBytes.length + ivBytes.length +
                    encryptedTextBytes.length];
            System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
            System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length +
                    ivBytes.length, encryptedTextBytes.length);
            return Base64.getMimeEncoder().encodeToString(buffer);
//return Base64.encodeBase64String(buffer);
        }catch(BadPaddingException | InvalidParameterSpecException |
                NoSuchAlgorithmException |
                InvalidKeySpecException | NoSuchPaddingException |
                InvalidKeyException |
                IllegalBlockSizeException | UnsupportedEncodingException ex2){
            final Exception ex = null;
            final Exception e = ex;
            return "ER001" + e.toString();
        }
    }
    @SuppressWarnings("null")
    public static String aesDecrypt(final String encryptedText, final String password){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//strip off the salt and iv
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getMimeDecoder().decode(encryptedText));
            byte[] saltBytes = new byte[20];
            buffer.get(saltBytes, 0, saltBytes.length);
            byte[] ivBytes1 = new byte[cipher.getBlockSize()];
            buffer.get(ivBytes1, 0, ivBytes1.length);
            byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length -
                    ivBytes1.length];
            buffer.get(encryptedTextBytes);
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 50,
                    128);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes1));
            byte[] decryptedTextBytes = null;
            try {
                decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
            } catch (Exception e) {
                log.error("Exception in aesDecrypt method: {}", e.getMessage());
            }
            return new String(decryptedTextBytes);
        }catch(NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                InvalidKeyException | InvalidAlgorithmParameterException ex2){
            final Exception ex = null;
            final Exception e = ex;
            return "ER001" + e.toString();
        }
    }

    public static String aesEncryptPpim(final String word, final String password, String iv) {
        try{
            byte[] ivBytes = iv.getBytes();
            SecretKeySpec secret = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));
            byte[] encryptedTextBytes = cipher.doFinal(word.getBytes(StandardCharsets.UTF_8));
            return Base64.getMimeEncoder().encodeToString(encryptedTextBytes);
        } catch(BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException ex2){
            return "ER001" + ex2.toString();
        }
    }

    public static String aesDecryptPpim(final String encryptedText, final String password, String iv){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getMimeDecoder().decode(encryptedText));
            byte[] ivBytes1 = iv.getBytes();
            byte[] encryptedTextBytes = new byte[buffer.capacity()];
            buffer.get(encryptedTextBytes);
            SecretKeySpec secret = new SecretKeySpec(password.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes1));
            byte[] decryptedTextBytes = null;
            try {
                decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
            } catch (Exception e) {
                log.error("Exception in aesDecryptPpim method: {}", e.getMessage());
            }
            return new String(decryptedTextBytes);
        }catch(NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | InvalidAlgorithmParameterException ex2){
            return "ER001" + ex2.toString();
        }
    }


    // Decrypt encrypted string from client using AES
    public static String decryptText(String cipherText, String secret) throws Exception {
        String decryptedText=null;
        byte[] cipherData = java.util.Base64.getDecoder().decode(cipherText);
        byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);
            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
            return decryptedText;
        }
        catch (Exception ex){
            log.error("error on decrypt: {}",ex.getMessage());
            throw new Exception("Failed to decrypt data");
        }
    }

    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;
        try {
            md.reset();
            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {
                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }
                generatedLength += digestLength;
            }
            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);
            return result;
        } catch (DigestException e) {
            throw new RuntimeException(e);
        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte)0);
        }
    }
}
