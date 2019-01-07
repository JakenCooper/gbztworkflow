package com.gbzt.gbztworkflow.modules.workflowengine.mybatisdao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UndoDao {
    public List<Task> getUndoLists(Map<String, Object> map);
    public Integer getUndoListsCount(Map<String,Object> map);
}
