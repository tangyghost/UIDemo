package com.sdj.ty.myapplication.usb;

import android.hardware.usb.UsbDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ty133 on 2017/1/13.
 */

public class USBBean implements Parcelable {
    private UsbDevice device;
    private byte[] newData;
    private long VID;
    private long PID;
    private int State;
    public static final int USB_CONNECT = 0X11;
    public static final int USB_UNCONNECT = 0X12;

    public USBBean() {

    }

    protected USBBean(Parcel in) {
        device = in.readParcelable(UsbDevice.class.getClassLoader());
        newData = in.createByteArray();
        VID = in.readLong();
        PID = in.readLong();
        State = in.readInt();
    }

    public static final Creator<USBBean> CREATOR = new Creator<USBBean>() {
        @Override
        public USBBean createFromParcel(Parcel in) {
            return new USBBean(in);
        }

        @Override
        public USBBean[] newArray(int size) {
            return new USBBean[size];
        }
    };

    public long getVID() {
        return VID;
    }

    public void setVID(long VID) {
        this.VID = VID;
    }

    public long getPID() {
        return PID;
    }

    public void setPID(long PID) {
        this.PID = PID;
    }

    public byte[] getNewData() {
        return newData;
    }

    public void setNewData(byte[] newData) {
        this.newData = newData;
    }

    public UsbDevice getDevice() {
        return device;
    }

    public void setDevice(UsbDevice device) {
        this.device = device;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    private String parserData(byte[] data) {
        return "";
    }

    public String getDataResults() {
        return parserData(newData);
    }

    public String bytes2HexString(byte[] b) {
        if (b == null)
            return null;
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase() + " ";
        }
        return ret;
    }

    private int byte2Int(byte value) {
        return value & 0xFF;
    }

    public boolean equalsByteArray(byte[] a, byte[] b) {
        if (a == null || b == null)
            return false;
        if (a.length != b.length)
            return true;
        for (int i = 0; i < a.length; i++) {
            Log.d("byte1", byte2Int(a[i]) + "");
            Log.d("byte2", byte2Int(b[i]) + "");
            if (byte2Int(a[i]) != byte2Int(b[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeByteArray(newData);
        dest.writeLong(VID);
        dest.writeLong(PID);
        dest.writeInt(State);

    }
}
