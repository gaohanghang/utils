package cn.gaohanghang.util;

import java.text.DecimalFormat;

/**
 * @Description 百分比计算工具类
 * @Author: 高行行
 */
public class PercentUtil {

    public static String myPercent(int y, int z) {
        String baifenbi = "";// 接受百分比的值
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = baiy / baiz;
        // NumberFormat nf = NumberFormat.getPercentInstance(); 注释掉的也是一种方法
        // nf.setMinimumFractionDigits( 2 ); 保留到小数点后几位
        DecimalFormat df1 = new DecimalFormat("##.00%"); // ##.00%
                                                            // 百分比格式，后面不足2位的用0补齐
        // baifenbi=nf.format(fen);
        baifenbi = df1.format(fen);
        return baifenbi;
    }

    public static void main(String[] args) {
        String s = myPercent(29, 59);
        System.out.println(s);
    }

}
