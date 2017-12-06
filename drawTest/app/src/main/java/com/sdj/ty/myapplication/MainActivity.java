package com.sdj.ty.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sdj.ty.myapplication.modle.CubLinesElement;
import com.sdj.ty.myapplication.modle.CubLinesEntiy;
import com.sdj.ty.myapplication.view.CubLinesView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.cubscrollview)
    CubLinesView cubscrollview;
    @BindView(R.id.button_usb)
    Button buttonUsb;
    @BindView(R.id.button_usb2)
    Button buttonUsb2;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.button_usb3)
    Button buttonUsb3;
    private Context context = MainActivity.this;
    public static int mScreenWidth;
    public static int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        ButterKnife.bind(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        cubscrollview = (CubLinesView) findViewById(R.id.cubscrollview);
        float dataSize = Dp2Px(13);
        float valueSize = Dp2Px(15);
        float spaceWidth = Dp2Px(70);
        int maxLineNumb = 15;
        float XRectOffset = Dp2Px(50);
        float YRectOffset = Dp2Px(25);
        int backgroundColor = Color.WHITE;
        int dataTextColor = Color.GRAY;
        int valueTextColor = Color.BLACK;
        String flagSelectColor = "ff0000";
        String flagColor = "DEDFFF";
        int showType = 1;
        List<CubLinesEntiy> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CubLinesEntiy entiy = new CubLinesEntiy();
            List<CubLinesEntiy.Line> lines = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                CubLinesEntiy.Line line = entiy.newLine();
                line.setDataText("02/0" + j);
                line.setValue((float) (i == 0 ? Math.random() * j + 3 : Math.random() * j + 5));
                line.setSelect(false);
                lines.add(line);
            }
            entiy.setColor(i == 0 ? Color.parseColor("#ff48bfef") : Color.parseColor("#4C00AAEE"));
//            entiy.setColors(new int[]{Color.parseColor("#FFCAB01E"), Color.parseColor("#FF67676D")});
            entiy.setColors(new int[]{Color.parseColor("#ff48bfef"), Color.parseColor("#0048bfef")});
            entiy.setLines(lines);
            entiy.setShowShade(true);
            list.add(entiy);
        }

        CubLinesElement element = new CubLinesElement(dataSize, valueSize, spaceWidth, maxLineNumb, XRectOffset, YRectOffset, backgroundColor, dataTextColor, valueTextColor, showType, list, flagColor, flagSelectColor);
        element.setRightPadding(Dp2Px(150));
        element.setSelectWidth(mScreenWidth * 0.75f);
        cubscrollview.setUpData(element);
//        buttonUsb.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // 像素和DP之间转换
    public int Dp2Px(float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button_usb:
////                startActivity(new Intent(context, USBActivity.class));
//                startActivity(new Intent(context, AAct.class));
//                break;
//        }
//    }

    @OnClick({R.id.button_usb, R.id.button_usb2, R.id.button_usb3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_usb:
                startActivity(new Intent(context, AAct.class));
                break;
            case R.id.button_usb2:
                startActivity(new Intent(context, BAct.class));
                break;
            case R.id.button_usb3:
                buttonUsb3.setText(AAct.test == null ? "null" : AAct.test);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
