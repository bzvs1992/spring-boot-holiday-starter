package cn.bzvs.holiday;

import static org.assertj.core.api.Assertions.assertThat;

import cn.bzvs.holiday.autoconfigure.HolidayAutoConfiguration;
import cn.bzvs.holiday.service.HolidayFixService;
import cn.bzvs.holiday.service.HolidayService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * 自动配置装配测试（Boot 3.x/4.x 标准 Starter 测试方式）
 *
 * @author bzvs
 * @since 2.0.0
 */
class HolidayAutoConfigurationTest {

  /** 上下文运行器 */
  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(HolidayAutoConfiguration.class));

  /** local 模式加载成功，装配全部 Bean 且数据可用 */
  @Test
  void localModeLoadsWithAllBeans() {
    contextRunner
        .withPropertyValues("holiday.type=local", "holiday.path=classpath:config/holiday.json")
        .run(
            context -> {
              assertThat(context).hasSingleBean(HolidayService.class);
              assertThat(context).hasSingleBean(HolidayFixService.class);
              HolidayService service = context.getBean(HolidayService.class);
              assertThat(service.isHoliday("2023-10-04")).isTrue();
              assertThat(service.isWeekend("2023-12-09")).isTrue();
              assertThat(service.isWorkDay("2023-10-07")).isTrue();
            });
  }

  /** local 模式文件不存在时启动失败 */
  @Test
  void localModeFailsWhenFileMissing() {
    contextRunner
        .withPropertyValues("holiday.type=local", "holiday.path=classpath:config/missing.json")
        .run(context -> assertThat(context).hasFailed());
  }

  /** local 模式文件后缀非 .json 时启动失败 */
  @Test
  void localModeFailsWhenNotJson() {
    contextRunner
        .withPropertyValues("holiday.type=local", "holiday.path=classpath:config/holiday.txt")
        .run(context -> assertThat(context).hasFailed());
  }

  /** 用户自定义 Bean 优先，自动配置不覆盖（@ConditionalOnMissingBean 生效） */
  @Test
  void userDefinedBeanIsNotOverridden() {
    contextRunner
        .withPropertyValues("holiday.type=local", "holiday.path=classpath:config/holiday.json")
        .withBean("holidayService", HolidayService.class, HolidayService::new)
        .run(context -> assertThat(context).hasSingleBean(HolidayService.class));
  }
}
