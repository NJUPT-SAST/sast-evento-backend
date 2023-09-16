package sast.evento.utils;

import org.springframework.stereotype.Component;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Aiden
 * @date 2023/8/12 17:28
 */
@Component
public class TimeUtil {

    /**
     * @param time String类型
     * @return 判断String类型的时间是否有效(满足yyyy - mm - dd格式)如果满足返回Calendar类型
     * @throws ErrorEnum TIME_ERROR String时间格式有误
     * @author Aiden
     */
    public Calendar validTime(String time) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(time);
            calendar.setTime(date);
        } catch (ParseException e) {
            throw new LocalRunTimeException(ErrorEnum.TIME_ERROR);
        }
        return calendar;
    }

    /**
     * @param time String类型
     * @return 获得某一日期所在的那一周的周一和周日的日期
     * @author Aiden
     */
    public List<Date> getDateOfMonday(String time) {
        Calendar date = validTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 设置每周的第一天为星期一
        date.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int dayWeek = date.get(Calendar.DAY_OF_WEEK);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        if (1 == dayWeek) {
            date.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 获得当前日期是一个星期的第几天
        int day = date.get(Calendar.DAY_OF_WEEK);
        // 获取该周第一天
        date.add(Calendar.DATE, date.getFirstDayOfWeek() - day);

        List<Date> resultDate = new ArrayList<>();
        resultDate.add(date.getTime());
        // 获取下一周第一天（因为查询默认00:00:00）
        date.add(Calendar.DATE, 7);
        resultDate.add(date.getTime());
        return resultDate;
    }

    /**
     * @return String 获取当前yyyy-MM-dd格式的日期
     * @author Aiden
     */
    public String getTime(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
