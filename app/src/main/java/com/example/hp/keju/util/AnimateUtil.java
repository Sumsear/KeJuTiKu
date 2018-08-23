package com.example.hp.keju.util;

import android.animation.ValueAnimator;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class AnimateUtil {

    public enum Type {
        X, Y, Z
    }

    public static void translation(final View view, final Type type, float... val) {

        ValueAnimator va = ValueAnimator.ofFloat(val);
        va.setDuration(500);
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();
                if (type.equals(Type.X)) {
                    view.setTranslationX(curValue);
                } else if (type.equals(Type.Y)) {
                    view.setTranslationY(curValue);
                } else {
                    ViewCompat.setTranslationZ(view, curValue);
                }
            }
        });
        va.start();
    }
}
