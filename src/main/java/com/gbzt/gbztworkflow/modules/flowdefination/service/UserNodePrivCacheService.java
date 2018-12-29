package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.UserNodePrivDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.UserNodePriv;
import com.gbzt.gbztworkflow.modules.flowruntime.model.UserTreeInfo;
import com.gbzt.gbztworkflow.modules.redis.service.JedisService;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@PropertySource("classpath:app.properties")
public class UserNodePrivCacheService extends MetadataService {

    @Autowired
    private Environment env;

    @Autowired
    private JedisService jedisService;

    private Logger logger= Logger.getLogger(UserNodePrivCacheService.class);

    private class UserOffice{
        private String id;
        private String name;
        private String loginName;
        private String officeId;
        private String officeName;
        private String officeParentId;

        public UserOffice(String id, String name, String loginName, String officeId, String officeName,String officeParentId) {
            this.id = id;
            this.name = name;
            this.loginName = loginName;
            this.officeId = officeId;
            this.officeName = officeName;
            this.officeParentId =officeParentId;
        }
    }

    
    public ExecResult getAllUserInfobynodeid(String nodeid){
       UserNodePriv userNodePriv = jedisService.findUserNodePrivByNodeId(nodeid);
       if(userNodePriv ==null ){
           userNodePriv = new UserNodePriv();
           userNodePriv.setLoginName("noone");
       }
       return buildResult(true,"",userNodePriv);
    }
    

