package cn.bzvs.holiday.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取spring中的bean对象工具类
 *
 * @author bzvs
 * @date 2024/12/06
 * @since 1.0.0
 */
public class ApplicationContextUtil implements ApplicationContextAware {

  /** Spring应用上下文环境 */
  @Getter private static ApplicationContext applicationContext;

  /** 实现ApplicationContextAware接口的回调方法，设置上下文环境 */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ApplicationContextUtil.applicationContext = applicationContext;
  }

  /** 获取对象 这里重写了bean方法，起主要作用 */
  public static Object getBean(String beanId) throws BeansException {
    return applicationContext.getBean(beanId);
  }

  /** 获取对象 这里重写了bean方法，起主要作用 */
  public static Object getBean(Class clzz) throws BeansException {
    return applicationContext.getBean(clzz);
  }
}
