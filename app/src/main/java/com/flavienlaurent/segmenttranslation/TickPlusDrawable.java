package com.flavienlaurent.segmenttranslation;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class TickPlusDrawable extends Drawable {

    private static final long ANIMATION_DURATION = 250;
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    private Paint mLinePaint;
    private Paint mBackgroundPaint;

    private float[] mLinePoints = new float[8];
    private FloatPoint[] mFloatPoints = new FloatPoint[4];
    private final RectF mBounds = new RectF();

    private boolean mTickMode;
    private FloatPointEvaluator mFloatPointEvaluator = new FloatPointEvaluator();
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    private float mRotation;

    public int mStrokeWidth = 10;
    private int mTickColor = Color.BLUE;
    private int mPlusColor = Color.WHITE;

    public TickPlusDrawable() {
        this(10, Color.BLUE, Color.WHITE);
    }

    public TickPlusDrawable(int strokeWidth, int tickColor, int plusColor) {
        mStrokeWidth = strokeWidth;
        mTickColor = tickColor;
        mPlusColor = plusColor;
        setupPaints();
        initFloatPoints();
    }

    private void initFloatPoints() {
        mFloatPoints[0] = new FloatPoint(0,0);
        mFloatPoints[1] = new FloatPoint(0,0);
        mFloatPoints[2] = new FloatPoint(0,0);
        mFloatPoints[3] = new FloatPoint(0,0);
    }

    private void setupPaints() {
        mLinePaint = new Paint(ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mPlusColor);
        mLinePaint.setStrokeWidth(mStrokeWidth);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);

        mBackgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mTickColor);
    }

    @Override
     protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int padding = bounds.centerX()/2;

        mBounds.left = bounds.left + padding;
        mBounds.right = bounds.right - padding;
        mBounds.top = bounds.top + padding;
        mBounds.bottom = bounds.bottom - padding;

        mFloatPoints[0].x = mBounds.left;
        mFloatPoints[0].y = mBounds.centerY();
        mFloatPoints[1].x = mBounds.right;
        mFloatPoints[1].y = mBounds.centerY();
        mFloatPoints[2].x = mBounds.centerX();
        mFloatPoints[2].y = mBounds.top;
        mFloatPoints[3].x = mBounds.centerX();
        mFloatPoints[3].y = mBounds.bottom;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), mBounds.centerX(), mBackgroundPaint);

        canvas.save();
        canvas.rotate(180* mRotation, (mFloatPoints[0].x+ mFloatPoints[1].x)/2, (mFloatPoints[0].y+ mFloatPoints[1].y)/2);
        canvas.drawLine(mFloatPoints[0].x, mFloatPoints[0].y, mFloatPoints[1].x, mFloatPoints[1].y, mLinePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 * mRotation, (mFloatPoints[2].x + mFloatPoints[3].x) / 2, (mFloatPoints[2].y + mFloatPoints[3].y) / 2);
        canvas.drawLine(mFloatPoints[2].x, mFloatPoints[2].y, mFloatPoints[3].x, mFloatPoints[3].y, mLinePaint);
        canvas.restore();
    }

    public void toggle() {
        if(mTickMode) {
            animatePlus();
        } else {
            animateTick();
        }
        mTickMode = !mTickMode;
    }

    public void animateTick() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofObject(this, mPropertyPA, mFloatPointEvaluator, new FloatPoint(mBounds.left, mBounds.centerY())),
                ObjectAnimator.ofObject(this, mPropertyPB, mFloatPointEvaluator, new FloatPoint(mBounds.centerX(), mBounds.bottom)),
                ObjectAnimator.ofObject(this, mPropertyPC, mFloatPointEvaluator, new FloatPoint(mBounds.right, mBounds.centerX()/2)),
                ObjectAnimator.ofObject(this, mPropertyPD, mFloatPointEvaluator, new FloatPoint(mBounds.centerX(), mBounds.bottom)),
                ObjectAnimator.ofFloat(this, mRotationProperty, 0f, 1f),
                ObjectAnimator.ofObject(this, mLineColorProperty, mArgbEvaluator, mTickColor),
                ObjectAnimator.ofObject(this, mBackgroundColorProperty, mArgbEvaluator, Color.WHITE)
        );
        set.setDuration(ANIMATION_DURATION);
        set.setInterpolator(ANIMATION_INTERPOLATOR);
        set.start();
    }

    public void animatePlus() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofObject(this, mPropertyPA, mFloatPointEvaluator, new FloatPoint(mBounds.left, mBounds.centerY())),
                ObjectAnimator.ofObject(this, mPropertyPB, mFloatPointEvaluator, new FloatPoint(mBounds.right, mBounds.centerY())),
                ObjectAnimator.ofObject(this, mPropertyPC, mFloatPointEvaluator, new FloatPoint(mBounds.centerX(), mBounds.top)),
                ObjectAnimator.ofObject(this, mPropertyPD, mFloatPointEvaluator, new FloatPoint(mBounds.centerX(), mBounds.bottom)),
                ObjectAnimator.ofFloat(this, mRotationProperty, 0f, 1f),
                ObjectAnimator.ofObject(this, mLineColorProperty, mArgbEvaluator, Color.WHITE),
                ObjectAnimator.ofObject(this, mBackgroundColorProperty, mArgbEvaluator, mTickColor)
        );
        set.setDuration(ANIMATION_DURATION);
        set.setInterpolator(ANIMATION_INTERPOLATOR);
        set.start();
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(ColorFilter cf) {}

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    private void setPointValue(int indexPoint, float value) {
        mLinePoints[indexPoint] = value;
        invalidateSelf();
    }

    private void setPointValue2(int indexPoint, FloatPoint value) {
        mFloatPoints[indexPoint] = value;
        invalidateSelf();
    }

    /*Color*/

    private Property<TickPlusDrawable, Integer> mBackgroundColorProperty = new Property<TickPlusDrawable, Integer>(Integer.class, "bg_color") {
        @Override
        public Integer get(TickPlusDrawable object) {
            return object.mBackgroundPaint.getColor();
        }

        @Override
        public void set(TickPlusDrawable object, Integer value) {
            object.mBackgroundPaint.setColor(value);
            invalidateSelf();
        }
    };

    private Property<TickPlusDrawable, Integer> mLineColorProperty = new Property<TickPlusDrawable, Integer>(Integer.class, "line_color") {
        @Override
        public Integer get(TickPlusDrawable object) {
            return object.mLinePaint.getColor();
        }

        @Override
        public void set(TickPlusDrawable object, Integer value) {
            object.mLinePaint.setColor(value);
            invalidateSelf();
        }
    };

    /*Rotation*/

    private Property<TickPlusDrawable, Float> mRotationProperty = new Property<TickPlusDrawable, Float>(Float.class, "rotation") {
        @Override
        public Float get(TickPlusDrawable object) {
            return object.mRotation;
        }

        @Override
        public void set(TickPlusDrawable object, Float value) {
            object.mRotation = value;
            invalidateSelf();
        }
    };

    /*FloatPoints*/

    private PointProperty2 mPropertyPA = new PointProperty2(0);
    private PointProperty2 mPropertyPB = new PointProperty2(1);
    private PointProperty2 mPropertyPC = new PointProperty2(2);
    private PointProperty2 mPropertyPD = new PointProperty2(3);

    private class PointProperty2 extends Property<TickPlusDrawable, FloatPoint> {

        private int mIndexPoint;

        private PointProperty2(int indexPoint) {
            super(FloatPoint.class, "point_" + indexPoint);
            mIndexPoint = indexPoint;
        }

        @Override
        public FloatPoint get(TickPlusDrawable object) {
            return object.mFloatPoints[mIndexPoint];
        }

        @Override
        public void set(TickPlusDrawable object, FloatPoint value) {
            setPointValue2(mIndexPoint, value);
        }
    }
}
