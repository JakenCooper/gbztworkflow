package com.gbzt.gbztworkflow.modules.formDesign.service;


import com.gbzt.gbztworkflow.modules.flowElement.dao.FlowElementDao;
import com.gbzt.gbztworkflow.modules.flowElement.entity.FlowElement;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.formDesign.Util.HtmlConstant;
import com.gbzt.gbztworkflow.modules.formDesign.Util.UeditorTools;
import com.gbzt.gbztworkflow.modules.formDesign.dao.FormDesignDao;
import com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.dao.TaskPermissionsDao;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class FormDesignService {
    @Autowired
    private FormDesignDao formDesignDao;
    @Autowired
    private TaskPermissionsDao taskPermissionsDao;
    @Autowired
    private FlowDao flowDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private FlowElementDao flowElementDao;
    public void save(FormDesign formDesign) throws ParseException {
     /*   if(formDesign.getCreateDate()!=null){
            formDesignDao.saveAndFlush(formDesign);
           *//* formDesignDao.update(formDesign.getFormHtml(),formDesign.getJspCode(),formDesign.getCurrentFlowId());*//*
        }else{*/
        String flowId=formDesign.getCurrentFlowId();
        Flow flow=flowDao.findById(flowId);
        String flowName="";
        if(flow!=null){
            formDesign.setFormName(flow.getFlowName());
            flowName=flow.getFlowName();
        }
        if(formDesign!=null &&formDesignDao.findFormDesignByCurrentFlowId(formDesign.getCurrentFlowId())!=null){

            formDesignDao.updateByflow(formDesign.getFormHtml(),formDesign.getJspCode(),formDesign.getJspCodeView(),formDesign.getRemark(),flowName,formDesign.getCurrentFlowId());
        }else{
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd  HH:mm:ss");
            String date=sdf.format(new Date());
            Date createDate= sdf.parse(date);
            formDesign.setCreateDate(createDate);
            formDesign.setDelflag("0");
            formDesignDao.save(formDesign);
        }

       /* }*/

    }
    public List<FormDesign> list(){
        return formDesignDao.findAll();
    }
    public FormDesign get(FormDesign formDesign){
        formDesign= formDesignDao.findFormDesignById(formDesign.getId());
        return formDesign;
    }
    public FormDesign findFormDesignsByCurrentFlowId(FormDesign formDesign){
        formDesign= formDesignDao.findFormDesignsByCurrentFlowId(formDesign.getCurrentFlowId());
        return formDesign;
    }
    public Integer delFromDesign(FormDesign formDesign){
        if(StringUtils.isBlank(formDesign.getId())){
            return 0;
        }
        return formDesignDao.deleteById(formDesign.getId());
    }

    public Map<String,Object> analysisHtml(String html){
        Map<String,Object> nameMap=new HashMap<>();
        if(StringUtils.isBlank(html)){
            return nameMap;
        }
        Document doc= Jsoup.parse(html);
        Elements inputElements=doc.getElementsByTag(HtmlConstant.INPUT_TAG);
        Elements selectElements=doc.getElementsByTag(HtmlConstant.SELECT_TAG);
        Elements textAreaElements=doc.getElementsByTag(HtmlConstant.TEXT_AREA_TAG);
        for(int i=0;i<inputElements.size();i++){
            Element element=inputElements.get(i);
            String name=element.attr("name");
            if(name.contains("\\\"")){
                name=name.replaceAll("\\\"","");
                name=name.replaceAll("\\\\","");
            }
            nameMap.put(name,name);
        }
        for(int i=0;i<selectElements.size();i++){
            Element element=selectElements.get(i);
            String name=element.attr("name");
            if(name.contains("\\\"")){
                name=name.replaceAll("\\\"","");
                name=name.replaceAll("\\\\","");
            }
            nameMap.put(name,name);
        }
        for(int i=0;i<textAreaElements.size();i++){
            Element element=textAreaElements.get(i);
            String name=element.attr("name");
            if(name.contains("\\\"")){
                name=name.replaceAll("\\\"","");
                name=name.replaceAll("\\\\","");
            }
            nameMap.put(name,name);
        }
        return nameMap;
    }

    public String createTable(String sql){

        return "";
    }

    public Document addLabel(String html){
        Document doc= Jsoup.parse(html);
        Elements inputElements=doc.getElementsByTag(HtmlConstant.INPUT_TAG);
        Elements tableElements=doc.getElementsByTag(HtmlConstant.TABLE_AREA_TAG);
        for(int i=0;i<tableElements.size();i++){
            Element element=tableElements.get(i);
            element.addClass("table  table-bordered table-condensed");
        }
        return doc;
    }

    public String createJsp(String parseForm, String currentFlowId, HttpServletRequest request, HttpServletResponse response, HttpSession session,boolean isView,String mode,boolean isElement) {
        //用来操作form 代码
        long startTime = System.currentTimeMillis();
        Document doc= Jsoup.parseBodyFragment(parseForm);
        Elements inputElements=doc.getElementsByTag(HtmlConstant.INPUT_TAG);
        Elements textareaElements=doc.getElementsByTag(HtmlConstant.TEXT_AREA_TAG);
        Elements selectElements=doc.getElementsByTag(HtmlConstant.SELECT_TAG);
        Elements checkBoxElements=doc.getElementsByAttributeValue("leipiplugins","checkboxs");
        Elements radioElements=doc.getElementsByAttributeValue("leipiplugins","radios");
        Elements tbElements=doc.getElementsByTag(HtmlConstant.TABLE_AREA_TAG);
        boolean deleteFlag=false;
        Map<String,String> nodesMap=new HashMap<>();
        FormDesign flowName=formDesignDao.findFormDesignByCurrentFlowId(currentFlowId);
        try {
            /*radio -------------------------------------------------------------start*/
            out:
            for(int i=0;i<radioElements.size();i++){
                Element element=radioElements.get(i);
                String name=element.child(i).attr("name");
                //控件拼接id接不到 暂时使用控件自身字段接收
                String id=element.attr("fieldname");
                String orgtype=element.attr("orgtype");
                String orgName=element.attr("name");
                String fieldname= element.attr("title");

                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    int childSize=element.children().size();
                    for(int m=0;m<childSize;m++){
                        element.child(m).attr("name",name).removeAttr("type");
                    }
                }
                if(isElement){
                    List<FlowElement> flowElementList=flowElementDao.findFlowElementsByFlowId(currentFlowId);
                    if(flowElementList.size()>0&&!deleteFlag){
                        flowElementDao.deleteFlowElementByFlowId(currentFlowId);
                        deleteFlag=true;
                    }
                    FlowElement flowElement=new FlowElement(UUID.randomUUID().toString(),currentFlowId,flowName.getFormName(),id,fieldname,orgName,name, new Date());
                    flowElementDao.save(flowElement);
                }
                String style=element.child(i).attr("style");
                //字段名称
                //拼接c:if
                List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsDao.findByTaskNodeIdAndAndCurrentFlowId(id,currentFlowId);
                for(int j=0;j<taskNodePermissionsList.size();j++){
                    String permission=taskNodePermissionsList.get(j).getPermission();
                    String taskName=taskNodePermissionsList.get(j).getTaskName();
                 /*   String startCif="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                    String endCif="\n\r\t\t##end_c:when _###end\n\r";*/
                    String startWhen="";
                    String endWhen="";
                    if(j==0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r "+"##chooseEnd";
                    }else if((j!=(taskNodePermissionsList.size()-1)&&j>0)){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }else if(j==(taskNodePermissionsList.size()-1)){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }
                    //重置前一个input样式
                    element.attr("style",style);
                    element.removeAttr("readonly");
                    element.children().removeAttr("type");
                    String outerHtml=element.outerHtml().replace("input","form:radiobutton").replace("name=","path=").replace(">","/>");
                    if("不可编辑".equals(permission)){
                        outerHtml="${workFlowObject."+name+"}";
                    }
                    if(outerHtml.contains("checked")){
                        outerHtml=outerHtml.replace("checked","${checkState}");
                    }
                    String code="";
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    if(isView){
                        code="\t\t\t"+outerHtml;
                        element.after(key);
                        nodesMap.put(key,code);
                        break;
                    }else{
                        code=startWhen+"\t\t\t"+outerHtml+endWhen;
                        element.after(key);
                        nodesMap.put(key,code);

                    }


                }

                if(taskNodePermissionsList.size()==0){
                    String uuid=UUID.randomUUID().toString();
                    String outerHtml=radioElements.outerHtml();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    outerHtml=element.removeAttr("type").outerHtml().replace("input","form:radiobutton").replace("name=","path=").replace(">","/>");
                    if(outerHtml.contains("checked")){
                        outerHtml=outerHtml.replace("checked","${checkState}");
                    }
                    nodesMap.put(key,"\t\t\t"+outerHtml);
                    radioElements.after(key);
                }
                if(!isView) {
                    if (taskNodePermissionsList.size() != 0) {
                        String uuid = UUID.randomUUID().toString();
                        String key = "##tranJsp" + uuid + "##tranJsp_";
                        StringBuffer testInfo=getTestInfo(taskNodePermissionsList);
                        String startOther = "\n\r\t\t##start_:when test=" +testInfo+
                                " _###end\n\r";
                        String endOther = "\n\r\t\t##end_c:when _###end\n\r";
                        String outerHtml = radioElements.removeAttr("type").outerHtml();
                        if(outerHtml.contains("checked")){
                            outerHtml=outerHtml.replace("checked","${checkState}");
                        }
                        // = 号为避免实体类属性中也存在name字符串
                        outerHtml = element.outerHtml().replace("input","form:radiobutton").replace("name=","path=").replace(">","/>");
                        nodesMap.put(key, "##chooseStart\t\t\t" + startOther + "\n\r" + outerHtml + "\n\r" + endOther);
                        radioElements.after(key);
                    }
                }

                element.remove();
                break out;
            }
            /*radio -------------------------------------------------------------end*/

            /*checkbox -------------------------------------------------------------start*/
            out:
            for(int i=0;i<checkBoxElements.size();i++){
                Element element=checkBoxElements.get(i);
                String name=element.child(i).attr("name");
                //控件拼接id接不到 暂时使用控件自身字段接收
                String ids[]=element.attr("title").split(",");
                String id="";
                String orgName="";
                String fieldname="";
                if(ids.length>=2){
                     id=ids[0];
                     orgName=element.child(i).attr("name");
                    fieldname=ids[1];
                }else{
                     id=element.attr("title");
                     orgName=name;
                }

                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    Elements elements=element.children();
                    int childSize=element.children().size();
                    for(int m=0;m<childSize;m++){
                        element.child(m).attr("name",name).removeAttr("type");
                    }
                }

                if(isElement){
                    List<FlowElement> flowElementList=flowElementDao.findFlowElementsByFlowId(currentFlowId);
                    if(flowElementList.size()>0&&!deleteFlag){
                        flowElementDao.deleteFlowElementByFlowId(currentFlowId);
                        deleteFlag=true;
                    }
                    FlowElement flowElement=new FlowElement(UUID.randomUUID().toString(),currentFlowId,flowName.getFormName(),id,fieldname,orgName,name, new Date());
                    flowElementDao.save(flowElement);
                }
                String style=element.child(i).attr("style");
                //字段名称
                String fieldName=element.child(i).attr("fieldname");
                //拼接c:if
                List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsDao.findByTaskNodeIdAndAndCurrentFlowId(id,currentFlowId);
                for(int j=0;j<taskNodePermissionsList.size();j++){
                    String permission=taskNodePermissionsList.get(j).getPermission();
                    String taskName=taskNodePermissionsList.get(j).getTaskName();
                   /* String startCif="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                    String endCif="\n\r\t\t##end_c:when _###end\n\r";*/
                    String startWhen="";
                    String endWhen="";
                    if(j==0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r "+"##chooseEnd";
                    }else if(j!=(taskNodePermissionsList.size()-1)&&j>0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }else if(j==(taskNodePermissionsList.size()-1)){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }
                    //重置前一个input样式
                    element.attr("style",style);
                    element.removeAttr("readonly");
                    element.children().removeAttr("type");
                    String outerHtml=element.outerHtml().replace("input","form:checkbox").replace("name=","path=").replace(">","/>");;
                    if("不可编辑".equals(permission)){
                        outerHtml="${workFlowObject."+name+"}";
                    }
                    if(outerHtml.contains("checked")){
                        outerHtml=outerHtml.replace("checked","${checkState}");
                    }
                    String code="";
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    if(isView){
                        code="\t\t\t"+outerHtml;
                        element.after(key);
                        nodesMap.put(key,code);
                        break;
                    }else{
                        code=startWhen+"\t\t\t"+outerHtml+endWhen;
                        element.after(key);
                        nodesMap.put(key,code);

                    }


                }
                if(taskNodePermissionsList.size()==0){
                    String uuid=UUID.randomUUID().toString();
                    String outerHtml=checkBoxElements.outerHtml();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    outerHtml=element.outerHtml().replace("input","form:checkbox").replace("name=","path=").replace(">","/>");;
                    if(outerHtml.contains("checked")){
                        outerHtml=outerHtml.replace("checked","${checkState}");
                    }
                    nodesMap.put(key,"\t\t\t"+outerHtml);
                    checkBoxElements.after(key);
                }
                if(!isView) {
                    if (taskNodePermissionsList.size() != 0) {
                        String uuid = UUID.randomUUID().toString();
                        String key = "##tranJsp" + uuid + "##tranJsp_";
                        StringBuffer testInfo=getTestInfo(taskNodePermissionsList);
                        String startOther = "\n\r\t\t##start_:when test=" +testInfo+
                                " _###end\n\r";
                        String endOther = "\n\r\t\t##end_c:when _###end\n\r";
                        String outerHtml = checkBoxElements.removeAttr("type").outerHtml();
                        if(outerHtml.contains("checked")){
                            outerHtml=outerHtml.replace("checked","${checkState}");
                        }
                        // = 号为避免实体类属性中也存在name字符串
                        outerHtml =element.outerHtml().replace("input","form:checkbox").replace("name=","path=").replace(">","/>");;
                        nodesMap.put(key, "##chooseStart\t\t\t" + startOther + "\n\r" + outerHtml + "\n\r" + endOther);
                        checkBoxElements.after(key);
                    }
                }

                element.remove();
                break out;
            }
            /*checkbox -------------------------------------------------------------end*/
            /*input -------------------------------------------------------------start*/
            for(int i=0;i<inputElements.size();i++){
                Element element=inputElements.get(i);
                String id=element.attr("id");
                String name=element.attr("name");
                String type=element.attr("type");
                String orgtype=element.attr("orgtype");
                String orgName=element.attr("name");
                String fieldname=null;
                if(StringUtils.isBlank(element.attr("fieldname"))){
                    fieldname=element.attr("title");
                }else{
                    fieldname=element.attr("fieldname");
                }

                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    element.attr("name",name);
                }
                if(isElement&& HtmlConstant.INPUT_TYPE_TEXT.equals(type)){
                    List<FlowElement> flowElementList=flowElementDao.findFlowElementsByFlowId(currentFlowId);
                    if(flowElementList.size()>0&&!deleteFlag){
                        flowElementDao.deleteFlowElementByFlowId(currentFlowId);
                        deleteFlag=true;
                    }
                    FlowElement flowElement=new FlowElement(UUID.randomUUID().toString(),currentFlowId,flowName.getFormName(),id,fieldname,orgName,name, new Date());
                    flowElementDao.save(flowElement);
                }
                String style=element.attr("style");
                //字段名称
                String fieldName=element.attr("fieldname");
                //拼接c:if
                List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsDao.findByTaskNodeIdAndAndCurrentFlowId(id,currentFlowId);
                for(int j=0;j<taskNodePermissionsList.size();j++){
                    String permission=taskNodePermissionsList.get(j).getPermission();
                    String taskName=taskNodePermissionsList.get(j).getTaskName();
                    String startWhen="";
                    String endWhen="";
                    if(j==0){
                         startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                         endWhen="\n\r\t\t##end_c:when_###end\n\r "+"##chooseEnd";
                    }else if(j!=(taskNodePermissionsList.size()-1)&&j>0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }else if(j==(taskNodePermissionsList.size()-1)){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }
                    /*String lable="\n\r\t\t\t<lable for=\""+id+"\">"+fieldName+"</lable>:\n\r";*/


                    String outerHtml=element.outerHtml();

                    if(HtmlConstant.INPUT_TYPE_TEXT.equals(type)){
                        outerHtml=outerHtml.replace("<input","<form:input").replace("name=","path=").replace(">","/>");
                    }else {
                        continue;
                    }
                    if(HtmlConstant.DATE_INPUT_TYPE_TAG.equals(orgtype)){
                        outerHtml=HtmlConstant.getDateInput(name,permission,"");
                    }
                    //重置前一个input样式
                    element.attr("style",style);
                    element.removeAttr("readonly");
                    if(HtmlConstant.INPUT_TYPE_TEXT.equals(type)&&!HtmlConstant.INPUT_TYPE_TEXT.equals(type)){
                        if("不可见".equals(permission)){
                            //设置新的权限
                            element.attr("style",style+"display:none");
                        }
                        if("不可编辑".equals(permission)){
                            element.attr("readonly","${readonly}");
                        }
                    }
                    //时间控件设置权限
                    if(HtmlConstant.DATE_INPUT_TYPE_TAG.equals(orgtype)){
                        outerHtml=HtmlConstant.getDateInput(name,permission,"");
                    }
                    //人员树
                    if(HtmlConstant.USER_TREE_TYPE_TAG.equals(type)){
                        outerHtml=HtmlConstant.getUserTreeTag(name,permission,"");
                    }
                   String code="";
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    if(isView){
                        code="\t\t\t"+outerHtml;
                        element.after(key);
                        nodesMap.put(key,code);
                        break;
                    }else{
                        code=startWhen+"\t\t\t"+outerHtml+endWhen;
                        element.after(key);
                        nodesMap.put(key,code);
                    }


                }
                if(taskNodePermissionsList.size()==0){
                    Node node= nodeDao.findNodeByFlowIdAndBeginNode(currentFlowId,true);
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    String outerHtml=element.removeAttr("type").outerHtml();
                    if(HtmlConstant.INPUT_TYPE_TEXT.equals(type)){
                        // = 号为避免实体类属性中也存在name字符串
                        outerHtml=outerHtml.replace("input","form:input").replace("name=","path=").replace(">","/>");
                        nodesMap.put(key,"\t\t\t"+outerHtml);
                    }
                    //时间控件设置权限
                    if(HtmlConstant.DATE_INPUT_TYPE_TAG.equals(orgtype)){
                        outerHtml=HtmlConstant.getDateInput(name,"",node.getName());
                        nodesMap.put(key,"\t\t\t"+outerHtml);
                    }
                    //人员树
                    if(HtmlConstant.USER_TREE_TYPE_TAG.equals(orgtype)){
                        outerHtml=HtmlConstant.getUserTreeTag(name,"",node.getName());
                        nodesMap.put(key,"\t\t\t"+outerHtml);
                    }

                    if(HtmlConstant.FILE_TAG.equals(type)){

                        String startWhen="\n\r\t\t##start_:when test=\"${'"+node.getName()+"'==taskName"+"}\"_###end\n\r";
                        String endWhen="\n\r\t\t##end_c:when_###end\n\r";
                        String otherwiseStart="\n\r\t\t##start_:otherwise _###end\n\r";
                        String otherwiseEnd="\n\r\t\t##end_c:otherwise _###end\n\r";
                        String fileCode="##chooseStart\t\t\t" + startWhen+"\r\n"+HtmlConstant.NEW_FILE_TAG+endWhen+"\r\n"+
                                otherwiseStart+"\n\r"+HtmlConstant.FILE_LIST_TAG+"\n\r"+otherwiseEnd+"##chooseEnd";
                        nodesMap.put(key, "\t\t\t" + fileCode);
                    }



                    element.after(key);
                }
                if(!isView) {

                    if (taskNodePermissionsList.size() != 0) {
                        String uuid = UUID.randomUUID().toString();
                        String key = "##tranJsp" + uuid + "##tranJsp_";
                        StringBuffer testInfo=getTestInfo(taskNodePermissionsList);
                        String startOther = "\n\r\t\t##start_:when test=" +testInfo+
                                " _###end\n\r";
                        String endOther = "\n\r\t\t##end_c:when _###end\n\r";

                        if (HtmlConstant.INPUT_TYPE_TEXT.equals(type)) {
                            String outerHtml = element.removeAttr("type").removeAttr("readonly").outerHtml();
                            // = 号为避免实体类属性中也存在name字符串
                            outerHtml = outerHtml.replace("input", "form:input").replace("name=", "path=").replace(">", "/>");
                            nodesMap.put(key, "##chooseStart\t\t\t" + startOther + "\n\r" + outerHtml + "\n\r" + endOther);
                        }

                        element.after(key);
                    }
                }
                element.remove();
            }
            /*input    ---------------------------------------------------------end*/
            /*textarea ---------------------------------------------------------start*/
            for(int i=0;i<textareaElements.size();i++){
                Element element=textareaElements.get(i);
                String id=element.attr("id");
                String style=element.attr("style");
                //字段名称
                String fieldName=element.attr("fieldname");
                String type=element.attr("type");
                String name=element.attr("name");
                String orgtype=element.attr("orgtype");
                String orgName=element.attr("name");
                String fieldname=null;
                if(StringUtils.isBlank(element.attr("fieldname"))){
                    fieldname=element.attr("title");
                }else{
                    fieldname=element.attr("fieldname");
                }
                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    element.attr("name",name);
                }

                if(isElement){
                    List<FlowElement> flowElementList=flowElementDao.findFlowElementsByFlowId(currentFlowId);
                    if(flowElementList.size()>0&&!deleteFlag){
                        flowElementDao.deleteFlowElementByFlowId(currentFlowId);
                        deleteFlag=true;
                    }
                    FlowElement flowElement=new FlowElement(UUID.randomUUID().toString(),currentFlowId,flowName.getFormName(),id,fieldname,orgName,name, new Date());
                    flowElementDao.save(flowElement);
                }
                //拼接c:if
                List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsDao.findByTaskNodeIdAndAndCurrentFlowId(id,currentFlowId);
                for(int j=0;j<taskNodePermissionsList.size();j++){
                    String permission=taskNodePermissionsList.get(j).getPermission();
                    String taskName=taskNodePermissionsList.get(j).getTaskName();
                /*    String startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                    String endWhen="\n\r\t\t##end_c:when_###end\n\r";*/
                    String startWhen="";
                    String endWhen="";
                    if(j==0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r "+"##chooseEnd";
                    }else if(j!=(taskNodePermissionsList.size()-1)&&j>0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }else if(j==(taskNodePermissionsList.size()-1)){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }
                    String titleStyle="";
                    if("title".equals(name)){
                         titleStyle="width:96%;font-family:SimHei;font-size:32px;text-align:center;";
                    }
                    element.attr("style",style+titleStyle);
                    element.removeAttr("readonly");

                    if("不可见".equals(permission)){
                        //设置新的权限
                        element.attr("style",style+"display:none");
                    }
                    if("不可编辑".equals(permission)){
                        element.attr("readonly","${readonly}");
                    }
                    String outerHtml=element.outerHtml();
                    outerHtml=outerHtml.replace("<textarea","<form:textarea").replace("name","path").replace(">","/>");

                    String code="";
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    if(isView){
                        code="\t\t\t"+outerHtml;
                        element.after(key);
                        nodesMap.put(key,code);
                        break;
                    }else{
                        code=startWhen+"\t\t\t"+outerHtml+endWhen;
                        element.after(key);
                        nodesMap.put(key,code);
                    }



                }
                if(taskNodePermissionsList.size()==0){
                    String uuid=UUID.randomUUID().toString();
                    String outerHtml=element.outerHtml();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    outerHtml=outerHtml.replace("<textarea","<form:textarea").replace("name","path").replace(">","/>");

                    nodesMap.put(key,"\t\t\t"+outerHtml);
                    element.after(key);
                }
                if(!isView) {
                    if (taskNodePermissionsList.size() != 0) {
                        String uuid = UUID.randomUUID().toString();
                        String key = "##tranJsp" + uuid + "##tranJsp_";
                        StringBuffer testInfo=getTestInfo(taskNodePermissionsList);
                        String startOther = "\n\r\t\t##start_:when test=" +testInfo+
                                " _###end\n\r";
                        String endOther = "\n\r\t\t##end_c:when _###end\n\r";
                        String outerHtml = element.removeAttr("type").removeAttr("readonly").outerHtml();
                        // = 号为避免实体类属性中也存在name字符串
                        outerHtml = outerHtml.replace("<textarea","<form:textarea").replace("name","path").replace(">","/>");
                        nodesMap.put(key, "##chooseStart\t\t\t" + startOther + "\n\r" + outerHtml + "\n\r" + endOther);
                        element.after(key);
                    }
                }

                element.remove();
            }
            /*textarea ---------------------------------------------------------end*/
            /*select -----------------------------------------------------------start*/
            for(int i=0;i<selectElements.size();i++){
                Element selectElement=selectElements.get(i);
                String name=selectElement.attr("name");
                String id=selectElement.attr("id");
                String type=selectElement.attr("type");
                String style=selectElement.attr("style");
                String orgtype=selectElement.attr("orgtype");
                String orgName=selectElement.attr("name");
                String fieldname=null;
                if(StringUtils.isBlank(selectElement.attr("fieldname"))){
                    fieldname=selectElement.attr("title");
                }else{
                    fieldname=selectElement.attr("fieldname");
                }
                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    selectElement.attr("name",name);
                }

                if(isElement){
                    List<FlowElement> flowElementList=flowElementDao.findFlowElementsByFlowId(currentFlowId);
                    if(flowElementList.size()>0&&!deleteFlag){
                        flowElementDao.deleteFlowElementByFlowId(currentFlowId);
                        deleteFlag=true;
                    }
                    FlowElement flowElement=new FlowElement(UUID.randomUUID().toString(),currentFlowId,flowName.getFormName(),id,fieldname,orgName,name, new Date());
                    flowElementDao.save(flowElement);
                }
                List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsDao.findByTaskNodeIdAndAndCurrentFlowId(id,currentFlowId);
                for(int j=0;j<taskNodePermissionsList.size();j++){
                    String permission=taskNodePermissionsList.get(j).getPermission();
                    String taskName=taskNodePermissionsList.get(j).getTaskName();
                /*    String startCif="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                    String endCif="\n\r\t\t##end_c:when_###end\n\r";*/
                    String startWhen="";
                    String endWhen="";
                    if(j==0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r "+"##chooseEnd";
                    }else if(j!=(taskNodePermissionsList.size()-1)&&j>0){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }else if(j==(taskNodePermissionsList.size()-1)){
                        startWhen="\n\r\t\t##start_:when test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                        endWhen="\n\r\t\t##end_c:when_###end\n\r";
                    }
                    selectElement.attr("style",style);
                    selectElement.removeAttr("readonly");
                    String code="";
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    String outerHtml=selectElement.outerHtml().replaceFirst("<select","<form:select").replace("<option","<form:option").replace("name=","path=");
                    if("不可编辑".equals(permission)){
                        outerHtml="${workFlowObject."+name+"}";
                    }
                    if(outerHtml.contains("selected")){
                        outerHtml=outerHtml.replace("selected","${selectState}");
                    }
                    nodesMap.put(key,outerHtml);
                    if(isView){
                        code="\t\t\t"+outerHtml;
                        selectElement.after(key);
                        nodesMap.put(key,code);
                        break;
                    }else{
                        code=startWhen+"\t\t\t"+outerHtml+endWhen;
                        selectElement.after(key);
                        nodesMap.put(key,code);
                    }


                }
                if(taskNodePermissionsList.size()==0){
                    String uuid=UUID.randomUUID().toString();
                    String outerHtml=selectElement.outerHtml();
                    if(outerHtml.contains("selected")){
                        outerHtml=outerHtml.replace("selected","${selectState}");
                    }
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    outerHtml=outerHtml.replaceFirst("<select","<form:select").replace("<option","<form:option").replace("name=","path=");

                    nodesMap.put(key,"\t\t\t"+outerHtml);
                    selectElement.after(key);
                }
                if(!isView){
                    if(taskNodePermissionsList.size()!=0){
                        String uuid=UUID.randomUUID().toString();
                        String key="##tranJsp"+uuid+"##tranJsp_";
                        StringBuffer testInfo=getTestInfo(taskNodePermissionsList);
                        String startOther = "\n\r\t\t##start_:when test=" +testInfo+
                                " _###end\n\r";
                        String endOther = "\n\r\t\t##end_c:when _###end\n\r";
                        String outerHtml=selectElement.removeAttr("type").outerHtml();
                        if(outerHtml.contains("selected")){
                            outerHtml=outerHtml.replace("selected","${selectState}");
                        }
                        // = 号为避免实体类属性中也存在name字符串
                        outerHtml=outerHtml.replaceFirst("<select","<form:select").replace("<option","<form:option").replace("name=","path=");
                        nodesMap.put(key,"##chooseStart\t\t\t"+startOther+"\n\r"+outerHtml+"\n\r"+endOther);
                        selectElement.after(key);
                    }
                }
                selectElement.remove();
            }
            /*select -----------------------------------------------------------end*/
           /*file  -----------------------------------------------------------start*/
        } catch (Exception e) {
            e.printStackTrace();
        }



        //table添加样式
       Elements tableElements=doc.getElementsByTag(HtmlConstant.TABLE_AREA_TAG);
        for(int i=0;i<tableElements.size();i++){
            Element element=tableElements.get(i);
            element.removeAttr("class");
        }
        String jspCode=doc.outerHtml();
        for (String key : nodesMap.keySet()) {
            if(StringUtils.isNotBlank(key)){
                if(jspCode.contains(key)){
                    jspCode=jspCode.replace(key,nodesMap.get(key));
                    if(jspCode.contains("##start_")&&jspCode.contains("##end_")&&jspCode.contains("###end")){
                        jspCode=jspCode
                                .replaceAll("##start_","<c")
                                .replaceAll("##end_","</")
                                .replaceAll("_###end",">");
                    }

                }
            }
        }

        Document bodyCode = Jsoup.parseBodyFragment(jspCode);

        Element body = bodyCode.body();
        Node node= nodeDao.findNodeByFlowIdAndBeginNode(currentFlowId,true);

        //只保留body标签中的数据
        jspCode=body.outerHtml().replace("<body>","").replace("</body>","").replaceAll("\\{\\|\\-","").replaceAll("-\\|}","");
        if(jspCode.contains("${selectTree}")){
            String startWhen="\n\r\t\t##start_:when test=\"${'"+node.getName()+"'==taskName"+"}\"_###end\n\r";
            String endWhen="\n\r\t\t##end_c:when_###end\n\r";
            String otherwiseStart="\n\r\t\t##start_:otherwise _###end\n\r";
            String otherwiseEnd="\n\r\t\t##end_c:otherwise _###end\n\r";
            String treeCode="##chooseStart\t\t\t" + startWhen+"\r\n"+HtmlConstant.TREE_SELECT_TAG+endWhen+"\r\n"+
                    otherwiseStart+"\n\r"+HtmlConstant.TREE_SELECT_VIEW_TAG+"\n\r"+otherwiseEnd+"##chooseEnd";
            jspCode=jspCode.replace("${selectTree}",treeCode);
        }
        if(jspCode.contains("##chooseStart")){
            jspCode=jspCode.replaceAll("##chooseStart","<c:choose>");
        }
        if(jspCode.contains("##chooseEnd")){
            jspCode=jspCode.replaceAll("##chooseEnd","</c:choose>");
        }
        if(jspCode.contains("${readonly}")){
            jspCode=jspCode.replace("${readonly}","true");
        }
        if(jspCode.contains("${timeSelect}")){
            String startWhen="\n\r\t\t##start_:when test=\"${'"+node.getName()+"'==taskName"+"}\"_###end\n\r";
            String endWhen="\n\r\t\t##end_c:when_###end\n\r";
            String otherwiseStart="\n\r\t\t##start_:otherwise _###end\n\r";
            String otherwiseEnd="\n\r\t\t##end_c:otherwise _###end\n\r";
            String timeCode="##chooseStart\t\t\t" + startWhen+"\r\n"+HtmlConstant.TIME_SELECT_TAG+endWhen+"\r\n"+
                    otherwiseStart+"\n\r"+HtmlConstant.TIME_SELECT_VIEW_TAG+"\n\r"+otherwiseEnd+"##chooseEnd";
            jspCode=jspCode.replace("${timeSelect}",timeCode);
        }
        if(jspCode.contains("${checkState}")){
            jspCode=jspCode.replace("${checkState}","checked=\"checked\"");
        }
        if(jspCode.contains("${selectstate}")){
            jspCode=jspCode.replace("${selectstate}","selected=\"selected\"");
        }
        //add by ym  default time and dafaule user

        if(jspCode.contains("${defultUser}")){
            String startWhen="\n\r\t\t##start_:when test=\"${'"+node.getName()+"'==taskName"+"}\"_###end\n\r";
            String endWhen="\n\r\t\t##end_c:when_###end\n\r";
            String otherwiseStart="\n\r\t\t##start_:otherwise _###end\n\r";
            String otherwiseEnd="\n\r\t\t##end_c:otherwise _###end\n\r";
            String userCode="##chooseStart\t\t\t" + startWhen+"\r\n"+HtmlConstant.DEFULT_USER_TAG+endWhen+"\r\n"+
                    otherwiseStart+"\n\r"+HtmlConstant.DEFULT_USER_VIEW_TAG+"\n\r"+otherwiseEnd+"##chooseEnd";
            jspCode=jspCode.replace("${defultUser}",userCode);
        }
        if(jspCode.contains("${dafultTime}")){
            String startWhen="\n\r\t\t##start_:when test=\"${'"+node.getName()+"'==taskName"+"}\"_###end\n\r";
            String endWhen="\n\r\t\t##end_c:when_###end\n\r";
            String otherwiseStart="\n\r\t\t##start_:otherwise _###end\n\r";
            String otherwiseEnd="\n\r\t\t##end_c:otherwise _###end\n\r";
            String timeCode="##chooseStart\t\t\t" + startWhen+"\r\n"+HtmlConstant.DEFULT_TIME_TAG+endWhen+"\r\n"+
                    otherwiseStart+"\n\r"+HtmlConstant.DEFULT_TIME_VIEW_TAG+"\n\r"+otherwiseEnd+"##chooseEnd";
            jspCode=jspCode.replace("${dafultTime}",timeCode);
        }
        jspCode=jspCode.replaceAll("##start_","<c").replaceAll("##end_","</").replaceAll("_###end",">").replaceAll("##chooseStart","<c:choose>").replaceAll("##chooseEnd","</c:choose>").replaceAll("foreach","forEach").replaceAll("<code>","").replaceAll("</code>","");
        long endTime = System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
        return jspCode;

    }

    public FormDesign findFormDesignByCurrentFlowId(String currentFlowId){
        FormDesign formDesign=formDesignDao.findFormDesignByCurrentFlowId(currentFlowId);
        return formDesign;
    }

    public StringBuffer getTestInfo(List<TaskNodePermissions> taskNodePermissionsList){
        StringBuffer testInfo=new StringBuffer();
        if(taskNodePermissionsList.size()==1){
            testInfo.append("\"${'"+taskNodePermissionsList.get(0).getTaskName()+"'!=taskName}\"");
            return testInfo;
        }
        for(int k=0;k<taskNodePermissionsList.size();k++){
            String perssionTaskName=taskNodePermissionsList.get(k).getTaskName();
            if(k==0){
                testInfo.append("\"${'"+perssionTaskName+"'!=taskName" );
            }else if(k!=(taskNodePermissionsList.size()-1)&&k>0){
                testInfo.append(" and '"+perssionTaskName+"'!=taskName");
            }else if(k==(taskNodePermissionsList.size()-1)){
                testInfo.append(" and '"+perssionTaskName+"'!=taskName }\"");
            }
        }
        System.out.println(testInfo);
        return testInfo;
    }

    public String editTableWidth(String content,String width){
        Document doc= Jsoup.parseBodyFragment(content);
        Elements tableElements=doc.getElementsByTag(HtmlConstant.TABLE_AREA_TAG);
        for(int i=0;i<tableElements.size();i++){
            Element element=tableElements.get(i);
            element.removeAttr("width");
            String style=tableElements.attr("style");
            style=style+"width:"+width+"px;";
            element.attr("style",style);
        }
        content=doc.outerHtml();
        return content;
    }

}
