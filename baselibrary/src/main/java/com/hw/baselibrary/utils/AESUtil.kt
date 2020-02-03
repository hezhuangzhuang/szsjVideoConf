package com.hw.baselibrary.utils

import android.util.Base64
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 *author：pc-20171125
 *data:2019/11/8 14:49
 */
object AESUtil {
    /**
     * AES Key 长度
     */
    private val AES_KEY_LENGTH = 16

    /**
     * 默认AES Key
     */
    private val DEFAULT_AES_KEY_STR = "KAziAsY#16_o0#z8"

    // 此处向量可自定义，请注意如果超过0x80请加(byte)强制转换
    private val OIV = byteArrayOf(
        0x01,
        0x02,
        0x03,
        0x04,
        0x05,
        0x06,
        0x07,
        0x08,
        0x09,
        0x0A,
        0x0B,
        0x0C,
        0x0D,
        0x0E,
        0x0F,
        0x10
    )

    /**
     * 加密，并对密文进行Base64编码，采用默认密钥
     * @param plainText
     * 明文
     * @return String
     * 做了Base64编码的密文
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encrypt(plainText: String): String {
        return encrypt(plainText, DEFAULT_AES_KEY_STR)
    }

    /**
     * 加密，并对密文进行Base64编码，可指定密钥
     * @param plainText
     * 明文
     * @param keyStr
     * 密钥
     * @return String
     * 做了Base64编码的密文
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encrypt(plainText: String, keyStr: String): String {
        try {
            val keyBytes = keyStr.toByteArray(charset("UTF-8"))
            val keyBytesTruncated = ByteArray(AES_KEY_LENGTH)
            for (i in 0 until AES_KEY_LENGTH) {
                if (i >= keyBytes.size) {
                    // keyBytesTruncated[i] = (byte)0x80;
                    keyBytesTruncated[i] = 0x12
                } else {
                    keyBytesTruncated[i] = keyBytes[i]
                }
            }
            val ckey = SecretKeySpec(keyBytesTruncated, "AES")
            val cp = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val iv = IvParameterSpec(OIV)
            cp.init(1, ckey, iv)
            val inputByteArray = plainText.toByteArray(charset("UTF-8"))

            val cipherBytes = cp.doFinal(inputByteArray)
            var result = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
            //			String result = Base64Util.encoder(cipherBytes);
            result = result.replace("+", "%2b")
            result = result.replace("\r\n", "").replace("\n", "")
            return result
        } catch (e: Exception) {
            throw e
        }

    }

    /**
     * 对做了Base64编码的密文进行解密，采用默认密钥
     * @param secretText
     * 做了Base64编码的密文
     * @return String
     * 解密后的字符串
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decrypt(secretText: String): String {
        return decrypt(secretText, DEFAULT_AES_KEY_STR)
    }

    /**
     * 对做了Base64编码的密文进行解密
     * @param secretText
     * 做了Base64编码的密文
     * @param keyStr
     * 密钥
     * @return String
     * 解密后的字符串
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decrypt(secretText: String, keyStr: String): String {
        var secretText = secretText
        secretText = secretText.replace("%2b", "+")
        try {
            //			byte[] cipherByte = Base64Util.decoder(secretText);
            val cipherByte = Base64.decode(secretText, Base64.DEFAULT)
            val keyBytes = keyStr.toByteArray(charset("UTF-8"))
            val keyBytesTruncated = ByteArray(AES_KEY_LENGTH)
            for (i in 0 until AES_KEY_LENGTH) {
                if (i >= keyBytes.size) {
                    keyBytesTruncated[i] = 0x12
                } else {
                    keyBytesTruncated[i] = keyBytes[i]
                }
            }
            val ckey = SecretKeySpec(keyBytesTruncated, "AES")
            val cp = Cipher.getInstance("AES/CBC/PKCS5Padding")
            // Cipher cp = Cipher.getInstance("AES/ECB/PKCS5Padding");
            val iv = IvParameterSpec(OIV)
            cp.init(2, ckey, iv)
            val decryptBytes = cp.doFinal(cipherByte)
            return String(decryptBytes, Charset.forName("UTF-8")).replace("", "")
        } catch (e: Exception) {
            throw e
        }

    }
}