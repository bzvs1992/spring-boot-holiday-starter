package cn.bzvs.holiday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.bzvs.holiday.entity.vo.DayType;
import org.junit.jupiter.api.Test;

/**
 * DayType 枚举测试
 *
 * @author bzvs
 * @since 2.0.0
 */
class DayTypeTest {

  /** fromCode 正确解析全部状态码 */
  @Test
  void fromCodeParsesAllCodes() {
    assertEquals(DayType.WORKDAY, DayType.fromCode(0));
    assertEquals(DayType.WEEKEND, DayType.fromCode(1));
    assertEquals(DayType.COMPENSATE_WORK, DayType.fromCode(2));
    assertEquals(DayType.HOLIDAY, DayType.fromCode(3));
  }

  /** fromCode 对未知状态码（超出范围、负数）抛出异常 */
  @Test
  void fromCodeThrowsOnUnknownCode() {
    assertThrows(IllegalArgumentException.class, () -> DayType.fromCode(9));
    assertThrows(IllegalArgumentException.class, () -> DayType.fromCode(-1));
  }

  /** 枚举中文描述正确 */
  @Test
  void remarkIsCorrect() {
    assertEquals("工作日", DayType.WORKDAY.getRemark());
    assertEquals("周末", DayType.WEEKEND.getRemark());
    assertEquals("补班", DayType.COMPENSATE_WORK.getRemark());
    assertEquals("节假日", DayType.HOLIDAY.getRemark());
  }
}
