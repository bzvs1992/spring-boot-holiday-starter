<p align="center">
    <img src="https://git.bzvs.cn/logo.png" />
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Spring-boot-holiday-starter v1.0.0</h1>
<h4 align="center">一个轻量级节假日判断的小工具！</h4>
<p align="center">
	<a href="https://github.com/bzvs1992/spring-boot-holiday-stater"><img src="https://img.shields.io/github/stars/bzvs1992/spring-boot-holiday-stater?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/bzvs1992/spring-boot-holiday-stater/network/members"><img src="https://img.shields.io/github/forks/bzvs1992/spring-boot-holiday-stater?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/bzvs1992/spring-boot-holiday-stater/watchers"><img src="https://img.shields.io/github/watchers/bzvs1992/spring-boot-holiday-stater?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/bzvs1992/spring-boot-holiday-stater/issues"><img src="https://img.shields.io/github/issues/bzvs1992/spring-boot-holiday-stater.svg?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/bzvs1992/spring-boot-holiday-stater/blob/master/LICENSE"><img src="https://img.shields.io/github/license/bzvs1992/spring-boot-holiday-stater.svg?style=flat-square"></a>
</p>

---

## 前言

-  由于项目原因，服务器无网络访问权限，在一些场景下需要判断是否是节假日，所以写了一个工具类，方便使用。
-  开源不易，点个 star 鼓励一下吧！


## 介绍

**Spring-boot-holiday-starter** 一个轻量级节假日判断的小工具，主要解决：获取节假日数据 和 判断是否是节假日。
- 支持说明
  - spirngboot 2.7 +
  - jdk17 +

## 使用
-  yml 配置
```yaml
holiday:
  ## 节假日数据源方式获取配置：network 在线模式，通过百度日历接口获取； local 本地模式，通过本地JSON文件获取
  type: network
  ## 在线模式配置下，通过设置 years 来进行初始化缓存的日历数据
  years:
    - 2020
    - 2021
  ## 节假日数据源地址，优先获取系统路径，不存在情况下，再使用 resources 路径
  path: /config/holiday.json
```

- HolidayService 接口（用于 获取日历数据 及 节假日/工作日/补班/周末 判断）
  - getDate 方法： 获取日历数据
  - isHoliday 方法： 是否是节假日
  - isWorkDay 方法： 是否是工作日或补班
  - isWeekend 方法： 是否是周末

- HolidayFixService 接口 （用于修复日历数据及本地模式下更新JSON文件）
  - reset 方法： 数据重置并刷新
  - fix 方法：修复缓存数据
  - updateFile 方法： 更新JSON文件数据

注：JSON文件可通过 GenerateHolidayTest 进行生成, 示例可参考 HolidayTest

## 计划
### springboot
- [ ] 自动解析官方《节假日安排的通知》，提供多种在线解析方式
- [ ] 支持redis
- [ ] 支持mysql
- [ ] 支持minio
- [ ] 支持mongodb

### solon
- [ ] 支持solon
