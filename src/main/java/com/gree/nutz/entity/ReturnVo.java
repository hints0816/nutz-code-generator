package com.gree.nutz.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnVo<T> implements Serializable {

    private static final long serialVersionUID = -5580228202640516960L;

    /**
     * @Desription: 响应参数  200：成功 201：失败
     */
    private String code;

    /**
     * @Desription: 响应信息
     */
    private String msg;

    /**
     * @Desription: 泛型，响应data
     */
    private T data;


    public ReturnVo() {
    }

    public ReturnVo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public ReturnVo(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ReturnVo success()
    {
        return ReturnVo.success("操作成功");
    }

    public static ReturnVo success(Object data)
    {
        return ReturnVo.success("操作成功", data);
    }

    public static ReturnVo success(String msg)
    {
        return ReturnVo.success(msg, null);
    }

    public static ReturnVo success(String msg, Object data)
    {
        return new ReturnVo("200", msg, data);
    }

    public static ReturnVo error()
    {
        return ReturnVo.error("操作失败");
    }

    public static ReturnVo error(String msg)
    {
        return ReturnVo.error(msg, null);
    }

    public static ReturnVo error(String msg, Object data)
    {
        return new ReturnVo("500", msg, data);
    }

    public static ReturnVo error(String code, String msg)
    {
        return new ReturnVo(code, msg, null);
    }

    public static ReturnVo toAjax(int rows)
    {
        return rows > 0 ? ReturnVo.success() : ReturnVo.error();
    }

}