package com.example.myapplication;

import androidx.annotation.NonNull;

public class BitConverter {
    public static byte[] getBytes(boolean x) {
        return new byte[] { (byte) (x ? 1 : 0) };
    }

    public static byte[] getBytes(char c) {
        return new byte[] { (byte) (c & 0xff), (byte) (c >> 8 & 0xff) };
    }

    public static byte[] getBytes(double x) {
        return getBytes(Double.doubleToRawLongBits(x));
    }

    public static byte[] getBytes(short x) {
        return new byte[] { (byte) (x >>> 8), (byte) x };
    }

    public static byte[] getBytes(int x) {
        return new byte[]{(byte) x, (byte) (x >>> 8), (byte) (x >>> 16), (byte) (x >>> 24)};
    }

    public static byte[] getBytes(long x) {
        return new byte[]{(byte) ((int) (x >>> 56)), (byte) ((int) (x >>> 48)), (byte) ((int) (x >>> 40)), (byte) ((int) (x >>> 32)), (byte) ((int) (x >>> 24)), (byte) ((int) (x >>> 16)), (byte) ((int) (x >>> 8)), (byte) ((int) x)};
    }

    public static byte[] getBytes(float x) {
        return getBytes(Float.floatToRawIntBits(x));
    }

    public static byte[] getBytes(String x) {
        return x.getBytes();
    }

    public static long doubleToInt64Bits(double x) {
        return Double.doubleToRawLongBits(x);
    }

    public static double int64BitsToDouble(long x) {
        return (double) x;
    }

    public boolean toBoolean(byte[] bytes, int index) throws Exception {
        if (bytes.length != 1)
            throw new Exception(
                    "The length of the byte array must be at least 1 byte long.");
        return bytes[index] != 0;
    }

    public char toChar(byte[] bytes, int index) throws Exception {
        if (bytes.length != 2)
            throw new Exception(
                    "The length of the byte array must be at least 2 bytes long.");
        return (char) ((0xff & bytes[index]) << 8 | (0xff & bytes[index + 1]) << 0);
    }

    public double toDouble(byte[] bytes, int index) throws Exception {
        if (bytes.length != 8)
            throw new Exception(
                    "The length of the byte array must be at least 8 bytes long.");
        return Double.longBitsToDouble(toInt64(bytes, index));
    }

    public static short toInt16(byte[] bytes, int index) throws Exception {
        if (bytes.length != 8)
            throw new Exception(
                    "The length of the byte array must be at least 8 bytes long.");
        return (short) ((0xff & bytes[index]) << 8 | (0xff & bytes[index + 1]) << 0);
    }

    public static int toInt32(byte[] bytes, int index) throws Exception {
        if (bytes.length == 4) {
            return ((bytes[index] & 255) << 56) | ((bytes[index + 1] & 255) << 48) | ((bytes[index + 2] & 255) << 40) | ((bytes[index + 3] & 255) << 32);
            // return ((bytes[index] & 255) << 32) | ((bytes[index + 1] & 255) << 40) | ((bytes[index + 2] & 255) << 48) | ((bytes[index + 3] & 255) << 56);
        }

        throw new Exception("The length of the byte array must be at least 4 bytes long.");
    }

    public static long toInt64(byte[] bytes, int index) throws Exception {
        if (bytes.length == 8) {
            return (((long) (bytes[index + 6] & 255)) << 8) | (((long) (bytes[index] & 255)) << 56) | (((long) (bytes[index + 1] & 255)) << 48) | (((long) (bytes[index + 2] & 255)) << 40) | (((long) (bytes[index + 3] & 255)) << 32) | (((long) (bytes[index + 4] & 255)) << 24) | (((long) (bytes[index + 5] & 255)) << 16) | (((long) (bytes[index + 7] & 255)) << 0);
        }

        throw new Exception("The length of the byte array must be at least 8 bytes long.");
    }

    public static float toSingle(byte[] bytes, int index) throws Exception {
        if (bytes.length != 4)
            throw new Exception(
                    "The length of the byte array must be at least 4 bytes long.");
        return Float.intBitsToFloat(toInt32(bytes, index));
    }

    public static String toString(byte[] bytes) throws Exception {
        if (bytes == null)
            throw new Exception("The byte array must have at least 1 byte.");
        return new String(bytes);
    }
}
