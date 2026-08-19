package cn.bzvs.holiday.entity.vo;

import java.time.LocalDate;
import lombok.Getter;

/**
 * 日历详细信息
 *
 * @author bzvs
 * @date 2024-12-04
 * @since 1.0.0
 */
@Getter
public class CalendarInfoVO extends CalendarVO {

  /** 年 */
  private final Integer year;

  /** 月 */
  private final Integer month;

  /** 日 */
  private final Integer day;

  /** 星期：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日 */
  private final Integer dayOfWeek;

  /** 星期中文描述 */
  private final String dayOfWeekStr;

  /** 日期类型备注 */
  private final String remark;

  /**
   * 构造日历详细信息
   *
   * @param date 日期，格式 yyyy-MM-dd
   * @param dayOfWeek 星期，1-7
   * @param status 状态码，0-工作日 1-周末 2-补班 3-节假日
   */
  public CalendarInfoVO(String date, Integer dayOfWeek, Integer status) {
    LocalDate localDate = LocalDate.parse(date);
    setDate(date);
    setStatus(status);
    this.year = localDate.getYear();
    this.month = localDate.getMonthValue();
    this.day = localDate.getDayOfMonth();
    this.dayOfWeek = dayOfWeek;
    this.dayOfWeekStr = toDayOfWeekStr(dayOfWeek);
    this.remark = DayType.fromCode(status).getRemark();
  }

  /**
   * 星期数字转中文
   *
   * @param dayOfWeek 星期，1-7
   * @return 中文描述
   */
  private static String toDayOfWeekStr(Integer dayOfWeek) {
    return switch (dayOfWeek) {
      case 1 -> "周一";
      case 2 -> "周二";
      case 3 -> "周三";
      case 4 -> "周四";
      case 5 -> "周五";
      case 6 -> "周六";
      case 7 -> "周日";
      default -> "未知";
    };
  }
}
