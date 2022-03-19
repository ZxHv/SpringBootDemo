package com.xxexample.selfchapter23.resp;

import lombok.Data;

@Data
public class RespVO {

    private String code;
    private String msg;
    private Long timestamp;

    public static RespVO ok(String msg){
        RespVO respVO = new RespVO();
        respVO.code = "200";
        respVO.setMsg(msg);
        respVO.timestamp = System.currentTimeMillis();
        return respVO;
    }

    public static RespVO error(String msg){
        RespVO respVO = new RespVO();
        respVO.code = "500";
        respVO.setMsg(msg);
        respVO.timestamp = System.currentTimeMillis();
        return respVO;
    }
}
