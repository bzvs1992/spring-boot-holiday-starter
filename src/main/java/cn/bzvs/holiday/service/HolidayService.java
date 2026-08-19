package cn.bzvs.holiday.service;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.entity.vo.CalendarInfoVO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.entity.vo.DayType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * 节假日相关的日历服务
 *
 * @author bzvs
 * @date 2024-12-06
 * @since 1.0.0
 */
public class HolidayService {

  /** 日期格式 */
  private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * 获取指定日期的节假日信息
   *
   * @param date 日期
   * @return CalendarInfoVO
   */
  public CalendarInfoVO getDate(Date date) {
    return getDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }

  /**
   * 获取指定日期的节假日信息
   *
   * @param localDate 日期
   * @return CalendarInfoVO
   */
  public CalendarInfoVO getDate(LocalDate localDate) {
    return getVo(localDate);
  }

  /**
   * 获取指定日期的节假日信息
   *
   * @param localDateTime 日期时间
   * @return CalendarInfoVO
   */
  public CalendarInfoVO getDate(LocalDateTime localDateTime) {
    return getVo(localDateTime.toLocalDate());
  }

  /**
   * 获取指定日期的节假日信息
   *
   * @param date 日期格式：yyyy-MM-dd
   * @return CalendarInfoVO
   */
  public CalendarInfoVO getDate(String date) {
    return getVo(LocalDate.parse(date, FORMAT));
  }

  /**
   * 是否是节假日
   *
   * @param date 日期
   * @return boolean
   */
  public boolean isHoliday(Date date) {
    return isHoliday(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }

  /**
   * 是否是节假日
   *
   * @param localDate 日期
   * @return boolean
   */
  public boolean isHoliday(LocalDate localDate) {
    return getStatus(localDate) == DayType.HOLIDAY.getCode();
  }

  /**
   * 是否是节假日
   *
   * @param localDateTime 日期时间
   * @return boolean
   */
  public boolean isHoliday(LocalDateTime localDateTime) {
    return isHoliday(localDateTime.toLocalDate());
  }

  /**
   * 是否是节假日
   *
   * @param day 日期格式：yyyy-MM-dd
   * @return boolean
   */
  public boolean isHoliday(String day) {
    return isHoliday(LocalDate.parse(day, FORMAT));
  }

  /**
   * 是否是工作日或补班
   *
   * @param date 日期
   * @return boolean
   */
  public boolean isWorkDay(Date date) {
    return isWorkDay(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }

  /**
   * 是否是工作日或补班
   *
   * @param localDate 日期
   * @return boolean
   */
  public boolean isWorkDay(LocalDate localDate) {
    int status = getStatus(localDate);
    return status == DayType.WORKDAY.getCode() || status == DayType.COMPENSATE_WORK.getCode();
  }

  /**
   * 是否是工作日或补班
   *
   * @param localDateTime 日期时间
   * @return boolean
   */
  public boolean isWorkDay(LocalDateTime localDateTime) {
    return isWorkDay(localDateTime.toLocalDate());
  }

  /**
   * 是否是工作日或补班
   *
   * @param day 日期格式：yyyy-MM-dd
   * @return boolean
   */
  public boolean isWorkDay(String day) {
    return isWorkDay(LocalDate.parse(day, FORMAT));
  }

  /**
   * 是否是周末
   *
   * @param date 日期
   * @return boolean
   */
  public boolean isWeekend(Date date) {
    return isWeekend(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }

  /**
   * 是否是周末
   *
   * @param localDate 日期
   * @return boolean
   */
  public boolean isWeekend(LocalDate localDate) {
    return getStatus(localDate) == DayType.WEEKEND.getCode();
  }

  /**
   * 是否是周末
   *
   * @param localDateTime 日期时间
   * @return boolean
   */
  public boolean isWeekend(LocalDateTime localDateTime) {
    return isWeekend(localDateTime.toLocalDate());
  }

  /**
   * 是否是周末
   *
   * @param day 日期格式：yyyy-MM-dd
   * @return boolean
   */
  public boolean isWeekend(String day) {
    return isWeekend(LocalDate.parse(day, FORMAT));
  }

  /**
   * 查询状态码：缓存命中返回缓存值，未命中按自然星期兜底（周一~周五为工作日，周六日为周末）
   *
   * @param localDate 日期
   * @return 状态码
   */
  private static int getStatus(LocalDate localDate) {
    String date = localDate.format(FORMAT);
    Map<String, CalendarVO> voMap = ConstantData.getAllDateMap();
    CalendarVO vo = voMap.get(date);
    if (vo != null) {
      return vo.getStatus();
    }
    int dow = localDate.getDayOfWeek().getValue();
    return dow < 6 ? DayType.WORKDAY.getCode() : DayType.WEEKEND.getCode();
  }

  /**
   * 构建日历详细信息
   *
   * @param localDate 日期
   * @return CalendarInfoVO
   */
  private static CalendarInfoVO getVo(LocalDate localDate) {
    String date = localDate.format(FORMAT);
    Map<String, CalendarVO> voMap = ConstantData.getAllDateMap();
    int dayOfWeek = localDate.getDayOfWeek().getValue();
    CalendarVO vo = voMap.get(date);
    if (vo != null) {
      return new CalendarInfoVO(vo.getDate(), dayOfWeek, vo.getStatus());
    }
    int status = dayOfWeek < 6 ? DayType.WORKDAY.getCode() : DayType.WEEKEND.getCode();
    return new CalendarInfoVO(date, dayOfWeek, status);
  }
}
