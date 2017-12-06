package com.sdj.ty.myapplication.usb;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ty133 on 2017/1/13.
 */

public class USBService extends Service {

    private static final String ACTION_USB_PERMISSION = "com.Android.example.USB_PERMISSION";
    private static final int USB_INSERT = 1;
    private static final int USB_PULL_OUT = 2;
    private static final int USB_CONN_SUCC = 3;
    private static final int USB_CONN_START = 4;
    private static final int USB_CONTENT_CHANGE = 5;
    private UsbManager mUsbManager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == USB_INSERT) {
                //插入USB设备
                usbFilterFuction();
            } else if (msg.what == USB_PULL_OUT) {
                //拔出USB设备
                closeDevice();
            } else if (msg.what == USB_CONN_SUCC) {
                //链接成功
                startDataThread();
                if (usbBean != null) {
                    usbBean.setState(USBBean.USB_CONNECT);
                    EventBus.getDefault().post(new EventMainThread(EventMainThread.ConnStateChange, usbBean));
                }
            } else if (msg.what == USB_CONN_START) {
                //开始链接
                getDeviceInfo(usbBean);
            } else if (msg.what == USB_CONTENT_CHANGE) {
                //USB内容发生改变
                if (usbBean != null) {
                    Toast.makeText(getApplicationContext(), "usb内容改变:" + usbBean.bytes2HexString(newBuff), Toast.LENGTH_SHORT).show();
                    usbBean.setNewData(newBuff);
                    EventBus.getDefault().post(new EventMainThread(EventMainThread.DeviceDataChange, usbBean));
                }
            } else if (msg.what == 900) {
                if (usbBean != null) {
                    String b = usbBean.bytes2HexString(newBuff);
                    Toast.makeText(getApplicationContext(), b == null ? "null" : b, Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(ACTION_USB_PERMISSION);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbFilter);
        usbBean = new USBBean();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * usb类对象
     */
    private USBBean usbBean;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            USBBean ub = intent.getParcelableExtra("usbBean");
            boolean find = intent.getBooleanExtra("usbfindAll", false);
            boolean close = intent.getBooleanExtra("closeusb", false);
            if (ub != null && ub.getDevice() != null) {
                //通过插入符合条件的设备触发启动act 获取符合条件的设备
                usbBean = ub;
                sendPermission(usbBean);
            }
            if (find) {
                //搜索usb设备列表
                handler.sendEmptyMessage(USB_INSERT);
            }
            if (close) {
                //关闭链接
                closeDevice();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private PendingIntent mPermissionIntent;

    /**
     * 判斷USB设备权限请求
     *
     * @param usbBean
     */
    private void sendPermission(USBBean usbBean) {
        if (usbBean == null)
            return;
        if (mUsbManager == null) {
            Toast.makeText(getApplicationContext(), "没有检测到USB接口!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (usbBean.getDevice() == null)
            return;
        if (mUsbManager.hasPermission(usbBean.getDevice())) {
            //已经获取到权限--准备链接
            handler.sendEmptyMessage(USB_CONN_START);
        } else {
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            mUsbManager.requestPermission(usbBean.getDevice(), mPermissionIntent);
        }

    }

    /**
     * USB过滤器
     */
    private void usbFilterFuction() {
        if (mUsbManager == null) {
            Toast.makeText(getApplicationContext(), "没有检测到USB接口!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (usbBean == null)
            return;
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (!(deviceList.isEmpty())) {
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                // 获取设备VID和PID
                int VendorID = device.getVendorId();
                int ProductID = device.getProductId();
                if (FilterConditions(VendorID, ProductID)) {
                    //匹配成功
                    usbBean.setDevice(device);
                    EventBus.getDefault().post(new EventMainThread(EventMainThread.DeviceMessage, usbBean));
                    sendPermission(usbBean);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "请连接USB设备!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 匹配设备条件
     *
     * @param VendorID
     * @param ProductID
     * @return
     */
    private boolean FilterConditions(int VendorID, int ProductID) {
        return usbBean.getVID() == VendorID && usbBean.getPID() == ProductID;
    }

    /**
     * 获取USB设备信息 链接USB设备
     *
     * @param usbBean
     */
    private void getDeviceInfo(USBBean usbBean) {
        if (usbBean == null)
            return;
        if (usbBean.getDevice() == null)
            return;
        closeDevice();
        for (int i = 0; i < usbBean.getDevice().getInterfaceCount(); i++) {
            UsbInterface usbInterface = usbBean.getDevice().getInterface(i);
            assignEndpoint(usbInterface);
            if ((null == epIntEndpointIn) || (null == epIntEndpointOut)) {
                Toast.makeText(getApplicationContext(), "设备链接失败！-null", Toast.LENGTH_SHORT).show();
                epIntEndpointIn = null;
                epIntEndpointOut = null;
            } else {
                mUsbInterface = usbInterface;
                mUsbDeviceConnection = openDevice(usbBean.getDevice(), mUsbInterface);
                if (mUsbDeviceConnection != null) {
                    //链接成功
                    Toast.makeText(getApplicationContext(), "设备链接成功！", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(USB_CONN_SUCC);
                } else {
                    Toast.makeText(getApplicationContext(), "设备链接失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private UsbInterface mUsbInterface = null;
    private UsbDeviceConnection mUsbDeviceConnection = null;
    /**
     * 输入输出流
     * <p>
     * 写入
     */
    private UsbEndpoint epIntEndpointOut = null;
    /**
     * 读取
     */
    private UsbEndpoint epIntEndpointIn = null;

    /**
     * 分配端点，IN | OUT，即输入输出；可以通过判断  
     *
     * @param mInterface
     * @return UsbEndpoint epIntEndpointIn
     */
    private UsbEndpoint assignEndpoint(UsbInterface mInterface) {
        for (int i = 0; i < mInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = mInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    epIntEndpointOut = ep;
                }
                if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                    epIntEndpointIn = ep;
                }
            }
        }
        return epIntEndpointIn;
    }

    /**
     * 链接usb
     *
     * @param device
     * @param usbInterface
     * @return
     */
    private UsbDeviceConnection openDevice(UsbDevice device, UsbInterface usbInterface) {
        if (device == null)
            return null;
        if (usbInterface == null)
            return null;
        UsbDeviceConnection conn = mUsbManager.openDevice(device);
        if (conn == null)
            return null;
        if (conn.claimInterface(usbInterface, true)) {
            //链接成功
            return conn;
        } else {
            return null;
        }
    }

    /**
     * 判断链接状态
     *
     * @return
     */
    private boolean checkConnect() {
        if (mUsbManager == null) {
            return false;
        }
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (!(deviceList.isEmpty())) {
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                // 获取设备VID和PID
                int VendorID = device.getVendorId();
                int ProductID = device.getProductId();
                if (usbBean.getVID() > 0 && usbBean.getPID() > 0 && FilterConditions(VendorID, ProductID)) {
                    //连接中
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }


    /**
     * 关闭usb链接
     */
    private void closeDevice() {
        if (mUsbDeviceConnection == null)
            return;
        synchronized (mUsbDeviceConnection) {
            mUsbDeviceConnection.releaseInterface(mUsbInterface);
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
            epIntEndpointOut = null;
            epIntEndpointIn = null;
            mUsbInterface = null;
            if (usbBean != null) {
                usbBean.setState(USBBean.USB_UNCONNECT);
                EventBus.getDefault().post(new EventMainThread(EventMainThread.ConnStateChange, usbBean));
            }
        }
    }

    /**
     * USB广播接收器
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                //插入USB
                handler.sendEmptyMessage(USB_INSERT);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //拔出USB
                if (!checkConnect())
                    handler.sendEmptyMessage(USB_PULL_OUT);
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                //用户权限选择完毕
                synchronized (this) {
                    //获取device
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //用户同意权限请求-准备链接
                            usbBean.setDevice(device);
                            handler.sendEmptyMessage(USB_CONN_START);
                        }
                    } else {
                        //用户拒绝权限请求
                        Log.d("USBActivity：", "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private Thread dataTD = null;
    private boolean dataTDSwitch = true;
    private byte[] newBuff = null, oldBuff = null;

    /**
     * 获取usb设备数据 监听数据变更
     */
    private void startDataThread() {
        dataTDSwitch = true;
        dataTD = new Thread(new Runnable() {
            @Override
            public void run() {
                while (dataTDSwitch) {
                    if (mUsbDeviceConnection != null) {
                        //读取数据
                        synchronized (mUsbDeviceConnection) {
                            newBuff = readBulkTransferData();
                            if (oldBuff != null && usbBean.equalsByteArray(newBuff, oldBuff)) {
                                //数值发生改变
                                handler.sendEmptyMessage(USB_CONTENT_CHANGE);
                            }
                            oldBuff = newBuff;
                            handler.sendEmptyMessage(900);
                        }
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mUsbInterface = null;
                mUsbDeviceConnection = null;
                epIntEndpointOut = null;
                epIntEndpointIn = null;
            }
        });
        dataTD.start();
    }


    /**
     * 读取数据
     */
    private byte[] readBulkTransferData() {
        if (epIntEndpointIn == null)
            return null;
        byte[] buffer = new byte[epIntEndpointIn.getMaxPacketSize()];
        if (mUsbDeviceConnection == null)
            return null;
        mUsbDeviceConnection.bulkTransfer(epIntEndpointIn, buffer, epIntEndpointIn.getMaxPacketSize(), 1000);
        return buffer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataTDSwitch = false;
        closeDevice();
        try {
            unregisterReceiver(mUsbReceiver);
        } catch (Exception e) {

        }
        usbBean = null;
    }
}
