package cn.bzvs.holiday.util;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.entity.dto.AlmanacDTO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * 节假日工具类
 *
 * @author bzvs
 * @date 2024-12-04
 * @since 1.0.0
 */
@Slf4j
public class HolidayUtil {

  /** 百度日历开放数据接口（非官方接口，无 SLA 保证） */
  private static final String DEFAULT_API_URL =
      "https://opendata.baidu.com/data/inner?tn=reserved_all_res_tn&type=json&resource_id=52109&apiType=yearMonthData";

  /** 在线请求超时时间（毫秒） */
  private static final int HTTP_TIMEOUT_MS = 10_000;

  /** 在线抓取失败哨兵值 */
  private static final String FAIL = "fail";

  private HolidayUtil() {}

  /**
   * 获取多年的日历数据
   *
   * @param years 年份集合
   * @return List<CalendarVO>
   */
  public static List<CalendarVO> getYears(@NonNull Set<Integer> years) {
    List<CalendarVO> calendarVOList = new ArrayList<>();
    Set<Integer> sortSet = new TreeSet<>(years);
    for (Integer year : sortSet) {
      calendarVOList.addAll(getYear(year));
    }
    return calendarVOList;
  }

  /**
   * 获取某年的日历数据
   *
   * @param year 年份
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
   * @param year 年份
   * @param month 月份
   * @return List<CalendarVO>
   */
  public static List<CalendarVO> getMonth(int year, int month) {
    List<CalendarVO> calendarVOList = new ArrayList<>();
    String result = getMonthStr(year, month);
    if (FAIL.equals(result)) {
      // 在线抓取失败：记录日志并返回空列表，由上层统一处理，不再让 JSON 解析异常向上冒泡
      log.warn("在线获取 {}年{}月 节假日数据失败", year, month);
      return calendarVOList;
    }
    JSONObject json = JSON.parseObject(result);
    JSONArray jsonArray =
        (JSONArray) JSONPath.eval(json, "$.Result[0].DisplayData.resultData.tplData.data.almanac");
    List<AlmanacDTO> almanacDTOList =
        JSON.parseArray(JSON.toJSONString(jsonArray), AlmanacDTO.class);
    for (AlmanacDTO vo : almanacDTOList) {
      String status = vo.getStatus();
      if (vo.getMonth().equals(String.valueOf(month)) && StringUtils.hasText(status)) {
        LocalDate localDate = LocalDate.of(year, month, Integer.parseInt(vo.getDay()));
        CalendarVO date = new CalendarVO();
        date.setDate(localDate.toString());
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
   * 获取某年某月的日历数据（默认接口地址）
   *
   * @param year 年份
   * @param month 月份
   * @return JSON 字符串，失败返回 "fail"
   */
  public static String getMonthStr(int year, int month) {
    return getMonthStr(DEFAULT_API_URL, year, month);
  }

  /**
   * 获取某年某月的日历数据
   *
   * @param baseApiUrl 接口基础地址（不含 query 参数）
   * @param year 年份
   * @param month 月份
   * @return JSON 字符串，失败返回 "fail"
   */
  public static String getMonthStr(String baseApiUrl, int year, int month) {
    String query = year + "年" + month + "月";
    long timeMillis = System.currentTimeMillis();
    try {
      String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
      String apiUrl = baseApiUrl + "&query=" + encodedQuery + "&cb=jsonp_" + timeMillis;
      String content = HttpRequest.get(apiUrl).timeout(HTTP_TIMEOUT_MS).execute().body();
      return reParseJson(content);
    } catch (Exception e) {
      log.error("在线获取 {} 数据异常", query, e);
    }
    return FAIL;
  }

  /**
   * 从 jsonp 包裹中提取 JSON 字符串
   *
   * @param old 原始响应内容
   * @return JSON 字符串
   */
  public static String reParseJson(String old) {
    int start = old.indexOf("{");
    int end = old.lastIndexOf("}");
    if (start < 0 || end <= start) {
      throw new IllegalArgumentException("响应内容不包含有效 JSON: " + old);
    }
    return old.substring(start, end + 1);
  }

  /**
   * 获取星期（ISO 定义：1-周一，7-周日）
   *
   * @param localDate 日期
   * @return 星期 1-7
   */
  public static int getDayOfWeek(LocalDate localDate) {
    return localDate.getDayOfWeek().getValue();
  }

  /**
   * 按配置加载节假日数据并初始化缓存
   *
   * @param properties 节假日配置
   * @param resourceLoader 资源加载器
   * @throws Exception 加载异常
   */
  public static void setHolidayData(HolidayProperties properties, ResourceLoader resourceLoader)
      throws Exception {
    List<CalendarVO> calendarVOList;
    // 本地模式
    if ("local".equals(properties.getType())) {
      String result;
      // 使用外部 JSON 文件数据
      if (FileUtil.exist(properties.getPath())) {
        result = FileUtil.readString(properties.getPath(), StandardCharsets.UTF_8);
      }
      // 使用内部 JSON 文件数据（显式 UTF-8 编码，try-with-resources 确保流关闭）
      else {
        Resource resource = resourceLoader.getResource(properties.getPath());
        try (InputStream inputStream = resource.getInputStream()) {
          result = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
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
      throw new IllegalStateException(
          "节假日数据初始化失败，缓存为空，请检查 holiday.type / holiday.years / holiday.path 配置或网络连通性");
    }
  }
}
