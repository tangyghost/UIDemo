package com.sdj.ty.myapplication.usb;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sdj.ty.myapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ty133 on 2017/1/13.
 */

public class USBActivity extends AppCompatActivity implements IUSBView, View.OnClickListener {

    @BindView(R.id.usb_findall)
    Button usbFindall;
    @BindView(R.id.usb_listview)
    ListView usbListview;
    ArrayAdapter<String> adapter;
    @BindView(R.id.usb_close)
    Button usbClose;
    private USBPresenter usbPresenter;
    private Context context = USBActivity.this;
    private USBBean usbBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_usb);
        ButterKnife.bind(this);
        usbPresenter = new USBPresenter(context, this);
        //通过插入符合条件的设备唤起act 获取符合条件的设备
        UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        usbBean = new USBBean();
        usbBean.setVID(1306);
        usbBean.setPID(20763);
        if (device != null) {
            usbBean.setDevice(device);
            usbPresenter.setDevice(usbBean);
        } else {
            usbPresenter.initUsbService();
            usbPresenter.setFilterConditions(usbBean);
        }
        usbFindall.setOnClickListener(this);
        usbClose.setOnClickListener(this);
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        usbListview.setAdapter(adapter);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device != null) {
            usbBean.setDevice(device);
            usbBean.setVID(1306);
            usbBean.setPID(20763);
            usbPresenter.setDevice(usbBean);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usbPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbPresenter.closeService();
    }

    @Override
    public void onDataChange(String data) {

    }

    @Override
    public void onDeviceConnect() {
        Toast.makeText(context, "链接成功！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceUnConnect() {
        Toast.makeText(context, "断开链接！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddDevice(UsbDevice device) {
        adapter.add(device.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.usb_findall:
                usbPresenter.findAllDevice();
                break;
            case R.id.usb_close:
                usbPresenter.closeUsbDevice();
                break;
        }
    }
}
