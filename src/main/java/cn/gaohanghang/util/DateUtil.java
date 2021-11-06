package cn.gaohanghang.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @Description 时间工具类
 * @Author: 高行行
 */
public class DateUtil {

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_FORMATTER_NUM = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static void main(String[] args) throws ParseException {
        Date date = DateUtil.stringToDate("1984-01-01 00:00:00");
        System.out.println(date);
    }

    public static String getCurrentDate(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String getCurrentDate() {
        return getCurrentDate(new Date());
    }

    public static Date stringToDate(String date) throws ParseException {
        if (StringUtil.isEmpty(date)) {
            return null;
        }

        if (date.indexOf(":") == -1) {
            date = String.format("%s 00:00:00", date);
        }
        System.out.println(date);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.parse(date);
    }

    public static long stringToTimestamp(String date) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.parse(date).getTime();
    }

    /**
     * 10位timestamp转日期
     *
     * @param time
     * @return
     */
    public static String timestampToString(Integer time) {
        long temp = 0;
        int valueLength = String.valueOf(time).length();
        if (valueLength == 10) {
            temp = (long) time * 1000;
        } else if (valueLength == 13) {
            temp = (long) time;
        } else {
            return null;
        }
        Timestamp ts = new Timestamp(temp);
        String tsStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            tsStr = dateFormat.format(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tsStr;
    }

    /**
     * 时间字符串转LocalDate
     * example:
     * "2017-09-28" -> 2019-12-25
     *
     * @param timeString
     * @return
     */
    public static LocalDate stringToLocalDate(String timeString) {
        return LocalDate.parse(timeString, DATE_FORMATTER);
    }

    /**
     * 时间字符串转LocalDateTime
     * example:
     * "2017-09-28 17:07:05" -> 2019-12-25 10:16:10
     *
     * @param timeString
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String timeString) {
        if (StringUtil.isEmpty(timeString)) {
            return null;
        }
        return LocalDateTime.parse(timeString, DATETIME_FORMATTER);
    }

    /**
     * 时间戳转 LocalDate
     * example:
     * 1577240170 -> 2019-12-25
     *
     * @param timestamp
     * @return
     */
    public static LocalDate timestampToLocalDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * 时间戳转 LocalDateTime
     * example:
     * 1577240170 -> 2019-12-25 10:16:10
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 时间戳字符串转 LocalDate
     * example:
     * "1577240170" -> 2019-12-25 10:16:10
     *
     * @param timestampString
     * @return
     */
    public static LocalDate timestampStringToLocalDate(String timestampString) {
        return Instant.ofEpochMilli(Long.parseLong(timestampString)).atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * 时间戳字符串转 LocalDateTime，毫秒
     * example:
     * "1577240170" -> 2019-12-25 10:16:10
     *
     * @param timestampString
     * @return
     */
    public static LocalDateTime timestampStringToLocalDateTime(String timestampString) {
        return Instant.ofEpochMilli(Long.parseLong(timestampString)).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 时间戳字符串转 LocalDateTime，秒
     * example:
     * "1577240170" -> 2019-12-25 10:16:10
     *
     * @param timestampString
     * @return
     */
    public static LocalDateTime timestampStringToLocalDateTimeOfEpochSecond(String timestampString) {
        return Instant.ofEpochSecond(Long.parseLong(timestampString)).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }


    /**
     * 返回当前的日期
     *
     * @return
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

    /**
     * 返回当前日期时间
     *
     * @return
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 返回当前日期字符串 yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDateStr() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public static String getCurrentDateToStr() {
        return LocalDate.now().format(DATE_FORMATTER_NUM);
    }

    /**
     * 返回当前日期时间字符串 yyyy-MM-ddHHmmss
     *
     * @return
     */
    public static String getCurrentDateTimeStr() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    public static LocalDate parseLocalDate(String dateStr, String pattern) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeStr, String pattern) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期相隔天数
     *
     * @param startDateInclusive
     * @param endDateExclusive
     * @return
     */
    public static int periodDays(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return Period.between(startDateInclusive, endDateExclusive).getDays();
    }

    /**
     * 日期相隔小时
     *
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    public static long durationHours(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toHours();
    }

    /**
     * 日期相隔分钟
     *
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    public static long durationMinutes(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toMinutes();
    }

    /**
     * 日期相隔毫秒数
     *
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    public static long durationMillis(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toMillis();
    }

    /**
     * 是否当天
     *
     * @param date
     * @return
     */
    public static boolean isToday(LocalDate date) {
        return getCurrentLocalDate().equals(date);
    }

    /**
     * 获取本月的第一天
     *
     * @return
     */
    public static String getFirstDayOfThisMonth() {
        return getCurrentLocalDate().with(TemporalAdjusters.firstDayOfMonth()).format(DATE_FORMATTER);
    }

    /**
     * 获取本月的最后一天
     *
     * @return
     */
    public static String getLastDayOfThisMonth() {
        return getCurrentLocalDate().with(TemporalAdjusters.lastDayOfMonth()).format(DATE_FORMATTER);
    }

    /**
     * 获取2017-01的第一个周一
     *
     * @return
     */
    public static String getFirstMonday() {
        return LocalDate.parse("2017-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
                .format(DATE_FORMATTER);
    }

    /**
     * 获取当前日期的后两周
     *
     * @return
     */
    public static String getCurDateAfterTwoWeek() {
        return getCurrentLocalDate().plus(2, ChronoUnit.WEEKS).format(DATE_FORMATTER);
    }

    /**
     * 获取当前日期的6个月后的日期
     *
     * @return
     */
    public static String getCurDateAfterSixMonth() {
        return getCurrentLocalDate().plus(6, ChronoUnit.MONTHS).format(DATE_FORMATTER);
    }

    /**
     * 获取当前日期的5年后的日期
     *
     * @return
     */
    public static String getCurDateAfterFiveYear() {
        return getCurrentLocalDate().plus(5, ChronoUnit.YEARS).format(DATE_FORMATTER);
    }

    /**
     * 获取当前日期的20年后的日期
     *
     * @return
     */
    public static String getCurDateAfterTwentyYear() {
        return getCurrentLocalDate().plus(2, ChronoUnit.DECADES).format(DATE_FORMATTER);
    }

    public static String timestampToDateString(String value) {
        if (StringUtil.isEmpty(value)) {
            return value;
        }

        if (value.indexOf("-") != -1) {
            return value;
        }

        if (StringUtil.isNumeric(value)) {
            long l = Long.parseLong(value);
            if (value.length() == 10) {
                l = l * 1000;
            }
            Date date = new Date(l);
            return DateUtil.getCurrentDate(date);
        }

        return value;
    }

}
