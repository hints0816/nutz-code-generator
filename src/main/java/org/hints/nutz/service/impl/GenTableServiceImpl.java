package org.hints.nutz.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.hints.nutz.config.GenConfig;
import org.hints.nutz.dao.GenTableMysqlDao;
import org.hints.nutz.dao.GenTableOracleDao;
import org.hints.nutz.domain.GenTable;
import org.hints.nutz.domain.GenTableColumn;
import org.hints.nutz.domain.TablePageData;
import org.hints.nutz.service.GenTableService;
import org.hints.nutz.util.*;
import org.nutz.dao.entity.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 180686 on 2021/9/23 15:45
 */
@Service
public class GenTableServiceImpl implements GenTableService {

    @Autowired
    private GenTableOracleDao genTableOracleDao;

    @Override
    public TablePageData<GenTable> selectGenTableList(GenTable genTable) {
        return genTableOracleDao.selectGenTableList(genTable);
    }

    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        return genTableOracleDao.selectDbTableListByNames(tableNames);
    }

    /**
     * 预览代码
     */
    @Override
    public Map<String, String> previewCode(String tableName)
    {
        Map<String, String> dataMap = new LinkedHashMap<>();
        String[] tableNames = {tableName};
        List<GenTable> genTables = genTableOracleDao.selectDbTableListByNames(tableNames);
        GenTable table = genTables.get(0);
        GenUtils.initTable(table, GenConfig.getAuthor());
        List<GenTableColumn> genTableColumns = genTableOracleDao.selectDbTableColumnsByName(table.getTableName());

        ArrayList<GenTableColumn> comPkColumn = new ArrayList<>();
        for (GenTableColumn column : genTableColumns) {
            if (column.isPk()) {
                comPkColumn.add(column);
            }
            GenUtils.initColumnField(column, table);
        }
        table.setComPkColumn(comPkColumn);
        table.setColumns(genTableColumns);
        setPkColumn(table);

        if (table.getComPkColumn().size() >= 2) {
            table.getPkColumn().setIsPk("2");
        }
        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            dataMap.put(template, sw.toString());
        }
        return dataMap;
    }

    @Override
    public void generatorCode(GenTable genTableParam) {
        String table = genTableParam.getTableName();
        String[] tableNames = Convert.toStrArray(table);
        List<GenTable> tableList = genTableOracleDao.selectDbTableListByNames(tableNames);;
        for (GenTable genTable : tableList){
            genTable.setGenPath(genTableParam.getGenPath());
            genTable.setFunctionName(genTableParam.getFunctionName());
            genTable.setClassName(genTableParam.getClassName());
            genTable.setFunctionName(genTableParam.getFunctionName());
            genTable.setIsDynamic(genTableParam.getIsDynamic());
            genTable.setDynamicTableName(genTableParam.getDynamicTableName());
            genTable.setDefaultParam(genTableParam.getDefaultParam());

            GenUtils.initTable(genTable, GenConfig.getAuthor());
            List<GenTableColumn> genTableColumns = genTableOracleDao.selectDbTableColumnsByName(genTable.getTableName());
            ArrayList<GenTableColumn> comPkColumn = new ArrayList<>();
            for (GenTableColumn column : genTableColumns) {
                if (column.isPk()) {
                    comPkColumn.add(column);
                }
                GenUtils.initColumnField(column, genTable);
            }
            genTable.setComPkColumn(comPkColumn);
            genTable.setColumns(genTableColumns);
            setPkColumn(genTable);

            if (genTable.getComPkColumn().size() >= 2) {
                genTable.getPkColumn().setIsPk("2");
            }
            VelocityInitializer.initVelocity();
            VelocityContext velocityContext = VelocityUtils.prepareContext(genTable);

            // 获取模板列表
            List<String> templates = VelocityUtils.getTemplateList(genTable.getTplCategory());
            // 获取模板列表
            for (String template : templates)
            {
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF8);
                tpl.merge(velocityContext, sw);
                try
                {
                    String path = getGenPath(genTable, template);
                    FileUtils.writeStringToFile(new File(path), sw.toString(), CharsetKit.UTF_8);
                }
                catch (IOException e)
                {
                    new RuntimeException("渲染模板失败");
                }
            }
        }
    }

    @Override
    public byte[] downloadCode(String tableName)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableName, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    @Override
    public byte[] downloadCode(String[] tableNames)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames)
        {
            generatorCode(tableName, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(String tableName, ZipOutputStream zip)
    {
        // 查询表信息
        String[] tableNames = {tableName};
        List<GenTable> genTables = genTableOracleDao.selectDbTableListByNames(tableNames);
        GenTable table = genTables.get(0);
        GenUtils.initTable(table, GenConfig.getAuthor());
        List<GenTableColumn> genTableColumns = genTableOracleDao.selectDbTableColumnsByName(table.getTableName());
        ArrayList<GenTableColumn> comPkColumn = new ArrayList<>();
        for (GenTableColumn column : genTableColumns) {
            if (column.isPk()) {
                comPkColumn.add(column);
            }
            GenUtils.initColumnField(column, table);
        }
        table.setComPkColumn(comPkColumn);
        table.setColumns(genTableColumns);
        setPkColumn(table);

        if (table.getComPkColumn().size() >= 2) {
            table.getPkColumn().setIsPk("2");
        }

        VelocityInitializer.initVelocity();
        VelocityContext context = VelocityUtils.prepareContext(table);
        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try
            {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
                IOUtils.write(sw.toString(), zip, Constants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            }
            catch (IOException e)
            {
                new RuntimeException("渲染模板失败");
            }
        }
    }

    /**
     * 设置主键列信息
     *
     * @param table 业务表信息
     */
    public void setPkColumn(GenTable table)
    {
        for (GenTableColumn column : table.getColumns())
        {
            if (column.isPk())
            {
                table.setPkColumn(column);
                break;
            }
        }
        if (StringUtils.isNull(table.getPkColumn()))
        {
            table.setPkColumn(table.getColumns().get(0));
        }
    }

    /**
     * 获取代码生成地址
     *
     * @param table 业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTable table, String template)
    {
        String genPath = table.getGenPath();
        if (StringUtils.equals(genPath, "/"))
        {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table);
    }
}
