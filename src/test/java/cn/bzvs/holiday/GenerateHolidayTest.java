package cn.bzvs.holiday;

import cn.bzvs.holiday.util.HolidayUtil;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 生成JSON文件测试类
 *
 * @author bzvs
 * @date 2024/12/04
 * @since 1.0.0
 */
@Slf4j
public class GenerateHolidayTest {

    /**
     * 生成本地JSON文件（某年）
     *
     * @throws Exception
     */
    @Test
    public void generateFile() throws Exception {
        int year = 2024;
        List<CalendarVO> calendarVOList = HolidayUtil.getYear(year);
        log.info("{} - 日历长度为：{}", year, calendarVOList.size());
        String json = JSON.toJSONString(calendarVOList);
        File file = new File("%s/config/holiday.json".formatted(year));
        log.info("{} - 文件路径为：{}", year, file.getAbsolutePath());
        FileUtil.writeString(json, file, StandardCharsets.UTF_8);
    }

    /**
     * 生成本地JSON文件（多年）
     *
     * @throws Exception
     */
    @Test
    public void generateFileYears() throws Exception {
        Set<Integer> years = new HashSet<>();
        years.add(2023);
        years.add(2024);
        List<CalendarVO> calendarVOList = HolidayUtil.getYears(years);
        log.info("{} - 日历长度为：{}", years, calendarVOList.size());
        String json = JSON.toJSONString(calendarVOList);
        File file = new File("%s/config/holiday.json".formatted(years));
        log.info("{} - 文件路径为：{}", years, file.getAbsolutePath());
        FileUtil.writeString(json, file, StandardCharsets.UTF_8);
    }

}
