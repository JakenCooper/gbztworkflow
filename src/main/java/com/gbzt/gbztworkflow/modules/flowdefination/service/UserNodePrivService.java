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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Service
@PropertySource("classpath:app.properties")
public class UserNodePrivService extends MetadataService {

    @Autowired
    private FlowDao flowDao;
    @Autowired
    private Environment env;
    @Autowired
    private UserNodePrivDao userNodePrivDao;

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
       UserNodePriv userNodePriv=  userNodePrivDao.findUseroneNodePrivByNodeId(nodeid);
        if(userNodePriv ==null ){
            userNodePriv = new UserNodePriv();
            userNodePriv.setLoginName("noone");
        }
        return buildResult(true,"",userNodePriv);
    }
    
    
    public ExecResult getAllUserInfo(Node node){
        Flow flowInst = flowDao.findOne(node.getFlowId());
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
            //post handle..
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

  /*  public List<UserNodePriv> findUserNodePrivsByNodeId(String nodeId){
        return userNodePrivDao.findUserNodePrivByNodeId(nodeId);
    }*/
  
    public UserNodePriv findUseroneNodePrivsByNodeId(String nodeId){
        return userNodePrivDao.findUseroneNodePrivByNodeId(nodeId);
    }

    /***
     * @author 小白
     * 专供接口使用
     */
    public List<UserTreeInfo> getAllUserInfo(UserNodePriv userNodePriv){
        Flow flowInst = flowDao.findOne(userNodePriv.getFlowId());
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
        /**
         * add by ym
         * 需要优化， 这里 现在拿到的是一个list 因为库里存储了很多个相同的nodeid 一个nodeid 针对很多条用户数据
         * 优化： 储存的时候   一个nodeid 针对一条数据  login_name  处 用（usre1,user2,user3,user4)这样的 方式储存 
         * 接收时 不用处理list 只获取一个然后 用,获取所有login_mame
         */
        
        UserNodePriv oriPrivs = userNodePrivDao.findUseroneNodePrivByNodeId(userNodePriv.getNodeId());
        if(oriPrivs!=null && !"".equals(oriPrivs)){
           userNodePrivDao.delete(oriPrivs);
        }

        // modify by zhangys @20181211 如果界面没有配置任何人员，此处在删除现有节点用户权限之后，直接返回页面
        if(userNodePriv.getLoginNames() == null || userNodePriv.getLoginNames().size() == 0){
            return buildResult(true,"","success");
        }
    
        /*
       modify by ym   优化：List 转为单条数据
      List<UserNodePriv> oriPrivs = userNodePrivDao.findUserNodePrivByNodeId(userNodePriv.getNodeId());
        if(oriPrivs!=null && oriPrivs.size() > 0){
            userNodePrivDao.delete(oriPrivs);
        }*/
    
    
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
        privs.add(priv);
        /* modify by ym   优化：List 转为单条数据
        for(String loginName : userNodePriv.getLoginNames()){
            UserNodePriv priv = new UserNodePriv();
            priv.setId(CommonUtils.genUUid());
            priv.genBaseVariables();
            priv.setFlowId(userNodePriv.getFlowId());
            priv.setNodeId(userNodePriv.getNodeId());
            priv.setNodeType(userNodePriv.getNodeType());
            priv.setLoginName(loginName);
            privs.add(priv);
        }*/

        
        userNodePrivDao.save(privs);
        List<UserTreeInfo> infos = getAllUserInfo(userNodePriv);
        if(infos != null && infos.size() > 0){
            SimpleCache.putIntoCache(JedisService.CACHE_KEY_PREFIX_USER_NODE_PRIV+userNodePriv.getNodeId(),
                    infos);
        }

        return buildResult(true,"","success");
    }

}
