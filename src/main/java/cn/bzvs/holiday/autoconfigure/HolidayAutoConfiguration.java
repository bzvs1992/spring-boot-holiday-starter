package cn.bzvs.holiday.autoconfigure;

import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.bzvs.holiday.service.HolidayService;
import cn.bzvs.holiday.util.HolidayUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HolidayProperties.class)
public class HolidayAutoConfiguration implements InitializingBean {

    private final HolidayProperties properties;

    private final ResourceLoader resourceLoader;

    @Bean
    public HolidayService holidayService() {
        return new HolidayService();
    }

    public HolidayAutoConfiguration(HolidayProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkConfigFileExists();
        setHolidayData();
    }

    private void checkConfigFileExists() {
        if (StringUtils.hasText(this.properties.getType()) && "local".equals(this.properties.getType())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getPath());
            Assert.state(resource.exists(),
                    "Cannot find config location: " + resource + " (please add config file or check your Holiday configuration)");
            Assert.state(Objects.requireNonNull(resource.getFilename()).toLowerCase().contains(".json"),
                    "The file format is incorrect, location: " + resource + " (please configure a json file or check your Holiday configuration)");
        }
    }

    private void setHolidayData() throws Exception {
        List<CalendarVO> calendarVOList;
        // 本地模式
        if (this.properties.getType().equals("local")) {
            Resource resource = this.resourceLoader.getResource(this.properties.getPath());
            InputStream inputStream = resource.getInputStream();
            String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining(System.lineSeparator()));
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

        } else {
            log.warn("节假日数据初始化失败");
        }
    }
}
