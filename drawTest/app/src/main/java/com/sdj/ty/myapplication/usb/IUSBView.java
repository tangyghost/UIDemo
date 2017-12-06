package com.sdj.ty.myapplication.usb;

import android.hardware.usb.UsbDevice;

/**
 * Created by ty133 on 2017/1/13.
 */

public interface IUSBView {
    void onDataChange(String data);

    void onDeviceConnect();

    void onDeviceUnConnect();

    void onAddDevice(UsbDevice device);
}
