package com.syc.blog.controller.onlineutils;

import com.alibaba.fastjson.JSON;
import com.syc.blog.controller.BaseController;
import com.syc.blog.utils.ResultHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 正则表达式
 * */
@Controller
@RequestMapping("/util/sqlToJava")
public class SqlToJavaController extends BaseController {

    @RequestMapping("")
    public String sqlToJava(ModelMap map, @RequestParam(value = "page",required = false,defaultValue = "1") Integer page
    , @RequestParam("bindId") Integer bindId) {
        byte type = 13;
        getCurrentCommentsListPage(map,page,bindId,type);
        map.put("bindId",bindId);
        return "onlineutils/sql_to_java";
    }

    @RequestMapping("/transfer")
    @ResponseBody
    public String transfer(@RequestParam("sql") String sql){
        ResultHelper result = null;
        /**
         * 分析字符串，词法分析器 ``
         * */
        if(!sql.contains("CREATE TABLE")|| !sql.contains("(")){
            return JSON.toJSONString(ResultHelper.wrapErrorResult(1,"请确定建表语句完整"));
        }
        int n = sql.indexOf("(");
        sql =sql.substring(n);
        System.out.println(sql);
        StringBuilder sb = new StringBuilder();
        char[] chars = sql.toCharArray();
        boolean flag = false;
        int count = 0;
        for(int i = 0; i< chars.length; i++){
            char c = chars[i];
            if(flag && count != 2){
                sb.append(i);
            }
            if(c == '`'){ //字段开始
                count++;
                flag = true;
            }
        }
        return "";
    }

}
