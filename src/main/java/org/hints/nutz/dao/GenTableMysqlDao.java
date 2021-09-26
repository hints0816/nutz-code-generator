package org.hints.nutz.dao;

import org.hints.nutz.domain.GenTable;
import org.hints.nutz.domain.GenTableColumn;
import org.hints.nutz.domain.TablePageData;
import org.hints.nutz.util.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.Static;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 180686 on 2021/9/23 15:49
 */

@Repository
public class GenTableMysqlDao {

    @Resource
    private Dao dao;

    public TablePageData<GenTable> selectGenTableList(GenTable genTable){
        Sql sql = Sqls.create("select table_name as tableName, table_comment as tableComment, create_time as createTime, update_time as updateTime " +
                " from information_schema.tables $condition");
        Sql sql2 = Sqls.create("select count(*) as count from information_schema.tables $condition");
        Cnd cnd = Cnd.where(new Static("table_schema = (select database())"));
        if (StringUtils.isNotEmpty(genTable.getTableName())) {
            cnd.and(new Static("lower(table_name) like lower('%"+genTable.getTableName()+"%')"));
        }
        sql2.setCondition(cnd);
        sql2.setCallback(Sqls.callback.integer());
        int TotalCount=dao.execute(sql2).getInt();
        Pager pager = dao.createPager(genTable.getPageNum(), genTable.getPageSize());
        pager.setRecordCount(TotalCount);
        genTable.genTableNoPage();
        sql.setCondition(cnd);
        sql.setPager(pager);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(GenTable.class));
        List<GenTable> query = dao.execute(sql).getList(GenTable.class);
        TablePageData<GenTable> tablePageData = new TablePageData(query, pager);
        return tablePageData;
    }

    public List<GenTable> selectDbTableListByNames(String[] tableNames){
        Sql sql = Sqls.create("select table_name as tableName, table_comment as tableComment, create_time as createTime, update_time as updateTime" +
                " from information_schema.tables $condition");
        Cnd cnd = Cnd.where("table_name", "in", tableNames)
            .and(new Static("table_schema = (select database())"));
        sql.setCondition(cnd);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(GenTable.class));
        List<GenTable> list = dao.execute(sql).getList(GenTable.class);
        return list;
    }


    public List<GenTableColumn> selectDbTableColumnsByName(String tableName){
        Sql sql = Sqls.create("SELECT" +
                " column_name as columnName," +
                " ( CASE WHEN ( is_nullable = 'no' && column_key != 'PRI' ) THEN '1' ELSE NULL END ) AS isRequired," +
                " ( CASE WHEN column_key = 'PRI' THEN '1' ELSE '0' END ) AS isPk," +
                " ordinal_position AS sort," +
                " column_comment as columnComment," +
                " ( CASE WHEN extra = 'auto_increment' THEN '1' ELSE '0' END ) AS isIncrement," +
                " column_type as columnType" +
                " FROM" +
                " information_schema.COLUMNS " +
                " WHERE" +
                " table_schema = (" +
                " SELECT DATABASE" +
                " ()) " +
                " AND table_name = ( @table_name ) " +
                " ORDER BY" +
                " ordinal_position");
        sql.setParam("table_name", tableName);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(GenTableColumn.class));
        List<GenTableColumn> list = dao.execute(sql).getList(GenTableColumn.class);
        return list;
    }

 
}
