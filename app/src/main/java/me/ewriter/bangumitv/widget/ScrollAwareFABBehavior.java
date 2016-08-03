package me.ewriter.bangumitv.widget;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * Created by Zubin on 2016/8/3.
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;


    public ScrollAwareFABBehavior(Context context, AttributeSet attributeSet) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            child.show();
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void animateOut(final FloatingActionButton button) {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button)
                    .scaleX(0.0F)
                    .scaleY(0.0F)
                    .alpha(0.0F)
                    .setInterpolator(INTERPOLATOR)
                    .withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
                        }


                        public void onAnimationCancel(View view) {
                            ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                        }


                        public void onAnimationEnd(View view) {
                            ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                            view.setVisibility(View.GONE);
                        }
                    })
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(),
                    android.support.design.R.anim.design_fab_out);
            anim.setInterpolator(INTERPOLATOR);
            anim.setDuration(200L);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
                }


                public void onAnimationEnd(Animation animation) {
                    ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                    button.setVisibility(View.GONE);
                }


                @Override public void onAnimationRepeat(final Animation animation) {
                }
            });
            button.startAnimation(anim);
        }
    }


    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button)
                    .scaleX(1.0F)
                    .scaleY(1.0F)
                    .alpha(1.0F)
                    .setInterpolator(INTERPOLATOR)
                    .withLayer()
                    .setListener(null)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(),
                    android.support.design.R.anim.design_fab_in);
            anim.setDuration(200L);
            anim.setInterpolator(INTERPOLATOR);
            button.startAnimation(anim);
        }
    }
}
