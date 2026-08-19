package cn.bzvs.holiday;

import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.bzvs.holiday.entity.vo.CalendarInfoVO;
import org.junit.jupiter.api.Test;

/**
 * CalendarInfoVO 不可变性测试
 *
 * @author bzvs
 * @since 2.0.0
 */
class CalendarInfoVOTest {

  /** 不可变对象：调用继承的 setDate/setStatus 抛出 UnsupportedOperationException */
  @Test
  void settersThrowUnsupportedOperation() {
    CalendarInfoVO vo = new CalendarInfoVO("2023-10-04", 3, 3);
    assertThrows(UnsupportedOperationException.class, () -> vo.setDate("2023-10-05"));
    assertThrows(UnsupportedOperationException.class, () -> vo.setStatus(0));
  }

  /** 构造器判空：null 参数抛出 NullPointerException 而非拆箱裸 NPE */
  @Test
  void constructorRejectsNullArguments() {
    assertThrows(NullPointerException.class, () -> new CalendarInfoVO(null, 3, 3));
    assertThrows(NullPointerException.class, () -> new CalendarInfoVO("2023-10-04", null, 3));
    assertThrows(NullPointerException.class, () -> new CalendarInfoVO("2023-10-04", 3, null));
  }
}
