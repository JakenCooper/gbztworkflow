package com.gbzt.gbztworkflow.modules.formDesign.Util;


public class UeditorTools {
    /**
     * 去掉分界符 {|- 和  -|}
     * @param str
     * @return
     */
    public static String formatStr(String str){
        str = replaceAll(str,"{|-","");
        str = replaceAll(str,"-|}","");
        return str;
    }
    public static String replaceAll(String strAll,String oldStr,String newStr){
        return strAll.replaceAll(escapeExprSpecialWord(oldStr), "");
    }
    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (keyword != null && !keyword.equals("")) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
}
