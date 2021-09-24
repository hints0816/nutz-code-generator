package org.hints.nutz.dao;

import org.hints.nutz.domain.GenTable;
import org.hints.nutz.domain.GenTableColumn;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.Static;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 180686 on 2021/9/23 15:49
 */

@Repository
public class GenTableOracleDao {

    @Resource
    private Dao dao;

    public List<GenTable> selectGenTableList(String tableName){
        Sql sql = Sqls.create("select table_name as tableName, comments as tableComment, created as createTime, last_ddl_time as updateTime " +
                "from user_tab_comments t1,user_objects t2 where t1.table_name = t2.object_name and t1.table_name = upper(@tableName)");
        sql.setParam("tableName", tableName);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(GenTable.class));
        List<GenTable> list = dao.execute(sql).getList(GenTable.class);
        return list;
    }

    public List<GenTable> selectDbTableListByNames(String[] tableNames){
        Sql sql = Sqls.create("select table_name as tableName, comments as tableComment, created as createTime, last_ddl_time as updateTime " +
                "from user_tab_comments t1,user_objects t2 $condition");
        Cnd cnd = Cnd.where("t1.table_name", "in", tableNames)
            .and(new Static("t1.table_name = t2.object_name"));
        sql.setCondition(cnd);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(GenTable.class));
        List<GenTable> list = dao.execute(sql).getList(GenTable.class);
        return list;
    }


    public List<GenTableColumn> selectDbTableColumnsByName(String tableName){
        Sql sql = Sqls.create("select a.column_name as columnName," +
                " case" +
                " when nullable = 'N' and" +
                " a.column_name =" +
                " (select a.column_name" +
                " from user_cons_columns a, user_constraints b" +
                " where a.constraint_name = b.constraint_name" +
                " and b.constraint_type = 'P'" +
                " and a.table_name = upper(@table_name)) then" +
                " '1'" +
                " else" +
                " null" +
                " end as isRequired," +
                " case" +
                " when a.column_name =" +
                " (select a.column_name" +
                " from user_cons_columns a, user_constraints b" +
                " where a.constraint_name = b.constraint_name" +
                " and b.constraint_type = 'P'" +
                " and a.table_name = upper(@table_name)) then" +
                " '1'" +
                " else" +
                " '0'" +
                " end as isPk," +
                " b.comments as columnComment," +
                " a.column_id as sort," +
                " a.data_type as columnType" +
                " from user_tab_columns a, user_col_comments b" +
                " where a.table_name = b.table_name " +
                " and a.column_name = b.column_name " +
                " and a.table_name = upper(@table_name)" +
                " order by a.column_id");
        sql.setParam("table_name", tableName);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(GenTableColumn.class));
        List<GenTableColumn> list = dao.execute(sql).getList(GenTableColumn.class);
        return list;
    }

 
}
