package com.gbzt.gbztworkflow.modules.formDesign.service;


import com.gbzt.gbztworkflow.modules.formDesign.Util.HtmlConstant;
import com.gbzt.gbztworkflow.modules.formDesign.dao.FormDesignDao;
import com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.dao.TaskPermissionsDao;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        Document permissionsDoc=Jsoup.parse(html);
        Elements inputElements=doc.getElementsByTag(HtmlConstant.INPUT_TAG);
        Elements allElements=doc.getAllElements();

        for(int i=0;i<inputElements.size();i++){
            Element element=inputElements.get(i);
            String id=element.attr("id");
            //拼接lable
            String lable="<lable for="+id+">"+id+"</lable>:";
            element.before(lable);
        }
        Elements textAreaElements=doc.getElementsByTag(HtmlConstant.TEXT_AREA_TAG);
        for(int i=0;i<textAreaElements.size();i++){
            Element element=textAreaElements.get(i);
            element.addClass("table  table-bordered table-condensed");
        }
        return doc;
    }

    public String createJsp(String parse_form, String currentFlowId, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Document doc= Jsoup.parseBodyFragment(parse_form);
        Document permissionsDoc=Jsoup.parse(parse_form);
        //Document document=Jsoup.parseBodyFragment(parse_form);
        System.out.println("_______________________格式化代码——————————————————————");
        //System.out.println(document.outerHtml());
        System.out.println("_______________________格式化代码——————————————————————");
        Elements inputElements=doc.getElementsByTag(HtmlConstant.INPUT_TAG);
        Map<String,String> nodesMap=new HashMap<>();
        try {
            for(int i=0;i<inputElements.size();i++){
                Element element=inputElements.get(i);
                String id=element.attr("id");
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
                    String lable="\n\r\t\t\t<lable for=\""+id+"\">"+fieldName+"</lable>:\n\r";
                    //重置前一个input样式
                    element.attr("style",style);
                    element.removeAttr("readonly");
                    if("不可见".equals(permission)){
                        //设置新的权限
                        element.attr("style",style+"display:none");
                    }
                    if("不可编辑".equals(permission)){
                        element.attr("readonly","readonly");
                    }
               /*   element.before(startCif);
                    element.after(endCif);
                    element.before(lable); */
                    String outerHtml=element.outerHtml();
                    outerHtml=outerHtml.replace("input","form:input").replace("name","path").replace(">","/>");
                    if(outerHtml.contains("readonly")){
                        outerHtml=outerHtml.replace("readonly","readonly=\"readonly\"");
                    }
                    String code=startCif+lable+"\t\t\t"+outerHtml+endCif;
                    String uuid=UUID.randomUUID().toString();
                    System.out.println("复制的节点"+code);
                    element.after("##tranJsp"+uuid+"##tranJsp_");
                    nodesMap.put("##tranJsp"+uuid+"##tranJsp_",code);

                }
                if(taskNodePermissionsList.size()==0){
                    String uuid=UUID.randomUUID().toString();
                    String outerHtml=element.outerHtml();
                    outerHtml=outerHtml.replace("input","form:input").replace("name","path").replace(">","/>");
                    if(outerHtml.contains("readonly")){
                        outerHtml=outerHtml.replace("readonly","readonly=\"readonly\"");
                    }
                    nodesMap.put("##tranJsp"+uuid+"##tranJsp_","\t\t\t"+outerHtml);
                    element.after("##tranJsp"+uuid+"##tranJsp_");
                }
                element.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //table添加样式
        Elements textAreaElements=doc.getElementsByTag(HtmlConstant.TABLE_AREA_TAG);
        for(int i=0;i<textAreaElements.size();i++){
            Element element=textAreaElements.get(i);
            element.addClass("table  table-bordered table-condensed");
        }
        String jspCode=doc.outerHtml();
    /*    if(jspCode.contains("##start_")&&jspCode.contains("##end_")&&jspCode.contains("###end")){
            jspCode=jspCode.replaceAll("##start_","<c").replaceAll("##end_","</").replaceAll("_###end",">");
        }*/
        for (String key : nodesMap.keySet()) {
            if(StringUtils.isNotBlank(key)){
                if(jspCode.contains(key)){
                    System.out.println(nodesMap.get(key));
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


        System.out.println("-----------------------------------------code------------------------------------------");
        System.out.println(jspCode);
        System.out.println("----------------------------------------create-----------------------------------------");
        String path = session.getServletContext().getRealPath("/");
        System.out.println("路径"+path);
        Document bodyCode = Jsoup.parseBodyFragment(jspCode);
        Element body = bodyCode.body();
        System.out.println("-----------------------------------------code  body------------------------------------------");
        System.out.println();
        jspCode=body.outerHtml().replace("<body>","").replace("</body>","");
        return jspCode;
    }

    public FormDesign findFormDesignByCurrentFlowId(String currentFlowId){
        FormDesign formDesign=formDesignDao.findFormDesignByCurrentFlowId(currentFlowId);
        return formDesign;
    }
}
