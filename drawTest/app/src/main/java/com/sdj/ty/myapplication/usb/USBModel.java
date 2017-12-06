package com.sdj.ty.myapplication.usb;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;

/**
 * IusbModel接口实现类
 * Created by ty133 on 2017/1/13.
 */

public class USBModel implements IUSBModel {

    private Context context;

    public USBModel(Context context) {
        this.context = context;
    }

    @Override
    public void initService(USBBean usbBean) {
        Intent intent = new Intent(context, USBService.class);
        if (usbBean != null)
            intent.putExtra("usbBean", usbBean);
        context.startService(intent);
    }

    @Override
    public void closeService() {
        context.stopService(new Intent(context, USBService.class));
    }

    @Override
    public void setVendorIDandProductID(USBBean usbBean) {
        initService(usbBean);
    }

    @Override
    public void findAllDevice() {
        Intent intent = new Intent(context, USBService.class);
        intent.putExtra("usbfindAll", true);
        context.startService(intent);
    }

    @Override
    public void closeDevice() {
        Intent intent = new Intent(context, USBService.class);
        intent.putExtra("closeusb", true);
        context.startService(intent);
    }

}
