package com.gbzt.gbztworkflow.utils;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.redis.entity.RedisMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtils {

    public static String genUUid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String formatDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String convertTableName(String tableName){
        if(tableName.indexOf("_") == -1 || tableName.indexOf("_") == tableName.length()-1){
            return tableName;
        }
        String[] tableNameArr = tableName.split("_");
        List<char[]> charArrList = new ArrayList<char[]>();
        for(int i=0;i<tableNameArr.length;i++){
            if(i == 0){
                continue;
            }
            charArrList.add(tableNameArr[i].toCharArray());
        }
        List<char[]> resultArrList = new ArrayList<char[]>();
        for(char[] charr : charArrList){
            char[] resultarr = Arrays.copyOf(charr,charr.length);
            resultarr[0] = Character.toUpperCase(resultarr[0]);
            resultArrList.add(resultarr);
        }
        StringBuffer resultBuffer = new StringBuffer();
        resultBuffer.append(tableNameArr[0]);
        for(char[] resultarr : resultArrList){
            resultBuffer.append(new String(resultarr));
        }
        return resultBuffer.toString();
    }

    public static <T> ExecResult<T> buildResult(boolean charge,String message,T result){
        return new ExecResult<T>(charge,message,result);
    }

    public static <T> ExecResult<T> buildResult(ExecResult<T> oriEr,Boolean charge,String message,T result){
        boolean oriCharge = oriEr.charge;
        String oriMessage = oriEr.message;
        T oriResult = oriEr.result;
        if(charge != null){
            oriCharge = charge;
        }
        if(message != null){
            oriMessage = message;
        }
        if(result != null){
            oriResult = result;
        }
        return new ExecResult<T>(oriCharge,oriMessage,result);
    }

    public static <T> ResponseEntity buildResp(Integer code,T t){
        ResponseEntity result = null;
        switch (code){
           case 200: result = ResponseEntity.status(HttpStatus.OK).body(t);break;
           case 201: result = ResponseEntity.status(HttpStatus.CREATED).body(t);break;
           case 204: result = ResponseEntity.status(HttpStatus.NO_CONTENT).body(t);break;
           case 400: result = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(t);break;
           case 401: result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(t);break;
           case 403: result = ResponseEntity.status(HttpStatus.FORBIDDEN).body(t);break;
           case 404: result = ResponseEntity.status(HttpStatus.NOT_FOUND).body(t);break;
           case 405: result = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(t);break;
           case 409: result = ResponseEntity.status(HttpStatus.CONFLICT).body(t);break;
           case 500: result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t);break;
           case 502: result = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(t);break;
           default:result = ResponseEntity.status(code).body(t);break;
        }
        return result;
    }

    public static <T> Map<String,String>  redisConvert(T t){
        try {
            Map<String,String> redisMapper = new HashMap<String,String>();
            Class classT = t.getClass();
            Class superClassT = classT.getSuperclass();
            Field[] thisfields = classT.getDeclaredFields();
            Field[] superfields = superClassT.getDeclaredFields();
            Field[] fields = new Field[thisfields.length+superfields.length];
            List<Field> flist = new ArrayList<Field>();
            for(int i=0;i<thisfields.length;i++){
                flist.add(thisfields[i]);
            }
            for(int i=0;i<superfields.length;i++){
                flist.add(superfields[i]);
            }
            fields = flist.toArray(fields);
            for(Field field : fields){
                if(checkDeclaredAnnotation(field.getDeclaredAnnotations(),RedisMapper.class)){
                    field.setAccessible(true);
                    String fieldStringValue = convertToString(field.get(t));
                    redisMapper.put(field.getName(),fieldStringValue);
                }
            }
            return redisMapper;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T redisConvert(T t,Map<String,String> redisMapper){
        try {
            Class classT = t.getClass();
            Class superClassT = classT.getSuperclass();
            Field[] thisfields = classT.getDeclaredFields();
            Field[] superfields = superClassT.getDeclaredFields();
            Field[] fields = new Field[thisfields.length+superfields.length];
            List<Field> flist = new ArrayList<Field>();
            for(int i=0;i<thisfields.length;i++){
                flist.add(thisfields[i]);
            }
            for(int i=0;i<superfields.length;i++){
                flist.add(superfields[i]);
            }
            fields = flist.toArray(fields);
            for(Field field : fields){
                if(checkDeclaredAnnotation(field.getDeclaredAnnotations(),RedisMapper.class)
                        && redisMapper.get(field.getName()) != null){
                    field.setAccessible(true);
                    Object fieldValue = convertToFieldType(field.getType(),redisMapper.get(field.getName()));
                    field.set(t,fieldValue);
                }
            }
            return t;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkDeclaredAnnotation(Annotation[] annotations, Class targetAnno){
        for(Annotation annotation:annotations){
            if(annotation.annotationType() == targetAnno){
                return true;
            }
        }
        return false;
    }

    private static <T> String convertToString(T t){
        if(t.getClass() == String.class){
            return (String)t;
        }else if(t.getClass() == Integer.class){
            return String.valueOf(t);
        }else if(t.getClass() == Long.class){
            return String.valueOf(t);
        }else if(t.getClass() == Float.class){
            return String.valueOf(t);
        }else if(t.getClass() == Double.class){
            return String.valueOf(t);
        }else if(t.getClass() == Date.class){
            Date targetDate = (Date)t;
            return String.valueOf(targetDate.getTime());
        }else if(t.getClass() == Boolean.class){
            Boolean targetCharge = (Boolean)t;
            if(targetCharge){
                return "true";
            }else{
                return "false";
            }
        }else{
            return t.toString();
        }
    }

    private static <T> T convertToFieldType(Class<T> t,String target){
        if(t == String.class){
            return (T) target;
        }else if(t == Integer.class){
            return (T)new Integer(Integer.parseInt(target));
        }else if(t == Long.class){
            return (T)new Long(Long.parseLong(target));
        }else if(t == Float.class){
            return (T)new Float(Float.parseFloat(target));
        }else if(t == Double.class){
            return (T)new Double(Double.parseDouble(target));
        }else if(t == Date.class){
            Long targetTimeMills = Long.parseLong(target);
            Date targetDate = new Date();
            targetDate.setTime(targetTimeMills);
            return (T)targetDate;
        }else if(t == Boolean.class){
            if(target.equals("true")){
                return (T)new Boolean(true);
            }else{
                return (T)new Boolean(false);
            }
        }else{
            return (T)target;
        }
    }

    public static Double getCurrentTimeMillsDouble(){
        return new Double(new Long(System.currentTimeMillis()).doubleValue());
    }

    public static String getCurrentTimeMillsString(){
        return String.valueOf(System.currentTimeMillis());
    }

    public static void main(String[] args) {
    }
}
