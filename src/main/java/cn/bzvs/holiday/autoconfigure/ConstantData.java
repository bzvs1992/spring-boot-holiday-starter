package cn.bzvs.holiday.autoconfigure;

import cn.bzvs.holiday.entity.vo.CalendarVO;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 常量数据（全局节假日缓存）
 *
 * <p>采用 volatile 引用整体替换策略：初始化/重置时构建新 Map 后原子切换引用，并发读不会读到中间状态；查询直接读引用，无全量拷贝开销。
 *
 * @author bzvs
 * @date 2024-12-04
 * @since 1.0.0
 */
public class ConstantData {

  /** 所有日期数据（volatile 保证引用切换对并发读线程立即可见） */
  private static volatile Map<String, CalendarVO> allDateMap = Map.of();

  /** 私有构造器，防止实例化 */
  private ConstantData() {}

  /**
   * 全量初始化：构建新数据后原子替换，并发读不会读到空窗口
   *
   * @param calendarVOList 对象列表
   */
  public static void init(List<CalendarVO> calendarVOList) {
    allDateMap =
        calendarVOList.stream()
            .collect(Collectors.toMap(CalendarVO::getDate, Function.identity()));
  }

  /**
   * 增量合并数据
   *
   * @param calendarVOList 对象列表
   */
  public static synchronized void setDayInfoAll(List<CalendarVO> calendarVOList) {
    Map<String, CalendarVO> merged = new HashMap<>(allDateMap);
    calendarVOList.forEach(vo -> merged.put(vo.getDate(), vo));
    allDateMap = Collections.unmodifiableMap(merged);
  }

  /**
   * 获取所有日历数据（不可修改视图，O(1)，无拷贝）
   *
   * @return Map<String, CalendarVO> 对象MAP
   */
  public static Map<String, CalendarVO> getAllDateMap() {
    return Collections.unmodifiableMap(allDateMap);
  }
}
