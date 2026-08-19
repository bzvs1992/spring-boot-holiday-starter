package cn.bzvs.holiday;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.service.HolidayService;
import cn.bzvs.holiday.util.HolidayFix;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * HolidayFix 测试
 *
 * @author bzvs
 * @since 2.0.0
 */
class HolidayFixTest {

  /** 配置对象 */
  private HolidayProperties properties;

  /** 初始化 */
  @BeforeEach
  void setUp() {
    properties = new HolidayProperties();
    properties.setType("network");
    ConstantData.init(List.of());
  }

  /** 构造日历对象 */
  private CalendarVO vo(String date, int status) {
    CalendarVO v = new CalendarVO();
    v.setDate(date);
    v.setStatus(status);
    return v;
  }

  /** 修复成功且不更新文件时应返回 true（修复返回值语义缺陷） */
  @Test
  void fixReturnsTrueWithoutUpdateFile() {
    assertTrue(HolidayFix.fix(List.of(vo("2024-01-01", 3)), false, properties));
    assertTrue(new HolidayService().isHoliday("2024-01-01"));
  }

  /** fix 为增量合并：不清空已有数据 */
  @Test
  void fixMergesData() {
    HolidayFix.reset(List.of(vo("2024-01-01", 3)));
    HolidayFix.fix(List.of(vo("2024-05-01", 3)), false, properties);
    assertTrue(new HolidayService().isHoliday("2024-01-01"));
    assertTrue(new HolidayService().isHoliday("2024-05-01"));
  }

  /** reset 为全量替换 */
  @Test
  void resetReplacesData() {
    HolidayFix.reset(List.of(vo("2024-01-01", 3)));
    HolidayFix.reset(List.of(vo("2024-05-01", 3)));
    assertFalse(new HolidayService().isHoliday("2024-01-01"));
    assertTrue(new HolidayService().isHoliday("2024-05-01"));
  }

  /** 非法 JSON 修复返回 false */
  @Test
  void fixReturnsFalseOnInvalidJson() {
    assertFalse(HolidayFix.fix("not-a-json", false, properties));
  }
}
