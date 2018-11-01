package com.gbzt.gbztworkflow.modules.formDesign.Util;

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
   static {
       INPUT_TAG="input";
       SELECT_TAG="select";
       TEXT_AREA_TAG="textarea";
       TABLE_AREA_TAG="table";
       CHECK_BOX_TAG="checkbox";
       INPUT_TYPE_TEXT="text";
       INPUT_TYPE_RADIO="radio";
       TREE_SELECT_TAG="<sys:treeselect id=\"treeSelect\" name=\"userName\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
               "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>";
       TIME_SELECT_TAG="<input class=\"form-control form_datetime\" type=\"text\"name=\"timeset\" id=\"timeset\" size=\"16\" readonly=\"true\" >";
       FILE_TAG="file";
       NEW_FILE_TAG="<a href=\"javascript:;\" class=\"a-upload\">\n" +
               "    <input type=\"file\" name=\"\" >请选择文件\n" +
               "</a><span class=\"showFileName\"><span>";
   }
}
