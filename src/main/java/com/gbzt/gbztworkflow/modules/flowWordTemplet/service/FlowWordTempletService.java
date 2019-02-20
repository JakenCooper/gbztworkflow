package com.gbzt.gbztworkflow.modules.flowWordTemplet.service;


import com.gbzt.gbztworkflow.modules.flowWordTemplet.dao.FlowWordTempletDao;
import com.gbzt.gbztworkflow.modules.flowWordTemplet.entity.FlowWordTemplet;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Service
@Transactional
public class FlowWordTempletService {
    @Autowired
    private FlowWordTempletDao flowWordTempletDao;
    @Autowired
    private VelocityService velocityService;
    @Autowired
    private FlowDao flowDao;

    public void save(FlowWordTemplet flowWordTemplet) {
        flowWordTempletDao.save(flowWordTemplet);
    }
    
    public FlowWordTemplet findFlowWordTempletByFlowIdAndUploadUserIp(FlowWordTemplet flowWordTemplet) {
        return flowWordTempletDao.findFlowWordTempletByFlowIdAndUploadUserIp(flowWordTemplet.getFlowId(),flowWordTemplet.getUploadUserIp());
    }
    public Integer delete(FlowWordTemplet flowWordTemplet) {
        return flowWordTempletDao.deleteFlowWordTempletById(flowWordTemplet.getId());
    }

    
    public void saveFile(FileItem item,FlowWordTemplet flowWordTemplet) throws Exception{
        // 获取配置里的文件保存路径
        String wPath = velocityService.getValue("wordTempletUploadPath");
        
        // 通过id获取流程名称
        String flowName = flowDao.findById(flowWordTemplet.getFlowId()).getFlowName();
        if(StringUtils.isBlank(flowName)){
            throw new IllegalArgumentException("并没有找到对应流程名称.");
        }

        String fileName = flowWordTemplet.getWordTempletName();
        // 先判断上传文件类型是否合法
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String wName = flowName+"."+fileSuffix;
        String wAddress = wPath+wName;
        flowWordTemplet.setDelFlag("0");
        flowWordTemplet.setId(CommonUtils.genUUid());
        flowWordTemplet.setWordTempletAddress(wAddress);
        flowWordTemplet.setWordTempletName(wName);
        save(flowWordTemplet); // 保存数据库
        //创建文件输出流
        File file = new File(wAddress);
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        //创建输入流
        InputStream fis = (InputStream) item.getInputStream();
        //从输入流获取字节数组
        byte b[] = new byte[1];
        //读取一个输入流的字节到b[0]中
        int read = fis.read(b);
        while (read != -1) {
            fos.write(b, 0, 1);
            read = fis.read(b);
        }
        fis.close();
        fos.flush();
        fos.close();
        //打印List中的内容（每一个FileItem的实例代表一个文件，执行这行代码会打印该文件的一些基本属性，文件名，大小等）
        System.out.println(item);
    }
    
}
