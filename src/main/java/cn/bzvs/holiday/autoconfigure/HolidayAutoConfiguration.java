package cn.bzvs.holiday.autoconfigure;

import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.service.HolidayFixService;
import cn.bzvs.holiday.service.HolidayService;
import cn.bzvs.holiday.util.ApplicationContextUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * 组件自动配置
 *
 * @author bzvs
 * @date 2024-12-04
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(HolidayProperties.class)
public class HolidayAutoConfiguration {

  /** 配置对象 */
  private final HolidayProperties properties;

  /** 资源加载Loader */
  private final ResourceLoader resourceLoader;

  /**
   * 构造函数（local 模式下校验配置文件存在且格式正确）
   *
   * @param properties 配置对象
   * @param resourceLoader 资源加载Loader
   */
  public HolidayAutoConfiguration(HolidayProperties properties, ResourceLoader resourceLoader) {
    this.properties = properties;
    this.resourceLoader = resourceLoader;
    checkLocalConfigFile();
  }

  /**
   * 应用程序上下文工具类
   *
   * @return ApplicationContextUtil
   */
  @Bean
  @ConditionalOnMissingBean
  public ApplicationContextUtil applicationContextUtil() {
    return new ApplicationContextUtil();
  }

  /**
   * 节假日相关的日历服务
   *
   * @return HolidayService
   */
  @Bean
  @ConditionalOnMissingBean
  public HolidayService holidayService() {
    return new HolidayService();
  }

  /**
   * 节假日相关的日历修复服务（构造时触发数据加载）
   *
   * @return HolidayFixService
   */
  @Bean
  @ConditionalOnMissingBean
  public HolidayFixService holidayFixService() {
    return new HolidayFixService(properties, resourceLoader);
  }

  /** local 模式下校验 JSON 配置文件存在且格式正确 */
  private void checkLocalConfigFile() {
    if (!"local".equals(this.properties.getType())) {
      return;
    }
    // 外部系统路径文件
    if (FileUtil.exist(this.properties.getPath())) {
      return;
    }
    // classpath 资源文件
    Resource resource = this.resourceLoader.getResource(this.properties.getPath());
    Assert.state(
        resource.exists(),
        "Cannot find config location: "
            + resource
            + " (please add config file or check your Holiday configuration)");
    String filename = resource.getFilename();
    Assert.state(
        filename != null && filename.toLowerCase().endsWith(".json"),
        "The file format is incorrect, location: "
            + resource
            + " (please configure a json file or check your Holiday configuration)");
  }
}
