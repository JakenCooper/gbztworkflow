package com.gbzt.gbztworkflow.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.Hibernate;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableJpaRepositories(basePackages = "com.gbzt.gbztworkflow.modules")
public class RepoConfig {

    @Configuration
    @PropertySource("classpath:app.properties")
    static class RepoConfigInner {

        @Autowired
        private Environment env;

        @Bean(name = "dataSource")
        public DataSource getDefaultDataSource() {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(env.getProperty("jdbc.driver"));
            dataSource.setUrl(env.getProperty("jdbc.url"));
            dataSource.setUsername(env.getProperty("jdbc.username"));
            dataSource.setPassword(env.getProperty("jdbc.password"));

            /**
             * 配置初始化大小、最小、最大
             * */
            dataSource.setInitialSize(20);
            dataSource.setMinIdle(50);
            dataSource.setMaxActive(200);

            /**
             * 配置获取连接等待超时的时间
             * */
            dataSource.setMaxWait(60000);
            /**
             * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
             * */
            dataSource.setTimeBetweenEvictionRunsMillis(60000);
            dataSource.setMinEvictableIdleTimeMillis(300000);

            /**
             * 配置一个连接在池中最小生存的时间，单位是毫秒
             * */
            dataSource.setValidationQuery("select 'x' from DUAL");
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            return dataSource;
        }

        @Bean(name = "vendorAdapter")
        public JpaVendorAdapter getJpaVendorAdaptor() {
            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            vendorAdapter.setGenerateDdl(true);
            vendorAdapter.setShowSql(env.getProperty("jdbc.showsql").equals("true") ? true : false);
            vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
            return vendorAdapter;
        }

        @Bean(name = "entityManagerFactory")
        @Autowired
        public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(DataSource dataSource,
                                                                              JpaVendorAdapter vendorAdapter) {
            LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
            emf.setDataSource(dataSource);
            emf.setJpaVendorAdapter(vendorAdapter);
            emf.setPackagesToScan("com.gbzt.gbztworkflow.modules");
            return emf;
        }


        @Bean(name="sqlSessionFactory")
        @Autowired
        public SqlSessionFactoryBean getSqlSessionFactory(DataSource dataSource){
            SqlSessionFactoryBean sfb = new SqlSessionFactoryBean();
            sfb.setDataSource(dataSource);

            PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
            Resource[] mapperResources = null;
            try {
                mapperResources = resourceLoader.getResources("classpath:/mybatis/*.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Resource configResource= null;
            try {
                configResource = resourceLoader.getResource("mybatis-config.xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
            sfb.setTypeAliasesPackage("com.gbzt.gbztworkflow.modules.workflowengine.pojo");
            sfb.setConfigLocation(configResource);
            sfb.setMapperLocations(mapperResources);
            return sfb;
        }
    }

    @Bean
    public MapperScannerConfigurer getMapperScannerConfigurer(){
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setBasePackage("com.gbzt.gbztworkflow.modules.workflowengine.mybatisdao");
        return msc;
    }
}
