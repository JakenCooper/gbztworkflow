package com.gbzt.gbztworkflow.modules.flowruntime.invoker;

import com.gbzt.gbztworkflow.modules.flowruntime.service.IRuntimeService;
import com.gbzt.gbztworkflow.modules.flowruntime.service.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Component;

//@Component(value="flowRuntimeService")
public class FlowRuntimeServiceExporter extends HttpInvokerServiceExporter {

//    @Autowired
    public FlowRuntimeServiceExporter(RuntimeService runtimeService) {
        super();
        setServiceInterface(IRuntimeService.class);
        setService(runtimeService);
    }
}
