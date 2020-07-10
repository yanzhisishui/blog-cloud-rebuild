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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    @RequestMapping("/downloadMarkTextPicture")
    public void downloadMarkTextPicture(HttpServletResponse response,
                                        @RequestParam("url") String url,
                                        @RequestParam("text") String text,
                                        @RequestParam("fontFamily") String fontFamily,
                                        @RequestParam("fontWeight") String fontWeight,
                                        @RequestParam("fontStyle") String fontStyle,
                                        @RequestParam("color") String color,
                                        @RequestParam("fontSize") Integer fontSize,
                                        @RequestParam("opacity") Float opacity,
                                        @RequestParam("positionX") Integer positionX,
                                        @RequestParam("positionY") Integer positionY,
                                        @RequestParam("degree") Integer degree
    ) throws IOException {

        url="https://www.sunyuchao.com/files/img/58b8432586f7322b128d90b403d85aae?w=600&q=100";
        color = color.replace("rgb(","").replace(")","");
        Image srcImg = ImageIO.read(getImageStream(url));
        BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        // 2、得到画笔对象
        Graphics2D g = buffImg.createGraphics();
        // 3、设置对线段的锯齿状边缘处理
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(srcImg, 0, 0,buffImg.getWidth(),buffImg.getHeight(), null);
        // 4、设置水印旋转
        if (0 != degree && degree != 360) {
            g.rotate(Math.toRadians(degree),
                    (double) buffImg.getWidth() / 2,
                    (double) buffImg.getHeight() / 2);
        }
        // 5、设置水印文字颜色
        String[] colorArr = color.split(",");
        g.setColor(new Color(Integer.parseInt(colorArr[0].trim()),Integer.parseInt(colorArr[1].trim()),Integer.parseInt(colorArr[2].trim())));
        // 6、设置水印文字Font
        g.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        // 7、设置水印文字透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
        // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)


        g.drawString(text, positionX, positionY);
        g.dispose();
        ImageIO.write(buffImg, "JPG", response.getOutputStream());
    }

    /**
     * 获取网络图片的输入流
     * */
    public InputStream getImageStream(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                return inputStream;
            }
        } catch (IOException e) {
            System.out.println("获取网络图片出现异常，图片路径为：" + url);
            e.printStackTrace();
        }
        return null;
    }


}
