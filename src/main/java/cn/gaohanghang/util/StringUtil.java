package cn.gaohanghang.util;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: wuyc
 * @Date: 2019/2/18 14:20
 * @Desc:
 */
public class StringUtil {

    public static void main(String[] args) {
        String str = "zhou20105";
        boolean s = isCharacterOrNumber(str);
        System.out.println(s);
    }

    public static String toStringList(List<Object> item) {
        String str = "";
        for (Object o : item) {
            if (str.length() > 0) {
                str += ",";
            }

            str += String.valueOf(o);
        }
        return str;
    }

    public static boolean isEmpty(String value) {
        if (null == value || "".equals(value) || "null".equals(value)) {
            return true;
        }
        return false;
    }

    public static String changeToNull(String value) {
        if (isEmpty(value)) {
            return "";
        }
        return value;
    }

    public static String changeSpecialCharacters(String value) {
        if (isEmpty(value)) {
            return "";
        }

        value = value.replaceAll(",", " ");
        value = value.replaceAll("'", "");
        value = value.replaceAll("\"", "");
        return value;
    }

    /**
     * 过滤非法字符
     * 注意，以下正则表达式过滤不全面，过滤范围为
     * 0x00 - 0x08
     * 0x0b - 0x0c
     * 0x0e - 0x1f
     *
     * @param str
     * @return
     */
    public static String stripNonValidXMLChars(String str) {
        if (str == null || "".equals(str)) {
            return str;
        }
        return str.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
    }

    /**
     * 去除括号
     * 例子：
     * 哈哈哈哈(111) -> 哈哈哈哈
     *
     * @param str
     * @return
     */
    public static String removeParentheses(String str) {
        if (!StringUtils.isEmpty(str)) {
            if (str.contains("(")) {
                return str.substring(0, str.indexOf("("));
            }
        }
        return str;
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String getNumbericFromString(String value) {
        if (isEmpty(value)) {
            return null;
        }
        String regxp = "((-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?)";
        String result = "0";

        Pattern p = Pattern.compile(regxp);
        Matcher m = p.matcher(value);
        while (m.find()) {
            result = m.group();
        }
        return result;
    }

    /**
     * 是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符是否由字母或数字组成
     *
     * @param str
     * @return
     */
    public static boolean isCharacterOrNumber(String str) {
        String reg = "^[a-zA-Z0-9]*$";
        return str.matches(reg);
    }


}
