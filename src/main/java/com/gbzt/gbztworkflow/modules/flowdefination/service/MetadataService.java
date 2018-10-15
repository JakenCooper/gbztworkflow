package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.base.TreeNode;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.model.FlowMetadata;
import com.gbzt.gbztworkflow.utils.LogUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:app.properties")
public class MetadataService extends BaseService {

    @Autowired
    private Environment env;

    private Logger logger = Logger.getLogger(MetadataService.class);
    private static String LOGGER_TYPE_PREFIX = "MetadataService,";

    private Connection getConnection(String driver,String url,String userName,String password){
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url,userName,password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void closeConnection(Connection connection){
        if(connection == null){
            return ;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ExecResult<FlowMetadata> getTables(FlowMetadata metadata){
        String loggerType = LOGGER_TYPE_PREFIX+"getTables";

        String driver,prefix,suffix = "";
        StringBuffer speBuffer = new StringBuffer();
        speBuffer.append("metadata_tables:");
        if("mysql".equals(metadata.getBussDbType())){
            speBuffer.append("mysql").append(",");
            driver = env.getProperty("jdbc.mysql.buss.driver");
            prefix = env.getProperty("jdbc.mysql.buss.url.prefix");
            suffix = env.getProperty("jdbc.mysql.buss.url.suffix");
        }else{
            speBuffer.append("oscar").append(",");
            driver = env.getProperty("jdbc.oscar.buss.driver");
            prefix = env.getProperty("jdbc.oscar.buss.url.prefix");
            suffix = env.getProperty("jdbc.oscar.buss.url.suffix");
        }
        speBuffer.append(metadata.getBussDbHost()).append(",").append(metadata.getBussDbPort()).append(",")
                .append(metadata.getBussDbName());
        String url = prefix+metadata.getBussDbHost()+":"+metadata.getBussDbPort()+"/"+metadata.getBussDbName()
                +suffix;
        if(SimpleCache.inCache(speBuffer.toString())){
            metadata.setDbTables((List<String>)SimpleCache.getFromCache(speBuffer.toString()));
            return buildResult(true,"",metadata);
        }
        Connection connection = null;
        try {
            connection = getConnection(driver,url,metadata.getBussDbUserName(),metadata.getBussDbUserPwd());
            if(connection == null){
                logger.warn(LogUtils.getMessage(loggerType,"获取元数据失败: "+metadata.toString()));
                return buildResult(true,"jdbc连接为空，请检查参数",null);
            }
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet resultSet = dmd.getTables(null, null, null, new String[] { "TABLE" });
            List<String> dbTables = new ArrayList<String>();
            while (resultSet.next()) {
                String table = resultSet.getString("TABLE_NAME");
                dbTables.add(table);
            }
            metadata.setDbTables(dbTables);
            // 特征值，用作简单缓存中的key
            SimpleCache.putIntoCache(speBuffer.toString(),dbTables);
            return buildResult(true,"",metadata);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(LogUtils.getMessage(loggerType,"查询元数据失败 "+e.getMessage()));
            return buildResult(true,"元数据获取后台异常，请检查日志",null);
        } finally{
            closeConnection(connection);
        }
    }

    public ExecResult<FlowMetadata> getColumnsByTable(FlowMetadata metadata){
        String loggerType = LOGGER_TYPE_PREFIX+"getColumnsByTable";

        if(StringUtils.isBlank(metadata.getBussTableName())){
            return buildResult(true,"没有选择业务表",null);
        }
        String driver,prefix,suffix = "";
        StringBuffer speBuffer = new StringBuffer();
        StringBuffer colBuffer =new StringBuffer();
        speBuffer.append("metadata_tables:");
        if("mysql".equals(metadata.getBussDbType())){
            speBuffer.append("mysql").append(",");
            driver = env.getProperty("jdbc.mysql.buss.driver");
            prefix = env.getProperty("jdbc.mysql.buss.url.prefix");
            suffix = env.getProperty("jdbc.mysql.buss.url.suffix");
        }else{
            speBuffer.append("oscar").append(",");
            driver = env.getProperty("jdbc.oscar.buss.driver");
            prefix = env.getProperty("jdbc.oscar.buss.url.prefix");
            suffix = env.getProperty("jdbc.oscar.buss.url.suffix");
        }
        speBuffer.append(metadata.getBussDbHost()).append(",").append(metadata.getBussDbPort()).append(",")
                .append(metadata.getBussDbName());
        String orispecification = speBuffer.toString();
        colBuffer.append(orispecification.replace("metadata_tables","metadata_columns")).append(",").append(metadata.getBussTableName());
        String url = prefix+metadata.getBussDbHost()+":"+metadata.getBussDbPort()+"/"+metadata.getBussDbName()
                +suffix;
        if(SimpleCache.inCache(speBuffer.toString()) && SimpleCache.inCache(colBuffer.toString())){
            metadata.setDbTables((List<String>)SimpleCache.getFromCache(speBuffer.toString()));
            metadata.setDbTableColumns((List<String>)SimpleCache.getFromCache(colBuffer.toString()));
            return buildResult(true,"",metadata);
        }
        Connection connection = null;
        try {
            connection = getConnection(driver,url,metadata.getBussDbUserName(),metadata.getBussDbUserPwd());
            if(connection == null){
                logger.warn(LogUtils.getMessage(loggerType,"获取元数据失败: "+metadata.toString()));
                return buildResult(true,"jdbc连接为空，请检查参数",null);
            }
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet resultSet = dmd.getTables(null, null, null, new String[] { "TABLE" });
            List<String> dbTables = new ArrayList<String>();
            List<String> tableColumns = new ArrayList<String>();
            while (resultSet.next()) {
                String table = resultSet.getString("TABLE_NAME");
                dbTables.add(table);
                if(table.equals(metadata.getBussTableName())){
                    String fetchsql = "select * from "+table+" limit 0,1";
                    ResultSet rs = connection.prepareStatement(fetchsql).executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();
                    for(int index=1;index<=columnCount;index++) {
                        String columnName = rsmd.getColumnName(index);
                        tableColumns.add(columnName);
                    }
                }
            }
            metadata.setDbTableColumns(tableColumns);
            SimpleCache.putIntoCache(colBuffer.toString(),tableColumns);
            if(metadata.getDbTables() == null || metadata.getDbTables().size() == 0){
                metadata.setDbTables(dbTables);
                // 特征值，用作简单缓存中的key
                SimpleCache.putIntoCache(speBuffer.toString(),dbTables);
            }
            return buildResult(true,"",metadata);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(LogUtils.getMessage(loggerType,"查询元数据失败 "+e.getMessage()));
            return buildResult(true,"元数据获取后台异常，请检查日志",null);
        } finally{
            closeConnection(connection);
        }
    }


    /***
     * 构建单层树状结构，因为界面使用tab控件作为级联方式，所以只需要单层结构
     * */
    public List<TreeNode> buildTree(String rootName,List<String> nodeNames){
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        Integer currentId = 1;
        TreeNode rootNode = new TreeNode();
        rootNode.setText(rootName);
        rootNode.setNodeid(String.valueOf(currentId++));
        for(String nodeName : nodeNames){
            TreeNode node = new TreeNode(nodeName);
            node.setNodeid(String.valueOf(currentId++));
            rootNode.getNodes().add(node);
        }
        nodes.add(rootNode);
        return nodes;
    }



 }
