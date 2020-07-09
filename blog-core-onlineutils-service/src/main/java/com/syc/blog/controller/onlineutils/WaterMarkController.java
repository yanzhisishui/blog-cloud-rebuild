package com.syc.blog.controller.onlineutils;

import com.syc.blog.config.ApplicationConfig;
import com.syc.blog.controller.BaseController;
import com.syc.blog.utils.JsonHelper;
import com.syc.blog.utils.ZimgUploadHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/util/watermark")
public class WaterMarkController extends BaseController {

    @Autowired
    ApplicationConfig applicationConfig;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @RequestMapping("")
    public String timestamp(ModelMap map, @RequestParam(value = "page",required = false,defaultValue = "1") Integer page
            , @RequestParam("bindId") Integer bindId) {
        byte type = 14;
        getCurrentCommentsListPage(map,page,bindId,type);
        map.put("bindId",bindId);
        return "onlineutils/water_mark";
    }


    /**
     * 上传需要添加水印的原图
     * */
    @RequestMapping("/uploadOrigin")
    @ResponseBody
    public String test(@RequestParam("file") MultipartFile file){
        String s = ZimgUploadHelper.uploadImageToZimgResource(file, applicationConfig.getZimgUploadUrl(), applicationConfig.getZimgAddressUrl());
        String md5 = s.replace(applicationConfig.getZimgAddressUrl(),"").replace("?p=0","");
        //把md5保留到redis，为了以后删除，因为这里工具使用的图片并不需要长期存储，占用空间
        stringRedisTemplate.opsForSet().add("zimg:image:delete",md5);
        //ZimgUploadHelper.deleteImageFromZimg(applicationConfig.getZimgAddressUrlDelete(),"8dfb6d62a34d0743a19971b87932e4ed");
        return JsonHelper.jsonForUpload(s);
    }
}
