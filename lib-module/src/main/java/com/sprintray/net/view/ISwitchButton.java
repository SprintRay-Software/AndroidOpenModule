package com.sprintray.net.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.sprintray.net.R;
import com.sprintray.net.binding.command.BindingCommand;

public class ISwitchButton extends View {

    private static final String TAG = "SwitchButton";
    /**
     * 控件默认宽度
     */
    private static final int DEFAULT_WIDTH = 200;
    /**
     * 控件默认高度
     */
    private static final int DEFAULT_HEIGHT = DEFAULT_WIDTH / 8 * 5;
    /**
     * 画笔
     */
    private Paint mPaint;


    private Paint textPaint;


    /**
     * 控件背景的矩形范围
     */
    private RectF mRectF;
    /**
     * 开关指示器按钮圆心 X 坐标的偏移量
     */
    private float mButtonCenterXOffset;
    /**
     * 颜色渐变系数
     */
    private float mColorGradientFactor = 1;


    /**
     * 状态切换时的动画时长
     */
    private long mAnimateDuration = 500;
    /**
     * 开关未选中状态,即关闭状态时的背景颜色
     */
    private int mBackgroundColorUnchecked = Color.parseColor("#EEEEEE");
    /**
     * 开关选中状态,即打开状态时的背景颜色
     */
    private int mBackgroundColorChecked = Color.parseColor("#191919");


    private int selectedTextColor = Color.parseColor("#3d830c");
    private int unSelectedTextColor = Color.parseColor("#FFFFFF");
    private int buttonSelectColor = Color.parseColor("#FFFFFF");
    private int buttonUnselectColor = Color.parseColor("#535455");


//    private int colorWhite = Color.WHITE;
//    private int colorTransparent = Color.parseColor("#61FFFFFF");
//    private int colorGreen= Color.parseColor("#3d830c");
//    private int colorBlackDark = Color.parseColor("#191919");
//    private int colorBlackLight = Color.parseColor("#535455");




    /**
     * 是否使用渐变背景
     */
    private Boolean gradientBackground = false;


    /**
     * 选中文字
     */
    private String switchOn = "";
    /**
     * 非选中文字
     */
    private String switchOff = "";


    private int switchTextSize = 26;


    private OnCheckedChangeListener mOnCheckedChangeListener;

    private boolean mChecked = true;


    private boolean drawCircle = false;


    private RectF thumbRect = new RectF();


    /**
     * 判断是否在切换
     */
    private boolean inSwitch = false;

    private float switchOnWidth;
    private float switchOffWidth;


    private OnClickListener expandClick;


    /**
     * 开关指示器按钮的颜色
     */
    private int mButtonColor = Color.parseColor("#3D830C");


    public ISwitchButton(Context context) {
        this(context, null);
    }

    public ISwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ISwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ISwitchButton);


        switchOn = typedArray.getString(R.styleable.ISwitchButton_android_switchTextOn);
        switchOff = typedArray.getString(R.styleable.ISwitchButton_android_switchTextOff);
        gradientBackground = typedArray.getBoolean(R.styleable.ISwitchButton_gradientBackground, false);
        drawCircle = typedArray.getBoolean(R.styleable.ISwitchButton_drawCircle, false);
        selectedTextColor = typedArray.getInteger(R.styleable.ISwitchButton_selectedTextColor, Color.parseColor("#3d830c"));
        unSelectedTextColor = typedArray.getInteger(R.styleable.ISwitchButton_unSelectedTextColor, Color.parseColor("#FFFFFF"));
        buttonSelectColor = typedArray.getInteger(R.styleable.ISwitchButton_buttonSelectColor, Color.parseColor("#FFFFFF"));
        buttonUnselectColor = typedArray.getInteger(R.styleable.ISwitchButton_buttonUnselectColor, Color.parseColor("#535455"));
        mBackgroundColorChecked = typedArray.getInteger(R.styleable.ISwitchButton_backgroundColorChecked, Color.parseColor("#191919"));
        mBackgroundColorUnchecked = typedArray.getInteger(R.styleable.ISwitchButton_backgroundColorUnchecked, Color.parseColor("#EEEEEE"));

        typedArray.recycle();


        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(switchTextSize);
        textPaint.setAntiAlias(true);

        mRectF = new RectF();


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inSwitch) {
                    return;
                }

                setChecked(!isChecked(), true);

            }
        });


        // 点击时开始动画


        if (!TextUtils.isEmpty(switchOn)) {
            switchOnWidth = textPaint.measureText(switchOn);
        }
        if (!TextUtils.isEmpty(switchOff)) {
            switchOffWidth = textPaint.measureText(switchOff);
        }


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

        if (isChecked()) {
            thumbRect.top = mPaint.getStrokeWidth();
            thumbRect.bottom = getMeasuredHeight();

            thumbRect.left = mPaint.getStrokeWidth();
            thumbRect.right = getMeasuredWidth() / 2f - mPaint.getStrokeWidth();
        } else {
            thumbRect.top = mPaint.getStrokeWidth();
            thumbRect.bottom = getMeasuredHeight();

            thumbRect.left = getMeasuredWidth() / 2f + mPaint.getStrokeWidth();
            thumbRect.right = getMeasuredWidth() - mPaint.getStrokeWidth();
        }

        startAnimate();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置画笔宽度为控件宽度的 1/40,准备绘制控件背景
