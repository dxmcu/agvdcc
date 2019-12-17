package com.agvdcc.network.serialport;

import com.agvdcc.core.exceptions.AgvException;
import com.agvdcc.core.interfaces.IPlugin;
import com.agvdcc.utils.SettingUtils;
import com.agvdcc.utils.ToolsKit;
import com.openagv.core.AppContext;
import com.openagv.core.interfaces.IEnable;
import com.openagv.core.interfaces.IPlugin;
import com.openagv.core.interfaces.IResponse;
import com.openagv.core.interfaces.ITelegramSender;
import com.openagv.exceptions.AgvException;
import com.openagv.opentcs.enums.CommunicationType;
import com.openagv.tools.SettingUtils;
import com.openagv.tools.ToolsKit;
import gnu.io.SerialPort;
import org.apache.log4j.Logger;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;

import java.util.List;

/**
 * 串口插件类
 *
 * @author Laotang
 */
public class SerialPortPlugin implements IPlugin, IEnable, ITelegramSender {

    private static final Logger logger = Logger.getLogger(SerialPortPlugin.class);
    /**串口名称*/
    private String serialPortName;
    /**获取波特率，默认为38400*/
    private int baudrate;
    /**事件回调*/
    private ConnectionEventListener eventListener;

    public SerialPortPlugin(String serialPortName, int baudrate) {
        this.serialPortName = serialPortName;
        this.baudrate = baudrate;
    }

    @Override
    public void start() throws Exception {
        List<String> mCommList = SerialPortManager.duang().findPorts();

        if(ToolsKit.isEmpty(mCommList)) {
            throw new NullPointerException("没有找到可用的串串口！");
        }
        if(!mCommList.contains(serialPortName)) {
            throw new IllegalArgumentException("指定的串口名称["+serialPortName+"]与系统允许使用的不符");
        }
        if(baudrate == 0) {
            throw new IllegalArgumentException("串口波特率["+baudrate+"]没有设置");
        }
        try {
            AppContext.setSerialPort(SerialPortManager.duang().openPort(serialPortName, baudrate));
        } catch (Exception e) {
            throw new RuntimeException("打开串口时失败，名称["+serialPortName+"]， 波特率["+baudrate+"], 串口可能已被占用！");
        }
        eventListener = AppContext.getAgvConfigure().getConnectionEventListener();
        if(ToolsKit.isEmpty(eventListener)) {
            throw new NullPointerException("事件监听器没有实现，请先实现");
        }
        AppContext.setCommunicationType(CommunicationType.SERIALPORT);
    }


    private String readTelegram(SerialPort serialPort) {
        java.util.Objects.requireNonNull(serialPort, "串口对象不能为null");
        byte[] data = null;
        try {
            // 读取串口数据
            data = SerialPortManager.duang().readFromPort(serialPort);
            // 以字符串的形式接收数据
            return new String(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

    @Override
    public Object enable() {
        final SerialPort serialPort = AppContext.getSerialPort();
        if(null == serialPort) {
            try {
                start();
            } catch (Exception e) {
                throw new AgvException(e.getMessage(), e);
            }
        }
        eventListener.onConnect();
        SerialPortManager serialPortManager = SerialPortManager.duang();
        serialPortManager.addListener(serialPort, new DataAvailableListener() {
            @Override
            public void dataAvailable() {
                String telegram = readTelegram(serialPort);
                eventListener.onIncomingTelegram(telegram);
            }
        });
        logger.warn("串口["+serialPortName+"]启动成功！波特率为["+baudrate+"]");
        return serialPortManager;
    }

    public void disconnect() {
        SerialPortManager.duang().closePort(AppContext.getSerialPort());
    }

    /**
     * 广播电报到设备
     * @param response The {@link IResponse} to be sent.
     */
    @Override
    public void sendTelegram(IResponse response) {
        if(null == response) {
            return;
        }
        logger.info("串口发送报文: "+response.toString());
        SerialPortManager.duang().sendToPort(AppContext.getSerialPort(), response.toString().getBytes());
    }
}
