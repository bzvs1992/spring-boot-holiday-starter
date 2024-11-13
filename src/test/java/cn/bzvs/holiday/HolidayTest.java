package cn.bzvs.holiday;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.entity.vo.CalendarInfoVO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.service.HolidayService;
import com.alibaba.fastjson2.JSON;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * 节假日 & 工作日 & 周末 测试
 *
 * @author bzvs
 * @date 2024/12/04
 * @since 1.0.0
 */
@Slf4j
public class HolidayTest {

  private HolidayService holidayService;

  /**
   * 初始化数据
   *
   * @throws Exception
   */
  @Before
  public void init() throws Exception {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource resource = resourceLoader.getResource("/config/holiday.json");
    assert resource != null;
    InputStream inputStream = resource.getInputStream();
    String result =
        new BufferedReader(new InputStreamReader(inputStream))
            .lines()
            .collect(Collectors.joining(System.lineSeparator()));
    List<CalendarVO> calendarVOList = JSON.parseArray(result, CalendarVO.class);
    ConstantData.init(calendarVOList);

    holidayService = new HolidayService();
  }

  /**
   * 节假日测试
   *
   * @throws Exception
   */
  @Test
  public void checkHoliday() throws Exception {
    String date = "2023-10-04";
    boolean isHoliday = holidayService.isHoliday(date);
    CalendarInfoVO vo = holidayService.getDate(date);
    Assert.state(isHoliday, String.format("不是节假日, 是 %s, %s", vo.getRemark(), vo.getDayOfWeekStr()));
    log.info("{} 是{}, {}", date, vo.getRemark(), vo.getDayOfWeekStr());
  }

  /**
   * 工作日测试
   *
   * @throws Exception
   */
  @Test
  public void checkWorkDay() throws Exception {
    String date = "2023-12-04";
    boolean isWorkDay = holidayService.isWorkDay(date);
    CalendarInfoVO vo = holidayService.getDate(date);
    Assert.state(
        isWorkDay, String.format("不是工作日或补班, 是 %s, %s", vo.getRemark(), vo.getDayOfWeekStr()));
    log.info("{} 是{}, {}", date, vo.getRemark(), vo.getDayOfWeekStr());
  }

  /**
   * 周末测试
   *
   * @throws Exception
   */
  @Test
  public void checkWeekend() throws Exception {
    String date = "2023-12-09";
    boolean isWeekend = holidayService.isWeekend(date);
    CalendarInfoVO vo = holidayService.getDate(date);
    Assert.state(isWeekend, String.format("不是周末, 是 %s, %s", vo.getRemark(), vo.getDayOfWeekStr()));
    log.info("{} 是{}, {}", date, vo.getRemark(), vo.getDayOfWeekStr());
  }
}