//        mPaint.setStrokeWidth((float) getMeasuredWidth() / 40);


        //设置背景颜色 如果判断是否运用渐变色
        if (gradientBackground) {
            if (isChecked()) {
                // 选中状态时,背景颜色由未选中状态的背景颜色逐渐过渡到选中状态的背景颜色
                mPaint.setColor(getCurrentColor(mColorGradientFactor, mBackgroundColorUnchecked, mBackgroundColorChecked));
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            } else {
                // 未选中状态时,背景颜色由选中状态的背景颜色逐渐过渡到未选中状态的背景颜色
                mPaint.setColor(getCurrentColor(mColorGradientFactor, mBackgroundColorUnchecked, mBackgroundColorChecked));
//            mPaint.setColor(getCurrentColor(mColorGradientFactor, mBackgroundColorChecked, mBackgroundColorUnchecked));
                mPaint.setStyle(Paint.Style.STROKE);
            }
        } else {
            mPaint.setColor(mBackgroundColorChecked);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        }


        // 根据是否选中的状态设置画笔颜色

        // 设置背景的矩形范围


        mRectF.set(mPaint.getStrokeWidth(), mPaint.getStrokeWidth(), getMeasuredWidth() - mPaint.getStrokeWidth()
                , getMeasuredHeight() - mPaint.getStrokeWidth());
        // 绘制圆角矩形作为背景


        canvas.drawRoundRect(mRectF, getMeasuredHeight() / 8f, getMeasuredHeight() / 8f, mPaint);


        if (drawCircle) {
            drawCircleThumb(canvas);
        } else {
            drawableRoundRectThumb(canvas);
        }

        drawText(canvas);


    }


    /**
     * 绘制圆形指示器
     *
     * @param canvas
     */
    private void drawCircleThumb(Canvas canvas) {

        mPaint.setColor(mButtonColor);

        float radius = (getMeasuredHeight() - mPaint.getStrokeWidth() * 4) / 2;
        float x;
        float y;
        // 根据是否选中的状态来决定开关按钮指示器圆心的 X 坐标
        if (isChecked()) {
//            // 选中状态时开关按钮指示器在右边
//            x = getMeasuredWidth() - radius - mPaint.getStrokeWidth() - mPaint.getStrokeWidth();
            // 选中状态时开关按钮指示器圆心的 X 坐标从左边逐渐移到右边
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(mBackgroundColorUnchecked);
            x = getMeasuredWidth() - radius - mPaint.getStrokeWidth() - mPaint.getStrokeWidth() - mButtonCenterXOffset;
        } else {
//            // 未选中状态时开关按钮指示器在左边
//            x = radius + mPaint.getStrokeWidth() + mPaint.getStrokeWidth();
            // 未选中状态时开关按钮指示器圆心的 X 坐标从右边逐渐移到左边
            x = radius + mPaint.getStrokeWidth() + mPaint.getStrokeWidth() + mButtonCenterXOffset;

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mBackgroundColorChecked);

        }
        // Y 坐标就是控件高度的一半不变
        y = (float) getMeasuredHeight() / 2;
        canvas.drawCircle(x, y, radius, mPaint);
    }


    private void drawableRoundRectThumb(Canvas canvas) {


        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        float radius = getMeasuredHeight() / 8f;


        // 根据是否选中的状态来决定开关按钮指示器圆心的 X 坐标
        if (isChecked()) {

            mPaint.setColor(buttonSelectColor);

        } else if (!isChecked()) {

            mPaint.setColor(buttonUnselectColor);


        }

        canvas.drawRoundRect(thumbRect, radius, radius, mPaint);
    }


    private void drawText(Canvas canvas) {
        if (switchOn == null || switchOff == null) {
            return;
        }
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = (getMeasuredHeight() / 2f) + distance + (getPaddingTop() - getPaddingBottom());

        if (isChecked()) {
            textPaint.setColor(selectedTextColor);
            canvas.drawText(switchOn, getMeasuredWidth() / 4f, baseline, textPaint);
            textPaint.setColor(unSelectedTextColor);
            canvas.drawText(switchOff, 3 * getMeasuredWidth() / 4f, baseline, textPaint);

        } else {
            textPaint.setColor(unSelectedTextColor);
            canvas.drawText(switchOn, getMeasuredWidth() / 4f, baseline, textPaint);
            textPaint.setColor(selectedTextColor);
            canvas.drawText(switchOff, 3 * getMeasuredWidth() / 4f, baseline, textPaint);

        }


    }


    /**
     * Description:获取一个过渡期中当前颜色,fraction 为过渡系数,取值范围 0f-1f,值越接近 1,颜色就越接近 endColor
     *
     * @param fraction   当前渐变系数
     * @param startColor 过渡开始颜色
     * @param endColor   过渡结束颜色
     * @return 当前颜色
     */
    private int getCurrentColor(float fraction, int startColor, int endColor) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaStart = Color.alpha(startColor);

        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaEnd = Color.alpha(endColor);

        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaDifference = alphaEnd - alphaStart;

        int redCurrent = (int) (redStart + fraction * redDifference);
        int blueCurrent = (int) (blueStart + fraction * blueDifference);
        int greenCurrent = (int) (greenStart + fraction * greenDifference);
        int alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }

    /**
     * Description:开始开关按钮切换状态和背景颜色过渡的动画
     */
    private void startAnimate() {
        // 计算开关指示器的半径
        float radius = (getMeasuredHeight() - mPaint.getStrokeWidth() * 4) / 2;
        // 计算开关指示器的 X 坐标的总偏移量


        float start = isChecked() ? getMeasuredWidth() / 2f : 0;
        float end = isChecked() ? 0 : getMeasuredWidth() / 2f;

        AnimatorSet animatorSet = new AnimatorSet();
        // 偏移量逐渐变化到 0
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "buttonCenterXOffset", start, end);
        objectAnimator.setDuration(mAnimateDuration);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                thumbRect.left = (float) animation.getAnimatedValue();
                thumbRect.right = thumbRect.left + getMeasuredWidth() / 2f;
                invalidate();
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                inSwitch = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                inSwitch = false;
            }
        });


        // 背景颜色过渡系数逐渐变化到 1
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "colorGradientFactor", 0, 1);
        objectAnimator2.setDuration(mAnimateDuration);

        // 同时开始修改开关指示器 X 坐标偏移量的动画和修改背景颜色过渡系数的动画
        animatorSet.play(objectAnimator).with(objectAnimator2);
        animatorSet.start();
    }


    public void setButtonCenterXOffset(float buttonCenterXOffset) {
        mButtonCenterXOffset = buttonCenterXOffset;
    }

    public void setColorGradientFactor(float colorGradientFactor) {
        mColorGradientFactor = colorGradientFactor;
    }

    public void setAnimateDuration(long animateDuration) {
        mAnimateDuration = animateDuration;
    }

    public void setBackgroundColorUnchecked(int backgroundColorUnchecked) {
        mBackgroundColorUnchecked = backgroundColorUnchecked;
    }

    public void setBackgroundColorChecked(int backgroundColorChecked) {
        mBackgroundColorChecked = backgroundColorChecked;
    }

    public void setButtonColor(int buttonColor) {
        mButtonColor = buttonColor;
    }


    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }


    public void setExpandClick(OnClickListener expandClick) {
        this.expandClick = expandClick;
    }


    public boolean isChecked() {
        return mChecked;
    }


    public void setChecked(boolean checked, boolean callListener) {



        if (checked!= mChecked){

            Log.d(TAG, "setChecked: " + checked);

            mChecked = checked;
            startAnimate();
        }
        if (callListener && mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
        }

    }


    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(ISwitchButton buttonView, boolean isChecked);
    }


    @BindingAdapter(value = "onCheckedChangeCommand", requireAll = true)
    public static void setCheckCommand(ISwitchButton switchButtonL, BindingCommand bindingCommand) {


        switchButtonL.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ISwitchButton buttonView, boolean isChecked) {
                bindingCommand.execute(isChecked);
            }
        });


    }

    @BindingAdapter(value = {"switchState"}, requireAll = true)
    public static void setSwitchState(ISwitchButton switchButtonL, boolean switchState) {

        Log.d(TAG, "setStateCommand: "+switchState);
        switchButtonL.setChecked(switchState, false);

    }


}
