package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.base.TreeNode;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.ConnectConfig;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.model.FlowMetadata;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.DateUtils;
import com.gbzt.gbztworkflow.utils.LogUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@PropertySource("classpath:app.properties")
public class MetadataService extends BaseService {

    public static final String ADVISE_BUSS_TBL_SUFFIX = "_advise";

    @Autowired
    private Environment env;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private FlowDao flowDao;

    private Logger logger = Logger.getLogger(MetadataService.class);
    private static String LOGGER_TYPE_PREFIX = "MetadataService,";

    protected Connection getConnection(String driver,String url,String userName,String password){
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url,userName,password);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected void closeConnection(Connection connection){
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
        if(SimpleCache.inCache(speBuffer.toString()) && !metadata.getRefresh()){
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
            String schema = null;
            if(!"mysql".equals(metadata.getBussDbType())){
                schema = "PUBLIC";
            }
            ResultSet resultSet = dmd.getTables(null, schema, null, new String[] { "TABLE" });
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
//            metadata.setDbTables((List<String>)SimpleCache.getFromCache(speBuffer.toString()));
//            metadata.setDbTableColumns((List<String>)SimpleCache.getFromCache(colBuffer.toString()));
//            return buildResult(true,"",metadata);
        }
        Connection connection = null;
        try {
            connection = getConnection(driver,url,metadata.getBussDbUserName(),metadata.getBussDbUserPwd());
            if(connection == null){
                logger.warn(LogUtils.getMessage(loggerType,"获取元数据失败: "+metadata.toString()));
                return buildResult(true,"jdbc连接为空，请检查参数",null);
            }
            DatabaseMetaData dmd = connection.getMetaData();
            String schema = null;
            if(!"mysql".equals(metadata.getBussDbType())){
                schema = "PUBLIC";
            }
            ResultSet resultSet = dmd.getTables(null, schema, null, new String[] { "TABLE" });
            List<String> dbTables = new ArrayList<String>();
            List<String> tableColumns = new ArrayList<String>();
            while (resultSet.next()) {
                String table = resultSet.getString("TABLE_NAME");
                dbTables.add(table);
                /*if(table.equals(metadata.getBussTableName())){
                    String fetchsql = "select * from "+table+" limit 0,1";
                    ResultSet rs = connection.prepareStatement(fetchsql).executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();
                    for(int index=1;index<=columnCount;index++) {
                        String columnName = rsmd.getColumnName(index);
                        String columnLable = rsmd.getColumnLabel(index);
                        System.out.println("label ========== "+columnLable);
                        tableColumns.add(columnName);
                    }
                }*/
                if(table.equals(metadata.getBussTableName())){
                    ResultSet colResultSet = dmd.getColumns(null,schema,table.toUpperCase(),"%");
                    while(colResultSet.next()){
                        String tmpColumnName = colResultSet.getString("COLUMN_NAME");
                        String remark = colResultSet.getString("REMARKS");
                        tableColumns.add(tmpColumnName);
                        System.out.println("remark ==================== "+remark);
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
            TreeNode node = new TreeNode("&nbsp;&nbsp;&nbsp;"+nodeName);
            node.setNodeid(String.valueOf(currentId++));
            node.setIcon("glyphicon glyphicon-list-alt");
            node.setSelectedIcon("glyphicon glyphicon-list-alt");
            rootNode.getNodes().add(node);
        }
        nodes.add(rootNode);
        return nodes;
    }



    public ExecResult<String> createBussTable(FlowMetadata metadata){
        String loggerType = LOGGER_TYPE_PREFIX+"createTable";

        if(metadata.getBussColumns().size() != metadata.getBussColumnsType().size()){
            return buildResult(false,"字段数量和类型数量不匹配",null);
        }

        String driver,prefix,suffix = "";
        StringBuffer speBuffer = new StringBuffer();
        speBuffer.append("metadata_tables:");

        ConnectConfig connectConfig = connectConfigService.list().get(0);
        metadata.setBussDbType(connectConfig.getDataBaseType());

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

        Connection connection = null;
        try {
            connection = getConnection(driver,url,metadata.getBussDbUserName(),metadata.getBussDbUserPwd());
            if(connection == null){
                logger.warn(LogUtils.getMessage(loggerType,"jdb连接失败 "+metadata.toString()));
                return buildResult(false,"jdbc连接为空，请检查参数",null);
            }

            boolean tableExists = false;
            DatabaseMetaData dmd = connection.getMetaData();
            String schema = null;
            if(!"mysql".equals(metadata.getBussDbType())){
                schema = "PUBLIC";
            }
            ResultSet resultSet = dmd.getTables(null, schema, null, new String[] { "TABLE" });
            while (resultSet.next()) {
                String table = resultSet.getString("TABLE_NAME");
                if(table.equals(metadata.getBussTableName())){
                    return buildResult(false,"表名儿重复，需要重新选择",null);
                }
                if(table.equals(metadata.getAttachBussTableName())){
                    return buildResult(false,"附件表名儿重复，请重新填写",null);
                }
            }

            //对于神通数据库必须将所有表名和列名转换成大写
            if(!"mysql".equals(metadata.getBussDbType())){
                metadata.setBussTableName(metadata.getBussTableName().toUpperCase());
                List<String> upperCaseColumns = new ArrayList<String>();
                for(String column : metadata.getBussColumns()){
                    upperCaseColumns.add(column.toUpperCase());
                }
                metadata.setBussColumns(upperCaseColumns);
            }

            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("create table ");
            //对于神通数据库必须创建在public表空间下
            if(schema != null){
                sqlBuffer.append("PUBLIC.");
            }
            sqlBuffer.append(metadata.getBussTableName()).append(" (");
            for(int i = 0 ; i<metadata.getBussColumns().size() ; i ++){
                String column = metadata.getBussColumns().get(i);
                String columnType = metadata.getBussColumnsType().get(i);
                sqlBuffer.append(column).append(" ").append(columnType).append(",");
            }
            sqlBuffer = new StringBuffer(sqlBuffer.substring(0,sqlBuffer.length()-1));
            sqlBuffer.append(" );");
            StringBuffer sqlAtttachBuffer=new StringBuffer();

            if(StringUtils.isNotBlank(metadata.getAttachBussTableName())){
                sqlAtttachBuffer.append("create table ");
                if(schema != null){
                    sqlAtttachBuffer.append("PUBLIC.");
                }
                sqlAtttachBuffer.append(metadata.getAttachBussTableName()).append(" (");
                for(int i=0;i<metadata.getAttachBussColumns().size();i++){
                    String column=metadata.getAttachBussColumns().get(i);
                    String columnType=metadata.getAttachBussColumnsType().get(i);
                    sqlAtttachBuffer.append(column).append(" ").append(columnType).append(",");
                }
                sqlAtttachBuffer= new StringBuffer(sqlAtttachBuffer.substring(0,sqlAtttachBuffer.length()-1));
                sqlAtttachBuffer.append(" );");
                PreparedStatement attachPs=connection.prepareStatement(sqlAtttachBuffer.toString());
                attachPs.executeUpdate();
            }

            PreparedStatement ps = connection.prepareStatement(sqlBuffer.toString());
            ps.executeUpdate();

            StringBuffer adviseSqlBuffer = new StringBuffer();
            adviseSqlBuffer.append("create table ");
            if(schema != null){
                adviseSqlBuffer.append("PUBLIC.");
            }
            adviseSqlBuffer.append(metadata.getBussTableName()).append(ADVISE_BUSS_TBL_SUFFIX)
                    .append(" (id varchar(36) primary key,content varchar(2000),advise_time timestamp,advise_user varchar(100)," +
                            "advise_user_zh varchar(100),advise_type varchar(20),proc_inst_id varchar(50),task_id varchar(50))");
            PreparedStatement advisePs = connection.prepareStatement(adviseSqlBuffer.toString());
            advisePs.executeUpdate();


            return buildResult(true,"","success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LogUtils.getMessage(loggerType,"业务表创建失败 "+e.getMessage()));
            return buildResult(false,"业务表创建失败，请检查日志",null);
        } finally{
            closeConnection(connection);
        }
    }


    public String addAdviseSummary(String flowId,String attrName){
        String loggerType = LOGGER_TYPE_PREFIX+"addAdviseSummary";


        FlowMetadata metadata = new FlowMetadata();
        Flow currentFlow = flowDao.findOne(flowId);
        if(currentFlow == null){
            logger.warn(LogUtils.getMessage(loggerType,"无法找到对应流程，参数流程id为=== "+flowId));
            return "-2";
        }
        String moduleName = currentFlow.getModuleName();
        String tableName = currentFlow.getBussTableName();
        metadata.setFlowName(currentFlow.getFlowName());
        metadata.setBussDbType(currentFlow.getBussDbType());
        metadata.setBussDbHost(currentFlow.getBussDbHost());
        metadata.setBussDbPort(currentFlow.getBussDbPort());
        metadata.setBussDbName(currentFlow.getBussDbName());
        metadata.setBussDbUserName(currentFlow.getBussDbUserName());
        metadata.setBussDbUserPwd(currentFlow.getBussDbUserPwd());
        String driver,prefix,suffix = "";

        ConnectConfig connectConfig = connectConfigService.list().get(0);
        metadata.setBussDbType(connectConfig.getDataBaseType());

        if("mysql".equals(metadata.getBussDbType())){
            driver = env.getProperty("jdbc.mysql.buss.driver");
            prefix = env.getProperty("jdbc.mysql.buss.url.prefix");
            suffix = env.getProperty("jdbc.mysql.buss.url.suffix");
        }else{
            driver = env.getProperty("jdbc.oscar.buss.driver");
            prefix = env.getProperty("jdbc.oscar.buss.url.prefix");
            suffix = env.getProperty("jdbc.oscar.buss.url.suffix");
        }
        String url = prefix+metadata.getBussDbHost()+":"+metadata.getBussDbPort()+"/"+metadata.getBussDbName()
                +suffix;

        Connection connection = null;
        try {
            connection = getConnection(driver,url,metadata.getBussDbUserName(),metadata.getBussDbUserPwd());
            if(connection == null){
                logger.warn(LogUtils.getMessage(loggerType,"jdb连接失败 "+metadata.toString()));
                return "-2";
            }


            StringBuffer checkBuffer = new StringBuffer();
            checkBuffer.append("select advise_type from advise_summary").append(" where module_name='").append(moduleName)
                    .append("' and attr_name='").append(attrName).append("'");
            PreparedStatement checkPreparedStatement = connection.prepareStatement(checkBuffer.toString());
            ResultSet checkResultSet = checkPreparedStatement.executeQuery();
            String currentAdviseType = "-1";
            while(checkResultSet.next()){
                currentAdviseType = checkResultSet.getString(1);
                break;
            }
            if(!currentAdviseType.equals("-1")){
                logger.warn("[addAdviseSummary] ==============> already has record for module:"+moduleName
                        +" and attr:"+attrName);
                return currentAdviseType;
            }

            StringBuffer fetchBuffer = new StringBuffer();
            fetchBuffer.append("select * from advise_summary where module_name ='").append(moduleName).append("'");
            PreparedStatement fetchPreparedStatement = connection.prepareStatement(fetchBuffer.toString());
            ResultSet fetchResultSet = fetchPreparedStatement.executeQuery();
            List<String> typeList = new ArrayList<String>();
            while(fetchResultSet.next()){
                String adviseType = fetchResultSet.getString("advise_type");
                typeList.add(adviseType);
            }

            Integer thisTypeValue = 1;
            if(typeList.size() > 0) {
                Collections.sort(typeList, new Comparator<String>() {
                    @Override
                    public int compare(String target1, String target2) {
                        if (Integer.parseInt(target1) > Integer.parseInt(target2)) {
                            return -1;
                        } else if (Integer.parseInt(target1) < Integer.parseInt(target2)) {
                            return 1;
                        }
                        return 0;
                    }
                });
                thisTypeValue = Integer.parseInt(typeList.get(0))+1;
            }

            String currentTime = DateUtils.formatDateTime(new Date());
            StringBuffer insertBuffer = new StringBuffer();
            insertBuffer.append("insert into advise_summary")
                    .append("(id,module_name,table_name,attr_name,advise_type,create_by,create_date,update_by,update_date,del_flag)").append(" values ")
                    .append("(").append("'"+CommonUtils.genUUid() +"'").append(",").append("'"+moduleName+"'").append(",")
                    .append("'"+tableName+"'").append(",").append("'"+attrName+"'").append(",")
                    .append("'"+String.valueOf(thisTypeValue)+"'").append(",").append("'admin'")
                    .append(",").append("'"+currentTime+"'").append(",").append("'admin'")
                    .append(",").append("'"+currentTime+"'").append(",").append("'0'")
                    .append(");");
            PreparedStatement insertPreparedStatement = connection.prepareStatement(insertBuffer.toString());
            insertPreparedStatement.executeUpdate();

            return String.valueOf(thisTypeValue);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LogUtils.getMessage(loggerType,"AdviseSummary 添加失败 "+e.getMessage()));
            return "-2";
        } finally{
            closeConnection(connection);
        }
    }

 }
