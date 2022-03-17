## 使用 jasypt 加密配置信息
### 在 pom.xml 文件中引入 jasypt 依赖
```
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.4</version>
</dependency>
```
### 在插件配置中加入：
```
<plugin>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-maven-plugin</artifactId>
    <version>3.0.4</version>
</plugin>
```
### 在配置文件中加入加密时需要使用的密码
```
jasypt.encryptor.password=xxspace
```

### 在配置文件中输入加密的内容，用 DEC() 包裹的内容实现批量加密
```
datasource.password=DEC(xxspace.com)
```
### 使用  jasypt-maven-plugin 插件来给 DEC() 包裹的内容实现批量加密，终端执行：
```
mvn jasypt:encrypt -Djasypt.encryptor.password=xxspace
执行以后，配置文件中 DEC() 包裹的内容则自动变成加密信息
```

### 使用 ENC() 解密配置文件，查看原始信息
```
mvn jasypt:decrypt -Djasypt.encryptor.password=xxspace
执行完毕后会在控制台输出解密的结果
```
