package cn.bzvs.holiday.util;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 节假日修复工具
 *
 * @author bzvs
 * @date 2024-12-04
 * @since 1.0.0
 */
@Slf4j
public class HolidayFix {

  private HolidayFix() {}

  /**
   * 重置数据
   *
   * @param file JSON文件
   * @return 是否重置成功
   */
  public static boolean reset(@NonNull File file) {
    String json = FileUtil.readString(file, StandardCharsets.UTF_8);
    return reset(json);
  }

  /**
   * 重置数据
   *
   * @param json JSON数据
   * @return 是否重置成功
   */
  public static boolean reset(String json) {
    try {
      List<CalendarVO> calendarVOList = JSON.parseArray(json, CalendarVO.class);
      return reset(calendarVOList);
    } catch (Exception e) {
      log.warn("节假日重置失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
      return false;
    }
  }

  /**
   * 重置数据
   *
   * @param calendarVOList 对象列表
   * @return 是否重置成功
   */
  public static boolean reset(List<CalendarVO> calendarVOList) {
    try {
      ConstantData.init(calendarVOList);
    } catch (Exception e) {
      log.warn("节假日重置失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * 修复数据（配置从 Spring 上下文获取）
   *
   * @param file JSON文件
   * @return 是否修复成功
   */
  public static boolean fix(File file) {
    return fix(file, false, getProperties());
  }

  /**
   * 修复数据（配置从 Spring 上下文获取）
   *
   * @param json JSON数据
   * @return 是否修复成功
   */
  public static boolean fix(String json) {
    return fix(json, false, getProperties());
  }

  /**
   * 修复数据（配置从 Spring 上下文获取）
   *
   * @param calendarVOList 对象列表
   * @return 是否修复成功
   */
  public static boolean fix(List<CalendarVO> calendarVOList) {
    return fix(calendarVOList, false, getProperties());
  }

  /**
   * 修复数据
   *
   * @param file JSON文件
   * @param updateFile 是否同步更新本地JSON文件
   * @param properties 节假日配置
   * @return 是否修复成功（updateFile=true 时还要求文件写入成功）
   */
  public static boolean fix(File file, boolean updateFile, HolidayProperties properties) {
    if ("json".equalsIgnoreCase(FileUtil.getPrefix(file))) {
      String json = FileUtil.readString(file, StandardCharsets.UTF_8);
      return fix(json, updateFile, properties);
    }
    log.warn("文件：{} 不是 JSON 文件，无法修复", file.getName());
    return false;
  }

  /**
   * 修复数据
   *
   * @param json JSON数据
   * @param updateFile 是否同步更新本地JSON文件
   * @param properties 节假日配置
   * @return 是否修复成功（updateFile=true 时还要求文件写入成功）
   */
  public static boolean fix(String json, boolean updateFile, HolidayProperties properties) {
    try {
      List<CalendarVO> calendarVOList = JSON.parseArray(json, CalendarVO.class);
      return fix(calendarVOList, updateFile, properties);
    } catch (Exception e) {
      log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
      return false;
    }
  }

  /**
   * 修复数据
   *
   * @param calendarVOList 对象列表
   * @param updateFile 是否同步更新本地JSON文件
   * @param properties 节假日配置
   * @return 是否修复成功（updateFile=true 时还要求文件写入成功）
   */
  public static boolean fix(
      List<CalendarVO> calendarVOList, boolean updateFile, HolidayProperties properties) {
    try {
      ConstantData.setDayInfoAll(calendarVOList);
    } catch (Exception e) {
      log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
      return false;
    }
    if (updateFile) {
      return updateFile(properties);
    }
    // 修复成功且不更新文件：返回 true（修复旧版本此处误返回 false 的缺陷）
    return true;
  }

  /**
   * 更新 JSON 文件（将当前缓存以数组格式写回，与本地模式加载格式保持一致）
   *
   * @param properties 节假日配置
   * @return 是否更新成功
   */
  public static boolean updateFile(@NonNull HolidayProperties properties) {
    if ("local".equals(properties.getType()) && StringUtils.hasText(properties.getPath())) {
      if (FileUtil.exist(properties.getPath())) {
        FileUtil.writeString(
            JSON.toJSONString(ConstantData.getAllDateMap().values()),
            properties.getPath(),
            StandardCharsets.UTF_8);
        return true;
      }
      log.warn("文件更新失败，Jar内部JSON文件数据不可更改，请配置外部JSON文件");
    } else {
      log.warn(
          "文件更新失败，请检查配置文件，type 或 path 配置错误, type: {}  path: {}",
          properties.getType(),
          properties.getPath());
    }
    return false;
  }

  /**
   * 从 Spring 上下文获取节假日配置
   *
   * @return HolidayProperties
   */
  private static HolidayProperties getProperties() {
    return ApplicationContextUtil.getBean(HolidayProperties.class);
  }
}
