package com.sdj.ty.myapplication.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ty133 on 2017/1/13.
 */

public class USBPresenter {

    private IUSBModel mUsbModel;
    private IUSBView iusbView;

    public USBPresenter(Context context, IUSBView iusbView) {
        this.iusbView = iusbView;
        mUsbModel = new USBModel(context);
        EventBus.getDefault().register(this);
    }


    /**
     * 启动Service初始化
     */
    public void initUsbService() {
        mUsbModel.initService(null);
    }


    /**
     * 停止Service运行
     */
    public void closeService() {
        mUsbModel.closeService();
    }

    /**
     * 设置匹配设备条件
     *
     * @param usbBean
     */
    public void setFilterConditions(USBBean usbBean) {
        mUsbModel.setVendorIDandProductID(usbBean);
    }

    /**
     * 设置新的匹配成功的device
     *
     * @param usbBean
     */
    public void setDevice(USBBean usbBean) {
        mUsbModel.initService(usbBean);
    }

    /**
     * 搜索链接的USB设备
     */
    public void findAllDevice() {
        mUsbModel.findAllDevice();
    }

    public void closeUsbDevice() {
        mUsbModel.closeDevice();
    }

    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventMainThread event) {
        int type = event.getType();
        switch (type) {
            case EventMainThread.EmptyMessage:
                break;
            case EventMainThread.DeviceMessage:
                iusbView.onAddDevice(event.getBean().getDevice());
                break;
            case EventMainThread.DeviceDataChange:
                iusbView.onDataChange(event.getBean().getDataResults());
                break;
            case EventMainThread.ConnStateChange:
                if (event.getBean().getState() == USBBean.USB_CONNECT) {
                    iusbView.onDeviceConnect();
                } else {
                    iusbView.onDeviceUnConnect();
                }
                break;
        }
    }

}
