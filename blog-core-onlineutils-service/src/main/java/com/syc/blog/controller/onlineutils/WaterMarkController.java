package com.syc.blog.controller.onlineutils;

import com.syc.blog.config.ApplicationConfig;
import com.syc.blog.controller.BaseController;
import com.syc.blog.utils.JsonHelper;
import com.syc.blog.utils.ZimgUploadHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.awt.SunHints;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/util/watermark")
@Slf4j
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
                                        @RequestParam("centerX") Integer centerX,
                                        @RequestParam("centerY") Integer centerY,
                                        @RequestParam(value = "degree",required = false) Integer degree

    ) throws IOException {


        color = color.replace("rgb(","").replace(")","");
        Image srcImg = ImageIO.read(getImageStream(url));
        BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        // 2、得到画笔对象
        Graphics2D g = buffImg.createGraphics();

        g.drawImage(srcImg, 0, 0,buffImg.getWidth(),buffImg.getHeight(), null);
        // 5、设置水印文字颜色
        String[] colorArr = color.split(",");
        int colorR = Integer.parseInt(colorArr[0].trim());
        int colorG = Integer.parseInt(colorArr[1].trim());
        int colorB = Integer.parseInt(colorArr[2].trim());

        // 6、设置水印文字Font
        int mode = Font.PLAIN;
        if(fontWeight.equals("700")){
            mode = Font.BOLD;
        }
        if(fontStyle.equals("italic")){
            mode = Font.ITALIC;
        }
        g.setFont(new Font(fontFamily, mode, fontSize));

        Point point  = null;
         if (null != degree && degree != 0) {
             AffineTransform affineTransform = new AffineTransform();
             affineTransform.rotate(Math.toRadians(degree), 0, 0);
             Font rotatedFont = g.getFont().deriveFont(affineTransform);
             g.setFont(rotatedFont);
             //计算旋转后,左上角的坐标
              point = calcNewPoint(new Point(positionX, positionY), new Point(centerX, centerY), degree);

         }

        // 7、设置水印文字透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
        // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)

        FontMetrics metrics = g.getFontMetrics(g.getFont());

        if(point != null){ //旋转了，point是旋转之后左上角的坐标，但是g.drawString是从左下角开始计算的
            positionX = point.x;
            positionY = point.y;
        }
        else{ //没有旋转，//计算文字的坐标位置，根据基线、高度来计算
             positionY = positionY + metrics.getHeight() - metrics.getLeading() ;
        }

        //设置抗锯齿，并且先用阴影画一遍，不然字体会模糊
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);//设置抗锯齿
        g.setPaint(new Color(colorR, colorG, colorB));//阴影颜色
        g.drawString("", positionX, positionY);//先绘制阴影

        //画水印
        g.setColor(new Color(colorR,colorG,colorB));
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

    /**
     * 计算按照中心点旋转后，新坐标位置
     * */

    private static Point calcNewPoint(Point p, Point pCenter, int angle) {
        // calc arc
        float l = (float) ((angle * Math.PI) / 180);

        //sin/cos value
        float cosv = (float) Math.cos(l);
        float sinv = (float) Math.sin(l);

        // calc new point
        float newX = (float) ((p.x - pCenter.x) * cosv - (p.y - pCenter.y) * sinv + pCenter.x);
        float newY = (float) ((p.x - pCenter.x) * sinv + (p.y - pCenter.y) * cosv + pCenter.y);
        System.out.println("新坐标:"+newX+","+newY);
        return new Point((int) newX, (int) newY);
    }
}
