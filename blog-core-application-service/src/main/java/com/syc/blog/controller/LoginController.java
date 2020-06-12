package com.syc.blog.controller;

import com.alibaba.fastjson.JSON;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import com.syc.blog.constants.Constant;
import com.syc.blog.constants.RedisConstant;
import com.syc.blog.entity.user.User;
import com.syc.blog.entity.user.UserAuth;
import com.syc.blog.service.user.UserAuthService;
import com.syc.blog.service.user.UserService;
import com.syc.blog.utils.MD5Helper;
import com.syc.blog.utils.ResultHelper;
import com.syc.blog.utils.StringHelper;
import com.syc.blog.utils.VCodeHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@RequestMapping("/login")
@Controller
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    UserAuthService userAuthService;
    @Autowired
    JavaMailSender mailSender;//邮件发送器
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String from;//发送账户
    @Autowired
    UserService userService;
    @RequestMapping(value = "/login/createVCodeImg",produces = "image/jpeg")
    public void createVCodeImg(HttpSession session, HttpServletResponse response){
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        VCodeHelper vCode = new VCodeHelper(140, 40, 4, 110);
        session.setAttribute("validateCode", vCode.getCode());
        try {
            vCode.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/login")
    public String login(ModelMap map){
        String iconfontUrl = stringRedisTemplate.opsForValue().get(RedisConstant.DICT_ICONFONT_URL);
        map.put("iconfontUrl",iconfontUrl);
        return "login";
    }

    @RequestMapping("/regist")
    public String regist(ModelMap map){
        String iconfontUrl = stringRedisTemplate.opsForValue().get(RedisConstant.DICT_ICONFONT_URL);
        map.put("iconfontUrl",iconfontUrl);
        return "regist";
    }

    @RequestMapping("/forgotPassword")
    public String forgotPassword(ModelMap map){
        String iconfontUrl = stringRedisTemplate.opsForValue().get(RedisConstant.DICT_ICONFONT_URL);
        map.put("iconfontUrl",iconfontUrl);
        return "forgot_pass";
    }

    /**
     * 忘记密码，修改密码
     * */
    @RequestMapping("/saveModifyPassword")
    @ResponseBody
    public String saveModifyPassword(@RequestParam("email") String email, @RequestParam("password") String password){
        UserAuth userAuth = userAuthService.selectByTypeAndIdentifier(Constant.LOGIN_EMAIL, email);
        ResultHelper result;
        if(userAuth == null){
            result = ResultHelper.wrapErrorResult(1,"用户不存在！");
            return JSON.toJSONString(result);
        }
        password= MD5Helper.encrypt(password);
        int row = userAuthService.saveModifyPassword(email,password);
        result = row == 0 ? ResultHelper.wrapErrorResult(2,"设置新密码失败！")  : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }
    @RequestMapping("/check")
    @ResponseBody
    public String check(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session){
        ResultHelper result;
        UserAuth userAuth = userAuthService.selectByTypeAndIdentifier(Constant.LOGIN_EMAIL, email);
        if(userAuth == null){
            result= ResultHelper.wrapErrorResult(1,"用户不存在");
            return JSON.toJSONString(result);
        }else{
            if(!userAuth.getCredential().equals(MD5Helper.encrypt(password))){
                result= ResultHelper.wrapErrorResult(2,"用户名或者密码错误");
                return JSON.toJSONString(result);
            }
        }
        User currentUser=userService.selectById(userAuth.getUserId());
        session.setAttribute("currentUser",currentUser);
        session.setAttribute("isThirdLogin",false);
        result= ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }

    @RequestMapping("/saveRegister")
    @ResponseBody
    public String saveRegister(@RequestParam("email") String email,
                               @RequestParam("nickname") String nickname,
                               @RequestParam("password") String password){
        UserAuth userAuth = userAuthService.selectByTypeAndIdentifier(Constant.LOGIN_EMAIL, email);
        ResultHelper result;
        if(userAuth != null){
            result= ResultHelper.wrapErrorResult(1,"该邮箱已存在，可直接登录");
            return JSON.toJSONString(result);
        }
        if(StringHelper.hasIllegal(nickname)){
            result= ResultHelper.wrapErrorResult(2,"您的昵称包含敏感词汇，请重新输入");
            return JSON.toJSONString(result);
        }
        String defaultIcon = stringRedisTemplate.opsForValue().get(RedisConstant.DICT_DEFAULT_ICON);
        User user=new User();
        user.setStatus((byte)1);
        user.setNickname(nickname);
        user.setAvatar(defaultIcon);
        user.setDateInsert(new Date());

        userAuth=new UserAuth();
        userAuth.setIdentifier(email);
        userAuth.setCredential(MD5Helper.encrypt(password));
        userAuth.setIdentityType(Constant.LOGIN_EMAIL);
        userAuth.setDateInsert(new Date());

        int row=userService.save(user,userAuth);
        result=row == 0 ? ResultHelper.wrapErrorResult(2,"系统繁忙，请稍后重试") : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }


    @ResponseBody
    @RequestMapping("/sendEmail")
    public String sendEmail(@RequestParam("email") String email, HttpSession session){
        SimpleMailMessage message=new SimpleMailMessage();
        VCodeHelper vCode = new VCodeHelper(6);
        String dynamicCode=vCode.createNumCode();//即将发送的验证码
        ResultHelper resultHelper;
        try{
            message.setSubject("暮色妖娆丶博客注册验证");
            message.setText("您的暮色妖娆丶博客网站动态码为\t"+dynamicCode+","+
                    "十分钟内有效,"+"如果这不是您本人的操作，请忽略");
            message.setFrom(from);
            message.setTo(email);
            mailSender.send(message);
            session.setAttribute("dynamicCode",dynamicCode);
            session.setMaxInactiveInterval(600);
            resultHelper= ResultHelper.wrapSuccessfulResult(null);
        }catch (Exception e) {
            log.error("发送邮件失败,腾讯SMTP服务出现问题，请暂时使用QQ登录",e);
            resultHelper= ResultHelper.wrapErrorResult(1,"发送邮件失败,腾讯SMTP服务出现问题，请暂时使用QQ登录");
        }
        return JSON.toJSONString(resultHelper);//发送成功

    }

    @RequestMapping("/checkValCode")
    @ResponseBody
    public String checkValCode(@RequestParam("valCode") String valCode, HttpSession session){
        String dynamicCode = (String) session.getAttribute("dynamicCode");
        boolean success = dynamicCode.equalsIgnoreCase(valCode);
        ResultHelper result=success ? ResultHelper.wrapSuccessfulResult(null) : ResultHelper.wrapErrorResult(1,"动态码错误");
        return JSON.toJSONString(result);
    }

    /*QQ登录入口*/
    @RequestMapping(value = "/qqLogin")
    public String user_login_qq(HttpServletRequest request) throws QQConnectException {
        String authorizeURL = new Oauth().getAuthorizeURL(request);
        return "redirect:"+authorizeURL;    //重定向到腾讯提供的登录页面
    }


    //*调用QQ登录之后的回调*//*
    @RequestMapping(value = "/qqLoginCallback")
    public String qqLoginCallback(HttpServletResponse response,HttpServletRequest request,HttpSession session) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        session.setAttribute("isThirdLogin",true);//标记第三方登录
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken = null;//授权令牌
            String openID = null;      //用户身份的标识
            long tokenExpireIn = 0L;    //授权过期时间
            if (!accessTokenObj.getAccessToken().equals(""))  {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();
                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();         //用户代号
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);//实例化用户信息对象
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                if (userInfoBean.getRet() == 0) {
                    UserAuth userAuth=userAuthService.selectByTypeAndIdentifier(Constant.LOGIN_QQ,openID);
                    User user;
                    String avatarURL50 = userInfoBean.getAvatar().getAvatarURL50();
                    String nickname = userInfoBean.getNickname();
                    if(userAuth == null) { //新用户
                        user=new User();
                        user.setDateInsert(new Date());
                        user.setAvatar(avatarURL50);
                        user.setNickname(nickname);
                        user.setStatus((byte)1);

                        userAuth =new UserAuth();
                        userAuth.setIdentityType(Constant.LOGIN_QQ);
                        userAuth.setDateInsert(new Date());
                        userAuth.setCredential(accessToken);
                        userAuth.setIdentifier(openID);
                        int save = userService.save(user,userAuth);
                        user.setId(save);
                        if(save != 0){
                            session.setAttribute("currentUser",user);
                            return "redirect:/";
                        }

                    } else { //老用户
                        log.info("用户信息------"+userAuth.toString());
                        if(userAuth.getCredential().equals(accessToken)){
                            Integer userId = userAuth.getUserId();
                            user = userService.selectById(userId);
                            session.setAttribute("currentUser",user);
                            //检查用户信息是否变更
                            log.info("开始检查用户信息是否变更");
                            if(!user.getAvatar().equals(avatarURL50) || !user.getNickname().equals(nickname)){
                                userService.updateUserTencentInfo(userId, avatarURL50, nickname);
                                log.info("修改用户变更信息成功"+"头像:"+avatarURL50+"昵称:"+nickname);
                            }
                            return "redirect:/";//验证通过
                        }else{
                            //登录失败，校验未通过

                        }
                    }
                }
            }
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @RequestMapping("/exit")
    public String exit(HttpSession session){
        session.setAttribute("currentUser",null);
        return "redirect:/";
    }

    @RequestMapping("/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email){
        UserAuth userAuth = userAuthService.selectByTypeAndIdentifier(Constant.LOGIN_EMAIL, email);
        ResultHelper result = userAuth == null ? ResultHelper.wrapErrorResult(1,"用户不存在") : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }
}
