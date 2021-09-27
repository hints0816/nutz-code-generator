package com.gree.nutz.controller;

import org.apache.commons.io.IOUtils;
import com.gree.nutz.dao.GenTableOracleDao;
import com.gree.nutz.entity.GenTable;
import com.gree.nutz.entity.ReturnVo;
import com.gree.nutz.entity.TablePageData;
import com.gree.nutz.service.GenTableService;
import com.gree.nutz.util.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by 180686 on 2021/9/23 14:00
 */
@CrossOrigin
@RestController
@RequestMapping("/generate")
public class GenController {

    @Autowired
    private GenTableService genTableService;

    @Autowired
    private GenTableOracleDao genTableOracleDao;

    /**
     * 代码生成器首页
     * @return
     */
    @GetMapping
    public ModelAndView index(){
        ModelAndView view = new ModelAndView();
        view.setViewName("index");
        return view;
    }

    /**
     * 查询
     * @param genTable
     * @return
     */
    @GetMapping("/list")
    public ReturnVo dataList(GenTable genTable)
    {
        TablePageData<GenTable> genTableTablePageData = genTableService.selectGenTableList(genTable);
        return ReturnVo.success(genTableTablePageData);
    }

    /**
     * 生成代码(自定义路径)
     * @param genTableParam
     */
    @PostMapping("/importTable")
    public void importTableSave(@RequestBody GenTable genTableParam)
    {
       genTableService.generatorCode(genTableParam);
    }

    /**
     * 生成代码(预览代码)
     * @param tableName
     * @return
     */
    @GetMapping("/preview/{tableName}")
    public ReturnVo preview(@PathVariable("tableName") String tableName)
    {
        Map<String, String> dataMap = genTableService.previewCode(tableName);
        return ReturnVo.success(dataMap);
    }

    /**
     * 生成代码(下载代码)
     * @param response
     * @param tableName
     * @throws IOException
     */
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response,  @PathVariable("tableName") String tableName) throws IOException
    {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, GenTable genTableParam) throws IOException
    {
        byte[] data = genTableService.downloadCode(genTableParam);
        genCode(response, data);
    }

    /**
     * 批量生成代码
     * @param response
     * @param tables
     * @throws IOException
     */
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException
    {
        String[] tableNames = Convert.toStrArray(tables);
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException
    {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"nutzcode-generate.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

}
