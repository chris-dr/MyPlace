package com.drevnitskaya.myplace.ui.custom;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by air on 03.07.17.
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    private int totalDy = 0;

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        totalDy = dyConsumed < 0 && totalDy > 0 || dyConsumed > 0 && totalDy < 0 ? 0 : totalDy;
        totalDy += dyConsumed;

        int height = child.getHeight();

        if (totalDy > height) {
            scaleTo(child, 0f);
            child.setClickable(false);

        } else if (totalDy < 0) {
            scaleTo(child, 1f);
            child.setClickable(true);
        }
    }

    private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutLinearInInterpolator();

    private void scaleTo(View floatingActionButton, float value) {
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(floatingActionButton)
                .scaleX(value)
                .scaleY(value)
                .setDuration(100)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
        viewPropertyAnimatorCompat.start();
    }
}
