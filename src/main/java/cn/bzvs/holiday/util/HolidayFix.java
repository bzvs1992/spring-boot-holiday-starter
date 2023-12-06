package cn.bzvs.holiday.util;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.autoconfigure.properties.HolidayProperties;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 节假日修复工具
 *
 * @author bzvs
 * @date 2024/12/04
 * @since 1.0.0
 */
@Slf4j
public class HolidayFix {

    private HolidayFix() {
    }

    /**
     * 重置数据
     *
     * @param file JSON文件
     * @return boolean
     */
    public static boolean reset(@NonNull File file) {
        String json = FileUtil.readString(file, StandardCharsets.UTF_8);
        return reset(json);
    }

    /**
     * 重置数据
     *
     * @param json JSON数据
     * @return boolean
     */
    public static boolean reset(String json) {
        try {
            List<CalendarVO> calendarVOList = JSON.parseArray(json, CalendarVO.class);
            return reset(calendarVOList);
        } catch (Exception e) {
            log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 重置数据
     *
     * @param calendarVOList 对象列表
     * @return boolean
     */
    public static boolean reset(List<CalendarVO> calendarVOList) {
        try {
            ConstantData.init(calendarVOList);
        } catch (Exception e) {
            log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 修复数据
     *
     * @param file JSON文件
     * @return boolean
     */
    public static boolean fix(File file) {
        return fix(file, false);
    }

    /**
     * 修复数据
     *
     * @param json JSON数据
     * @return boolean
     */
    public static boolean fix(String json) {
        return fix(json, false);
    }

    /**
     * 修复数据
     *
     * @param calendarVOList 对象列表
     * @return boolean
     */
    public static boolean fix(List<CalendarVO> calendarVOList) {
        return fix(calendarVOList, false);
    }

    /**
     * 修复数据
     *
     * @param file JSON文件
     * @param updateFile boolean
     * @return boolean
     */
    public static boolean fix(File file, boolean updateFile) {
        if ("json".equalsIgnoreCase(FileUtil.getPrefix(file))) {
            String json = FileUtil.readString(file, StandardCharsets.UTF_8);
            return fix(json, updateFile);
        }
        log.warn("文件：{} 不是 JSON 文件，无法修复", file.getName());
        return false;
    }

    /**
     * 修复数据
     *
     * @param json JSON数据
     * @param updateFile boolean
     * @return boolean
     */
    public static boolean fix(String json, boolean updateFile) {
        try {
            List<CalendarVO> calendarVOList = JSON.parseArray(json, CalendarVO.class);
            return fix(calendarVOList, updateFile);
        } catch (Exception e) {
            log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 修复数据
     *
     * @param calendarVOList 对象列表
     * @param updateFile boolean
     * @return boolean
     */
    public static boolean fix(List<CalendarVO> calendarVOList, boolean updateFile) {
        try {
            ConstantData.setDayInfoAll(calendarVOList);
        } catch (Exception e) {
            log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
            return false;
        }
        if (updateFile) {
            return updateFile();
        }
        return false;
    }

    /**
     * 更新 JSON 文件
     *
     * @return boolean
     */
    public static boolean updateFile() {
        HolidayProperties holidayProperties = (HolidayProperties) ApplicationContextUtil.getBean(HolidayProperties.class);
        if ("local".equals(holidayProperties.getType()) && StringUtils.hasText(holidayProperties.getPath())) {
            if (FileUtil.exist(holidayProperties.getPath())) {
                FileUtil.writeString(JSON.toJSONString(ConstantData.getAllDateMap()), holidayProperties.getPath(), StandardCharsets.UTF_8);
                return true;
            } else {
                log.warn("文件更新失败，Jar内部JSON文件数据不可更改，请配置外部JSON文件");
            }
        } else {
            log.warn("文件更新失败，请检查配置文件，type 或 path 配置错误, type: {}  path: {}", holidayProperties.getType(), holidayProperties.getPath());
        }
        return false;
    }
}
