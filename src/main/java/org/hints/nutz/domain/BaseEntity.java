package org.hints.nutz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Entity基类
 */
@Data
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 搜索值 */
    private String searchValue;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;

    /** 请求参数 */
    private Map<String, Object> params;

    /** 页码 */
    private Integer pageNum;

    /** 页数 */
    private Integer pageSize;

    protected void setBaseEntityNull() {
        this.searchValue = null;
        this.createBy = null;
        this.createTime = null;
        this.updateBy = null;
        this.updateTime = null;
        this.remark = null;
        this.params = null;
        this.pageNum = null;
        this.pageSize = null;
    }

}
