package com.sdj.ty.myapplication.usb;

import android.hardware.usb.UsbDevice;

/**
 * Created by ty133 on 2017/1/13.
 */

public interface IUSBModel {
    void initService(USBBean usbBean);

    void closeService();

    void setVendorIDandProductID(USBBean usbBean);

    void findAllDevice();

    void closeDevice();

}
