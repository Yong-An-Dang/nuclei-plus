package com.g3g4x5x6.nuclei.ultils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils {
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static String base64Encode(String text) {
        byte[] textByte = text.getBytes(StandardCharsets.UTF_8);
        //System.out.println(encodedText);
        return encoder.encodeToString(textByte);
    }

    public static String base64EncodeByByte(byte[] textByte) {
        return encoder.encodeToString(textByte);
    }

    public static String base64Decode(String encodedText) {
        //System.out.println(text);
        return new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
    }
}

