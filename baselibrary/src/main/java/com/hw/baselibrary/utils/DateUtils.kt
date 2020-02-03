package com.hw.baselibrary.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 *author：pc-20171125
 *data:2019/11/7 15:59
 * 时间工具类
 */
object DateUtils {
    val FORMAT_YEAR = "yyyy"
    val FORMAT_MONTH_DAY = "MM月dd日"
    val FORMAT_DATE = "yyyy-MM-dd"
    val FORMAT_TIME = "HH:mm"
    val FORMAT_MONTH_DAY_TIME = "MM月dd日  HH:mm"
    val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm"
    val FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm"
    val FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss"
    val FORMAT_MM_DD = "MM.dd"
    val FORMAT_YYYY_MM_DD = "yyyy.MM.dd"
    val FORMAT_DATE_CHINA = "yyyy年MM月dd日"
    val FORMAT_DATE_TIME_CHINA = "yyyy年MM月dd日 HH:mm"

    // 显示聊天记录的格式
    val CHAT_TIME = "mm:ss"

    private val sdf = SimpleDateFormat()
    private val YEAR = 365 * 24 * 60 * 60// 年
    private val MONTH = 30 * 24 * 60 * 60// 月
    private val DAY = 24 * 60 * 60// 天
    private val HOUR = 60 * 60// 小时
    private val MINUTE = 60// 分钟


    /**
     * 根据时间戳获取描述性时间，如3分钟前，1天前
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 时间字符串
     */
    fun getDescriptionTimeFromTimestamp(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        // 与现在时间相差秒数
        val timeGap = (currentTime - timestamp) / 1000
        println("timeGap: $timeGap")
        val timeStr: String
        if (timeGap > YEAR) {
            timeStr = (timeGap / YEAR).toString() + "年前"
        } else if (timeGap > MONTH) {
            timeStr = (timeGap / MONTH).toString() + "个月前"
        } else if (timeGap > DAY) {// 1天以上
            timeStr = (timeGap / DAY).toString() + "天前"
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = (timeGap / HOUR).toString() + "小时前"
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = (timeGap / MINUTE).toString() + "分钟前"
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚"
        }
        return timeStr
    }


    /**
     * 获取当前日期的指定格式的字符串
     *
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     */
    fun getCurrentTime(format: String?): String {
        if (format == null || format.trim { it <= ' ' } == "") {
            sdf.applyPattern(FORMAT_DATE_TIME)
        } else {
            sdf.applyPattern(format)
        }
        return sdf.format(Date())
    }

    /**
     * date类型转换为String类型
     * formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * data Date类型的时间
     */
    fun dateToString(data: Date?, formatType: String): String {
        return SimpleDateFormat(formatType).format(data)
    }

    /**
     * long类型转换为String类型
     * currentTime要转换的long类型的时间
     * formatType要转换的string类型的时间格式
     */
    //TODO:加了@JvmStatic之后，在java类中可以直接通过DateUtils.longToString调用
    @JvmStatic
    fun longToString(currentTime: Long, formatType: String): String {
        val strTime: String
        // long类型转成Date类型
        val date = longToDate(currentTime, formatType)
        // date类型转成String
        strTime = dateToString(date, formatType)
        return strTime
    }

