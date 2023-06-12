package com.sprintray.net.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IUpdateProgress  extends View {


    private static final String TAG = "IUpdateProgress";


    /**
     * set default width
     */
    private static final int DEFAULT_WIDTH = 200;
    /**
     * set default height
     */
    private static final int DEFAULT_HEIGHT = DEFAULT_WIDTH / 8;


    private Paint paint;

    private int colorDeep = Color.parseColor("#CC0033");
    private int colorTransparent = Color.TRANSPARENT;

    int[] colors = {Color.parseColor("#cc0033"), Color.parseColor("#a6cc0033")
            ,Color.TRANSPARENT,Color.parseColor("#a6cc0033"),Color.parseColor("#cc0033")};
    float[] position = {0f, 0.4f,0.5f,0.6f, 1.0f};




    private Matrix matrix = new Matrix();

    private LinearGradient linearGradient;
    private ValueAnimator animator = ValueAnimator.ofInt(0, 30000);
    private int animatedValue;
    private int colorEnd;
    private int colorStart;

    public IUpdateProgress(Context context) {
        this(context, null);

    }

    public IUpdateProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IUpdateProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);







    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (getPaddingLeft() + DEFAULT_WIDTH + getPaddingRight());
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (getPaddingTop() + DEFAULT_HEIGHT + getPaddingBottom());
        }
        setMeasuredDimension(width, height);
    }
    private void initAnimator() {


        if (animator.isStarted()){
            return;
        }

        animator.setDuration(60000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                start = true;


                animatedValue = (int) animation.getAnimatedValue();
                matrix.setTranslate(animatedValue%1000,0);

                postInvalidate();
            }
        });



        animator.start();
    }





    private boolean start = false;
    public void start(boolean start){


        if (!start){
            animator.cancel();
        }


    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility==View.GONE){

            animator.cancel();

        }else if (visibility == View.VISIBLE){

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    initAnimator();
                }
            }, 500);

        }

        Log.d(TAG, "onVisibilityChanged: "+visibility);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if (start){
            if (linearGradient ==null){
                linearGradient = new LinearGradient(0, 0, getMeasuredWidth()/4, getMeasuredHeight()/2, colors, position, Shader.TileMode.REPEAT);
            }
            linearGradient.setLocalMatrix(matrix);
            paint.setShader(linearGradient);
            canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
        }else {
            linearGradient = new LinearGradient(0, 0, getMeasuredWidth()/4, getMeasuredHeight()/2, colors, position, Shader.TileMode.REPEAT);
            linearGradient.setLocalMatrix(matrix);
            paint.setShader(linearGradient);

            canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
        }



    }



}
