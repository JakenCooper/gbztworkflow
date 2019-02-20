package com.gbzt.gbztworkflow.modules.taskNodePermissions.service;

import com.gbzt.gbztworkflow.modules.flowdefination.service.MetadataService;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.dao.TaskFormIdNameDao;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.dao.TaskPermissionsDao;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskFormIdName;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskPermissionsService {
    @Autowired
    private TaskPermissionsDao taskPermissionsDao;
    @Autowired
    private TaskFormIdNameDao taskFormIdNameDao;
    @Autowired
    private VelocityService velocityService;
    @Autowired
    private MetadataService metadataService;

    private Logger logger =Logger.getLogger(TaskPermissionsService.class);

    @Transactional(readOnly = false)
    public Integer savePermissions(String[] array){
        int flag=0;
//        boolean firstTimeSaved = false;
//        String nodeAdviseType = "-1";
        try {
            List<TaskNodePermissions> permissionList=new ArrayList<>();
            for(int i=0;i<array.length;i++){
                String taskName=array[i].split(":")[0];
                String permission=array[i].split(":")[1];
                String taskNodeId=array[i].split(":")[2];
                String currentFlowId=array[i].split(":")[3];
                String columnName=array[i].split(":")[4];
                /*if(!firstTimeSaved && "true".equals(adviseTag)){
                    String attrName = velocityService.humpNomenclature(columnName);
                    String adviseInsertResult = metadataService.addAdviseSummary(currentFlowId,attrName);
                    if("-2".equals(adviseInsertResult)){
                        throw new IllegalArgumentException("无法正确添加意见信息，请检查日志！");
                    }
                    nodeAdviseType = adviseInsertResult;
                    firstTimeSaved = true;
                }*/
                TaskNodePermissions taskNodePermissions=new TaskNodePermissions();
                taskNodePermissions.setTaskName(taskName);
                taskNodePermissions.setPermission(permission);
                taskNodePermissions.setTaskNodeId(taskNodeId);
                taskNodePermissions.setCurrentFlowId(currentFlowId);
                taskNodePermissions.setColumnName(columnName);
                taskNodePermissions.setDelFlag("0");
                taskNodePermissions.setId(UUID.randomUUID().toString());
//                taskNodePermissions.setAdviseType(nodeAdviseType);

                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd  HH:mm:ss");
                String date=sdf.format(new Date());
                Date createDate= sdf.parse(date);
                taskNodePermissions.setCreateDate(createDate);
                permissionList.add(taskNodePermissions);
            }
            taskPermissionsDao.save(permissionList);
            flag=1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return flag;
    }

    public Integer saveFormIdAndName(String[] array){
        int flag=0;
        try {
            List<TaskNodePermissions> permissionList=new ArrayList<>();
            String form_Id = array[0];
            String form_Name = array[1];
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            TaskFormIdName taskFormIdName = new TaskFormIdName();
            taskFormIdName.setFormId(form_Id);
            taskFormIdName.setFormName(form_Name);
            Date date = new Date();
            taskFormIdName.setCreateDate(date);
            taskFormIdName.setDeltag("N");
            taskFormIdName.setRemark("");
            taskFormIdName.setId(uuid);
            taskFormIdNameDao.save(taskFormIdName);
            flag=1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    
    public List<TaskNodePermissions> findTaskNodePermissionsByTaskNodeId(String tId){
        return taskPermissionsDao.findTaskNodePermissionsByTaskNodeId(tId);
    }
    public List<TaskFormIdName> findTaskFormNamebyformid(String tId){
        return taskFormIdNameDao.findTaskFormIdNamesByFormId(tId);
    }
    

    public Integer deleteTaskNodePermissionsById(String id){
        return taskPermissionsDao.deleteTaskNodePermissionsById(id);
    }
    
    public Integer deleteTaskFormNameandId(String id){
        return   taskFormIdNameDao.deleteTaskFormIdNameById(id);
    }
    
    
}
