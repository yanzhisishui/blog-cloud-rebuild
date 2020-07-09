package com.syc.blog.controller.onlineutils;

import com.syc.blog.config.ApplicationConfig;
import com.syc.blog.controller.BaseController;
import com.syc.blog.utils.ZimgUploadHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/watermark")
public class WaterMarkController extends BaseController {

    @Autowired
    ApplicationConfig applicationConfig;
    @RequestMapping("")
    public String timestamp(ModelMap map, @RequestParam(value = "page",required = false,defaultValue = "1") Integer page
            , @RequestParam("bindId") Integer bindId) {
        byte type = 14;
        getCurrentCommentsListPage(map,page,bindId,type);
        map.put("bindId",bindId);
        return "onlineutils/water_mark";
    }


    @RequestMapping("/test")
    @ResponseBody
    public String test(@RequestParam("file") MultipartFile file){
        ZimgUploadHelper.deleteImageFromZimg(applicationConfig.getZimgAddressUrlDelete(),"8dfb6d62a34d0743a19971b87932e4ed");
        return "";
    }
}
