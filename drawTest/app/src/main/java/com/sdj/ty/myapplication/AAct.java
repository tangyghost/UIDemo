package com.sdj.ty.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by tangyang on 2017/3/18.
 */

public class AAct extends Activity{

    public static String test =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","----------AAAAAAA---------Ondestroy");
    }
}
