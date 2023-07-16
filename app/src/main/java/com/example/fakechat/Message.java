package com.example.fakechat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

import com.google.firebase.database.Exclude;

public class Message {
    private String fakeText;
    @Exclude
    private String realText;
    private String author;
    private String key;
    private Date timestamp;
    private boolean isDecrypted;
    private String encryptedText;

    public Message(String fakeText, String realText, String key, String author) {
        this.fakeText = fakeText;
        this.realText = realText;
        this.key = key;
        this.author = author;
        this.timestamp = new Date();
        this.isDecrypted = false;
        try {
            this.encryptedText = encrypt(realText, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFakeText() {
        return fakeText;
    }

    public String getRealText() {
        return realText;
    }

    public String getKey() {
        return key;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDecrypted() {
        return isDecrypted;
    }
    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }
    public void setDecrypted(boolean isDecrypted) {
        this.isDecrypted = isDecrypted;
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fakeText", fakeText);
        result.put("key", key);
        result.put("author", author);
        result.put("timestamp", timestamp);
        result.put("isDecrypted", isDecrypted);
        result.put("encryptedText", encryptedText);
        return result;
    }

    public String decrypt(String inputKey) {
        if (inputKey.equals(key)) {
            this.isDecrypted = true;
            return decrypt(encryptedText, inputKey);
        } else {
            return null;
        }
    }

    private String encrypt(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(formatKey(key).getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(data.getBytes("UTF8"));
        return Base64.encodeToString(cipherText , Base64.DEFAULT);
    }

    private String decrypt(String cipherText, String inputKey) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(formatKey(inputKey).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedByteValue = cipher.doFinal(Base64.decode(cipherText.getBytes(),Base64.DEFAULT));
            return new String(decryptedByteValue,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatKey(String key) {
        key = key.length() < 16 ? String.format("%-16s", key) : key.substring(0, 16);
        return key;
    }
}
