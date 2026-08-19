package cn.bzvs.holiday.entity.vo;

/**
 * 日期类型枚举
 *
 * <p>对应 CalendarVO 的 status 字段：0-工作日，1-周末，2-补班工作日，3-法定节假日
 *
 * @author bzvs
 * @since 2.0.0
 */
public enum DayType {
  /** 普通工作日 */
  WORKDAY(0, "工作日"),
  /** 周末 */
  WEEKEND(1, "周末"),
  /** 需要补班的工作日 */
  COMPENSATE_WORK(2, "补班"),
  /** 法定节假日 */
  HOLIDAY(3, "节假日");

  /** 状态码 */
  private final int code;

  /** 中文描述 */
  private final String remark;

  DayType(int code, String remark) {
    this.code = code;
    this.remark = remark;
  }

  /**
   * 获取状态码
   *
   * @return 状态码
   */
  public int getCode() {
    return code;
  }

  /**
   * 获取中文描述
   *
   * @return 中文描述
   */
  public String getRemark() {
    return remark;
  }

  /**
   * 根据状态码解析枚举
   *
   * @param code 状态码（0/1/2/3）
   * @return DayType
   */
  public static DayType fromCode(int code) {
    for (DayType type : values()) {
      if (type.code == code) {
        return type;
      }
    }
    throw new IllegalArgumentException("未知的日期状态码: " + code);
  }
}
