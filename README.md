# Nutz-Code-Generator
#### 介绍
本项目是基于NutzDao的代码生成器

`注：仅支持单表CRUD，未支持外键关联`

#### 使用说明

[访问地址](http://localhost:8084/generate "index")


##### 配置数据源src/main/resources/application.properties
```properties
server.port=8084
spring.datasource.username=greescmdb2
spring.datasource.password=passwd
spring.datasource.url=jdbc:oracle:thin:@//10.2.25.123:1521/ggrdattest
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.thymeleaf.prefix=classpath:/views/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false

```
##### 配置代码生成器src/main/resources/generator.yml
```properties
# 代码生成
gen: 
  author: gscm
  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool
  packageName: com.gree.nutz
  # 自动去除表前缀，默认是false
  autoRemovePre: false
  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）
  tablePrefix: sys_
```