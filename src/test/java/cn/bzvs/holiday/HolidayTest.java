package cn.bzvs.holiday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.entity.vo.CalendarInfoVO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.service.HolidayService;
import com.alibaba.fastjson2.JSON;
import java.nio.charset.StandardCharsets;
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
}
