package com.gree.nutz.service;

import com.gree.nutz.entity.GenTable;
import com.gree.nutz.entity.TablePageData;

import java.util.List;
import java.util.Map;

/**
 * Created by 180686 on 2021/9/23 15:45
 */

public interface GenTableService {

    TablePageData<GenTable> selectGenTableList(GenTable genTable);

    List<GenTable> selectDbTableListByNames(String[] tableNames);

    Map<String, String> previewCode(String tableName);

    void generatorCode(GenTable genTableParam);

    byte[] downloadCode(String tableName);

    byte[] downloadCode(String[] tableNames);

    byte[] downloadCode(GenTable genTable);
}
