package com.gbzt.gbztworkflow.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class EntityUtils {

	private static final String EXTENDS_SECTION_PREFIX = " extends ActEntity<";
	private static final String EXTENDS_SECTION_SUFFIX = "> implements FormatAttribute,Cloneable";
	private static final Pattern EXTENDS_PATTERN;
	private static final String CLONE_SECTION;
	private static final String FORMAT_SECTION;
	private static final String PACKAGE_ENTITY;
	private static final String PACKAGE_FORMATATTRIBUTE;
	private static final String PACKAGE_REQUEST;

	private static final String ANNOTATION_CHARGE_PREFIX_STR = "get";
	private static final Pattern ANNOTATION_CHARGE_PATTERN;
	private static final String ANNOTATION_PREFIX_STR = "@FormItemAtt(dispalyName = \"";
	private static final String ANNOTATION_SUFFIX_STR = "\")";
	private static final String COMMON_NEXT_LINE = "\n";
	private static final String COMMON_TAB = "\t";


	static{
		EXTENDS_PATTERN = Pattern.compile("public class (\\w+)\\s{0,}\\{");
		StringBuffer cloneSectionBuffer = new StringBuffer();
		cloneSectionBuffer.append("\t").append("@Override").append("\n");
		cloneSectionBuffer.append("\t").append("public Object clone() throws CloneNotSupportedException {").append("\n");
		cloneSectionBuffer.append("\t").append("\t").append("return super.clone();").append("\n");
		cloneSectionBuffer.append("\t").append("}").append("\n").append("\n");
		CLONE_SECTION= cloneSectionBuffer.toString();
		StringBuffer formatSectionBuffer = new StringBuffer();
		formatSectionBuffer.append("\t").append("@Override").append("\n");
		formatSectionBuffer.append("\t").append("public void setFormatAttribute(HttpServletRequest dataSrc) {").append("\n");
		formatSectionBuffer.append("\t").append("}").append("\n").append("\n");
		FORMAT_SECTION = formatSectionBuffer.toString();
		PACKAGE_ENTITY = "import com.thinkgem.jeesite.common.persistence.ActEntity;";
		PACKAGE_FORMATATTRIBUTE = "import com.thinkgem.jeesite.modules.FormatAttribute;";
		PACKAGE_REQUEST = "import javax.servlet.http.HttpServletRequest;"+"\n";

		ANNOTATION_CHARGE_PATTERN = Pattern.compile("\\s{0,}public.+(get.+)\\(.{0,}\\)\\s{0,}\\{");
	}



	public static void addCommonMethods4Entity(String absFilePath, Map<String,String> attrMap){
		BufferedReader bufferedReader = null;
		boolean brCloseTag = false;
		PrintWriter pw = null;
		try{
			File targetFile = new File(absFilePath);
			if(!targetFile.isFile() || !targetFile.exists()){
				throw new IllegalArgumentException("file not exists..");
			}
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile)));
			List<String> contentList = new ArrayList<String>();
			String content = null;
			while((content=bufferedReader.readLine()) != null){
				if(!content.contains("public class")){
					contentList.add(content);
					Iterator<String> attrKeyIter = attrMap.keySet().iterator();
					begining: while(attrKeyIter.hasNext()){
						String attrKey = attrKeyIter.next();
						Matcher attrMatcher = ANNOTATION_CHARGE_PATTERN.matcher(content);
						while(attrMatcher.find()){
							String mn = attrMatcher.group(1);
							if(mn.toLowerCase().equals((ANNOTATION_CHARGE_PREFIX_STR + attrKey).toLowerCase())){
								String annotationStr = COMMON_TAB + ANNOTATION_PREFIX_STR + attrMap.get(attrKey) +
										ANNOTATION_SUFFIX_STR ;
								contentList.add(contentList.size() -1, annotationStr);
								break begining;
							}
						}
					}
					continue;
				}
				StringBuffer tempBuffer = new StringBuffer(content);
				if(content.contains("extends")){
					System.out.println("already converted!");
					return ;
				}
				String className = null;
				Matcher classNameMatcher = EXTENDS_PATTERN.matcher(content);
				while(classNameMatcher.find()){
					className = classNameMatcher.group(1);
				}
				if(StringUtils.isBlank(className)){
					throw new IllegalAccessError("illegal class file!");
				}
				String finalExtendSection = EXTENDS_SECTION_PREFIX+className+EXTENDS_SECTION_SUFFIX;
				tempBuffer.insert(content.length() - 1, finalExtendSection);
				contentList.add(tempBuffer.toString());
			}
			if(contentList.size()>0){
				int classIndex = -1;
				for(int index = 0 ;index<contentList.size();index++){
					if(contentList.get(index).contains("public class")){
						classIndex = index;
					}
				}
				contentList.add(classIndex,PACKAGE_REQUEST);
				contentList.add(classIndex,PACKAGE_FORMATATTRIBUTE);
				contentList.add(classIndex,PACKAGE_ENTITY);
				int lastIndex = -1;
				for(int index=contentList.size()-1;index>=0;index--){
					if(contentList.get(index).contains("}")){
						lastIndex = index;
						break;
					}
				}
				contentList.add(lastIndex, FORMAT_SECTION);
				contentList.add(lastIndex, CLONE_SECTION);
				try {
					// terminate reader buffer.
					bufferedReader.close();
					brCloseTag = true;
				} catch (Exception e) {
				}
				pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile)));
				for(String writeContent : contentList){
					pw.println(writeContent);
				}
				pw.flush();
				System.out.println("转换成功！");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(bufferedReader != null && !brCloseTag){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(pw != null){
				try {
					pw.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} // end of finally
	}

	public static void main(String[] args){
		//EntityUtils.addCommonMethods4Entity("D:\\testfiles\\UnitSendFile.java");

		Map<String,String> controllerMap = new HashMap<String,String>();
		controllerMap.put("sendOpinion","签发意见");
		controllerMap.put("sendOpinionUser","签发人");
		controllerMap.put("articleSize","文号");

		EntityUtils.addCommonMethods4Entity("D:\\testfiles\\UnitSendFile.java",controllerMap);
	}
}
