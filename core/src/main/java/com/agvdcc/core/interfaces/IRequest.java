package com.agvdcc.core.interfaces;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by laotang on 2019/9/25.
 */
public interface IRequest extends java.io.Serializable{

    /**传感器对象，放在paramMap里*/
    String SENSOR_FIELD = "_sensor";
    /***
     * 逻辑处理服务类的参数字段，json格式
     */
    String SERVICE_ACTION_FIELD = "_serviceAction";

    /**唯一的请求ID，用ObjectId作标识*/
    String getRequestId();

//    /**根据名称取参数值*/
//    <T> T getParameter(String name);
//
    /**参数值KV集合*/
    Map<String, Object> getPropertiesMap();

    /**设置请求目的路径，即指令名称*/
    void setCmdKey(String cmdKey);

    /**取请求目的路径*/
    String getCmdKey();
//
    /**取报文的原始字符串*/
    String getOriginalTelegram();

    /**请求类型*/
    String getRequestType();
//
//    /**发送车辆进程参数模型*/
//    void setModel(ProcessModel processModel);
//
//    /**取车辆进程参数模型*/
//    ProcessModel getProcessModel();
//
//    /**设置车辆移动命令*/
//    void setCmd(MovementCommand cmd);
//
//    /**取车辆移动命令*/
//    MovementCommand getMovementCommand();
//
    /**取协议对象*/
    Serializable getProtocol();
    /**设置协议对象*/
    void setProtocol(java.io.Serializable bean);

}
