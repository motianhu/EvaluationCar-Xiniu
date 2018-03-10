package com.smona.app.evaluationcar.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {

    private static final int STREAM_BUFFER_LENGTH = 1024;
    /**
     * The MD5 message digest algorithm defined in RFC 1321.
     */
    private static final String MD5 = "MD5";
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex
     * string.
     *
     * @param data Data to digest
     * @return MD5 digest as a hex string
     * @throws IOException On error reading from the stream
     * @since 1.4
     */
    public static String md5Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(md5(data));
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data Data to digest
     * @return MD5 digest
     * @throws IOException On error reading from the stream
     * @since 1.4
     */
    private static byte[] md5(final InputStream data) throws IOException {
        return digest(getMd5Digest(), data);
    }

    /**
     * Read through an InputStream and returns the digest for the data
     *
     * @param digest The MessageDigest to use (e.g. MD5)
     * @param data   Data to digest
     * @return MD5 digest
     * @throws IOException On error reading from the stream
     */
    private static byte[] digest(final MessageDigest digest,
                                 final InputStream data) throws IOException {
        return updateDigest(digest, data).digest();
    }

    /**
     * Reads through an InputStream and updates the digest for the data
     *
     * @param digest The MessageDigest to use (e.g. MD5)
     * @param data   Data to digest
     * @return MD5 digest
     * @throws IOException On error reading from the stream
     * @since 1.8
     */
    private static MessageDigest updateDigest(final MessageDigest digest,
                                              final InputStream data) throws IOException {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            digest.update(buffer, 0, read);
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return digest;
    }

    /**
     * Returns an MD5 MessageDigest.
     *
     * @return An MD5 digest instance.
     * @throws IllegalArgumentException when a {@link NoSuchAlgorithmException} is
     *                                  caught, which should never happen because MD5 is a built-in
     *                                  algorithm
     */
    private static MessageDigest getMd5Digest() {
        return getDigest(MD5);
    }

    /**
     * Returns a <code>MessageDigest</code> for the given <code>algorithm</code>
     * .
     *
     * @param algorithm the name of the algorithm requested. See <a href=
     *                  "http://java.sun.com/j2se/1.3/docs/guide/security/CryptoSpec.html#AppA"
     *                  >Appendix A in the Java Cryptography Architecture API
     *                  Specification & Reference</a> for information about standard
     *                  algorithm names.
     * @return An MD5 digest instance.
     * @throws IllegalArgumentException when a {@link NoSuchAlgorithmException} is
     *                                  caught.
     * @see MessageDigest#getInstance(String)
     */
    private static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable
     * for using as a disk filename.
     */
    public static String stringToMD5(String key) {
        String cacheKey = null;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes(DEFAULT_CHARSET));
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
