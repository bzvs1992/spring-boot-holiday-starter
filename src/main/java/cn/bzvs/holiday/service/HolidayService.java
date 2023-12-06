package cn.bzvs.holiday.service;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.entity.vo.CalendarInfoVO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.util.HolidayUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 节假日相关的日历服务
 *
 * @author bzvs
 * @date 2024/12/06
 * @since 1.0.0
 */
public class HolidayService {

    private static final String format = "yyyy-MM-dd";

    /**
     * 获取指定日期的节假日信息
     * @param date
     * @return HolidayVO
     */
    public CalendarInfoVO getDate(Date date) {
        String day = DateUtil.format(date, format);
        return getDate(day);
    }

    /**
     * 获取指定日期的节假日信息
     * @param localDate
     * @return HolidayVO
     */
    public CalendarInfoVO getDate(LocalDate localDate) {
        String day = DateUtil.format(localDate.atStartOfDay(), format);
        return getDate(day);
    }
    /**
     * 获取指定日期的节假日信息
     * @param localDateTime
     * @return HolidayVO
     */
    public CalendarInfoVO getDate(LocalDateTime localDateTime) {
        String day = DateUtil.format(localDateTime, format);
        return getDate(day);
    }
    /**
     * 获取指定日期的节假日信息
     * @param date 日期格式：yyyy-MM-dd
     * @return HolidayVO
     */
    public CalendarInfoVO getDate(String date) {
        return getVo(date);
    }

    /**
     * 是否是节假日
     * @param date
     * @return boolean
     */
    public boolean isHoliday(Date date) {
        String day = DateUtil.format(date, format);
        return isHoliday(day);
    }

    /**
     * 是否是节假日
     * @param localDate
     * @return boolean
     */
    public boolean isHoliday(LocalDate localDate) {
        return isHoliday(localDate.atStartOfDay());
    }

    /**
     * 是否是节假日
     * @param localDateTime
     * @return boolean
     */
    public boolean isHoliday(LocalDateTime localDateTime) {
        String day = DateUtil.format(localDateTime, format);
        return isHoliday(day);
    }

    /**
     * 是否是节假日
     * @param day
     * @return boolean
     */
    public boolean isHoliday(String day) {
        CalendarVO vo = getDate(day);
        return vo.getStatus() == 3;
    }

    /**
     * 是否是工作日或补班
     * @param date
     * @return boolean
     */
    public boolean isWorkDay(Date date) {
        String day = DateUtil.format(date, format);
        return isWorkDay(day);
    }

    /**
     * 是否是工作日或补班
     * @param localDate
     * @return boolean
     */
    public boolean isWorkDay(LocalDate localDate) {
        return isWorkDay(localDate.atStartOfDay());
    }

    /**
     * 是否是工作日或补班
     * @param localDateTime
     * @return boolean
     */
    public boolean isWorkDay(LocalDateTime localDateTime) {
        String day = DateUtil.format(localDateTime, format);
        return isWorkDay(day);
    }

    /**
     * 是否是工作日或补班
     * @param day
     * @return boolean
     */
    public boolean isWorkDay(String day) {
        CalendarVO vo = getDate(day);
        return vo.getStatus() == 0 || vo.getStatus() == 2;
    }

    /**
     * 是否是周末
     * @param date
     * @return boolean
     */
    public boolean isWeekend(Date date) {
        String day = DateUtil.format(date, format);
        return isWeekend(day);
    }

    /**
     * 是否是周末
     * @param localDate
     * @return boolean
     */
    public boolean isWeekend(LocalDate localDate) {
        return isWeekend(localDate.atStartOfDay());
    }

    /**
     * 是否是周末
     * @param localDateTime
     * @return boolean
     */
    public boolean isWeekend(LocalDateTime localDateTime) {
        String day = DateUtil.format(localDateTime, format);
        return isWeekend(day);
    }

    /**
     * 是否是周末
     * @param day
     * @return boolean
     */
    public boolean isWeekend(String day) {
        CalendarVO vo = getDate(day);
        return vo.getStatus() == 1;
    }



    private static CalendarInfoVO getVo(String date) {
        DateTime dateTime = DateUtil.parseDate(date);
        CalendarInfoVO infoVO;
        Map<String, CalendarVO> voMap = ConstantData.getAllDateMap();
        int dayOfWeek = HolidayUtil.getDayOfWeek(dateTime);
        if (voMap.containsKey(date)) {
            CalendarVO vo = voMap.get(date);
            infoVO = new CalendarInfoVO(vo.getDate(), dayOfWeek, vo.getStatus());
        } else {
            infoVO = new CalendarInfoVO(date, dayOfWeek, dayOfWeek < 6 ? 0 : 1);
        }
        return infoVO;
    }
}
