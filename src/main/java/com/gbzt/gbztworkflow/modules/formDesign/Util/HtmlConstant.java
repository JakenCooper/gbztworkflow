package com.gbzt.gbztworkflow.modules.formDesign.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class HtmlConstant {
    public static final String INPUT_TAG;
    public static final String SELECT_TAG;
    public static final String TEXT_AREA_TAG;
    public static final String TABLE_AREA_TAG;
    public static final String CHECK_BOX_TAG;
    public static final String INPUT_TYPE_TEXT;
    public static final String INPUT_TYPE_RADIO;
    public static final String TREE_SELECT_TAG;
    public static final String TIME_SELECT_TAG;
    public static final String FILE_TAG;
    public static final String NEW_FILE_TAG;
    public static final String MODEL_COMPACT;
    public static final String MODEL_NOT_COMPACT;
    public static final String DEFULT_USER_TAG;
    public static final String DEFULT_TIME_TAG;
    public static final String FILE_LIST_TAG;
    public static final String DEFULT_USER_VIEW_TAG;
    public static final String DEFULT_TIME_VIEW_TAG;
    public static final String TIME_SELECT_VIEW_TAG;
    public static final String TREE_SELECT_VIEW_TAG;
    public static final String TD_TAG;
    public static final String DATE_INPUT_TYPE_TAG;
    public static final String USER_TREE_TYPE_TAG;

   static {
       INPUT_TAG="input";

       SELECT_TAG="select";

       TEXT_AREA_TAG="textarea";

       TABLE_AREA_TAG="table";

       CHECK_BOX_TAG="checkbox";

       INPUT_TYPE_TEXT="text";

       INPUT_TYPE_RADIO="radio";

       FILE_TAG="file";

       TD_TAG="td";

       DATE_INPUT_TYPE_TAG="date";

       TREE_SELECT_TAG="\t\t\t<sys:treeselect id=\"treeSelect\" name=\"userName\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
               "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>";

       TIME_SELECT_TAG="<input name=\"timeSelect\" type=\"text\" readonly=\"readonly\"style=\"width\" class=\"input-medium Wdate \"\n" +
               "value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"/>\"\n" +
               "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});\"/>";

       DEFULT_USER_TAG="<input  class=\"defultUser\" style=\"width:99%;height:28px;text-align:center;\" name=\"defultUser\" type=\"text\" readonly=\"readonly\" value=\"${fns:getUser().name}\"/>";
       
       DEFULT_TIME_TAG="<c:set var=\"now\" value=\"<%=new java.util.Date()%>\"/> <fmt:formatDate type=\"both\" value=\"${now}\"/> <input type=\"hidden\" value=\"${now}\" name=\"defultCreateTime\"/> ";

       // NEW_FILE_TAG="<div id=\"filePicker\">选择附件</div>\n" +
       //         "<!--用来存放文件信息--" +
       //         ">\n" +
       //         "<div id=\"fileList\"></div>";
       NEW_FILE_TAG="\n" + "<a id=\"attrBrowse\" class=\"btn btn-primary\" style=\"float: left;margin-right: 5px;\">选择附件</a><div style=\"float: left;\" id=\"fileList\"></div>\n";
       MODEL_COMPACT= "<input type=\"hidden\" value=\"0\"/>";
       MODEL_NOT_COMPACT="<input type=\"hidden\" value=\"1\"/>";

       FILE_LIST_TAG="  <c:forEach var=\"obj\" items=\"${fileList}\">\n" +
               "<a class=\"btn-link fileList\" onclick=\"downloadAttachNew('${ctx}','${obj.fileUrl}','${obj.fileName}')\">${obj.fileName}</a>\n" +
               "</c:forEach>";
       DEFULT_USER_VIEW_TAG="\n\r\t\t\t<form:input path=\"defultUser\" type=\"text\" title=\"起草人\"    style=\"text-align:center; width: 99.6%;height:28px;\" readonly=\"true\" />";
       DEFULT_TIME_VIEW_TAG="\n\r\t\t\t<form:input path=\"defultCreateTime\" type=\"text\" title=\"起草时间\"  style=\"text-align: center; width: 99.6%;height:28px;\" readonly=\"true\" />";
       TIME_SELECT_VIEW_TAG="\n\r\t\t\t<form:input path=\"draftSelectTime\" type=\"text\" style=\"text-align:center; width: 99.6%;height:28px;\" readonly=\"true\" />";
       TREE_SELECT_VIEW_TAG="\n\r\t\t\t<form:input path=\"userName\" type=\"text\" style=\"text-align:center; width: 99.6%;height:28px;\" readonly=\"true\" />";
       USER_TREE_TYPE_TAG="userTree";

   }
   public static String getInput(String name){
       String html="#start_input_form:input cssStyle=\"width: 99.6%;height: 28px;\" path=\"" +name+ "\"_#end_input";
       return html;
   }
   public static String getDateInput(String path,String perssion,String startName,String objectName){
       String html="";
       switch (perssion){
           case "不可编辑":
               html="<input name=\"" +path+
                       "\" style=\"text-align: center;width:99.6%;height:28px;"+
                       "\" type=\"text\" readonly=\"readonly\" class=\"input-medium Wdate \" value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"_#end_input\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd ',isShowClear:false});\"/>";
               break;
           case "不可见":
               html="<input name=\"" +path+
                       "\" type=\"text\" style=\"visibility:hidden;width:99%;height:28px;\" readonly=\"readonly\" class=\"input-medium Wdate \" value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"_#end_input\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd ',isShowClear:false});\"/>";
               break;
           case "可见":
               html="<input name=\"" +path+
                       "\" style=\"width:99.6%;height:28px;"+
                       "\" type=\"text\" readonly=\"readonly\" class=\"input-medium Wdate \" value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"_#end_input\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd ',isShowClear:false});\"/>";
               break;
           case "可编辑":
               html="<input name=\"" +path+
                       "\" style=\"width:99.6%;height:28px;"+
                       "\" type=\"text\" readonly=\"readonly\" class=\"input-medium Wdate \" value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"_#end_input\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd ',isShowClear:false});\"/>";
               break;
            default:
                //html= "#start_input_form:input path=\"" +path+
                  //      "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" _#end_input\";";
                html= html="<input name=\"" +path+
                        "\" style=\"width:99.6%;height:28px;"+
                        "\" type=\"text\" readonly=\"readonly\" class=\"input-medium Wdate \" value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"_#end_input\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd ',isShowClear:false});\"/>";
       }
       if(StringUtils.isBlank(perssion)){
           String result=
                   "<c:choose>" +
                           "<c:when test=\"${'" +startName+
                           "'==taskName}\">" +
                           "<input name=\"" +path+
                           "\" type=\"text\"  " +
                           "style=\"width:99%;height:28px;\""+
                           "class=\"input-medium Wdate \"" + " value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"_#end_input\"\n" +
                           "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});\"/>"+
                           "</c:when>"+
                           "<c:otherwise>" +
                             /*  "#start_input_form:input path=\"" +path+
                               "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\"  _#end_input\""+*/
                             "#start_input_fmt:formatDate value=\"${" +objectName+
                           "." +path+
                           "}\" pattern=\"yyyy-MM-dd \"_#end_input"+
                           "</otherwise>"+
                           "</choose>";
           return result;
       }
     return html;
   }
    public static String getUserTreeTag(String path,String perssion ,String startName,String objectName){
        String html="";
        switch (perssion){
            case "不可编辑":
                html="<input name=\"" +path+
                        "\" type=\"text\" value=\"${fns:getUsersByIds(" +objectName+"."+path+")[0].name}\""+
                        " style=\"dispaly:none;text-align: center; width: 99.6%;height:28px;\" readonly=\"readonly\">";
                break;
            case "不可见":
                html= "<input name=\"" +path+
                        "\" type=\"text\" value=\"${fns:getUsersByIds(" +objectName+"."+path+")[0].name}\""+
                        " style=\"dispaly:none;text-align: center; width: 99.6%;height:28px;\" readonly=\"readonly\">";
                break;
            case "可编辑":
                html="\t\t\t<sys:treeselect id=\"treeSelect\" name=\"" +path+
                        "\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
                        "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>";
                break;
            case "可见":
                html= "<input name=\"" +path+
                        "\" type=\"text\" value=\"${fns:getUsersByIds(" +objectName+"."+path+")[0].name}\""+
                        " style=\"text-align: center; width: 99.6%;height:28px;\" readonly=\"readonly\">";
                break;
            default:
                html="\t\t\t<sys:treeselect id=\"treeSelect\" name=\"" +path+
                        "\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
                        "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>";
        }
        if(StringUtils.isBlank(perssion)){
            String result=
                    "<c:choose>" +
                        "<c:when test=\"${'" +startName+
                        "'==taskName}\">" +
                            "\t\t\t<sys:treeselect id=\"treeSelect\" name=\"" +path+
                            "\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
                            "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>"+
                        "</c:when>"+
                        "<c:otherwise>" +
                             "<input name=\"" +path+
                            "\" type=\"text\" value=\"${fns:getUsersByIds(" +objectName+"."+path+")[0].name}\""+
                            " style=\"text-align: center; width: 99.6%;height:28px;\" readonly=\"readonly\">"+
                            "</otherwise>"+
                    "</choose>";
            return result;
        }

        return html;
    }
    public static String getTurnOutSelect(String color){

       String code=
               " <tr id=\"turnOutTr\" style=\"display:none;\">" +
               "       <td style=\"text-align: right;" +"border-color:"+color+
               "\"><font color=\"red\">*</font> 转出类型：</td>\n" +
               "       <td style=\"border-color:" +color+";"+
               "\">\n" +
               "           <form:select id=\"documentType\" path=\"documentType\" style=\"min-width: 150px;width:99.6%;\" class=\"input-medium required\" onchange=\"changeUtil()\">\n" +
               "               <form:option value=\"\" label=\"\" />\n" +
               "               <form:options items=\"${fns:getDictList('doc_send_type')}\" itemLabel=\"label\" itemValue=\"value\" htmlEscape=\"false\" />\n" +
               "           </form:select>\n" +
               "       </td>\n" +
               "\n" +
               "       <td style=\"text-align: right;" +"border-color:"+color+
               "\">\n" +
               "           <font id=\"help-inline\" style=\"display:none;\" color=\"red\">*</font> 是否联合<br>盖章：\n" +
               "       </td>\n" +
               "       <td style=\"border-color:" +color+";"+
               "\">\n" +
               "           <div id=\"sealUnitName\"  style=\"display:none;\">"+
               "               <form:select  path=\"sealUnitName\" style=\"min-width: 150px;width:99.6%;\" class=\"input-medium required\">\n" +
               "                   <form:option value=\"\" label=\"\" />\n" +
               "                   <form:options items=\"${fns:getDictList('unit')}\" itemLabel=\"label\" itemValue=\"value\" htmlEscape=\"false\" />\n" +
               "               </form:select>\n" +
               "           </div>\n" +
               "       </td>\n" +
               "   </tr>";
       return code;
    }
}
