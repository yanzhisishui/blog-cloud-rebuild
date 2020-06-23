package com.syc.blog.controller;

import com.syc.blog.config.ApplicationConfig;
import com.syc.blog.utils.JsonHelper;
import com.syc.blog.utils.ZimgUploadHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {
    @Autowired
    ApplicationConfig applicationConfig;
    @RequestMapping("/uploadToZimg")
    @ResponseBody
    public String uploadToZimg(@RequestParam("file") MultipartFile file){
        String address = ZimgUploadHelper.uploadImageToZimg(file,applicationConfig.getZimgUploadUrl(),applicationConfig.getZimgAddressUrl());
        return JsonHelper.jsonForUpload(address);
    }
}
