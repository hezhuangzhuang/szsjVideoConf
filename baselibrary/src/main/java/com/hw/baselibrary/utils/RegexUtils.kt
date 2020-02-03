package com.hw.baselibrary.utils

import androidx.collection.SimpleArrayMap
import com.hw.baselibrary.constant.RegexConstants
import java.util.*
import java.util.regex.Pattern

/**
 *author：pc-20171125
 *data:2019/11/11 15:04
 *  正则校验
 */
object RegexUtils {
    private val CITY_MAP = SimpleArrayMap<String, String>()

    ///////////////////////////////////////////////////////////////////////////
    // If u want more please visit http://toutiao.com/i6231678548520731137
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 简单验证手机号
     *
     * Return whether input matches regex of simple mobile.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMobileSimple(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_MOBILE_SIMPLE, input)
    }

    /**
     * 精确验证手机号
     *
     * Return whether input matches regex of exact mobile.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMobileExact(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_MOBILE_EXACT, input)
    }

    /**
     * 验证电话号码
     *
     * Return whether input matches regex of telephone number.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isTel(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_TEL, input)
    }

    /**
     * 验证身份证号码 15 位
     *
     * Return whether input matches regex of id card number which length is 15.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIDCard15(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_ID_CARD15, input)
    }

    /**
     * 简单验证身份证号码 18 位
     *
     * Return whether input matches regex of id card number which length is 18.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIDCard18(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_ID_CARD18, input)
    }

    /**
     * 精确验证身份证号码 18 位
     *
     * Return whether input matches regex of exact id card number which length is 18.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIDCard18Exact(input: CharSequence): Boolean {
        if (isIDCard18(input)) {
            val factor = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
            val suffix = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')
            if (CITY_MAP.isEmpty()) {
                CITY_MAP.put("11", "北京")
                CITY_MAP.put("12", "天津")
                CITY_MAP.put("13", "河北")
                CITY_MAP.put("14", "山西")
                CITY_MAP.put("15", "内蒙古")

                CITY_MAP.put("21", "辽宁")
                CITY_MAP.put("22", "吉林")
                CITY_MAP.put("23", "黑龙江")

                CITY_MAP.put("31", "上海")
                CITY_MAP.put("32", "江苏")
                CITY_MAP.put("33", "浙江")
                CITY_MAP.put("34", "安徽")
                CITY_MAP.put("35", "福建")
                CITY_MAP.put("36", "江西")
                CITY_MAP.put("37", "山东")

                CITY_MAP.put("41", "河南")
                CITY_MAP.put("42", "湖北")
                CITY_MAP.put("43", "湖南")
                CITY_MAP.put("44", "广东")
                CITY_MAP.put("45", "广西")
                CITY_MAP.put("46", "海南")

                CITY_MAP.put("50", "重庆")
                CITY_MAP.put("51", "四川")
                CITY_MAP.put("52", "贵州")
                CITY_MAP.put("53", "云南")
                CITY_MAP.put("54", "西藏")

                CITY_MAP.put("61", "陕西")
                CITY_MAP.put("62", "甘肃")
                CITY_MAP.put("63", "青海")
                CITY_MAP.put("64", "宁夏")
                CITY_MAP.put("65", "新疆")

                CITY_MAP.put("71", "台湾")
                CITY_MAP.put("81", "香港")
                CITY_MAP.put("82", "澳门")
                CITY_MAP.put("91", "国外")
            }
            if (CITY_MAP.get(input.subSequence(0, 2).toString()) != null) {
                var weightSum = 0
                for (i in 0..16) {
                    weightSum += (input[i] - '0') * factor[i]
                }
                val idCardMod = weightSum % 11
                val idCardLast = input[17]
                return idCardLast == suffix[idCardMod]
            }
        }
        return false
    }

    /**
     * 验证邮箱
     *
     * Return whether input matches regex of email.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isEmail(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_EMAIL, input)
    }

    /**
     * 验证 URL
     *
     * Return whether input matches regex of url.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isURL(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_URL, input)
    }

    /**
     * 验证汉字
     *
     * Return whether input matches regex of Chinese character.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isZh(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_ZH, input)
    }

    /**
     * 验证用户名
     *
     * Return whether input matches regex of username.
     *
     * scope for "a-z", "A-Z", "0-9", "_", "Chinese character"
     *
     * can't end with "_"
     *
     * length is between 6 to 20.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUsername(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_USERNAME, input)
    }

    /**
     * 验证 yyyy-MM-dd 格式的日期校验，已考虑平闰年
     *
     * Return whether input matches regex of date which pattern is "yyyy-MM-dd".
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDate(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_DATE, input)
    }

    /**
     * 验证 IP 地址
     *
     * Return whether input matches regex of ip address.
     *
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIP(input: CharSequence): Boolean {
        return isMatch(RegexConstants.REGEX_IP, input)
    }

    /**
     * 判断是否匹配正则
     *
     * Return whether input matches the regex.
     *
     * @param regex The regex.
     * @param input The input.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMatch(regex: String, input: CharSequence?): Boolean {
        return input != null && input.length > 0 && Pattern.matches(regex, input)
    }

    /**
     * 获取正则匹配的部分
     *
     * Return the list of input matches the regex.
     *
     * @param regex The regex.
     * @param input The input.
     * @return the list of input matches the regex
     */
    fun getMatches(regex: String, input: CharSequence?): List<String> {
        if (input == null) return emptyList()
        val matches = ArrayList<String>()
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(input)
        while (matcher.find()) {
            matches.add(matcher.group())
        }
        return matches
    }

    /**
     * 获取正则匹配分组
     *
     * Splits input around matches of the regex.
     *
     * @param input The input.
     * @param regex The regex.
     * @return the array of strings computed by splitting input around matches of regex
     */
    fun getSplits(input: String?, regex: String): Array<String> {
        return input?.split(regex.toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
            ?: arrayOf("")
    }

    /**
     * 替换正则匹配的第一部分
     *
     * Replace the first subsequence of the input sequence that matches the
     * regex with the given replacement string.
     *
     * @param input       The input.
     * @param regex       The regex.
     * @param replacement The replacement string.
     * @return the string constructed by replacing the first matching
     * subsequence by the replacement string, substituting captured
     * subsequences as needed
     */
    fun getReplaceFirst(
        input: String?,
        regex: String,
        replacement: String
    ): String {
        return if (input == null) "" else Pattern.compile(regex).matcher(input).replaceFirst(
            replacement
        )
    }

    /**
     * 替换所有正则匹配的部分
     *
     * Replace every subsequence of the input sequence that matches the
     * pattern with the given replacement string.
     *
     * @param input       The input.
     * @param regex       The regex.
     * @param replacement The replacement string.
     * @return the string constructed by replacing each matching subsequence
     * by the replacement string, substituting captured subsequences
     * as needed
     */
    fun getReplaceAll(
        input: String?,
        regex: String,
        replacement: String
    ): String {
        return if (input == null) "" else Pattern.compile(regex).matcher(input).replaceAll(
            replacement
        )
    }
}