package com.sprintray.net.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sprintray.net.R;

import java.util.LinkedList;

public class LoadView  extends View {
    private static final String TAG = "LoadView";


    //默认值
    private final float DEFAULT_RADIUS = dp2px(12);
    private final int DEFAULT_NUMBER = 4;
    private final float DEFAULT_GAP = dp2px(12);
    private static final float RTL_SCALE = 0.7f;
    private static final float LTR_SCALR = 1.3f;
    private static final int LEFT_COLOR = 0XFFFF4040;
    private static final int RIGHT_COLOR = 0XFF00EEEE;
    private static final int MIX_COLOR = Color.BLACK;
    private static final int DURATION = 350;
    private static final int PAUSE_DUARTION = 80;
    private static final float SCALE_START_FRACTION = 0.2f;
    private static final float SCALE_END_FRACTION = 0.8f;

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = DEFAULT_WIDTH / 8;


    //属性

    private float gap  =DEFAULT_GAP; //两小球直接的间隔
    private float rtlScale; //小球从右边移动到左边时大小倍数变化(rtl = right to left)
    private float ltrScale;//小球从左边移动到右边时大小倍数变化
    private int color1;//初始左小球颜色
    private int color2;//初始右小球颜色
    private int mixColor;//两小球重叠处的颜色
    private int duration; //小球一次移动时长
    private int pauseDuration;//小球一次移动后停顿时长
    private float scaleStartFraction; //小球一次移动期间，进度在[0,scaleStartFraction]期间根据rtlScale、ltrScale逐渐缩放，取值为[0,0.5]
    private float scaleEndFraction;//小球一次移动期间，进度在[scaleEndFraction,1]期间逐渐恢复初始大小,取值为[0.5,1]

    private LinkedList<Integer> colorList = new LinkedList<Integer>();


    private boolean isAnimationStart = false;
    private ValueAnimator valueAnimator;


    private Paint paint;  //画笔
    private int ballNum = 4;//小球数目
    private boolean isBallColorWhite = false;
    private float ballRadius = 24; //初始时LoadView左小球半径

    private boolean autoStart;



    public LoadView(Context context) {
        this(context, null);
    }

    public LoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadView);
        ballRadius = ta.getDimension(R.styleable.LoadView_ballRadius, DEFAULT_RADIUS);
        ballNum = ta.getInt(R.styleable.LoadView_ballNum, DEFAULT_NUMBER);
        autoStart = ta.getBoolean(R.styleable.LoadView_android_autoStart, false);
        isBallColorWhite = ta.getBoolean(R.styleable.LoadView_ballColorWhite, false);
        ta.recycle();
        initDraw();
        initAnim();

    }
    /**
     * 初始化绘图数据
     */
    private void initDraw() {
        paint = new Paint();
        paint.setColor(Color.RED);

        if (isBallColorWhite){
            colorList.clear();
            colorList.add(Color.parseColor("#40FFFFFF"));
            colorList.add(Color.parseColor("#99FFFFFF"));
            colorList.add(Color.parseColor("#FFFFFFFF"));
            colorList.add(Color.parseColor("#FFFFFFFF"));
        }else{
            colorList.clear();
            colorList.add(Color.parseColor("#40D80000"));
            colorList.add(Color.parseColor("#99D80000"));
            colorList.add(Color.parseColor("#FFD80000"));
            colorList.add(Color.parseColor("#FFD80000"));
        }
    }

    private void initAnim() {




        float gapValue = 255f/ballNum;
        valueAnimator = ValueAnimator.ofInt(0, 255);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(0);//设置不循环
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

//             Log.d(TAG, "onAnimationUpdate: "+animation.getAnimatedFraction()+" "+animation.getAnimatedValue());

                //80 120 255 255;
                //80 120 255 255;

                int value = (int) animation.getAnimatedValue();

                // 0 1 20 50 100
                //
                for (int i=0;i<colorList.size();i++){

//                    Log.d(TAG, "onAnimationUpdate: i"+i+"  "+    Math.abs (value+(gapValue*i)-255));
                    if (isBallColorWhite){
                        float alpha =  Math.abs (value+(gapValue*(i+1))-255);
                        colorList.set(i, Color.argb((int)alpha,255,255,255));
                    }else{
                        float alpha =  Math.abs (value+(gapValue*(i+1))-255);
                        colorList.set(i, Color.argb((int)alpha,216,0,0));
                    }
                }
                postInvalidate();

            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimationStart){
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animation.start();
                        }
                    },800);

                }


            }
        });




    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) ((ballRadius*ballNum*2)+((ballNum-1)*gap)+getPaddingBottom()+getPaddingTop());
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) ((ballRadius*2)+getPaddingBottom()+getPaddingTop());
        }
        setMeasuredDimension(width, height);




    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircle(canvas);


    }


    private void drawCircle(Canvas canvas){

        for (int i= 0;i<ballNum;i++){
            paint.setColor(colorList.get(i));
            canvas.drawCircle(gap*i+(ballRadius*(2*i+1)),getMeasuredHeight()/2f,ballRadius,paint);
        }
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");

        stop();
        
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.GONE ||visibility == View.INVISIBLE){
            stop();
        }

        if (visibility== View.VISIBLE && autoStart){
            start();
        }

    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    //公开方法

    /**
     * 停止动画
     */
    public void stop() {

        isAnimationStart = false;
        if (valueAnimator!=null && valueAnimator.isRunning()){
            valueAnimator.cancel();
        }

        valueAnimator = null;

    }

    /**
     * 开始动画
     */
    public void start() {

        isAnimationStart = true;

        if (valueAnimator==null){
            initAnim();
        }
        valueAnimator.start();

    }





    /**
     * 设置动画时长
     *
     * @param duration      {@link #duration}
     * @param pauseDuration {@link #pauseDuration}
     */
    public void setDuration(int duration, int pauseDuration) {
        this.duration = duration;
        this.pauseDuration = pauseDuration;

        initAnim();
    }

    /**
     * 设置移动过程中缩放倍数
     *
     * @param ltrScale {@link #ltrScale}
     * @param rtlScale {@link #rtlScale}
     */
    public void setScales(float ltrScale, float rtlScale) {
        stop();
        this.ltrScale = ltrScale;
        this.rtlScale = rtlScale;
        ;
        requestLayout(); //可能涉及宽高变化
    }



}
