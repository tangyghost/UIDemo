package com.sdj.ty.myapplication.modle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ty133 on 2017/1/5.
 */

public class CubLinesElement implements Serializable {

    private float dataSize;
    private float valueSize;
    private float spaceWidth;
    private float rightPadding;
    private int maxLineNumb;
    private float XRectOffset, YRectOffset;
    private int backgroundColor;
    private int dataTextColor;
    private int valueTextColor;
    private String flagColor;
    private String flagSelectColor;
    private int showType = 2;
    private float selectWidth;
    public final int CUBLINE = 1;
    public final int BROKENLINE = 2;

    private List<CubLinesEntiy> list;

    public List<CubLinesEntiy> newList() {
        return new ArrayList<>();
    }

    private final int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    private double magOfInt(float x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return Math.pow(10d, (double) i);
    }

    public int nearInt(float x) {
        double mag = magOfInt(x);
        int n = (int) (x / mag + 0.5f);
        return (n) * (int) mag;
    }

    public int nearMaxInt(float x) {
        double mag = magOfInt(x);
        int n = (int) (x / mag);
        return (n + 1) * (int) mag;
    }

    public CubLinesElement() {
    }

    public CubLinesElement(float dataSize, float valueSize, float spaceWidth, int maxLineNumb, float XRectOffset, float YRectOffset, int backgroundColor, int dataTextColor, int valueTextColor, int showType, List<CubLinesEntiy> list, String flagColor, String flagSelectColor) {
        this.dataSize = dataSize;
        this.valueSize = valueSize;
        this.spaceWidth = spaceWidth;
        this.maxLineNumb = maxLineNumb;
        this.XRectOffset = XRectOffset;
        this.YRectOffset = YRectOffset;
        this.backgroundColor = backgroundColor;
        this.dataTextColor = dataTextColor;
        this.valueTextColor = valueTextColor;
        this.showType = showType;
        this.flagSelectColor = flagSelectColor;
        this.flagColor = flagColor;
        this.list = list;
    }

    public float getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(float rightPadding) {
        this.rightPadding = rightPadding;
    }

    public void setFlagColor(String flagColor) {
        this.flagColor = flagColor;
    }

    public void setFlagSelectColor(String flagSelectColor) {
        this.flagSelectColor = flagSelectColor;
    }

    public float getSelectWidth() {
        return selectWidth;
    }

    public void setSelectWidth(float selectWidth) {
        this.selectWidth = selectWidth;
    }

    public String getFlagColor() {

        return flagColor;
    }

    public String getFlagSelectColor() {
        return flagSelectColor;
    }

    public void setMaxLineNumb(int maxLineNumb) {
        this.maxLineNumb = maxLineNumb;
    }

    public int getMaxLineNumb() {

        return maxLineNumb;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getShowType() {

        return showType;
    }

    public void setDataTextColor(int dataTextColor) {
        this.dataTextColor = dataTextColor;
    }

    public void setValueTextColor(int valueTextColor) {
        this.valueTextColor = valueTextColor;
    }

    public int getDataTextColor() {

        return dataTextColor;
    }

    public int getValueTextColor() {
        return valueTextColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {

        return backgroundColor;
    }

    public void setDataSize(float dataSize) {
        this.dataSize = dataSize;
    }

    public void setValueSize(float valueSize) {
        this.valueSize = valueSize;
    }

    public void setSpaceWidth(float spaceWidth) {
        this.spaceWidth = spaceWidth;
    }

    public void setXRectOffset(float XRectOffset) {
        this.XRectOffset = XRectOffset;
    }

    public void setYRectOffset(float YRectOffset) {
        this.YRectOffset = YRectOffset;
    }

    public void setList(List<CubLinesEntiy> list) {
        this.list = list;
    }

    public float getDataSize() {
        return dataSize;
    }

    public float getValueSize() {
        return valueSize;
    }

    public float getSpaceWidth() {
        return spaceWidth;
    }

    public float getXRectOffset() {
        return XRectOffset;
    }

    public float getYRectOffset() {
        return YRectOffset;
    }

    public List<CubLinesEntiy> getList() {
        return list;
    }
}
