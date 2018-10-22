package com.gbzt.gbztworkflow.config;

import com.gbzt.gbztworkflow.modules.flowruntime.service.IRuntimeService;
import com.gbzt.gbztworkflow.modules.flowruntime.service.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@Import(RepoConfig.class)
@ComponentScan({"com.gbzt.gbztworkflow.modules",
        "com.gbzt.gbztworkflow.consts"})
@EnableTransactionManagement(proxyTargetClass = true)
public class RootConfig {

    @Bean(name="transactionManager")
    @Qualifier("jtm")
    public JpaTransactionManager getJpaTransactionManager(EntityManagerFactory emf){
        JpaTransactionManager jtm = new JpaTransactionManager();
        jtm.setEntityManagerFactory(emf);
        return jtm;
    }

    @Bean(name="dataSourceTransactionManager")
    @Qualifier("dtm")
    public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource){
        DataSourceTransactionManager dtm = new DataSourceTransactionManager();
        dtm.setDataSource(dataSource);
        return dtm;
    }


    @Bean(name="flowService")
    @Autowired
    public HttpInvokerServiceExporter getHttpInvokerServiceExporter(RuntimeService runtimeService){
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService(runtimeService);
        exporter.setServiceInterface(IRuntimeService.class);
        return exporter;
    }
}
