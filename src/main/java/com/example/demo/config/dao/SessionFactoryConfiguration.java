package com.example.demo.config.dao;

import java.io.IOException;
import javax.sql.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class SessionFactoryConfiguration
{

	@Value("${mybatis_config_file}")
	private String mybatisConfigFilePath;

	@Value("${mapper_path}")
	private String mapperPath;

	@Value("${entity_package}")
	private String entityPackage;

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Bean(name="sqlSessionFactory")
	public SqlSessionFactoryBean createSqlSessionFactoryBean() throws IOException
	{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

		//设置mybatis configuration的扫描路径mybatis-config.xml
		sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(mybatisConfigFilePath));

		// CLASSPATH_ALL_URL_PREFIX -> "classpath*:"
		PathMatchingResourcePatternResolver resolver = new  PathMatchingResourcePatternResolver();

		String packageSearchPath = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mapperPath;

		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(packageSearchPath));

		sqlSessionFactoryBean.setDataSource(dataSource);

		//设置实体类的扫描路径
		sqlSessionFactoryBean.setTypeAliasesPackage(entityPackage);

		return sqlSessionFactoryBean;

	}
}
