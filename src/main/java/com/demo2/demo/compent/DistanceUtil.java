package com.demo2.demo.compent;
import java.text.DecimalFormat;

public class DistanceUtil {

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：)
     *

     * @return 距离
     */
    public static String getDistance(double lat1, double lng1,double lat2, double lng2) {

        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);

        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        //s = s * 1000;如果需要返回单位为米

        DecimalFormat df = new DecimalFormat("0.00");//格式化，区小数后两位
        String distance = df.format(s);

        return distance;
    }

}