package org.hints.nutz.controller;

import org.apache.commons.io.IOUtils;
import org.hints.nutz.dao.GenTableOracleDao;
import org.hints.nutz.domain.GenTable;
import org.hints.nutz.domain.ReturnVo;
import org.hints.nutz.domain.TablePageData;
import org.hints.nutz.service.GenTableService;
import org.hints.nutz.util.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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

    @GetMapping
    public ModelAndView index(){
        ModelAndView view = new ModelAndView();
        view.setViewName("index");
        return view;
    }

    @GetMapping("/list")
    public ReturnVo dataList(GenTable genTable)
    {
        TablePageData<GenTable> genTableTablePageData = genTableService.selectGenTableList(genTable);
        return ReturnVo.success(genTableTablePageData);
    }

    @PostMapping("/importTable")
    public void importTableSave(@RequestBody GenTable genTableParam)
    {
       genTableService.generatorCode(genTableParam);
    }

    @GetMapping("/preview/{tableName}")
    public ReturnVo preview(@PathVariable("tableName") String tableName) throws IOException
    {
        Map<String, String> dataMap = genTableService.previewCode(tableName);
        return ReturnVo.success(dataMap);
    }

    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response,  @PathVariable("tableName") String tableName) throws IOException
    {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 批量生成代码
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
        response.setHeader("Content-Disposition", "attachment; filename=\"ruoyi.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

}
