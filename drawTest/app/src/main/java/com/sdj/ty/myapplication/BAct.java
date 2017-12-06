package com.sdj.ty.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by tangyang on 2017/3/18.
 */

public class BAct extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AAct.test = "bs";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","-----------BBBBB--------Ondestroy");
    }
}
