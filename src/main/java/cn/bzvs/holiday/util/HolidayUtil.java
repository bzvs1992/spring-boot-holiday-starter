package cn.bzvs.holiday.util;

import cn.bzvs.holiday.entity.dto.AlmanacDTO;
import cn.bzvs.holiday.entity.vo.CalendarVO;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 节假日工具类
 *
 * @author bzvs
 * @date 2024/12/04
 * @since 1.0.0
 */
@Slf4j
public class HolidayUtil {

    private HolidayUtil() {
    }

    /**
     * 获取多年的日历数据
     *
     * @param years
     * @return
     */
    public static List<CalendarVO> getYears(@NonNull Set<Integer> years) {
        List<CalendarVO> calendarVOList = new ArrayList<>();
        Set<Integer> sortSet = new TreeSet<>(Comparator.naturalOrder());
        sortSet.addAll(years);
        for (Integer year : sortSet) {
            calendarVOList.addAll(getYear(year));
        }
        return calendarVOList;
    }

    /**
     * 获取某年的日历数据
     *
     * @param year
     * @return
     */
    public static List<CalendarVO> getYear(int year) {
        List<CalendarVO> calendarVOList = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            calendarVOList.addAll(getMonth(year, month));
        }
        return calendarVOList;
    }

    /**
     * 获取某年某月的日历数据
     *
     * @param year
     * @param month
     * @return
     */
    public static List<CalendarVO> getMonth(int year, int month) {
        List<CalendarVO> calendarVOList = new ArrayList<>();
        String result = getMonthStr(year, month);
        JSONObject json = JSON.parseObject(result);
        JSONArray data = json.getJSONArray("data");
        JSONObject dataObj = JSON.parseObject(data.get(0).toString());
        List<AlmanacDTO> almanacDTOList = JSONArray.parseArray(dataObj.getString("almanac"), AlmanacDTO.class);
        for (AlmanacDTO vo : almanacDTOList) {
            String status = vo.getStatus();
            if (vo.getMonth().equals(String.valueOf(month)) && StringUtils.hasText(status)) {
                DateTime dateTime = DateUtil.parse("%s-%s-%s".formatted(year, month, Integer.parseInt(vo.getDay())));
                CalendarVO date = new CalendarVO();
                date.setDate(DateUtil.formatDate(dateTime));
                if (status.equals("1")) {
                    date.setStatus(3);
                } else if (status.equals("2")) {
                    date.setStatus(2);
                }
                calendarVOList.add(date);
            }
        }
        return calendarVOList;
    }

    /**
     * 获取某年某月的日历数据
     *
     * @param year
     * @param month
     * @return
     */
    public static String getMonthStr(int year, int month) {
        String result = "fail";
        String query;
        query = year + "年" + month + "月";
        long timeMillis = System.currentTimeMillis();
        try {
            String apiUrl = "https://sp1.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?tn=wisetpl&format=json&query=" + query + "&co=&resource_id=39043&t=" + timeMillis + "&cb=op_aladdin_callback" + timeMillis;
            String content = HttpUtil.get(apiUrl);
            result = reParseJson(content);
        } catch (Exception e) {
            log.error("在线获取 {} 数据异常", query, e);
        }
        return result;
    }

    /**
     * 解析json
     */
    public static String reParseJson(String old) {
        int start = old.indexOf("{");
        int end = old.lastIndexOf("}");
        return old.substring(start, end + 1);
    }

    public static int getDayOfWeek(DateTime dateTime) {
        Calendar calendar = dateTime.toCalendar();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY -> {
                return 7;
            }
            case Calendar.MONDAY -> {
                return 1;
            }
            case Calendar.TUESDAY -> {
                return 2;
            }
            case Calendar.WEDNESDAY -> {
                return 3;
            }
            case Calendar.THURSDAY -> {
                return 4;
            }
            case Calendar.FRIDAY -> {
                return 5;
            }
            case Calendar.SATURDAY -> {
                return 6;
            }
            default -> throw new IllegalStateException("Unexpected value: " + day);
        }
    }
}
