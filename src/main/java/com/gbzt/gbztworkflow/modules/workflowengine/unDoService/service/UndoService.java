package com.gbzt.gbztworkflow.modules.workflowengine.unDoService.service;

import com.gbzt.gbztworkflow.modules.workflowengine.mybatisdao.UndoDao;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UndoService {
    @Autowired
    private UndoDao undoDao;

    @Transactional("dtm")
    public List<Task> getUndoList(Map<String,Object> map){
        List<Task> taskList=undoDao.getUndoLists(map);
        return taskList;
    }
    @Transactional("dtm")
    public Integer getUndoListsCount(Map<String,Object> map){
        Integer count=undoDao.getUndoListsCount(map);
        return count;
    }
}
