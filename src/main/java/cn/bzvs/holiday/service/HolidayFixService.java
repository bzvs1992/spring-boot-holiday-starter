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
 * @date 2024-12-06
 * @since 1.0.0
 */
@Slf4j
public class HolidayFixService {

  /** 配置对象 */
  private final HolidayProperties properties;

  /** 资源加载器 */
  private final ResourceLoader resourceLoader;

  /**
   * 构造函数（构造时触发数据加载）
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
   * @return 是否修复成功
   */
  public boolean fix(File file) {
    return HolidayFix.fix(file, false, properties);
  }

  /**
   * 修复缓存数据
   *
   * @param json JSON数据
   * @return 是否修复成功
   */
  public boolean fix(String json) {
    return HolidayFix.fix(json, false, properties);
  }

  /**
   * 修复缓存数据
   *
   * @param calendarVOList 对象列表
   * @return 是否修复成功
   */
  public boolean fix(List<CalendarVO> calendarVOList) {
    return HolidayFix.fix(calendarVOList, false, properties);
  }

  /**
   * 修复缓存数据并更新本地JSON文件
   *
   * @param file JSON文件
   * @param updateFile 是否更新文件
   * @return 是否修复成功
   */
  public boolean fix(File file, boolean updateFile) {
    return HolidayFix.fix(file, updateFile, properties);
  }

  /**
   * 修复缓存数据并更新本地JSON文件
   *
   * @param json JSON数据
   * @param updateFile 是否更新文件
   * @return 是否修复成功
   */
  public boolean fix(String json, boolean updateFile) {
    return HolidayFix.fix(json, updateFile, properties);
  }

  /**
   * 修复缓存数据并更新本地JSON文件
   *
   * @param calendarVOList 对象列表
   * @param updateFile 是否更新文件
   * @return 是否修复成功
   */
  public boolean fix(List<CalendarVO> calendarVOList, boolean updateFile) {
    return HolidayFix.fix(calendarVOList, updateFile, properties);
  }

  /**
   * 更新 JSON 文件数据
   *
   * @return 是否更新成功
   */
  public boolean updateFile() {
    return HolidayFix.updateFile(properties);
  }
}
