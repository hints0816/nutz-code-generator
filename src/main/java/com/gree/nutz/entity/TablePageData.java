package com.gree.nutz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.nutz.dao.pager.Pager;

import java.util.List;

/**
 * Created by 180686 on 2021/9/24 15:54
 */

@Data
@AllArgsConstructor
public class TablePageData<T> {

    private List<T> dataList;

    private Pager pager;
}
