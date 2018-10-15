package com.gbzt.gbztworkflow.modules.test;

import com.gbzt.gbztworkflow.utils.LogUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    private Logger logger = Logger.getLogger(TestController.class);

    private static String LOGGER_TYPE_PREFIX = "TestController,";

    @RequestMapping("")
    public String test(){
        String loggerType = LOGGER_TYPE_PREFIX+"test";
        logger.info(LogUtils.getMessage(loggerType,"测试"));
        return "test/test.jsp";
    }
}
