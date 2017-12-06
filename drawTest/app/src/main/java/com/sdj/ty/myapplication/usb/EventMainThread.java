package com.sdj.ty.myapplication.usb;

/**
 * Created by ty133 on 2017/1/16.
 */

class EventMainThread {

    private USBBean usbBean = null;
    private int Type = 0;
    //空消息
    public static final int EmptyMessage = 0;
    //链接状态改变
    public static final int ConnStateChange = 1;
    //设备消息
    public static final int DeviceMessage = 2;
    //设备内容改变
    public static final int DeviceDataChange = 3;


    EventMainThread(int Type, USBBean usbBean) {
        this.Type = Type;
        this.usbBean = usbBean;
    }

    public int getType() {
        return Type;
    }

    public USBBean getBean() {
        return usbBean;
    }

}
