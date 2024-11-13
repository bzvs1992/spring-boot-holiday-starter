package cn.bzvs.holiday.service;

import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.util.HolidayFix;
import cn.bzvs.holiday.util.HolidayUtil;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;

/**
 * 节假日相关的日历修复服务
 *
 * @author bzvs
 * @date 2024/12/06
 * @since 1.0.0
 */
@Slf4j
public class HolidayFixService {

  private final HolidayProperties properties;

  private final ResourceLoader resourceLoader;

  /**
   * 构建函数
   *
   * @param properties 配置
   * @param resourceLoader 资源加载Loader
   */
  public HolidayFixService(HolidayProperties properties, ResourceLoader resourceLoader) {
    this.properties = properties;
    this.resourceLoader = resourceLoader;
    reset();
  }

  /** 数据重置并刷新 */
  public void reset() {
    try {
      HolidayUtil.setHolidayData(properties, resourceLoader);
    } catch (Exception e) {
      log.error("数据重置失败", e);
    }
  }

  /**
   * 修复缓存数据
   *
   * @param file JSON文件
   * @return boolean
   */
  public boolean fix(File file) {
    return HolidayFix.fix(file);
  }

  /**
   * 修复缓存数据
   *
   * @param json JSON数据
   * @return boolean
   */
  public boolean fix(String json) {
    return HolidayFix.fix(json);
  }

  /**
   * 修复缓存数据
   *
   * @param calendarVOList 对象列表
   * @return boolean
   */
  public boolean fix(List<CalendarVO> calendarVOList) {
    return HolidayFix.fix(calendarVOList);
  }

  /**
   * 修复缓存数据并更新本地JSON文件
   *
   * @param file JSON文件
   * @return boolean
   */
  public boolean fix(File file, boolean updateFile) {
    return HolidayFix.fix(file, updateFile);
  }

  /**
   * 修复缓存数据并更新本地JSON文件
   *
   * @param json JSON数据
   * @return boolean
   */
  public boolean fix(String json, boolean updateFile) {
    return HolidayFix.fix(json, updateFile);
  }

  /**
   * 修复缓存数据并更新本地JSON文件
   *
   * @param calendarVOList 对象列表
   * @return boolean
   */
  public boolean fix(List<CalendarVO> calendarVOList, boolean updateFile) {
    return HolidayFix.fix(calendarVOList, updateFile);
  }

  /**
   * 更新 JSON 文件数据
   *
   * @return boolean
   */
  public boolean updateFile() {
    return HolidayFix.updateFile();
  }
}
