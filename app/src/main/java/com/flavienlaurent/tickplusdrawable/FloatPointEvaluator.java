package com.flavienlaurent.tickplusdrawable;

import android.animation.TypeEvaluator;

public class FloatPointEvaluator implements TypeEvaluator<FloatPoint> {

    @Override
    public FloatPoint evaluate(float fraction, FloatPoint startValue, FloatPoint endValue) {
        return new FloatPoint(startValue.x + fraction * (endValue.x - startValue.x), startValue.y + fraction * (endValue.y - startValue.y));
    }
}