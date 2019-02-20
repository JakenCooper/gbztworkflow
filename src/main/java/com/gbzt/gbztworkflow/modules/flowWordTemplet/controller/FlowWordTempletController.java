package com.gbzt.gbztworkflow.modules.flowWordTemplet.controller;


import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowWordTemplet.entity.FlowWordTemplet;
import com.gbzt.gbztworkflow.modules.flowWordTemplet.service.FlowWordTempletService;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/flowWordTemplet/")
public class FlowWordTempletController extends BaseController {
    @Autowired
    FlowWordTempletService flowWordTempletService;

    /**
     * 上传模板
     * @param request
     * @return
     */
    @RequestMapping(value = "upload",method = RequestMethod.POST)
    public ResponseEntity upload(HttpServletRequest request){
        if(ServletFileUpload.isMultipartContent(request)){
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> list = new ArrayList<>();
            try {
                list = upload.parseRequest(request);
                Iterator<FileItem> iter = list.iterator();

                FlowWordTemplet flowWordTemplet = new FlowWordTemplet();
                flowWordTemplet.setUploadUserIp(CommonUtils.getIpAddress(request));
                // 暂时先保存文件字段,接受到id字段后再做处理
                FileItem fileItem = null;
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if(item.isFormField()) {
                        // 传过来的普通表单字段
                        String name = item.getFieldName();
                        String value = item.getString();
                        if("flowId".equals(name)) {
                            flowWordTemplet.setFlowId(value);
                        }
                    } else {
                        // 传过来的文件字段
                        fileItem = item;
                        flowWordTemplet.setWordTempletName(item.getName());
                    }
                }
                String fileSuffix = flowWordTemplet.getWordTempletName().substring(flowWordTemplet.getWordTempletName().lastIndexOf(".") + 1);
                if(!"doc".equals(fileSuffix) && !"docx".equals(fileSuffix)){
                    return buildResp(400,"fileType");
                }
                // 判断是否已经保存过(保存过就删除后再报存)
                FlowWordTemplet flowWordTemplet2 = flowWordTempletService.findFlowWordTempletByFlowIdAndUploadUserIp(flowWordTemplet);
                if(null != flowWordTemplet2){
                    flowWordTempletService.delete(flowWordTemplet2);
                }
                flowWordTempletService.saveFile(fileItem,flowWordTemplet); // 保存文件
            } catch (Exception e) {
                e.printStackTrace();
                return buildResp(400,"error");
            }
        }else {
            return buildResp(400,"上传内容不是有效的multipart/form-data类型.");
        }
        return buildResp(200,"success");
    }

    /**
     * 判断在用户是否已经上传过模板
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadedTemplate/{flowId}",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public ResponseEntity uploadedTemplate(@PathVariable("flowId") String flowId, HttpServletRequest request){
        FlowWordTemplet flowWordTemplet = new FlowWordTemplet();
        flowWordTemplet.setUploadUserIp(CommonUtils.getIpAddress(request));
        flowWordTemplet.setFlowId(flowId);
        FlowWordTemplet flowWordTemplet2 = flowWordTempletService.findFlowWordTempletByFlowIdAndUploadUserIp(flowWordTemplet);
        if(null != flowWordTemplet2){
            return buildResp(200,true);
        }
        return buildResp(200,false);
    }
}
