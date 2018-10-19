package com.gbzt.gbztworkflow.modules.workflowengine.runtime;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineCallback;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineExecutable;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineManager {
    private static EngineManager self = new EngineManager();

    private EngineManager() {
    }

    private Logger logger = Logger.getLogger(EngineManager.class);
    private static String LOGGER_TYPE_PREFIX = "EngineManager,";

    private EngineManagerThreadPool threadPool;
    private boolean isInitialized = false;

    private void initialize(){
        if(isInitialized){
            return ;
        }
        threadPool = new EngineManagerThreadPool(AppConst.TASK_EXECUTION_POOL_THREAD_INIT_COUNT);
        isInitialized = true;
    }


    /**
     * attention : must run under multi-thread environment,otherwise be blocked.
     */
    public static <T> T execute(EngineTask engineTask) {
        self.initialize();
        return self.executeInner(engineTask);
    }

    private <T> T executeInner(EngineTask task) {
        String loggerType = LOGGER_TYPE_PREFIX + "executeInner";
        try {
            class ExecutorInnerInitializer {
                public EngineExecutable executor = null;
                public EngineCallback callback = null;

                public void init(EngineTask task) throws Exception{
                    try {
                        executor = task.getExecutor().newInstance();
                        callback = task.getCallback().newInstance();
                        executor.preHandleTask(task);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
            ExecutorInnerInitializer initializer = new ExecutorInnerInitializer();
            initializer.init(task);

            if (AppConst.TASK_EXECUTION_TYPE_SYNC.equals(task.getExecutionType())) {
                if (AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE.equals(task.getThreadType())) {
                    // block task do not rely on execution result,actual result is in task object
                    initializer.executor.executeEngineTask(task);
                    return initializer.callback.handleCallback(task);
                } else {
                    if(task.getChildren() == null || task.getChildren().size() == 0){
                        throw new IllegalArgumentException("no sub task founded...");
                    }
                    List<Future<T>> futureList = new ArrayList<>();
                    List<Callable<T>>  callableList = new ArrayList<Callable<T>>();
                    for(EngineTask child : task.getChildren()){
                        EngineCallableTemplate<T> runtimeTask = new EngineCallableTemplate<T>(initializer.executor,child);
                        callableList.add(runtimeTask);
                    }
                    futureList = self.threadPool.batchInvoke(callableList);
                    task.setExecutedResult(futureList);
                    return initializer.callback.handleCallback(task);
                }
            }else if(AppConst.TASK_EXECUTION_TYPE_ASYNC.equals(task.getExecutionType())){
                if (AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE.equals(task.getThreadType())) {
                    EngineRunnableTemplate runtimeTask = new EngineRunnableTemplate(initializer.executor,task);
                    self.threadPool.singleSubmit(runtimeTask);
                    // for runnable task,callback should return a default value.
                    return initializer.callback.handleCallback(null);
                }else{
                    if(task.getChildren() == null || task.getChildren().size() == 0){
                        throw new IllegalArgumentException("no sub task founded...");
                    }
                    List<Runnable>  runnableList = new ArrayList<Runnable>();
                    for(EngineTask child : task.getChildren()){
                        EngineRunnableTemplate runtimeTask = new EngineRunnableTemplate(initializer.executor,child);
                        runnableList.add(runtimeTask);
                    }
                    self.threadPool.batchSubmit(runnableList);
                    // for runnable task,callback should return a default value.
                    return initializer.callback.handleCallback(null);
                }
            }
        } catch (Exception e) {
            logger.error(new EngineRuntimeException(loggerType, "任务执行失败！",e).getMessage(), e);
        }
        return null;
    }


    private class EngineRunnableTemplate1 implements Runnable{
        @Override
        public void run() {
            try {
                System.out.println("111111111111111111");
            } catch (Exception e) {
                throw new EngineRuntimeException(e.getMessage());
            }
        }
    }

    private class EngineRunnableTemplate implements Runnable {
        private EngineExecutable executable;
        private EngineTask task;
        public EngineRunnableTemplate(EngineExecutable executable,EngineTask task){
            this.executable = executable;
            this.task = task;
        }
        @Override
        public void run() {
            try {
                executable.executeEngineTask(task);
            } catch (Exception e) {
                throw new EngineRuntimeException(e.getMessage());
            }
        }
    }



    private class EngineCallableTemplate<T> implements Callable<T>{
        private EngineExecutable executable;
        private EngineTask task;
        public EngineCallableTemplate(EngineExecutable executable,EngineTask task){
            this.executable = executable;
            this.task = task;
        }
        @Override
        public T call() throws Exception {
            return executable.executeEngineTask(task) ;
        }

    }

    private class EngineManagerThreadPool {
        private ExecutorService executorService;

        public EngineManagerThreadPool(int threadSize) {
            this.executorService = Executors.newFixedThreadPool(threadSize);
        }

        public void singleSubmit(Runnable runnable) {
//			this.executorService.execute(runnable);
            try {
                this.executorService.submit(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public <T> Future<T> singleSubmit(Callable<T> callable) {
            Future<T> future = this.executorService.submit(callable);
            return future;
        }

        public void batchSubmit(List<Runnable> runnables) {
            for (Runnable runnable : runnables) {
                this.executorService.execute(runnable);
            }
        }

        public <T> List<Future<T>> batchInvoke(List<Callable<T>> callables) {
            try {
                return this.executorService.invokeAll(callables);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
