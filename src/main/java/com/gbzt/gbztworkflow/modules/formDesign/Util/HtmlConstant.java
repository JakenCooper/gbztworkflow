package com.gbzt.gbztworkflow.modules.formDesign.Util;

import org.apache.commons.lang3.StringUtils;

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
               "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\"/>";

       DEFULT_USER_TAG="<input name=\"draftUser\" type=\"text\" readonly=\"readonly\" value=\"${fns:getUser().name}\"/>";
       
       DEFULT_TIME_TAG="<c:set var=\"now\" value=\"<%=new java.util.Date()%>\"/> <fmt:formatDate type=\"both\" value=\"${now}\"/> <input type=\"hidden\" value=\"${now}\" name=\"defultCreateTime\"/> ";

     /*  NEW_FILE_TAG="<a href=\"javascript:;\" class=\"a-upload\">\n" +
               "    <input id=\"file\" type=\"file\" name=\"file\"  >请选择文件\n" +
               "</a><span class=\"showFileName\"></span>";*/

       NEW_FILE_TAG="<div id=\"filePicker\">选择附件</div>\n" +
               "<!--用来存放文件信息-->\n" +
               "<div id=\"fileList\"></div>";
       MODEL_COMPACT= "<input type=\"hidden\" value=\"0\"/>";
       MODEL_NOT_COMPACT="<input type=\"hidden\" value=\"1\"/>";

       FILE_LIST_TAG="  <c:forEach var=\"obj\" items=\"${fileList}\">\n" +
               "<a class=\"btn-link\" onclick=\"downloadAttachNew('${ctx}','${obj.fileUrl}','${obj.fileName}')\">${obj.fileName}</a>\n" +
               "</c:forEach>";
       DEFULT_USER_VIEW_TAG="\n\r\t\t\t<form:input path=\"draftUser\" type=\"text\" title=\"起草人\"    style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />";
       DEFULT_TIME_VIEW_TAG="\n\r\t\t\t<form:input path=\"defultCreateTime\" type=\"text\" title=\"起草时间\"  style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />";
       TIME_SELECT_VIEW_TAG="\n\r\t\t\t<form:input path=\"draftSelectTime\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />";
       TREE_SELECT_VIEW_TAG="\n\r\t\t\t<form:input path=\"userName\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />";
       USER_TREE_TYPE_TAG="userTree";
   }
   public static String getDateInput(String path,String perssion,String startName){
       String html="";
       switch (perssion){
           case "不可编辑":
               html= "<form:input path=\"" +path+
                       "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />\";";
               break;
           case "不可见":
               html= "<input name=\"" +path+
                       "\" type=\"text\" readonly=\"readonly\"style=\"display:none\" class=\"input-medium Wdate \"\n" +
                       "value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"/>\"\n" +
                       "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\"/>";
               break;
           case "可见":
               html= "<form:input path=\"" +path+
                       "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />\";";
               break;
           case "可编辑":
               html="<input name=\"" +path+
                       "\" type=\"text\" readonly=\"readonly\"  class=\"input-medium Wdate \"\n" +
                       "value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"/>\"\n" +
                       "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\"/>";
            default:
                html= "<form:input path=\"" +path+
                        "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />\";";
       }
       if(StringUtils.isBlank(perssion)){
           String result=
                   "<c:choose>" +
                           "<c:when test=\"${" +startName+
                           "==taskName}\">" +
                                   "<input name=\"" +path+
                                   "\" type=\"text\" readonly=\"readonly\"  class=\"input-medium Wdate \"\n" +
                                   "value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd \"/>\"\n" +
                                   "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\"/>"+
                                   "</c:when>"+
                           "<c:otherwise>" +
                               "<form:input path=\"" +path+
                               "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />\";"+
                           "</otherwise>"+
                           "</choose>";
           return result;
       }
     return html;
   }
    public static String getUserTreeTag(String path,String perssion ,String startName){
        String html="";
        switch (perssion){
            case "不可编辑":
                html= "<form:input path=\"" +path+
                        "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />\";";
                break;
            case "不可见":
                html= "<form:hidden path=\"" +path+
                        "\" />";
                break;
            case "可编辑":
                html="\t\t\t<sys:treeselect id=\"treeSelect\" name=\"" +path+
                        "\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
                        "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>";
                break;
            case "可见":
                html="<form:input path=\"" +path+
                        "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />\";";
                break;
            default:
                html="\t\t\t<sys:treeselect id=\"treeSelect\" name=\"" +path+
                        "\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
                        "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>";
        }
        if(StringUtils.isBlank(perssion)){
            String result=
                    "<c:choose>" +
                        "<c:when test=\"${" +startName+
                        "==taskName}\">" +
                            "\t\t\t<sys:treeselect id=\"treeSelect\" name=\"" +path+
                            "\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
                            "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>"+
                        "</c:when>"+
                        "<c:otherwise>" +
                             "<form:input path=\"" +path+
                            "\" type=\"text\" style=\"text-align: left; width: 150px;\" readonly=\"readonly\" />\";"+
                            "</otherwise>"+
                    "</choose>";
            return result;
        }

        return html;
    }
}
