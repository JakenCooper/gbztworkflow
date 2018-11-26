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
    public static final String MODEL_COMPACT;
    public static final String MODEL_NOT_COMPACT;
   static {
       INPUT_TAG="input";

       SELECT_TAG="select";

       TEXT_AREA_TAG="textarea";

       TABLE_AREA_TAG="table";

       CHECK_BOX_TAG="checkbox";

       INPUT_TYPE_TEXT="text";

       INPUT_TYPE_RADIO="radio";

       FILE_TAG="file";

       TREE_SELECT_TAG="<sys:treeselect id=\"treeSelect\" name=\"userName\" value=\"${office.primaryPerson.name}\" labelName=\"office.primaryPerson.name\" labelValue=\"${office.primaryPerson.name}\"\n" +
               "title=\"用户\" url=\"/sys/office/treeData?type=3\" allowClear=\"true\" notAllowSelectParent=\"true\"/>";

       TIME_SELECT_TAG="<input name=\"timeset\" type=\"text\" readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate \"\n" +
               "value=\"<fmt:formatDate value=\"${testData.inDate}\" pattern=\"yyyy-MM-dd HH:mm:ss\"/>\"\n" +
               "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\"/>";

       NEW_FILE_TAG="<a href=\"javascript:;\" class=\"a-upload\">\n" +
               "    <input type=\"file\" name=\"\" >请选择文件\n" +
               "</a><p class=\"showFileName\"></p>";
       MODEL_COMPACT= "<input type=\"hidden\" value=\"0\"/>";
       MODEL_NOT_COMPACT="<input type=\"hidden\" value=\"1\"/>";
   }
}
