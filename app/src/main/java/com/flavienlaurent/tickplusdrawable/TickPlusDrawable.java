package com.flavienlaurent.tickplusdrawable;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class TickPlusDrawable extends Drawable {

    private static final long ANIMATION_DURATION = 280;
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    private Paint mLinePaint;
    private Paint mBackgroundPaint;

    private float[] mPoints = new float[8];
    private final RectF mBounds = new RectF();

    private boolean mTickMode;
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    private float mRotation;

    private int mStrokeWidth = 10;
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

        setupPlusMode();
    }

    private void setupPlusMode() {
        mPoints[0] = mBounds.left;
        mPoints[1] = mBounds.centerY();
        mPoints[2] = mBounds.right;
        mPoints[3] = mBounds.centerY();
        mPoints[4] = mBounds.centerX();
        mPoints[5] = mBounds.top;
        mPoints[6] = mBounds.centerX();
        mPoints[7] = mBounds.bottom;
    }

    private float x(int pointIndex) {
        return mPoints[xPosition(pointIndex)];
    }

    private float y(int pointIndex) {
        return mPoints[yPosition(pointIndex)];
    }

    private int xPosition(int pointIndex) {
        return pointIndex*2;
    }

    private int yPosition(int pointIndex) {
        return xPosition(pointIndex) + 1;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), mBounds.centerX(), mBackgroundPaint);

        canvas.save();
        canvas.rotate(180 * mRotation, (x(0) + x(1))/2, (y(0) + y(1))/2);
        canvas.drawLine(x(0), y(0), x(1), y(1), mLinePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(180 * mRotation, (x(2) + x(3)) / 2, (y(2) + y(3)) / 2);
        canvas.drawLine(x(2), y(2), x(3), y(3), mLinePaint);
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
                ObjectAnimator.ofFloat(this, mPropertyPointAX, mBounds.left),
                ObjectAnimator.ofFloat(this, mPropertyPointAY, mBounds.centerY()),

                ObjectAnimator.ofFloat(this, mPropertyPointBX, mBounds.centerX()),
                ObjectAnimator.ofFloat(this, mPropertyPointBY, mBounds.bottom),

                ObjectAnimator.ofFloat(this, mPropertyPointCX, mBounds.right),
                ObjectAnimator.ofFloat(this, mPropertyPointCY, mBounds.centerX()/2),

                ObjectAnimator.ofFloat(this, mPropertyPointDX, mBounds.centerX()),
                ObjectAnimator.ofFloat(this, mPropertyPointDY, mBounds.bottom),

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
                ObjectAnimator.ofFloat(this, mPropertyPointAX, mBounds.left),
                ObjectAnimator.ofFloat(this, mPropertyPointAY, mBounds.centerY()),

                ObjectAnimator.ofFloat(this, mPropertyPointBX, mBounds.right),
                ObjectAnimator.ofFloat(this, mPropertyPointBY, mBounds.centerY()),

                ObjectAnimator.ofFloat(this, mPropertyPointCX, mBounds.centerX()),
                ObjectAnimator.ofFloat(this, mPropertyPointCY, mBounds.top),

                ObjectAnimator.ofFloat(this, mPropertyPointDX, mBounds.centerX()),
                ObjectAnimator.ofFloat(this, mPropertyPointDY, mBounds.bottom),

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
        return PixelFormat.TRANSLUCENT;
    }

    private Property<TickPlusDrawable, Integer> mBackgroundColorProperty = new Property<TickPlusDrawable, Integer>(Integer.class, "bg_color") {
        @Override
        public Integer get(TickPlusDrawable object) {
            return object.mBackgroundPaint.getColor();
        }

        @Override
        public void set(TickPlusDrawable object, Integer value) {
            object.mBackgroundPaint.setColor(value);
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
        }
    };

    private Property<TickPlusDrawable, Float> mRotationProperty = new Property<TickPlusDrawable, Float>(Float.class, "rotation") {
        @Override
        public Float get(TickPlusDrawable object) {
            return object.mRotation;
        }

        @Override
        public void set(TickPlusDrawable object, Float value) {
            object.mRotation = value;
        }
    };

    private PointProperty mPropertyPointAX = new XPointProperty(0);
    private PointProperty mPropertyPointAY = new YPointProperty(0);
    private PointProperty mPropertyPointBX = new XPointProperty(1);
    private PointProperty mPropertyPointBY = new YPointProperty(1);
    private PointProperty mPropertyPointCX = new XPointProperty(2);
    private PointProperty mPropertyPointCY = new YPointProperty(2);
    private PointProperty mPropertyPointDX = new XPointProperty(3);
    private PointProperty mPropertyPointDY = new YPointProperty(3);

    private abstract class PointProperty extends Property<TickPlusDrawable, Float> {

        protected int mPointIndex;

        private PointProperty(int pointIndex) {
            super(Float.class, "point_" + pointIndex);
            mPointIndex = pointIndex;
        }
    }

    private class XPointProperty extends PointProperty {

        private XPointProperty(int pointIndex) {
            super(pointIndex);
        }

        @Override
        public Float get(TickPlusDrawable object) {
            return object.x(mPointIndex);
        }

        @Override
        public void set(TickPlusDrawable object, Float value) {
            object.mPoints[object.xPosition(mPointIndex)] = value;
            invalidateSelf();
        }
    }

    private class YPointProperty extends PointProperty {

        private YPointProperty(int pointIndex) {
            super(pointIndex);
        }

        @Override
        public Float get(TickPlusDrawable object) {
            return object.y(mPointIndex);
        }

        @Override
        public void set(TickPlusDrawable object, Float value) {
            object.mPoints[object.yPosition(mPointIndex)] = value;
            invalidateSelf();
        }
    }
}
