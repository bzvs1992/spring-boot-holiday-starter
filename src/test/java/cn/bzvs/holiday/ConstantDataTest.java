package cn.bzvs.holiday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * ConstantData 缓存测试
 *
 * @author bzvs
 * @since 2.0.0
 */
class ConstantDataTest {

  /** 构造日历对象 */
  private CalendarVO vo(String date, int status) {
    CalendarVO v = new CalendarVO();
    v.setDate(date);
    v.setStatus(status);
    return v;
  }

  /** init 为全量替换：旧数据被清除 */
  @Test
  void initReplacesAllData() {
    ConstantData.init(List.of(vo("2024-01-01", 3)));
    ConstantData.init(List.of(vo("2024-05-01", 3)));
    Map<String, CalendarVO> map = ConstantData.getAllDateMap();
    assertEquals(1, map.size());
    assertNull(map.get("2024-01-01"));
    assertNotNull(map.get("2024-05-01"));
  }

  /** getAllDateMap 返回不可修改视图，防止外部污染缓存 */
  @Test
  void getAllDateMapIsImmutable() {
    ConstantData.init(List.of(vo("2024-01-01", 3)));
    Map<String, CalendarVO> map = ConstantData.getAllDateMap();
    assertThrows(
        UnsupportedOperationException.class,
        () -> map.put("2024-01-02", vo("2024-01-02", 1)));
  }

  /** setDayInfoAll 增量合并：新日期追加，旧数据保留 */
  @Test
  void setDayInfoAllMergesData() {
    ConstantData.init(List.of(vo("2024-01-01", 3)));
    ConstantData.setDayInfoAll(List.of(vo("2024-05-01", 3)));
    Map<String, CalendarVO> map = ConstantData.getAllDateMap();
    assertEquals(2, map.size());
    assertNotNull(map.get("2024-01-01"));
    assertNotNull(map.get("2024-05-01"));
  }

  /** setDayInfoAll 同日期覆盖：修复语义生效 */
  @Test
  void setDayInfoAllOverridesSameDate() {
    ConstantData.init(List.of(vo("2024-01-01", 3)));
    ConstantData.setDayInfoAll(List.of(vo("2024-01-01", 1)));
    Map<String, CalendarVO> map = ConstantData.getAllDateMap();
    assertEquals(1, map.size());
    assertEquals(1, map.get("2024-01-01").getStatus());
  }

  /** setDayInfoAll 输入内重复日期快速失败 */
  @Test
  void setDayInfoAllThrowsOnDuplicateInput() {
    assertThrows(
        IllegalStateException.class,
        () -> ConstantData.setDayInfoAll(List.of(vo("2024-01-01", 3), vo("2024-01-01", 1))));
  }
}
