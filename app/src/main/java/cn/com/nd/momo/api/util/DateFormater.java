
package cn.com.nd.momo.api.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.text.format.DateFormat;

public class DateFormater {
    static public String GetDateString(long anchor) {

        StringBuffer sb = new StringBuffer("");
        long now = System.currentTimeMillis();
        long aday = 24 * 60 * 60 * 1000;
        long yesterday = now - aday;

        String todayDate = GetDate(now);
        String yesterdayDate = GetDate(yesterday);
        String anchorDate = GetDate(anchor);

        if (anchorDate.equals(todayDate)) {
            sb.append("今天");
        } else if (anchorDate.equals(yesterdayDate)) {
            sb.append("昨天");
        } else {
            sb.append(anchorDate);
        }

        sb.append(" ");

        String time = GetTime(anchor);
        sb.append(time);

        return sb.toString();
    }

    static public String GetDate(long date) {
        CharSequence val = DateFormat.format("yyyy-MM-dd", new Date(date));
        return val.toString();
    }

    static public String GetTime(long date) {
        CharSequence val = DateFormat.format("k:mm", new Date(date));
        return val.toString();
    }

    public static String GetFullTime(long date) {
        CharSequence val = DateFormat.format("yyyy-MM-dd k:mm:ss", new Date(date));
        return val.toString();
    }

    static public String GetDurationString(int nDuration) {
        int nHour = 0;
        int nMin = 0;
        int nSec = 0;

        nHour = nDuration / 3600;
        nMin = nDuration / 60;
        nSec = nDuration % 60;

        StringBuffer strTime = new StringBuffer("");

        if (nHour != 0) {
            strTime.append(nHour);
            strTime.append(" 小时 ");
        }

        strTime.append(nMin);
        strTime.append(" 分钟 ");

        strTime.append(nSec);
        strTime.append(" 秒 ");

        return strTime.toString();
    }

    /*
     * 将YYYY-MM-DD格式的日期转换成Unix时间戳
     */
    static public long getTimeStamp(String date) {
        if (null == date || date.length() < 1)
            return 0;
        String arrDate[] = date.split("-");
        if (arrDate.length == 3) {
            return Date.UTC(Integer.valueOf(arrDate[0]) - 1900,
                    Integer.valueOf(arrDate[1]) - 1,
                    Integer.valueOf(arrDate[2]), 0, 0, 0);

        } else if (arrDate.length == 2)
            return Date.UTC(0,
                    Integer.valueOf(arrDate[0]) - 1,
                    Integer.valueOf(arrDate[1]), 0, 0, 0);
        return 0;
    }

    public static String getTime(long timeStamp) {
        return getTimeFormat(
                "MM-dd HH:mm",
                timeStamp * (timeStamp < 10000000000L ? 1000 : 1)
                        + TimeZone.getTimeZone("GMT+8:00 ").getRawOffset()
                        - TimeZone.getDefault().getRawOffset());
    }

    public static String getTimeFormat(String format, long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date curDate = new Date(time);// 获取当前时间
        return formatter.format(curDate);
    }
}
