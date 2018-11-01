package com.gbzt.gbztworkflow.modules.formDesign.service;


import com.gbzt.gbztworkflow.modules.formDesign.Util.HtmlConstant;
import com.gbzt.gbztworkflow.modules.formDesign.Util.UeditorTools;
import com.gbzt.gbztworkflow.modules.formDesign.dao.FormDesignDao;
import com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.dao.TaskPermissionsDao;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
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

    public void save(FormDesign formDesign) throws ParseException {
     /*   if(formDesign.getCreateDate()!=null){
            formDesignDao.saveAndFlush(formDesign);
           *//* formDesignDao.update(formDesign.getFormHtml(),formDesign.getJspCode(),formDesign.getCurrentFlowId());*//*
        }else{*/
        if(formDesign!=null &&formDesignDao.findFormDesignByCurrentFlowId(formDesign.getCurrentFlowId())!=null){
            formDesignDao.updateByflow(formDesign.getFormHtml(),formDesign.getJspCode(),formDesign.getCurrentFlowId());
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

  /*      for(int i=0;i<inputElements.size();i++){
            Element element=inputElements.get(i);
            String id=element.attr("id");
            //拼接lable lable="<lable for="+id+">"+id+"</lable>:";
            String lable="";
            if(element.parentNode().outerHtml().contains("lable")){
                 continue;
            }else{
                lable="<lable for="+id+">"+id+"</lable>:";
            }

            element.before(lable);
        }*/
        Elements tableElements=doc.getElementsByTag(HtmlConstant.TABLE_AREA_TAG);
        for(int i=0;i<tableElements.size();i++){
            Element element=tableElements.get(i);
            element.addClass("table  table-bordered table-condensed");
        }
        return doc;
    }

    public String createJsp(String parseForm, String currentFlowId, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Document doc= Jsoup.parseBodyFragment(parseForm);
        Elements inputElements=doc.getElementsByTag(HtmlConstant.INPUT_TAG);
        Elements textareaElements=doc.getElementsByTag(HtmlConstant.TEXT_AREA_TAG);
        Elements selectElements=doc.getElementsByTag(HtmlConstant.SELECT_TAG);
        //用来jsp code
        Map<String,String> nodesMap=new HashMap<>();
        try {
            /*input -------------------------------------------------------------start*/
            for(int i=0;i<inputElements.size();i++){
                Element element=inputElements.get(i);
                String id=element.attr("id");
                String name=element.attr("name");
                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    element.attr("name",name);
                }
                String style=element.attr("style");
                //字段名称
                String fieldName=element.attr("fieldname");
                //拼接c:if
                List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsDao.findByTaskNodeIdAndAndCurrentFlowId(id,currentFlowId);
                for(int j=0;j<taskNodePermissionsList.size();j++){
                    String permission=taskNodePermissionsList.get(j).getPermission();
                    String taskName=taskNodePermissionsList.get(j).getTaskName();
                    String startCif="\n\r\t\t##start_:if test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                    String endCif="\n\r\t\t##end_c:if_###end\n\r";
                    /*String lable="\n\r\t\t\t<lable for=\""+id+"\">"+fieldName+"</lable>:\n\r";*/
                    //重置前一个input样式
                    element.attr("style",style);
                    element.removeAttr("readonly");
                    if("不可见".equals(permission)){
                        //设置新的权限
                        element.attr("style",style+"display:none");
                    }
                    if("不可编辑".equals(permission)){
                        element.attr("readonly","${readonly}");
                    }
                    String outerHtml=element.outerHtml();
                    String type=element.attr("type");
                    if(HtmlConstant.INPUT_TYPE_TEXT.equals(type)){
                        outerHtml=outerHtml.replace("input","form:input").replace("name","path").replace(">","/>");
                    }
                   /* if(outerHtml.contains("readonly")){
                        outerHtml=outerHtml.replace("readonly","readonly=\"readonly\"");
                    }*/
                    String code=startCif+"\t\t\t"+outerHtml+endCif;
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    element.after(key);
                    nodesMap.put(key,code);

                }
                if(taskNodePermissionsList.size()==0){
                    String uuid=UUID.randomUUID().toString();

                    String type=element.attr("type");
                    String key="##tranJsp"+uuid+"##tranJsp_";

                    if(HtmlConstant.INPUT_TYPE_TEXT.equals(type)){
                        String outerHtml=element.removeAttr("type").outerHtml();
                        // = 号为避免实体类属性中也存在name字符串
                        outerHtml=outerHtml.replace("input","form:input").replace("name=","path=").replace(">","/>");
                        nodesMap.put(key,"\t\t\t"+outerHtml);
                    }
                    if(HtmlConstant.CHECK_BOX_TAG.equals(type)){
                        String outerHtml=element.removeAttr("type").outerHtml();
                        outerHtml=outerHtml.replace("input","form:checkbox").replace("name=","path=").replace(">","/>");
                        nodesMap.put(key,"\t\t\t"+outerHtml);
                    }
                    if(HtmlConstant.INPUT_TYPE_RADIO.equals(type)){
                        String outerHtml=element.removeAttr("type").outerHtml();
                        outerHtml=outerHtml.replace("input","form:radiobutton").replace("name=","path=").replace(">","/>");
                        if(outerHtml.contains("checked")){
                            outerHtml=outerHtml.replace("checked","${checkState}");
                        }
                        nodesMap.put(key,"\t\t\t"+outerHtml);
                    }
                    if(HtmlConstant.FILE_TAG.equals(type)){
                        String outerHtml=element.outerHtml();
                        nodesMap.put(key,"\t\t\t"+HtmlConstant.NEW_FILE_TAG);
                    }

                    element.after(key);
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
                String name=element.attr("name");
                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    element.attr("name",name);
                }
                //拼接c:if
                List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsDao.findByTaskNodeIdAndAndCurrentFlowId(id,currentFlowId);
                for(int j=0;j<taskNodePermissionsList.size();j++){
                    String permission=taskNodePermissionsList.get(j).getPermission();
                    String taskName=taskNodePermissionsList.get(j).getTaskName();
                    String startCif="\n\r\t\t##start_:if test=\"${'"+taskName+"'==taskName"+"}\"_###end\n\r";
                    String endCif="\n\r\t\t##end_c:if_###end\n\r";
                   /* String lable="\n\r\t\t\t<lable for=\""+id+"\">"+fieldName+"</lable>:\n\r";*/
                    //重置前一个input样式
                    element.attr("style",style);
                    element.removeAttr("readonly");
                    if("不可见".equals(permission)){
                        //设置新的权限
                        element.attr("style",style+"display:none");
                    }
                    if("不可编辑".equals(permission)){
                        element.attr("readonly","${readonly}");
                    }
                    String outerHtml=element.outerHtml();
                    outerHtml=outerHtml.replace("textarea","form:textarea").replace("name","path").replace(">","/>");
                   /* if(element.parentNode().outerHtml().contains("lable")){
                        lable="";
                    }*/
                    String code=startCif+"\t\t\t"+outerHtml+endCif;
                    String uuid=UUID.randomUUID().toString();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    element.after(key);
                    nodesMap.put(key,code);

                }
                if(taskNodePermissionsList.size()==0){
                    String uuid=UUID.randomUUID().toString();
                    String outerHtml=element.outerHtml();
                    String key="##tranJsp"+uuid+"##tranJsp_";
                    /*outerHtml=outerHtml.replace("input","form:input").replace("name","path").replace(">","/>");*/

                    nodesMap.put(key,"\t\t\t"+outerHtml);
                    element.after(key);
                }
                element.remove();
            }
            /*textarea ---------------------------------------------------------end*/
            /*select -----------------------------------------------------------start*/
            for(int i=0;i<selectElements.size();i++){
                Element selectElement=selectElements.get(i);
                String name=selectElement.attr("name");
                if(name.contains("_")){
                    name= UeditorTools.humpNomenclature(name);
                    selectElement.attr("name",name);
                }
                // < 避免字段中有option 字符串
                String outerHtml=selectElement.outerHtml().replaceFirst("select","form:select").replace("<option","<form:option").replace("name","path");
                if(outerHtml.contains("selected")){
                    outerHtml=outerHtml.replace("selected","${selectState}");
                }
                String uuid=UUID.randomUUID().toString();
                String key="##tranJsp"+uuid+"##tranJsp_";
                selectElement.after(key);
                nodesMap.put(key,outerHtml);
                selectElement.remove();
            }
            /*select -----------------------------------------------------------end*/

        } catch (Exception e) {
            e.printStackTrace();
        }



        //table添加样式
        Elements tableElements=doc.getElementsByTag(HtmlConstant.TABLE_AREA_TAG);
        for(int i=0;i<tableElements.size();i++){
            Element element=tableElements.get(i);
            element.addClass("table table-bordered table-condensed");
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
        //只保留body标签中的数据
        jspCode=body.outerHtml().replace("<body>","").replace("</body>","").replaceAll("\\{\\|\\-","").replaceAll("-\\|}","");
        if(jspCode.contains("${selectTree}")){
            jspCode=jspCode.replace("${selectTree}",HtmlConstant.TREE_SELECT_TAG);
        }
        if(jspCode.contains("${readonly}")){
            jspCode=jspCode.replace("${readonly}","readonly");
        }
        if(jspCode.contains("${timeSelect}")){
            jspCode=jspCode.replace("${timeSelect}",HtmlConstant.TIME_SELECT_TAG);
        }
        if(jspCode.contains("${checkState}")){
            jspCode=jspCode.replace("${checkState}","checked");
        }
        File file = new File("d://test.jsp");
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(file));
            ps.println(jspCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return jspCode;
    }

    public FormDesign findFormDesignByCurrentFlowId(String currentFlowId){
        FormDesign formDesign=formDesignDao.findFormDesignByCurrentFlowId(currentFlowId);
        return formDesign;
    }

}
