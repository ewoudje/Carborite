package com.ewoudje.carborite;

//Made by ewoudje

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class MinecraftEncryption {

    public static KeyPair generateKeyPair() { //b
        try {
            KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");

            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            Server.error("Key pair generation failed!");
            return null;
        }
    }

    public static byte[] a(String s, PublicKey publickey, SecretKey secretkey) {
        try {
            return a("SHA-1", new byte[][] { s.getBytes("ISO_8859_1"), secretkey.getEncoded(), publickey.getEncoded()});
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            unsupportedencodingexception.printStackTrace();
            return null;
        }
    }

    private static byte[] a(String s, byte[]... abyte) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance(s);
            byte[][] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte[] abyte2 = abyte1[j];

                messagedigest.update(abyte2);
            }

            return messagedigest.digest();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }

    public static PublicKey getPublicKey(byte[] abyte) { //a
        try {
            X509EncodedKeySpec x509encodedkeyspec = new X509EncodedKeySpec(abyte);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");

            return keyfactory.generatePublic(x509encodedkeyspec);
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            ;
        } catch (InvalidKeySpecException invalidkeyspecexception) {
            ;
        }

        Server.error("Public key reconstitute failed!");
        return null;
    }

    public static SecretKey getSecretKey(PrivateKey privatekey, byte[] abyte) { //a
        return new SecretKeySpec(b(privatekey, abyte), "AES");
    }

    public static byte[] b(Key key, byte[] abyte) {
        return a(2, key, abyte);
    }

    private static byte[] a(int i, Key key, byte[] abyte) {
        try {
            return a(i, key.getAlgorithm(), key).doFinal(abyte);
        } catch (IllegalBlockSizeException illegalblocksizeexception) {
            illegalblocksizeexception.printStackTrace();
        } catch (BadPaddingException badpaddingexception) {
            badpaddingexception.printStackTrace();
        }

        Server.error("Cipher data failed!");
        return null;
    }

    private static Cipher a(int i, String s, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(s);

            cipher.init(i, key);
            return cipher;
        } catch (InvalidKeyException invalidkeyexception) {
            invalidkeyexception.printStackTrace();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
        } catch (NoSuchPaddingException nosuchpaddingexception) {
            nosuchpaddingexception.printStackTrace();
        }

        Server.error("Cipher creation failed!");
        return null;
    }

    public static Cipher a(int i, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");

            cipher.init(i, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (GeneralSecurityException generalsecurityexception) {
            throw new RuntimeException(generalsecurityexception);
        }
    }
}