    // 流程引擎前端调用的方法,这里是获取到所有的节点权限
    public ExecResult getAllUserInfo(Node node){
        Flow flowInst = jedisService.findFlowByIdOrName(node.getFlowId(),null);
        System.out.println("ready to read metadata ,flowname ====== "+flowInst.getFlowName());
        String driver,prefix,suffix = "";
        if(StringUtils.isBlank(flowInst.getBussDbHost())){
            return buildResult(true,"",new ArrayList<UserTreeInfo>());
        }
        if("mysql".equals(flowInst.getBussDbType())){
            driver = env.getProperty("jdbc.mysql.buss.driver");
            prefix = env.getProperty("jdbc.mysql.buss.url.prefix");
            suffix = env.getProperty("jdbc.mysql.buss.url.suffix");
        }else{
            driver = env.getProperty("jdbc.oscar.buss.driver");
            prefix = env.getProperty("jdbc.oscar.buss.url.prefix");
            suffix = env.getProperty("jdbc.oscar.buss.url.suffix");
        }
        String url = prefix+flowInst.getBussDbHost()+":"+flowInst.getBussDbPort()+"/"+flowInst.getBussDbName()
                +suffix;
        Connection connection = null;
        String partyName = env.getProperty("buss.officerootname");
        List<UserOffice> userOffices = new ArrayList<UserOffice>();
        Set<String> officeSet =new HashSet<String>();
        try {
            connection = getConnection(driver,url,flowInst.getBussDbUserName(),flowInst.getBussDbUserPwd());
            if(connection == null){
                return buildResult(false,"connection is null",null);
            }
            String rootId = "";
            PreparedStatement rootPs = connection.prepareStatement("select id from sys_office where name='"+partyName+"'");
            ResultSet rootRs = rootPs.executeQuery();
            while(rootRs.next()){
                rootId = rootRs.getString(1);
                break;
            }

            PreparedStatement ps = connection.prepareStatement("select a.id,a.name,a.login_name,a.office_id,b.name,b.parent_id\n" +
                    "\tfrom sys_user a LEFT JOIN sys_office b on a.office_id = b.id\n" +
                    "\twhere a.del_flag!='1' and b.parent_id!='0' and b.parent_id = (select id from sys_office where name='"+partyName+"')\n" +
                    "\torder by b.code  ");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                UserOffice userOffice = new UserOffice(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),
                        resultSet.getString(5),resultSet.getString(6));
                userOffices.add(userOffice);
            }
            List<UserTreeInfo> infos = new ArrayList<UserTreeInfo>();
            UserTreeInfo root = new UserTreeInfo(rootId,"-1",partyName,"invalid",true);
            Set<String> tagSet = new HashSet<String>();
            infos.add(root);
            // 将所有人员查询出来之后按照部门进行分组（因为在sql中已经提前进行排序，所以此处直接按照部门进行分组会非常简单）
            // 如果tagSet没有当前人员对应的部门，则将部门添加到tagSet中，并且为这个部门添加第一个人员（即当前人员），否则直接取最后一个UserTreeInfo，然后把自己放进去
            for(UserOffice userOffice:userOffices){
                if(tagSet.contains(userOffice.officeId)){
                    UserTreeInfo parent = infos.get(infos.size()-1);
                    parent.getChildren().add(new UserTreeInfo(userOffice.id,parent.getId(),userOffice.name,userOffice.loginName));
                    continue;
                }
//                UserTreeInfo parentparent =  infos.get(infos.size()-1);
                UserTreeInfo parentparent = null;
                for(UserTreeInfo tempInfo : infos){
                    if(tempInfo.getId().equals(userOffice.officeParentId)){
                        parentparent = tempInfo;
                    }
                }
                UserTreeInfo parent = new UserTreeInfo(userOffice.officeId,parentparent.getId(),
                        userOffice.officeName,"invalid");
                parent.getChildren().add(new UserTreeInfo(userOffice.id,parent.getId(),userOffice.name,userOffice.loginName));
                tagSet.add(userOffice.officeId);
                infos.add(parent);
            }
            return buildResult(true,"",infos);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            super.closeConnection(connection);
        }
        return null;
    }

    public UserNodePriv findUseroneNodePrivsByNodeId(String nodeId){
        return jedisService.findUserNodePrivByNodeId(nodeId);
    }

    /***
     * @author 小白
     * oa系统接口调用方法，通过loginname列表获取人员树（但是该方法其实对于接口访问的情形下不应该被调用——应该只查询缓存即可）
     */
    public List<UserTreeInfo> getAllUserInfo(UserNodePriv userNodePriv){
        Flow flowInst = jedisService.findFlowByIdOrName(userNodePriv.getFlowId() , null);
        String driver,prefix,suffix = "";
        if(StringUtils.isBlank(flowInst.getBussDbHost())){
            return new ArrayList<UserTreeInfo>();
        }
        if("mysql".equals(flowInst.getBussDbType())){
            driver = env.getProperty("jdbc.mysql.buss.driver");
            prefix = env.getProperty("jdbc.mysql.buss.url.prefix");
            suffix = env.getProperty("jdbc.mysql.buss.url.suffix");
        }else{
            driver = env.getProperty("jdbc.oscar.buss.driver");
            prefix = env.getProperty("jdbc.oscar.buss.url.prefix");
            suffix = env.getProperty("jdbc.oscar.buss.url.suffix");
        }
        String url = prefix+flowInst.getBussDbHost()+":"+flowInst.getBussDbPort()+"/"+flowInst.getBussDbName()
                +suffix;
        Connection connection = null;
        String partyName = env.getProperty("buss.officerootname");
        List<UserOffice> userOffices = new ArrayList<UserOffice>();
        try {
            connection = getConnection(driver,url,flowInst.getBussDbUserName(),flowInst.getBussDbUserPwd());
            if(connection == null){
                return new ArrayList<UserTreeInfo>();
            }
            String rootId = "";
            PreparedStatement rootPs = connection.prepareStatement("select id from sys_office where name='"+partyName+"'");
            ResultSet rootRs = rootPs.executeQuery();
            while(rootRs.next()){
                rootId = rootRs.getString(1);
                break;
            }
            StringBuffer inBuffer =new StringBuffer("(");
            if(userNodePriv.getLoginNames() != null && userNodePriv.getLoginNames().size() > 0) {
                for (String loginName : userNodePriv.getLoginNames()) {
                    inBuffer.append("'").append(loginName).append("'").append(",");
                }
                inBuffer = new StringBuffer(inBuffer.substring(0,inBuffer.length()-1));
            }
            inBuffer.append(")");

            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("select a.id,a.name,a.login_name,a.office_id,b.name,b.parent_id\n" +
                    "\tfrom sys_user a LEFT JOIN sys_office b on a.office_id = b.id\n" +
                    "\twhere a.del_flag!='1' and  b.parent_id!='0' and b.parent_id = (select id from sys_office where name='"+partyName+"')\n");
            if(inBuffer.toString().equals("()")){
                return new ArrayList<UserTreeInfo>();
            }else{
                sqlBuffer.append("\tand a.login_name in "+inBuffer.toString()+" order by b.code  ");
            }


            PreparedStatement ps = connection.prepareStatement(sqlBuffer.toString());
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                UserOffice userOffice = new UserOffice(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),
                        resultSet.getString(5),resultSet.getString(6));
                userOffices.add(userOffice);
            }
            List<UserTreeInfo> infos = new ArrayList<UserTreeInfo>();
            UserTreeInfo root = new UserTreeInfo(rootId,"-1",partyName,"invalid",true);
            root.setNodeType(userNodePriv.getNodeType());
            Set<String> tagSet = new HashSet<String>();
            infos.add(root);
            //post handle..

            //Logic1: original user data is sorted by department code,so can always put latest user into latest UserTreeInfo
            //Logic2: return department tree at last
            for(UserOffice userOffice:userOffices){
                if(tagSet.contains(userOffice.officeId)){
                    UserTreeInfo parent = infos.get(infos.size()-1);
                    parent.getChildren().add(new UserTreeInfo(userOffice.id,parent.getId(),userOffice.name,userOffice.loginName));
                    continue;
                }
//                UserTreeInfo parentparent =  infos.get(infos.size()-1);
                UserTreeInfo parentparent = null;
                for(UserTreeInfo tempInfo : infos){
                    if(tempInfo.getId().equals(userOffice.officeParentId)){
                        parentparent = tempInfo;
                    }
                }
                UserTreeInfo parent = new UserTreeInfo(userOffice.officeId,parentparent.getId(),
                        userOffice.officeName,"invalid");
                parent.getChildren().add(new UserTreeInfo(userOffice.id,parent.getId(),userOffice.name,userOffice.loginName));
                tagSet.add(userOffice.officeId);
                infos.add(parent);
            }
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            super.closeConnection(connection);
        }
    }


    public ExecResult saveUserNodePriv(UserNodePriv userNodePriv){

        String cacheKey = JedisService.CACHE_KEY_PREFIX_USER_NODE_PRIV + userNodePriv.getNodeId();

        // modify by zhangys @20181211 如果界面没有配置任何人员，此处在删除现有节点用户权限之后，直接返回页面
        if(userNodePriv.getLoginNames() == null || userNodePriv.getLoginNames().size() == 0){
            jedisService.delUserNodePrivs(userNodePriv);
            jedisService.setCachedString(cacheKey,null);
            return buildResult(true,"","success");
        }

        List<UserNodePriv> privs = new ArrayList<UserNodePriv>();
        //add by ym 遍历所有logainname
        UserNodePriv priv = new UserNodePriv();
        priv.setId(CommonUtils.genUUid());
        priv.genBaseVariables();
        priv.setFlowId(userNodePriv.getFlowId());
        priv.setNodeId(userNodePriv.getNodeId());
        priv.setNodeType(userNodePriv.getNodeType());
        //priv.setLoginNames(userNodePriv.getLoginNames());
        String loginname ="";
        if(userNodePriv.getLoginNames().size()==0){
            
        }else {
            for(String loginName : userNodePriv.getLoginNames()) {
                loginname += loginName + ",";

            }
            loginname = loginname.substring(0, loginname.length() - 1);
        }
        priv.setLoginName(loginname);

        jedisService.saveUserNodePrivs(priv);
        // 【！】构造时使用的是原始的UserNodePriv，因为查询方法中并没有对loginname做特殊处理
        List<UserTreeInfo> infos = getAllUserInfo(userNodePriv);
        if(infos != null && infos.size() > 0){
            jedisService.setCachedString(cacheKey,JSONArray.fromObject(infos).toString());
        }

        return buildResult(true,"","success");
    }

    public List<UserTreeInfo> getFromCache(JedisService jedisService , String nodeId){
        String cacheKey = JedisService.CACHE_KEY_PREFIX_USER_NODE_PRIV + nodeId;
        String str = jedisService.findCachedString(cacheKey);
        if(StringUtils.isBlank(str)){
            logger.warn("cache str empty,nodeid === "+nodeId);
            return new ArrayList();
        }
        JSONObject jsonObject = JSONObject.fromObject(str);
        List<UserTreeInfo> userTreeInfos = (List<UserTreeInfo>)JSONObject.toBean(jsonObject,List.class);
        return userTreeInfos;
    }



}
