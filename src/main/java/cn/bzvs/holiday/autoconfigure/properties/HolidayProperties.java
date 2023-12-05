package cn.bzvs.holiday.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * 节假日配置
 */
@Data
@ConfigurationProperties(prefix = "holiday")
public class HolidayProperties {

    /**
     * 类型：在线模式：network, 本地模式：local
     */
    private String type = "network";

    /**
     * 配置 在线获取 模式下，数据获取的年份
     */
    private Set<Integer> years;
    /**
     * 配置 本地获取 模式下，数据路径
     */
    private String path = "/config/holiday.json";
}
