package com.gbzt.gbztworkflow.modules.formDesign.Util;
/*
*JSOUP 拼接字符串时会自动添加结束标签 用自定义字符串替代
* 2019-1-18
* */
public class JstlConstant {
     public static final String C_START;
     public static final String C_END;
     public static final String C_CHOOSE_END;
     public static final String C_CHOOSE_START;
     public static final String END_TAG;
     public static final String START_INPUT;
     public static final String END_IPUT;
     static {
         C_START="##start_";//最终替换成:<c
         C_END="_###end";//最终替换成：>
         C_CHOOSE_END="##chooseEnd";//最终替换成：</c:choose>
         C_CHOOSE_START="##chooseStart";//最终替换成：<c:choose>
         END_TAG="##end_";//最终替换成</
         START_INPUT="#start_input_";//最终替换成<
         END_IPUT="_#end_input";//最终替换成/>
     }
}
