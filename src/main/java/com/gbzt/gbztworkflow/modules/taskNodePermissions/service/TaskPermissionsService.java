package com.gbzt.gbztworkflow.modules.taskNodePermissions.service;

import com.gbzt.gbztworkflow.modules.taskNodePermissions.dao.TaskPermissionsDao;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
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

    public Integer savePermissions(String[] array){
        int flag=0;
        try {
            List<TaskNodePermissions> permissionList=new ArrayList<>();
            for(int i=0;i<array.length;i++){
                String taskName=array[i].split(":")[0];
                String permission=array[i].split(":")[1];
                String taskNodeId=array[i].split(":")[2];
                String currentFlowId=array[i].split(":")[3];
                String columnName=array[i].split(":")[4];
                TaskNodePermissions taskNodePermissions=new TaskNodePermissions();
                taskNodePermissions.setTaskName(taskName);
                taskNodePermissions.setPermission(permission);
                taskNodePermissions.setTaskNodeId(taskNodeId);
                taskNodePermissions.setCurrentFlowId(currentFlowId);
                taskNodePermissions.setColumnName(columnName);
                taskNodePermissions.setDelFlag("0");
                taskNodePermissions.setId(UUID.randomUUID().toString());
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
        }
        return flag;
    }
    public List<TaskNodePermissions> findTaskNodePermissionsByTaskNodeId(String tId){
        return taskPermissionsDao.findTaskNodePermissionsByTaskNodeId(tId);
    }

    public Integer deleteTaskNodePermissionsById(String id){
        return taskPermissionsDao.deleteTaskNodePermissionsById(id);
    }
}
