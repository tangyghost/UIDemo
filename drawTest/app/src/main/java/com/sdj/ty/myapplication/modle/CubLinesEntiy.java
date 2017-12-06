package com.sdj.ty.myapplication.modle;

import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.Shader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ty133 on 2017/1/5.
 */

public class CubLinesEntiy implements Serializable {

    private List<Line> lines;
    private float maxValue, minValue;
    private LinearGradient gradient = null;
    private int color;
    private int[] colors;
    private boolean showShade;
    private Path path;

    public CubLinesEntiy() {

    }

    public CubLinesEntiy(List<Line> lines, int color) {
        this.lines = lines;
        this.color = color;
        showShade = false;
    }

    public CubLinesEntiy(List<Line> lines, int[] colors, int color, boolean showShade) {
        this.lines = lines;
        this.colors = colors;
        this.color = color;
        this.showShade = showShade;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setShowShade(boolean showShade) {
        this.showShade = showShade;
    }

    public boolean isShowShade() {

        return showShade;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {

        return color;
    }

    public void setGradient(LinearGradient gradient) {
        this.gradient = gradient;
    }

    public LinearGradient getGradient() {

        return gradient;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {

        return maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public Line newLine() {
        return new Line();
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public class Line implements Serializable {
        private float value;
        private boolean isSelect;
        private String dataText;
        private LinearGradient flagGradient;
        private LinearGradient flagSelectGradient;
        private LinePoint linePoint;

        public LinearGradient getFlagSelectGradient() {
            return flagSelectGradient;
        }

        public void setFlagSelectGradient(LinearGradient flagSelectGradient) {
            this.flagSelectGradient = flagSelectGradient;
        }

        public LinePoint getLinePoint() {
            return linePoint;
        }

        public void setLinePoint(LinePoint linePoint) {
            this.linePoint = linePoint;
        }

        public LinePoint newPoint() {
            return new LinePoint();
        }

        public class LinePoint implements Serializable {
            private float X, Y;

            public float getX() {
                return X;
            }

            public void setX(float x) {
                X = x;
            }

            public float getY() {
                return Y;
            }

            public void setY(float y) {
                Y = y;
            }
        }

        public void setFlagGradient(LinearGradient flagGradient) {
            this.flagGradient = flagGradient;
        }

        public LinearGradient getFlagGradient() {

            return flagGradient;
        }

        public void setDataText(String dataText) {
            this.dataText = dataText;
        }

        public String getDataText() {

            return dataText;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public boolean isSelect() {

            return isSelect;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

    }

    public void calculateValue() {
        maxValue = 0;
        try {
            minValue = lines.get(0).getValue();
        } catch (Exception e) {
            minValue = 0;
        }
        for (int i = 0; i < lines.size(); i++) {
            if (maxValue < lines.get(i).getValue()) {
                maxValue = lines.get(i).getValue();
            }
            if (minValue > lines.get(i).getValue()) {
                minValue = lines.get(i).getValue();
            }
        }
    }
}
