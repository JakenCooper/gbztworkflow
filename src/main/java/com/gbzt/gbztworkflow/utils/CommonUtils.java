package com.gbzt.gbztworkflow.utils;

import com.gbzt.gbztworkflow.consts.ExecResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    public static void main(String[] args) {
    }
}
