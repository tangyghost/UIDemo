package com.sdj.ty.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sdj.ty.myapplication.modle.CubLinesElement;
import com.sdj.ty.myapplication.modle.CubLinesEntiy;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by ty133 on 2017/1/5.
 */

public class CubLinesView extends View implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private Context context;
    private Paint mPaint = null;
    private Path mPath = null;
    private RectF targetRect = null;
    /**
     * 数据Model
     */
    private CubLinesElement element = null;
    /**
     * 单位度量值
     */
    private float yorkers;
    /**
     * 底部日期文字大小
     */
    private float dataSize;
    /**
     * 顶部数字文字大小
     */
    private float valueSize;
    /**
     * Y轴单位高度
     */
    private float unitValue;
    private NumberFormat nf;
    /**
     * 组件显示宽度
     */
    private int drawViewWidth;
    /**
     * 组件高度
     */
    private int drawViewHeight;
    /**
     * 组件总宽度
     */
    private float RealRectWidth;

    /**
     * 滑动状态
     */
    private boolean isScroll = false;
    /**
     * 滑动阻力值
     */
    private int obstruction = 350;
    /**
     * 基准线X
     */
    private float baseLineX = 0;
    /**
     * 基准值
     */
    private float baseValue = 0.75f;

    private float min;
    private float max;
    private GestureDetector mGestureDetector;

    public CubLinesView(Context context) {
        super(context);
        init(context);
        setOnTouchListener(this);
        // 构造GestureDetector对象，传入监听器对象
        mGestureDetector = new GestureDetector(context, this);
    }

    public CubLinesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setOnTouchListener(this);
        // 构造GestureDetector对象，传入监听器对象
        mGestureDetector = new GestureDetector(context, this);
    }

    public CubLinesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setOnTouchListener(this);
        // 构造GestureDetector对象，传入监听器对象
        mGestureDetector = new GestureDetector(context, this);
    }

    private final Xfermode[] xferModes = {
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            new PorterDuffXfermode(PorterDuff.Mode.SRC),
            new PorterDuffXfermode(PorterDuff.Mode.DST),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
            new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.XOR),
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    };
    private final Xfermode sModes = xferModes[5];
    private final Shader.TileMode sTileMode = Shader.TileMode.MIRROR;

    private void init(Context context) {
        this.context = context;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        mPath = new Path();
        targetRect = new RectF();
//        setWillNotDraw(false);
    }

    public CubLinesElement getElement() {
        return element;
    }

    private CubLinesEntiy fistCubLine = null;

    /**
     * 设置图表数据
     *
     * @param element 图表数据类
     */
    public void setUpData(CubLinesElement element) {
        this.element = element;
        candrawsValue = true;
        if (element.getList().size() > 0) {
            fistCubLine = element.getList().get(0);
            RealRectWidth = element.getXRectOffset() + element.getSpaceWidth() * (fistCubLine.getLines().size() - 1) + element.getSpaceWidth() / 2;
        } else {
            RealRectWidth = element.getXRectOffset() + element.getSpaceWidth() / 2;
        }
        dataSize = element.getDataSize();
        valueSize = element.getValueSize();
        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        for (CubLinesEntiy linesEntiy : element.getList()) {
            linesEntiy.calculateValue();
        }
        min = fistCubLine.getMinValue();
        max = 1;
        for (CubLinesEntiy linesEntiy : element.getList()) {
            if (min > linesEntiy.getMinValue()) {
                min = linesEntiy.getMinValue();
            }
            if (max < linesEntiy.getMaxValue()) {
                max = linesEntiy.getMaxValue();
            }
        }
        max = element.nearMaxInt(max);
        min = element.nearInt(min);
        if (min > 0) {
            int num = (int) (max / min) + 1;
            if (num < element.getMaxLineNumb())
                element.setMaxLineNumb(num);
        }
        yorkers = max / element.getMaxLineNumb();

    }

    private boolean candrawsValue = true;

    /**
     * 设置图表数据
     *
     * @param canvasHeight
     */
    private void drawsValue(float canvasHeight) {
        if (candrawsValue && canvasHeight > 0) {
            candrawsValue = false;
            baseLineX = drawViewWidth * baseValue;
            offsetX = baseLineX - RealRectWidth;
            unitValue = (canvasHeight - element.getYRectOffset() - valueSize - 10f) / element.getMaxLineNumb();
            for (CubLinesEntiy linesEntiy : element.getList()) {
                linesEntiy.setGradient(new LinearGradient(drawViewWidth / 2, (canvasHeight - element.getYRectOffset()) * (1f - linesEntiy.getMaxValue() / max), drawViewWidth / 2, canvasHeight - element.getYRectOffset(), linesEntiy.getColors(), null, sTileMode));
                List<CubLinesEntiy.Line> lines = linesEntiy.getLines();
                for (int i = 0; i < lines.size(); i++) {
                    CubLinesEntiy.Line.LinePoint point = lines.get(i).newPoint();
                    float Px = element.getXRectOffset() + element.getSpaceWidth() * i + element.getSpaceWidth() / 2;
                    float Py = canvasHeight - element.getYRectOffset() - (lines.get(i).getValue() / yorkers) * unitValue;
                    point.setX(Px);
                    point.setY(Py);
                    lines.get(i).setLinePoint(point);
                    int[] flagColors = new int[]{Color.parseColor("#00" + element.getFlagColor()), Color.parseColor("#ff" + element.getFlagColor())};
                    int[] flagSelectColors = new int[]{Color.parseColor("#00" + element.getFlagSelectColor()), Color.parseColor("#ff" + element.getFlagSelectColor())};
                    LinearGradient flagGradient = new LinearGradient(Px, Py * 0.9f, Px, canvasHeight - element.getYRectOffset() + 1, flagColors, null, sTileMode);
                    LinearGradient flagSelectGradient = new LinearGradient(Px, Py * 0.9f, Px, canvasHeight - element.getYRectOffset() + 1, flagSelectColors, null, sTileMode);
                    lines.get(i).setFlagGradient(flagGradient);
                    lines.get(i).setFlagSelectGradient(flagSelectGradient);
                }
            }
        }
    }

    /**
     * 设置背景颜色
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
//        mPaint.setColor(element.getBackgroundColor());
//        canvas.drawRect(0, 0, RealRectWidth + element.getSpaceWidth() / 2, canvas.getHeight(), mPaint);
        canvas.drawColor(element.getBackgroundColor());
    }

    /**
     * 绘制时间文本
     *
     * @param canvas
     * @param paint
     */
    private void drawDataText(Canvas canvas, Paint paint) {
        if (element.getList().size() <= 0)
            return;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(element.getDataTextColor());
        paint.setTextSize(dataSize);//设置字体大小
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        CubLinesEntiy entiy = element.getList().get(element.getList().size() - 1);
        for (int n = 0; n < entiy.getLines().size(); n++) {
            float lt = element.getSpaceWidth() * (n + 1) + element.getXRectOffset() + offsetX;
            if (lt > -element.getSpaceWidth() && lt < drawViewWidth + element.getSpaceWidth()) {
                targetRect.set(element.getXRectOffset() + element.getSpaceWidth() * n, drawViewHeight - element.getYRectOffset(), element.getXRectOffset() + element.getSpaceWidth() * (n + 1), drawViewHeight);
                int baseline = (int) (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                paint.setColor(element.getBackgroundColor());
                canvas.drawRect(targetRect, paint);
                paint.setColor(element.getDataTextColor());
                canvas.drawText(entiy.getLines().get(n).getDataText(), targetRect.centerX(), baseline, paint);
                targetRect.set(element.getXRectOffset() + element.getSpaceWidth() * n, ((float) drawViewHeight - element.getYRectOffset()) * (1f - entiy.getLines().get(n).getValue() / max) - element.getYRectOffset(), element.getXRectOffset() + element.getSpaceWidth() * (n + 1), ((float) drawViewHeight - element.getYRectOffset()) * (1f - entiy.getLines().get(n).getValue() / max));
                int baseline2 = (int) (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                paint.setColor(Color.parseColor("#00ffffff"));
                canvas.drawRect(targetRect, paint);
                paint.setColor(element.getValueTextColor());
                canvas.drawText(nf.format(entiy.getLines().get(n).getValue()), targetRect.centerX(), baseline2, paint);
            }
        }
    }

    /**
     * 绘制底部变色标志
     *
     * @param canvas
     * @param paint
     * @param canvasHeight 组件高度
     * @param entiy        线对象
     */
    private void drawflag(Canvas canvas, Paint paint, int canvasHeight, CubLinesEntiy entiy) {
        List<CubLinesEntiy.Line> lines = entiy.getLines();
        if (isShowFlag && !isMoving && !isClicking)
            for (int n = 0; n < lines.size(); n++) {
                float lts = element.getSpaceWidth() * (n + 1) + element.getXRectOffset() + offsetX;
                if (lts > -element.getSpaceWidth() && lts < drawViewWidth + element.getSpaceWidth()) {
                    float startX = lines.get(n).getLinePoint().getX();
                    float startPy = lines.get(n).getLinePoint().getY();
                    paint.setAntiAlias(true); //去锯齿
                    paint.setStrokeWidth(1);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setShader(null);
                    paint.setXfermode(null);
                    float lt = element.getXRectOffset() + element.getSpaceWidth() * n + element.getSpaceWidth() / 2f + offsetX;
                    if (lt < baseLineX + 10 && lt > baseLineX - 10) {
                        paint.setColor(Color.parseColor("#ff" + element.getFlagSelectColor()));
                    } else {
                        paint.setColor(Color.parseColor("#ff" + element.getFlagColor()));
                    }
                    canvas.drawRect(startX - 1, (canvasHeight - element.getYRectOffset()) * 0.1f + startPy * 0.9f, startX + 1, canvasHeight - element.getYRectOffset(), paint);
                    if (lt < baseLineX + 10 && lt > baseLineX - 10) {
                        paint.setShader(lines.get(n).getFlagSelectGradient());
                    } else {
                        paint.setShader(lines.get(n).getFlagGradient());
                    }
                    paint.setXfermode(sModes);
                    canvas.drawRect(startX - 1, (canvasHeight - element.getYRectOffset()) * 0.1f + startPy * 0.9f, startX + 1, canvasHeight - element.getYRectOffset(), paint);
                    paint.setShader(null);
                    paint.setXfermode(null);
                }
            }
    }

    private long startM = 0;
    private int saveFlags = Canvas.ALL_SAVE_FLAG;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("方法2", "onMeasure: ");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("方法2", "onLayout: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("方法2", "onDraw: ");
        if (element == null) {
            return;
        }
        long tmpTime = startM;
        startM = System.currentTimeMillis();
        Log.i("间隔时间：", (startM - tmpTime) + "ms");
        drawViewWidth = canvas.getWidth();
        drawViewHeight = canvas.getHeight();
        drawsValue(drawViewHeight);
//        drawBackground(canvas);
//        int layer = canvas.saveLayer(0f, 0f, RealRectWidth, drawViewHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.translate(offsetX, 0);
        drawLine(canvas);
        if (element.getList().size() > 0)
            drawflag(canvas, mPaint, drawViewHeight, element.getList().get(0));
        drawDataText(canvas, mPaint);
//        canvas.restoreToCount(layer);
//        Log.i("绘制层数：", layer + "层");
        canvas.clipRect(-offsetX, 0, drawViewWidth - offsetX, drawViewHeight);
        Log.i("绘制用时：", (System.currentTimeMillis() - startM) + "ms");
//        super.draw(canvas);
    }


    /**
     * 绘制线
     *
     * @param canvas
     */
    private long lineM = 0L;

    private void drawLine(Canvas canvas) {
        lineM = System.currentTimeMillis();
        for (int i = element.getList().size(); i > 0; i--) {
            if (element.getShowType() == 1)
                drawCubLineShade(canvas, mPaint, mPath, element.getList().get(i - 1));
            else if (element.getShowType() == 2)
                drawLineShade(canvas, mPaint, mPath, element.getList().get(i - 1));
        }
        Log.i("绘制Line时间：", (System.currentTimeMillis() - lineM) + "ms");
    }

    /**
     * 绘制曲线
     *
     * @param canvas
     * @param myPaint
     * @param path
     * @param linesEntiy 线对象数据
     */
    private void drawCubLineShade(Canvas canvas, Paint myPaint, Path path, CubLinesEntiy linesEntiy) {
        List<CubLinesEntiy.Line> lines = linesEntiy.getLines();
        myPaint.setAntiAlias(true); //去锯齿
        myPaint.setStrokeWidth(1);
        myPaint.setColor(linesEntiy.getColor());
        myPaint.setShader(null);
        myPaint.setXfermode(null);
        myPaint.setStyle(Paint.Style.FILL);
        path.reset();
        for (int n = 1; n < lines.size(); n++) {
            float startPx = lines.get(n - 1).getLinePoint().getX();//element.getXRectOffset()  + element.getSpaceWidth() * (n - 1) + element.getSpaceWidth() / 2;
            float startPy = lines.get(n - 1).getLinePoint().getY();//canvasHeight - element.getYRectOffset() - (linesEntiy.getLines().get(n - 1).getValue() / yorkers) * unitValue;
            float endPx = lines.get(n).getLinePoint().getX(); //element.getXRectOffset()  + element.getSpaceWidth() * n + element.getSpaceWidth() / 2;
            float endPy = lines.get(n).getLinePoint().getY(); //canvasHeight - element.getYRectOffset() - (linesEntiy.getLines().get(n).getValue() / yorkers) * unitValue;
            float wt = (startPx + endPx) / 2;
            if (startPx + offsetX > 0 - element.getSpaceWidth() && startPx + offsetX < drawViewWidth + element.getSpaceWidth()) {
                Log.i("绘制柱形n:", "第" + n + "个");
                path.moveTo(startPx, (drawViewHeight - element.getYRectOffset()));
                path.lineTo(startPx, startPy);
                path.cubicTo(wt, startPy, wt, endPy, endPx, endPy);
                path.lineTo(endPx, (drawViewHeight - element.getYRectOffset()));
            }
        }
        canvas.drawPath(path, myPaint);
        if (linesEntiy.isShowShade()) {
            myPaint.setShader(linesEntiy.getGradient());
            myPaint.setXfermode(sModes);
            canvas.drawRect(-offsetX - 1f, ((float) drawViewHeight - element.getYRectOffset()) * (1f - linesEntiy.getMaxValue() / max) - 1f, drawViewWidth + 1f - offsetX, drawViewHeight - element.getYRectOffset() + 1f, myPaint);
            myPaint.setShader(null);
            myPaint.setXfermode(null);
        }
    }

    /**
     * 绘制折线
     *
     * @param canvas
     * @param myPaint
     * @param path
     * @param linesEntiy
     */
    private void drawLineShade(Canvas canvas, Paint myPaint, Path path, CubLinesEntiy linesEntiy) {
        myPaint.setAntiAlias(true); //去锯齿
        myPaint.setStrokeWidth(1);
        myPaint.setColor(linesEntiy.getColor());
        myPaint.setShader(null);
        myPaint.setXfermode(null);
        myPaint.setStyle(Paint.Style.FILL);
        List<CubLinesEntiy.Line> lines = linesEntiy.getLines();
        path.reset();
        for (int n = 1; n < lines.size(); n++) {
            float startPx = lines.get(n - 1).getLinePoint().getX();//element.getXRectOffset()  + element.getSpaceWidth() * (n - 1) + element.getSpaceWidth() / 2;
            float startPy = lines.get(n - 1).getLinePoint().getY();//canvasHeight - element.getYRectOffset() - (linesEntiy.getLines().get(n - 1).getValue() / yorkers) * unitValue;
            float endPx = lines.get(n).getLinePoint().getX(); //element.getXRectOffset()  + element.getSpaceWidth() * n + element.getSpaceWidth() / 2;
            float endPy = lines.get(n).getLinePoint().getY(); //canvasHeight - element.getYRectOffset() - (linesEntiy.getLines().get(n).getValue() / yorkers) * unitValue;
            if (startPx + offsetX > 0 - element.getSpaceWidth() && startPx + offsetX < drawViewWidth + element.getSpaceWidth()) {
                Log.i("绘制柱形n:", "第" + n + "个");
                path.moveTo(startPx, (drawViewHeight - element.getYRectOffset()));
                path.lineTo(startPx, startPy);
                path.lineTo(endPx, endPy);
                path.lineTo(endPx, (drawViewHeight - element.getYRectOffset()));
            }
        }
        canvas.drawPath(path, myPaint);
        if (linesEntiy.isShowShade()) {
            myPaint.setShader(linesEntiy.getGradient());
            myPaint.setXfermode(sModes);
            canvas.drawRect(-offsetX - 1, ((float) drawViewHeight - element.getYRectOffset()) * (1f - linesEntiy.getMaxValue() / max) - 1, drawViewWidth + 1 - offsetX, drawViewHeight - element.getYRectOffset() + 1, myPaint);
            myPaint.setShader(null);
            myPaint.setXfermode(null);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (isScroll || isClicking || isMoving)
            return false;
        float w = e.getX() - offsetX;
        if (w > element.getXRectOffset() && w < RealRectWidth + element.getSpaceWidth() / 2f) {
            //点击曲线区域
            int index = (int) ((w - element.getXRectOffset()) / element.getSpaceWidth());
            startPer = 0;
            oldPer = 0;
            float tl = element.getSpaceWidth() * index + element.getXRectOffset() + element.getSpaceWidth() / 2 + offsetX;
            //需要移动的距离
            float X = baseLineX - tl;
            //距离增量
            int click_augmenter = (int) (X / 50);
            Message message = new Message();
            message.what = 5;
            message.arg1 = index;
            message.arg2 = click_augmenter;
            handler.sendMessageDelayed(message, 5);
        }
        return false;
    }

    /**
     * 是否显示flag(补充条件)
     */
    private boolean isShowFlag = true;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //计算移动偏移量
        float X = e2.getX() - e1.getX();
        if (!isScroll && !isClicking) {
            if (Math.abs(X) > 10)
                isMoving = true;
            offsetX = lastX + X;
            isShowFlag = false;
            if (isMoving)
                invalidate();
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (isClicking)
            return true;
        isScroll = true;
        handler.sendEmptyMessageDelayed((int) velocityX, 5);
        return true;
    }

    //View之前的位置  
    private float lastX = 0;
    /**
     * 总偏移量
     */
    private float offsetX = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = offsetX;
                if (isScroll) {
                    //当处于滑动状态时点击 停止滑动
                    stopHandle = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                if (!isScroll) {
                    add_duction = 0;
                    startPer = 0;
                    oldPer = 0;
                    handler.sendEmptyMessageDelayed(2, 5);
                } else {
                    stopHandle = false;
                }
                break;
        }
        return true;
    }

    /**
     * 控制滑动是否停止
     */
    private boolean stopHandle = false;
    private int add_duction = 0;
    private int duction = 500;
    /**
     * 判断是否处于手指点击状态
     */
    private boolean isClicking = false;
    /**
     * 判断是否处于手指触摸移动状态
     */
    private boolean isMoving = false;

    /**
     * viewHandle
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what > obstruction) {
                if (isClicking) {
                    return;
                }
                isScroll = true;
                onMovedraw(msg.what / 100);
                if (!stopHandle) {
                    isScroll = false;
                    handler.sendEmptyMessageDelayed(msg.what - obstruction, 5);
                }
                if (offsetX > baseLineX - element.getXRectOffset()) {
                    offsetX = baseLineX - element.getXRectOffset();
                } else if (offsetX == element.getXRectOffset() - baseLineX) {
                    stopHandle = true;
                }
                if (offsetX < baseLineX - RealRectWidth) {
                    offsetX = baseLineX - RealRectWidth;
                } else if (offsetX > baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset()) {
                    offsetX = baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset();
                }
            } else if (msg.what < -obstruction) {
                //
                if (isClicking) {
                    return;
                }
                isScroll = true;
                onMovedraw(msg.what / 100);
                if (!stopHandle) {
                    isScroll = false;
                    handler.sendEmptyMessageDelayed(msg.what + obstruction, 5);
                }
                if (offsetX < baseLineX - RealRectWidth) {
                    offsetX = baseLineX - RealRectWidth;
                } else if (offsetX == baseLineX - RealRectWidth) {
                    stopHandle = true;
                }
                if (offsetX > baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset()) {
                    offsetX = baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset();
                }
            } else if (msg.what == 2) {
                //手指抬起后/滑动结束后都会进行位置纠正滑动
                if (isClicking) {
                    return;
                }
                if (offsetX > baseLineX - element.getXRectOffset()) {
                    isScroll = true;
                    float X = offsetX - baseLineX + element.getXRectOffset();
                    add_duction += 5;
                    if (add_duction > duction)
                        add_duction = duction;
                    startPer = (float) (1.0f - Math.pow((1.0f - (float) add_duction / (float) duction), 2 * 2.5f)) * X;
                    float tX = startPer - oldPer;
                    oldPer = startPer;
                    offsetX = offsetX - tX;
                    invalidate();
                    handler.sendEmptyMessageDelayed(2, 5);
                } else if (offsetX < baseLineX - RealRectWidth) {
                    isScroll = true;
                    float X = baseLineX - RealRectWidth - offsetX;
                    add_duction += 5;
                    if (add_duction > duction)
                        add_duction = duction;
                    startPer = (float) (1.0f - Math.pow((1.0f - (float) add_duction / (float) duction), 2 * 2.5f)) * X;
                    float tX = startPer - oldPer;
                    oldPer = startPer;
                    offsetX = offsetX + tX;
                    invalidate();
                    handler.sendEmptyMessageDelayed(2, 5);
                } else {
                    isScroll = false;
                    for (int i = 0; i < fistCubLine.getLines().size(); i++) {
                        float lt = element.getXRectOffset() + element.getSpaceWidth() * i + offsetX;
                        float rt = element.getXRectOffset() + element.getSpaceWidth() * (i + 1) + offsetX;
                        if (lt <= baseLineX && rt > baseLineX) {
                            //需要移动的距离
                            float X = baseLineX - lt - element.getSpaceWidth() / 2f;
                            if (Math.abs(X) > 5) {
                                //需要移动
                                Message message = new Message();
                                message.what = 5;
                                message.arg1 = i;
                                handler.sendMessageDelayed(message, 5);
                            } else {
                                isShowFlag = true;
                            }

                        }
                    }
                    invalidate();
                }
                if (offsetX < baseLineX - RealRectWidth) {
                    offsetX = baseLineX - RealRectWidth;
                } else if (offsetX > baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset()) {
                    offsetX = baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset();
                }
            } else if (msg.what == 5) {
                if (isScroll || isMoving)
                    return;
                isClicking = true;
                isShowFlag = true;
                int what = msg.arg1;
                //到屏幕左侧的距离
                float tl = element.getSpaceWidth() * what + element.getXRectOffset() + element.getSpaceWidth() / 2 + offsetX;
                //需要移动的距离
                float X = baseLineX - tl;
                if (tl > baseLineX + 5) {
                    //基准线右侧
                    //增量
                    int ad = (int) Math.abs(X) / 5;
                    if (ad < 1)
                        ad = 1;
                    offsetX = offsetX - ad;
                    invalidate();
                    Message message = new Message();
                    message.what = 5;
                    message.arg1 = what;
                    handler.sendMessageDelayed(message, 5);
                } else if (tl < baseLineX - 5) {
                    //基准线左侧
                    //增量
                    int ad = (int) Math.abs(X) / 5;
                    if (ad < 1)
                        ad = 1;
                    offsetX = offsetX + ad;
                    invalidate();
                    Message message = new Message();
                    message.what = 5;
                    message.arg1 = what;
                    handler.sendMessageDelayed(message, 5);
                } else {
                    //基准线上
                    offsetX = offsetX + X;
                    isClicking = false;
                    add_duction = 0;
                    startPer = 0;
                    oldPer = 0;
                    invalidate();
                }
                if (offsetX < baseLineX - RealRectWidth) {
                    offsetX = baseLineX - RealRectWidth;
                } else if (offsetX > baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset()) {
                    offsetX = baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset();
                }
            } else {
                if (!isMoving) {
                    handler.sendEmptyMessageDelayed(2, 5);
                    if (offsetX < baseLineX - RealRectWidth) {
                        offsetX = baseLineX - RealRectWidth;
                    } else if (offsetX > baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset()) {
                        offsetX = baseLineX - element.getSpaceWidth() / 2 - element.getXRectOffset();
                    }
                }
                isScroll = false;
                invalidate();
            }
        }
    };
    private float startPer;
    private float oldPer;

    /**
     * 重绘当前view
     */
    private void onMovedraw(float X) {
        offsetX = offsetX + X;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
