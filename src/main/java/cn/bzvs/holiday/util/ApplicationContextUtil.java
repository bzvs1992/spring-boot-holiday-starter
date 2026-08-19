package cn.bzvs.holiday.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取spring中的bean对象工具类
 *
 * @author bzvs
 * @date 2024-12-06
 * @since 1.0.0
 */
public class ApplicationContextUtil implements ApplicationContextAware {

  /** Spring应用上下文环境 */
  private static volatile ApplicationContext applicationContext;

  /**
   * 实现ApplicationContextAware接口的回调方法，设置上下文环境
   *
   * @param applicationContext 应用上下文
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ApplicationContextUtil.applicationContext = applicationContext;
  }

  /**
   * 按名称获取Bean
   *
   * @param beanId bean名称
   * @return bean实例
   */
  public static Object getBean(String beanId) throws BeansException {
    return applicationContext.getBean(beanId);
  }

  /**
   * 按类型获取Bean（泛型，无需强转）
   *
   * @param clazz bean类型
   * @param <T> bean类型
   * @return bean实例
   */
  public static <T> T getBean(Class<T> clazz) throws BeansException {
    return applicationContext.getBean(clazz);
  }
}
