package cn.bzvs.holiday.entity.vo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarInfoVO extends CalendarVO {

    /**
     *
     * isNullAble:0
     */
    private Integer year;

    /**
     *
     * isNullAble:0
     */
    private Integer month;

    /**
     *
     * isNullAble:0
     */
    private Integer day;


    /**
     * 1，周一 ；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日
     */
    private Integer dayOfWeek;
    /**
     * 星期几
     */
    private String dayOfWeekStr;

    /**
     * 备注
     */
    private String remark;

    private CalendarInfoVO() {
    }

    public CalendarInfoVO(String date, Integer dayOfWeek, Integer status) {
        setDate(date);
        setDayOfWeek(dayOfWeek);
        setStatus(status);
    }

    public void setDate(String date) {
        super.setDate(date);
        DateTime dateTime = DateUtil.parseDate(date);
        LocalDateTime localDateTime = dateTime.toLocalDateTime();
        this.year = localDateTime.getYear();
        this.month = localDateTime.getMonthValue();
        this.day = localDateTime.getDayOfMonth();
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        switch (this.dayOfWeek) {
            case 1:
                this.dayOfWeekStr = "周一";
                break;
            case 2:
                this.dayOfWeekStr = "周二";
                break;
            case 3:
                this.dayOfWeekStr = "周三";
                break;
            case 4:
                this.dayOfWeekStr = "周四";
                break;
            case 5:
                this.dayOfWeekStr = "周五";
                break;
            case 6:
                this.dayOfWeekStr = "周六";
                break;
            case 7:
                this.dayOfWeekStr = "周日";
                break;
            default:
                this.dayOfWeekStr = "未知";
                break;
        }
    }

    public void setStatus(Integer status) {
        // 0普通工作日 1周末 2需要补班的工作日 3法定节假日
        super.setStatus(status);
        switch (status) {
            case 0:
                this.remark = "工作日";
                break;
            case 1:
                this.remark = "周末";
                break;
            case 2:
                this.remark = "补班";
                break;
            case 3:
                this.remark = "节假日";
                break;
        }
    }
}
