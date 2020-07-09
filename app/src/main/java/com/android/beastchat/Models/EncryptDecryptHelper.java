package com.android.beastchat.Models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecryptHelper {

    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private static SecretKeySpec secretKey;
    private static byte[] key;

    //MD5 ENCRYPTION
    public String encryptWithSalt(String str, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.reset();
        digest.update(salt);
        byte[] hash = digest.digest(str.getBytes());
        return bytesToStringHex(hash);
    }

    //MD5 is a one way encryption technique
    //so decryption is just checking the encrypted hash with the stored one.
    public boolean decryptWithSalt(String str, byte[] salt, String hash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.reset();
        digest.update(salt);
        String myHash = bytesToStringHex(digest.digest(str.getBytes()));

        if(myHash.equals(hash)) {
            return true;
        }
        return false;
    }

    private static String bytesToStringHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length*2];
        for(int i = 0; i<bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i*2] = hexArray[v >>> 4];
            hexChars[i*2+1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] createSalt() {
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }





    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //AES Encryption - Advanced Encryption Standard
    public String encryptWithAES(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            }
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decryptWithAES(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}
