package com.syc.blog.constants;

import com.syc.blog.utils.SystemHelper;

public class Constant {

    public static final String IMAGE_PRESS_PATH= SystemHelper.getOperateSystem().equals("WINDOWS") ?
            "D:/test/" : "/usr/local/software/blog-server/imgpress/";
    public static final String QRCODE_LOGO_PATH=SystemHelper.getOperateSystem().equals("WINDOWS") ?
            "D:/test/" : "/usr/local/software/blog-server/qrcode/";}
