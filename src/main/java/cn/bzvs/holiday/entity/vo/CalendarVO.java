package cn.bzvs.holiday.entity.vo;

import lombok.Data;

/**
 * 日历信息
 *
 * @author bzvs
 * @date 2024/12/04
 * @since 1.0.0
 */
@Data
public class CalendarVO {

  /** 主键 日期yyyy-MM-dd isNullAble:0 */
  private String date;

  /** 0普通工作日1周末2需要补班的工作日3法定节假日 isNullAble:1,defaultVal:0 */
  private Integer status;
}
