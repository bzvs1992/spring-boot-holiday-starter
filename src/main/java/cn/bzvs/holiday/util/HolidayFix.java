package cn.bzvs.holiday.util;

import cn.bzvs.holiday.autoconfigure.ConstantData;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

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
     * 修复数据
     * @param file
     * @return
     */
    public static boolean fix(File file) {
        String json = FileUtil.readString(file, StandardCharsets.UTF_8);
        return fix(json);
    }

    /**
     * 修复数据
     * @param json
     * @return
     */
    public static boolean fix(String json) {
        try {
            List<CalendarVO> calendarVOList = JSON.parseArray(json, CalendarVO.class);
           return fix(calendarVOList);
        }catch (Exception e) {
            log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 修复数据
     * @param calendarVOList
     * @return
     */
    public static boolean fix(List<CalendarVO> calendarVOList) {
        try {
            ConstantData.setDayInfoAll(calendarVOList);
        }catch (Exception e) {
            log.warn("节假日修复失败, 请检查 JSON 数据，异常信息：{}", e.getMessage());
            return false;
        }
        return true;
    }
}
