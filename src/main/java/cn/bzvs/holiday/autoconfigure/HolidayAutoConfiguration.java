package cn.bzvs.holiday.autoconfigure;

import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.service.HolidayFixService;
import cn.bzvs.holiday.service.HolidayService;
import cn.bzvs.holiday.util.ApplicationContextUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 组件自动配置
 *
 * @author bzvs
 * @date 2024/12/04
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HolidayProperties.class)
public class HolidayAutoConfiguration implements InitializingBean {

    /**
     * 配置对象
     */
    private final HolidayProperties properties;

    /**
     * 资源加载Loader
     */
    private final ResourceLoader resourceLoader;

    /**
     * 应用程序上下文工具类
     * @return ApplicationContextUtil
     */
    @Bean
    public ApplicationContextUtil applicationContextUtil() {
        return new ApplicationContextUtil();
    }

    /**
     * 节假日相关的日历服务
     * @return HolidayService
     */
    @Bean
    public HolidayService holidayService() {
        return new HolidayService();
    }

    /**
     * 节假日相关的日历修复服务
     * @return HolidayFixService
     */
    @Bean
    public HolidayFixService holidayFixService() {
        return new HolidayFixService(properties, resourceLoader);
    }

    public HolidayAutoConfiguration(HolidayProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if (StringUtils.hasText(this.properties.getType()) && "local".equals(this.properties.getType())) {
            if (FileUtil.exist(this.properties.getPath())) {
                return;
            }
            Resource resource = this.resourceLoader.getResource(this.properties.getPath());
            Assert.state(resource.exists(),
                    "Cannot find config location: " + resource + " (please add config file or check your Holiday configuration)");
            Assert.state(Objects.requireNonNull(resource.getFilename()).toLowerCase().contains(".json"),
                    "The file format is incorrect, location: " + resource + " (please configure a json file or check your Holiday configuration)");
        }
    }
}
