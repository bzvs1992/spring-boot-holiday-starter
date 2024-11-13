package cn.bzvs.holiday.util;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.entity.dto.AlmanacDTO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 节假日工具类
 *
 * @author bzvs
 * @date 2024/12/04
 * @since 1.0.0
 */
@Slf4j
public class HolidayUtil {

  private HolidayUtil() {}

  /**
   * 获取多年的日历数据
   *
   * @param years
   * @return List<CalendarVO>
   */
  public static List<CalendarVO> getYears(@NonNull Set<Integer> years) {
    List<CalendarVO> calendarVOList = new ArrayList<>();
    Set<Integer> sortSet = new TreeSet<>(Comparator.naturalOrder());
    sortSet.addAll(years);
    for (Integer year : sortSet) {
      calendarVOList.addAll(getYear(year));
    }
    return calendarVOList;
  }

  /**
   * 获取某年的日历数据
   *
   * @param year
   * @return List<CalendarVO>
   */
  public static List<CalendarVO> getYear(int year) {
    List<CalendarVO> calendarVOList = new ArrayList<>();
    for (int month = 1; month <= 12; month++) {
      calendarVOList.addAll(getMonth(year, month));
    }
    return calendarVOList;
  }

  /**
   * 获取某年某月的日历数据
   *
   * @param year
   * @param month
   * @return List<CalendarVO>
   */
  public static List<CalendarVO> getMonth(int year, int month) {
    List<CalendarVO> calendarVOList = new ArrayList<>();
    String result = getMonthStr(year, month);
    JSONObject json = JSON.parseObject(result);
    JSONArray jsonArray =
        (JSONArray) JSONPath.eval(json, "$.Result[0].DisplayData.resultData.tplData.data.almanac");
    List<AlmanacDTO> almanacDTOList =
        JSON.parseArray(JSON.toJSONString(jsonArray), AlmanacDTO.class);
    for (AlmanacDTO vo : almanacDTOList) {
      String status = vo.getStatus();
      if (vo.getMonth().equals(String.valueOf(month)) && StringUtils.hasText(status)) {
        DateTime dateTime =
            DateUtil.parse(String.format("%s-%s-%s", year, month, Integer.parseInt(vo.getDay())));
        CalendarVO date = new CalendarVO();
        date.setDate(DateUtil.formatDate(dateTime));
        if (status.equals("1")) {
          date.setStatus(3);
        } else if (status.equals("2")) {
          date.setStatus(2);
        }
        calendarVOList.add(date);
      }
    }
    return calendarVOList;
  }

  /**
   * 获取某年某月的日历数据
   *
   * @param year
   * @param month
   * @return List<CalendarVO>
   */
  public static String getMonthStr(int year, int month) {
    String result = "fail";
    String query;
    query = year + "年" + month + "月";
    long timeMillis = System.currentTimeMillis();
    try {
      String apiUrl =
          "https://opendata.baidu.com/data/inner?tn=reserved_all_res_tn&type=json&resource_id=52109&query="
              + query
              + "&apiType=yearMonthData&cb=jsonp_"
              + timeMillis;
      String content = HttpUtil.get(apiUrl);
      result = reParseJson(content);
    } catch (Exception e) {
      log.error("在线获取 {} 数据异常", query, e);
    }
    return result;
  }

  /** 解析json */
  public static String reParseJson(String old) {
    int start = old.indexOf("{");
    int end = old.lastIndexOf("}");
    return old.substring(start, end + 1);
  }

  public static int getDayOfWeek(DateTime dateTime) {
    Calendar calendar = dateTime.toCalendar();
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    switch (day) {
      case Calendar.SUNDAY -> {
        return 7;
      }
      case Calendar.MONDAY -> {
        return 1;
      }
      case Calendar.TUESDAY -> {
        return 2;
      }
      case Calendar.WEDNESDAY -> {
        return 3;
      }
      case Calendar.THURSDAY -> {
        return 4;
      }
      case Calendar.FRIDAY -> {
        return 5;
      }
      case Calendar.SATURDAY -> {
        return 6;
      }
      default -> throw new IllegalStateException("Unexpected value: " + day);
    }
  }

  public static void setHolidayData(HolidayProperties properties, ResourceLoader resourceLoader)
      throws Exception {
    List<CalendarVO> calendarVOList;
    String result;
    // 本地模式
    if (properties.getType().equals("local")) {
      // 使用外部 JSON 文件数据
      if (FileUtil.exist(properties.getPath())) {
        result = FileUtil.readString(properties.getPath(), StandardCharsets.UTF_8);
      }
      // 使用内部 JSON 文件数据
      else {
        Resource resource = resourceLoader.getResource(properties.getPath());
        InputStream inputStream = resource.getInputStream();
        result =
            new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
      }
      Assert.state(StringUtils.hasText(result), "If the data is empty, please configure the data");
      calendarVOList = JSON.parseArray(result, CalendarVO.class);
    }
    // 在线模式
    else {
      Set<Integer> years = new HashSet<>();
      if (properties.getYears() != null && !properties.getYears().isEmpty()) {
        years = properties.getYears();
      }
      int year = LocalDate.now().getYear();
      years.add(year);
      calendarVOList = HolidayUtil.getYears(years);
    }

    if (calendarVOList != null && !calendarVOList.isEmpty()) {
      ConstantData.init(calendarVOList);
    } else {
      log.warn("节假日数据初始化失败");
    }
  }
}
