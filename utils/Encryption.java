package com.fta.myapplication.test;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

/**
 * 文件描述：
 * 作者： Created by fta on 2017/4/24.
 */

public class Encryption {

    /**
     * base64只是一种编码方式，不要用它加密，可用它传输，加密后使用base64
     * @param encryted 加密了的
     */
    public String base64(byte [] encryted){
        Log.i(TAG, "Encryption ->base64: "+Base64.encodeToString(encryted, Base64.DEFAULT));
       return Base64.encodeToString(encryted, Base64.DEFAULT);
    }


    //  Hash 算法，单项不可逆可以用来密码加密（还可以校验下载文件的完整性）

    /**
     * 如使用SHA-256算法对message字符串做哈希
     * @param message 需要加密的信息
     * @return  加密后密文
     */
    public String fromSha256(String message) throws NoSuchAlgorithmException {
        byte [] input = message.getBytes();
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(input);
        byte [] output = sha.digest();
        Log.i(TAG, "Encryption ->fromSha256:"+ Base64.encodeToString(output,Base64.DEFAULT));
        return Base64.encodeToString(output,Base64.DEFAULT);
    }

    /**
     * 消息认证法，防止消息仿造，需要提供一个消息认证码（MAC，Message authentication code）
     * 消息认证码是带密钥的hash函数，基于密钥和hash函数
     * 密钥双方事先约定，不能让第三方知道
     * 消息发送者使用MAC算法计算出消息的MAC值，追加到消息后面一起发送给接收者
     * 接收者收到消息后，用相同的MAC算法计算接收到消息MAC值，并与接收到的MAC值对比是否一样
     * 建议使用HMAC-SHA256算法
     * @param message
     * @return
     */
    public String fromAuthAndSha(String message) throws NoSuchAlgorithmException, InvalidKeyException {
        //初始化 KeyGenerator
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        //产生秘钥
        SecretKey secretKey = keyGenerator.generateKey();
        //获取秘钥
        byte[] key = secretKey.getEncoded();
        Log.i(TAG, "Encryption ->fromAuthAndSha: fromAuthAndSha=="+Base64.encodeToString(key,Base64.DEFAULT));

        //还原秘钥
        SecretKey restoreSecretKey = new SecretKeySpec(key,"HmacSHA256");
        //实例化 MAC
        Mac mac = Mac.getInstance(restoreSecretKey.getAlgorithm());
        //初始化 MAC
        mac.init(restoreSecretKey);
        //执行摘要
        byte[] hmacSHA256Bytes = mac.doFinal(message.getBytes());
        Log.i(TAG, "Encryption ->fromAuthAndSha:"+Base64.encodeToString(hmacSHA256Bytes,Base64.DEFAULT));
        return Base64.encodeToString(hmacSHA256Bytes,Base64.DEFAULT);
    }


    //对称加密算法，秘钥有且只有一个，收发双方需实现知道秘钥

    /**
     * AES加密算法
     * @param plainText
     * @return
     */
    private String fromAes(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //生成 key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        //产生秘钥
        SecretKey secretKey = keyGenerator.generateKey();
        //获取秘钥
        byte[] keyBytes = secretKey.getEncoded();
        Log.i(TAG, "Encryption ->fromAes: AES KEY"+ Base64.encodeToString(keyBytes,Base64.DEFAULT));

        //还原秘钥
        SecretKey key = new SecretKeySpec(keyBytes,"AES");

        //加密
        Cipher cipher = Cipher.getInstance("AES/CBC/PACS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] encodeResult = cipher.doFinal(plainText.getBytes());
        Log.i(TAG, "Encryption ->fromAes:AES encode"+Base64.encodeToString(encodeResult,Base64.DEFAULT));

        return Base64.encodeToString(encodeResult,Base64.DEFAULT);
    }

    // 非对称加密算法需要两个密钥：公开密钥（publickey）和私有密钥（privatekey）
    // 公开密钥与私有密钥是一对，如果用公开密钥对数据进行加密，只有用对应的私有密钥才能解密
    // 如果用私有密钥对数据进行加密，那么只有用对应的公开密钥才能解密（这个过程可以做数字签名）

    /**
     * 使用RSA进行数字签名的算法
     * @return
     */
    private byte [] digitalSignature(String src) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        //生成秘钥
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        //签名
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(src.getBytes());

        return signature.sign();
    }

    private String fromRsa(String src) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //生成秘钥
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        //公钥加密
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] result = cipher.doFinal(src.getBytes());

        //'''''''''''''''''
        //私钥解密
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
        KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory2.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher2 = Cipher.getInstance("RSA/ECB/OAEPWithSHA256AndMGF1Padding");
        cipher2.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] result2 = cipher2.doFinal(result);

        return null;
    }
}
