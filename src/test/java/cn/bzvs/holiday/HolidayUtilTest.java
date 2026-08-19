package cn.bzvs.holiday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.bzvs.holiday.util.HolidayUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * HolidayUtil 测试
 *
 * @author bzvs
 * @since 2.0.0
 */
class HolidayUtilTest {

  /** reParseJson 从 jsonp 包裹中提取 JSON */
  @Test
  void reParseJsonExtractsJson() {
    String raw = "jsonp_17123456({\"a\":1})";
    assertEquals("{\"a\":1}", HolidayUtil.reParseJson(raw));
  }

  /** reParseJson 对不含 JSON 的内容抛出明确异常 */
  @Test
  void reParseJsonThrowsOnInvalidContent() {
    assertThrows(IllegalArgumentException.class, () -> HolidayUtil.reParseJson("no-json"));
  }

  /** getDayOfWeek 符合 ISO 星期定义：1-周一，7-周日 */
  @Test
  void getDayOfWeekMapping() {
    assertEquals(1, HolidayUtil.getDayOfWeek(LocalDate.of(2023, 12, 4)));
    assertEquals(6, HolidayUtil.getDayOfWeek(LocalDate.of(2023, 12, 9)));
    assertEquals(7, HolidayUtil.getDayOfWeek(LocalDate.of(2023, 12, 10)));
  }

  /** 网络不可达时 getMonthStr 返回 fail 哨兵值而非抛异常 */
  @Test
  void getMonthStrReturnsFailOnNetworkError() {
    assertEquals("fail", HolidayUtil.getMonthStr("http://invalid.invalid.local", 2024, 1));
  }
}
