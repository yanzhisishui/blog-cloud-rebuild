package com.syc.blog.controller.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syc.blog.entity.config.BaseConfig;
import com.syc.blog.mapper.config.BaseConfigMapper;
import com.syc.blog.utils.JsonHelper;
import com.syc.blog.utils.ResultHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/baseConfig")
public class BaseConfigController {

    @Autowired
    BaseConfigMapper baseConfigMapper;
    @RequestMapping("/manage")
    public String manage(){
        return "config/manage";
    }

    @RequestMapping("/queryListPage")
    @ResponseBody
    public String queryListPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer pageSize){
        IPage<BaseConfig> iPage = new Page<>(page,pageSize);
        IPage<BaseConfig> bannerIPage = baseConfigMapper.selectPage(iPage, Wrappers.<BaseConfig>lambdaQuery().eq(BaseConfig::getArchive,0));
        return JsonHelper.objectToJsonForTable(bannerIPage.getRecords(),bannerIPage.getTotal());
    }

    @RequestMapping("/save")
    @ResponseBody
    public String save(BaseConfig baseConfig){
        baseConfig.setDateInsert(new Date());
        int row = baseConfigMapper.insert(baseConfig);
        ResultHelper result= row == 0 ? ResultHelper.wrapErrorResult(1,"添加技能失败") : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }

    @RequestMapping("/update")
    @ResponseBody
    public String update(BaseConfig baseConfig){
        baseConfig.setDateUpdate(new Date());
        int row = baseConfigMapper.updateById(baseConfig);
        ResultHelper result= row == 0 ? ResultHelper.wrapErrorResult(1,"更新技能失败") : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }
    @RequestMapping("/add")
    public String add(){
        return "config/add";
    }

    @RequestMapping("/edit")
    public String edit(@RequestParam("id") Integer id, ModelMap map){
        BaseConfig baseConfig=baseConfigMapper.selectById(id);
        map.put("baseConfig",baseConfig);
        return "config/edit";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam("id") Integer id){
        BaseConfig baseConfig = new BaseConfig();
        baseConfig.setDateUpdate(new Date());
        baseConfig.setId(id);
        baseConfig.setArchive((byte)1);
        int row=baseConfigMapper.updateById(baseConfig);
        ResultHelper result= ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }
}
