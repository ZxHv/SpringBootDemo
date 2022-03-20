## Swagger静态文档的生成
### 在有些况下，我们只需要提供静态文档给对接方时，可以利用 Swagger2Markup 工具自动生成 Swagger 的静态文档，如：AsciiDoc、Markdown、Confluence
Github 项目主页：https://github.com/Swagger2Markup/swagger2markup
### 生成 AsciiDoc 文档
1. 添加相关依赖和仓库，仅临时调用，scope 设置为 test，这样这个依赖就不会打包到正常的运行环境中：
```
<dependencies>
    ...
    
    <dependency>
        <groupId>io.github.swagger2markup</groupId>
        <artifactId>swagger2markup</artifactId>
        <version>1.3.3</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>jcenter-releases</id>
        <name>jcenter</name>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
```
2. 编写一个单元测试用例来生成静态文档：
```
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DemoApplicationTests {

    @Test
    public void generateAsciiDocs() throws Exception {
        URL remoteSwaggerFile = new URL("http://localhost:8080/v2/api-docs");
        Path outputDirectory = Paths.get("src/docs/asciidoc/generated");

        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC) //指定了要输出的最终格式,还有 MARKDOWN 和 CONFLUENCE_MARKUP
                .build();

        Swagger2MarkupConverter.from(remoteSwaggerFile) //指定了生成静态部署文档的源头配置
                .withConfig(config)
                .build()
                .toFolder(outputDirectory); //指定最终生成文件的具体目录位置
    }
}
```
3. 执行上述代码后，即可在 src 下生成一个 docs 目录，生成相关的 AsciiDoc 文档   
4. 生成 AsciiDoc 文档后，可以将其转换成 HTML 文档，须引入一个 Maven 插件：
```
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>1.5.6</version>
    <configuration>
   	    <sourceDirectory>src/docs/asciidoc/generated</sourceDirectory>
   	    <outputDirectory>src/docs/asciidoc/html</outputDirectory>
   	    <backend>html</backend>
   	    <sourceHighlighter>coderay</sourceHighlighter>
   	    <attributes>
            <toc>left</toc>
  	    </attributes>
  	</configuration>
</plugin>
```
5. 在该项目或者该 module 的 maven 窗口中，找到 Plugins 的 asciidoctor 下的 asciidoctor:process-asciidoc 命令，双击执行即可生成 HTML 文件

### 生成 Markdown 和 Confluence 文档
+ 只需要修改 withMarkupLanguage 属性，指定为 MarkupLanguage.MARKDOWN 或者 MarkupLanguage.CONFLUENCE_MARKUP
+ 并修改 toFolder 属性，指定输出不同的目录，如 “src/docs/markdown/generated”
```
URL remoteSwaggerFile = new URL("http://localhost:8080/v2/api-docs");
//修改输出目录
Path outputDirectory = Paths.get("src/docs/markdown/generated");

//    输出 MARKDOWN 格式，或 MarkupLanguage.CONFLUENCE_MARKUP 格式
Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
    .withMarkupLanguage(MarkupLanguage.MARKDOWN)
    .build();

Swagger2MarkupConverter.from(remoteSwaggerFile)
    .withConfig(config)
    .build()
    .toFolder(outputDirectory);
```
