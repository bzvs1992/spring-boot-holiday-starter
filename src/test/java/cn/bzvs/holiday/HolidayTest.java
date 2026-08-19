package cn.bzvs.holiday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.entity.vo.CalendarInfoVO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.service.HolidayService;
import com.alibaba.fastjson2.JSON;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

/**
 * 节假日 & 工作日 & 周末 测试
 *
 * @author bzvs
 * @date 2024-12-04
 * @since 1.0.0
 */
class HolidayTest {

  /** 节假日服务 */
  private HolidayService holidayService;

  /** 初始化缓存数据（同时修复原实现：显式 UTF-8 编码、try-with-resources 确保流关闭） */
  @BeforeEach
  void init() throws Exception {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource resource = resourceLoader.getResource("classpath:config/holiday.json");
    try (var inputStream = resource.getInputStream()) {
      String result = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
      List<CalendarVO> calendarVOList = JSON.parseArray(result, CalendarVO.class);
      ConstantData.init(calendarVOList);
    }

    holidayService = new HolidayService();
  }

  /** 节假日测试 */
  @Test
  void checkHoliday() {
    assertTrue(holidayService.isHoliday("2023-10-04"));
  }

  /** 工作日测试：缓存外日期，验证按自然星期的兜底判定（周一为工作日） */
  @Test
  void checkWorkDay() {
    assertTrue(holidayService.isWorkDay("2023-12-04"));
  }

  /** 周末测试：缓存外日期，验证按自然星期的兜底判定（周六为周末） */
  @Test
  void checkWeekend() {
    assertTrue(holidayService.isWeekend("2023-12-09"));
  }

  /** 补班测试：补班日属于工作日，且不是周末 */
  @Test
  void checkCompensateWorkDay() {
    assertTrue(holidayService.isWorkDay("2023-10-07"));
    assertFalse(holidayService.isWeekend("2023-10-07"));
  }

  /** getDate 返回完整日历信息 */
  @Test
  void checkGetDateInfo() {
    CalendarInfoVO vo = holidayService.getDate("2023-10-04");
    assertEquals("节假日", vo.getRemark());
    assertEquals(Integer.valueOf(2023), vo.getYear());
    assertEquals(Integer.valueOf(10), vo.getMonth());
    assertEquals(Integer.valueOf(4), vo.getDay());
  }

  /** Date / LocalDate / LocalDateTime 重载与 String 重载结果一致 */
  @Test
  void dateOverloadsAreEquivalent() {
    LocalDate localDate = LocalDate.of(2023, 10, 4);
    LocalDateTime localDateTime = localDate.atTime(15, 30);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    assertTrue(holidayService.isHoliday(localDate));
    assertTrue(holidayService.isHoliday(localDateTime));
    assertTrue(holidayService.isHoliday(date));
    assertEquals(holidayService.getDate("2023-10-04").getStatus(),
        holidayService.getDate(date).getStatus());
  }

  /** 缓存中不存在的日期按自然星期兜底判断 */
  @Test
  void unknownDateFallsBackToNaturalWeek() {
    // 2030-01-05 为周六（缓存外），应判定为周末
    assertTrue(holidayService.isWeekend("2030-01-05"));
    // 2030-01-07 为周一（缓存外），应判定为工作日
    assertTrue(holidayService.isWorkDay("2030-01-07"));
  }

  /** 非法日期（如 2023-02-30）严格解析时应抛出 DateTimeParseException */
  @Test
  void invalidDateThrowsException() {
    assertThrows(DateTimeParseException.class, () -> holidayService.isHoliday("2023-02-30"));
  }

  /** 补班测试：补班日走 LocalDate 重载，同样属于工作日 */
  @Test
  void compensateWorkDayViaLocalDate() {
    assertTrue(holidayService.isWorkDay(LocalDate.of(2023, 10, 7)));
  }
}
