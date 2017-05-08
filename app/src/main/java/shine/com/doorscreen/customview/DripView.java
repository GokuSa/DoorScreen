package shine.com.doorscreen.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Administrator on 2016/8/15.
 */
public class DripView extends View {
    private static final String TAG = "DripView";
    //画圆过程的关键参数
    private float step = 1.5f;
    private float radius;
    private Path mPath;
    private Paint mPaint;
    private Animation mAnimation;
    private HalfCircle mHalfCircle;
    private FullCircle mFullCircle;

    public DripView(Context context) {
        this(context, null);
    }

    public DripView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DripView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = w / 2;
        Log.d(TAG, "radius:" + radius);
        //用于设定坐标
        mHalfCircle = new HalfCircle(radius);
        mFullCircle = new FullCircle(radius/2);

    }

    private void init() {
        mPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1f);
        mPaint.setColor(Color.BLUE);
        mAnimation = new TranslateAnimation(0, 0, 0, 200);
        mAnimation.setDuration(1000);
        mAnimation.setInterpolator(new AccelerateInterpolator());
        mAnimation.setFillAfter(false);
        mAnimation.setRepeatCount(1);
        mAnimation.setAnimationListener(mAnimationListener);
    }

    private void formingDrip(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(mHalfCircle.left_Vertex_X, mHalfCircle.left_Vertex_Y);
        mPath.cubicTo(mHalfCircle.left_Down_X, mHalfCircle.left_Down_Y,
                mHalfCircle.bottom_Left_X, mHalfCircle.bottom_Left_Y,
                mHalfCircle.bottom_Vertex_X, mHalfCircle.bottom_Vertex_Y);
        mPath.cubicTo(mHalfCircle.bottom_Right_X, mHalfCircle.bottom_Right_Y,
                mHalfCircle.right_Down_X, mHalfCircle.right_Down_Y,
                mHalfCircle.right_Vertex_X, mHalfCircle.right_Vertex_Y);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawDrop(Canvas canvas) {
        mPath.reset();
        mFullCircle.setPath(mPath);
        canvas.drawPath(mPath, mPaint);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.d(TAG, "draw");
        if (mHalfCircle.bottom_Vertex_Y >= radius) {
            isForming = false;
            float margin = mHalfCircle.radius - mHalfCircle.keyDistance;
            mHalfCircle.left_Down_Y = 0f;
            mHalfCircle.bottom_Left_Y = margin;
            mHalfCircle.bottom_Vertex_Y = margin;
            mHalfCircle.bottom_Right_Y = margin;
            mHalfCircle.right_Down_Y = 0;
            Log.d(TAG, "drop aninaiotn");
            drawDrop(canvas);
            dropAnimation();
        } else if (isForming) {
//            isForming=true;
            mHalfCircle.left_Down_Y += step;
            mHalfCircle.bottom_Left_Y += step;
            mHalfCircle.bottom_Vertex_Y += step;
            mHalfCircle.bottom_Right_Y += step;
            mHalfCircle.right_Down_Y += step;
            formingDrip(canvas);
        } else {
            drawDrop(canvas);
        }

    }

    boolean isForming = true;

    public void form() {
        Log.d(TAG, "isForming:" + isForming);
        if (isForming) {
            invalidate();
        }
    }

    public void dropAnimation() {
        this.startAnimation(mAnimation);
    }

    Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
           /* float margin = mHalfCircle.radius - mHalfCircle.keyDistance;
            mHalfCircle.left_Down_Y = 0f;
            mHalfCircle.bottom_Left_Y = margin;
            mHalfCircle.bottom_Vertex_Y = margin;
            mHalfCircle.bottom_Right_Y = margin;
            mHalfCircle.right_Down_Y = 0;*/
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isForming = true;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 半圆的坐标
     */
    static class HalfCircle {
        public float radius;
        public float blackMagic = 0.551915024494f;
        public float keyDistance;
        public float left_Vertex_X = 0f;
        public float left_Vertex_Y = 0f;

        public float left_Down_X;
        public float left_Down_Y;

        public float bottom_Left_X;
        public float bottom_Left_Y;

        public float bottom_Vertex_X;
        public float bottom_Vertex_Y;

        public float bottom_Right_X;
        public float bottom_Right_Y;

        public float right_Down_X;
        public float right_Down_Y;

        public float right_Vertex_X;
        public float right_Vertex_Y;

        public HalfCircle() {
        }

        public HalfCircle(float radius) {
            this.radius = radius;
            keyDistance = radius * blackMagic;
            left_Down_X = 0f;
            left_Down_Y = 0f;
            bottom_Left_X = radius - keyDistance;
            bottom_Left_Y = radius - keyDistance;
            bottom_Vertex_X = radius;
            bottom_Vertex_Y = radius - keyDistance;
            bottom_Right_X = radius + keyDistance;
            bottom_Right_Y = radius - keyDistance;
            right_Down_X = 2 * radius;
            right_Down_Y = 0f;
            right_Vertex_X = 2 * radius;
            right_Vertex_Y = 0f;

        }
    }

    static class FullCircle {
        public float radius;
        public float blackMagic = 0.551915024494f;
        public float keyDistance;
        public float left_Vertex_X = 0f;
        public float left_Vertex_Y = 0f;

        public float left_Down_X;
        public float left_Down_Y;

        public float bottom_Left_X;
        public float bottom_Left_Y;

        public float bottom_Vertex_X;
        public float bottom_Vertex_Y;

        public float bottom_Right_X;
        public float bottom_Right_Y;

        public float right_Down_X;
        public float right_Down_Y;

        public float right_Vertex_X;
        public float right_Vertex_Y;

        public float right_Up_X;
        public float right_Up_Y;

        public float top_Vertex_X;
        public float top_Vertex_Y;

        public float top_Left_X;
        public float top_Left_Y;

        public float top_Right_X;
        public float top_Right_Y;


        public float left_up_X;
        public float left_up_Y;

        public FullCircle(float radius) {
            this.radius = radius;
            keyDistance = radius * blackMagic;
            bottom_Vertex_X = radius;
            bottom_Vertex_Y = 2 * radius;

            bottom_Right_X = radius + keyDistance;
            bottom_Right_Y = 2 * radius;
            right_Down_X = 2 * radius;
            right_Down_Y = radius + keyDistance;
            right_Vertex_X = 2 * radius;
            right_Vertex_Y = radius;
            //第一象限的四分之一圆
            right_Up_X = 2 * radius;
            right_Up_Y = radius - keyDistance;
            top_Right_X = radius + keyDistance;
            top_Right_Y = radius / 2;
            top_Vertex_X = radius;
            top_Vertex_Y = 0f;
            //第二象限的四分之一圆
            top_Left_X = radius - keyDistance;
            top_Left_Y = radius / 2;

            left_up_X = 0f;
            left_up_Y = radius - keyDistance;
            left_Vertex_X = 0f;
            left_Vertex_Y = radius;
            //第三象限的四分之一圆
            left_Down_X = 0;
            left_Down_Y = radius + keyDistance;
            bottom_Left_X = radius - keyDistance;
            bottom_Left_Y = 2 * radius;
        }

        public void setPath(Path path) {
            path.moveTo(bottom_Vertex_X, bottom_Vertex_Y);
            path.cubicTo(bottom_Right_X, bottom_Right_Y, right_Down_X, right_Down_Y, right_Vertex_X, right_Vertex_Y);
            path.cubicTo(right_Up_X, right_Up_Y, top_Right_X, top_Right_Y, top_Vertex_X, top_Vertex_Y);
            path.cubicTo(top_Left_X, top_Left_Y, left_up_X, left_up_Y, left_Vertex_X, left_Vertex_Y);
            path.cubicTo(left_Down_X, left_Down_Y, bottom_Left_X, bottom_Left_Y, bottom_Vertex_X, bottom_Vertex_Y);
        }
    }
}