    /**
     * string类型转换为date类型
     * strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * HH时mm分ss秒，
     * strTime的时间格式必须要与formatType的时间格式相同
     */
    fun stringToDate(strTime: String, formatType: String): Date? {
        val formatter = SimpleDateFormat(formatType)
        var date: Date? = null
        try {
            date = formatter.parse(strTime)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return date
    }

    /**
     * long转换为Date类型
     * currentTime要转换的long类型的时间
     * formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     */
    fun longToDate(currentTime: Long, formatType: String): Date? {
        // 根据long类型的毫秒数生命一个date类型的时间
        val dateOld = Date(currentTime)
        // 把date类型的时间转换为string
        val sDateTime = dateToString(dateOld, formatType)
        // 把String类型转换为Date类型
        return stringToDate(sDateTime, formatType)
    }

    /**
     * string类型转换为long类型
     *
     * strTime要转换的String类型的时间
     * formatType时间格式
     * strTime的时间格式和formatType的时间格式必须相同
     */
    fun stringToLong(strTime: String, formatType: String): Long {
        // String类型转成date类型
        val date = stringToDate(strTime, formatType)
        return if (date == null) {
            0
        } else {
            // date类型转成long类型
            dateToLong(date)
        }
    }

    /**
     * date类型转换为long类型
     * date要转换的date类型的时间
     */
    fun dateToLong(date: Date): Long {
        return date.time
    }


    /**
     * 仿照微信中的消息时间显示逻辑，将时间戳（单位：毫秒）转换为友好的显示格式.
     *
     *
     *
     *
     *
     * 1）7天之内的日期显示逻辑是：今天、昨天(-1d)、前天(-2d)、星期？（只显示总计7天之内的星期数，即<=-4d）；<br></br>
     *
     *
     * 2）7天之外（即>7天）的逻辑：直接显示完整日期时间。
     *
     * @param srcDate         要处理的源日期时间对象
     * @param mustIncludeTime true表示输出的格式里一定会包含“时间:分钟”，否则不包含（参考微信，不包含时分的情况，用于首页“消息”中显示时）
     * @return 输出格式形如：“10:30”、“昨天 12:04”、“前天 20:51”、“星期二”、“2019/2/21 12:09”等形式
     * @author 即时通讯网([url = http : / / www.52im.net]http : / / www.52im.net[ / url])
     * @since 4.5
     */
    fun getTimeStringAutoShort2(srcDate: Date, mustIncludeTime: Boolean): String {
        var ret = ""

        try {
            val gcCurrent = GregorianCalendar()
            gcCurrent.time = Date()
            val currentYear = gcCurrent.get(GregorianCalendar.YEAR)
            val currentMonth = gcCurrent.get(GregorianCalendar.MONTH) + 1
            val currentDay = gcCurrent.get(GregorianCalendar.DAY_OF_MONTH)

            val gcSrc = GregorianCalendar()
            gcSrc.time = srcDate
            val srcYear = gcSrc.get(GregorianCalendar.YEAR)
            val srcMonth = gcSrc.get(GregorianCalendar.MONTH) + 1
            val srcDay = gcSrc.get(GregorianCalendar.DAY_OF_MONTH)

            // 要额外显示的时间分钟
            val timeExtraStr = if (mustIncludeTime) " " + getTimeString(srcDate, "HH:mm") else ""
            // 当年
            if (currentYear == srcYear) {
                val currentTimestamp = gcCurrent.timeInMillis
                val srcTimestamp = gcSrc.timeInMillis
                // 相差时间（单位：毫秒）
                val delta = currentTimestamp - srcTimestamp

                // 当天（月份和日期一致才是）
                if (currentMonth == srcMonth && currentDay == srcDay) {
                    // 时间相差60秒以内
                    if (delta < 60 * 1000)
                        ret = "刚刚"
                    else
                        ret = getTimeString(srcDate, "HH:mm")// 否则当天其它时间段的，直接显示“时:分”的形式
                } else {
                    // 昨天（以“现在”的时候为基准-1天）
                    val yesterdayDate = GregorianCalendar()
                    yesterdayDate.add(GregorianCalendar.DAY_OF_MONTH, -1)
                    // 前天（以“现在”的时候为基准-2天）
                    val beforeYesterdayDate = GregorianCalendar()
                    beforeYesterdayDate.add(GregorianCalendar.DAY_OF_MONTH, -2)
                    // 用目标日期的“月”和“天”跟上方计算出来的“昨天”进行比较，是最为准确的（如果用时间戳差值

                    // 的形式，是不准确的，比如：现在时刻是2019年02月22日1:00、而srcDate是2019年02月21日23:00，

                    // 这两者间只相差2小时，直接用“delta/(3600 * 1000)” > 24小时来判断是否昨天，就完全是扯蛋的逻辑了）
                    if (srcMonth == yesterdayDate.get(GregorianCalendar.MONTH) + 1 && srcDay == yesterdayDate.get(
                            GregorianCalendar.DAY_OF_MONTH
                        )
                    ) {
                        ret = "昨天$timeExtraStr"// -1d
                    } else if (srcMonth == beforeYesterdayDate.get(GregorianCalendar.MONTH) + 1 && srcDay == beforeYesterdayDate.get(
                            GregorianCalendar.DAY_OF_MONTH
                        )
                    ) {
                        ret = "前天$timeExtraStr"// -2d
                    } else {
                        // 跟当前时间相差的小时数
                        val deltaHour = delta / (3600 * 1000)
                        // 如果小于 7*24小时就显示星期几
                        if (deltaHour < 7 * 24) {
                            val weekday = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
                            // 取出当前是星期几
                            val weedayDesc = weekday[gcSrc.get(GregorianCalendar.DAY_OF_WEEK) - 1]
                            ret = weedayDesc + timeExtraStr
                        } else
                            ret = getTimeString(srcDate, "yyyy/M/d") + timeExtraStr// 否则直接显示完整日期时间
                    }// “前天”判断逻辑同上
                }// 当年 && 当天之外的时间（即昨天及以前的时间）

            } else
                ret = getTimeString(srcDate, "yyyy/M/d") + timeExtraStr
        } catch (e: Exception) {
            System.err.println("【DEBUG-getTimeStringAutoShort】计算出错：" + e.message + " 【NO】")
        }
        return ret
    }


    /**
     * * 返回指定pattern样的日期时间字符串。
     *
     *
     * *
     *
     *
     * * @param dt
     *
     *
     * * @param pattern
     *
     *
     * * @return 如果时间转换成功则返回结果，否则返回空字符串""
     *
     *
     * * @author 即时通讯网([url=http://www.52im.net]http://www.52im.net[/url])
     *
     *
     */

    fun getTimeString(dt: Date, pattern: String): String {
        try {
            val sdf = SimpleDateFormat(pattern)//"yyyy-MM-dd HH:mm:ss"
            sdf.timeZone = TimeZone.getDefault()
            return sdf.format(dt)
        } catch (e: Exception) {
            return ""
        }
    }


}