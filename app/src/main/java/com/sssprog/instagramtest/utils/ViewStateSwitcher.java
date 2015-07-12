package com.sssprog.instagramtest.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import com.sssprog.instagramtest.R;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;

public class ViewStateSwitcher {

    public static final String STATE_MAIN = "main";
    public static final String STATE_LOADING = "loading";
    public static final String STATE_EMPTY = "empty";

    private static final int DEFAULT_ANIMATION_DURATION = 200;

    private Activity activity;
    private LayoutInflater layoutInflater;
    /**
     * Container containing all view states
     */
    private ViewGroup container;
    /**
     * View that is currently displayed
     */
    private View currentView;
    private final Map<String, ViewInfo> states = new HashMap<String, ViewInfo>();

    private int animationDuration;
    private boolean isAnimating;
    /**
     * View to which we're animating to
     */
    private View animatingView;

    public ViewStateSwitcher(Activity activity, int targetViewId) {
        this(activity, activity.findViewById(targetViewId));
    }

    public ViewStateSwitcher(Activity activity, View targetView) {
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
        states.put(STATE_MAIN, new ViewInfo(0, targetView));
        currentView = targetView;
        animationDuration = DEFAULT_ANIMATION_DURATION;
        container = (ViewGroup) targetView.getParent();
    }

    public void addViewState(String state, View stateView) {
        Assert.assertFalse(states.containsKey(state));
        stateView.setVisibility(View.GONE);
        states.put(state, new ViewInfo(0, stateView));
    }

    public void addViewState(String state, int viewId) {
        Assert.assertFalse(states.containsKey(state));
        states.put(state, new ViewInfo(viewId, null));
    }

    public void switchToState(String state, boolean animate) {
        ViewInfo viewInfo = states.get(state);
        Assert.assertNotNull(viewInfo);

        if (isAnimating) {
            clearAnimations();
        }
        if (viewInfo.view == null) {
            viewInfo.view = layoutInflater.inflate(viewInfo.layoutId, container, false);
        }
        View nextView = viewInfo.view;
        if (nextView == currentView) {
            return;
        }
        if (nextView.getParent() != container) {
            container.addView(nextView);
        }
        if (animate) {
            animate(nextView);
        } else {
            show(nextView);
        }
    }

    private void clearAnimations() {
        if (animatingView.getAnimation() != null) {
            animatingView.getAnimation().setAnimationListener(null);
        }
        currentView.clearAnimation();
        animatingView.clearAnimation();

        isAnimating = false;
        currentView.setVisibility(View.GONE);
        animatingView.setVisibility(View.VISIBLE);
        currentView = animatingView;
    }

    private void show(View nextView) {
        currentView.setVisibility(View.GONE);
        nextView.setVisibility(View.VISIBLE);
        currentView = nextView;
    }

    private void animate(final View nextView) {
        isAnimating = true;
        animatingView = nextView;
        currentView.setVisibility(View.VISIBLE);
        nextView.setVisibility(View.VISIBLE);

        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(animationDuration);

        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(animationDuration);
        fadeIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimations();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        currentView.startAnimation(fadeOut);
        nextView.startAnimation(fadeIn);
    }

    public void switchToMain(boolean animate) {
        switchToState(STATE_MAIN, animate);
    }

    public void switchToLoading(boolean animate) {
        switchToState(STATE_LOADING, animate);
    }

    public void switchToEmpty(boolean animate) {
        switchToState(STATE_EMPTY, animate);
    }

    private static class ViewInfo {
        int layoutId;
        View view;

        private ViewInfo(int layoutId, View view) {
            this.layoutId = layoutId;
            this.view = view;
        }
    }

    public View inflateStateView(int layout) {
        return layoutInflater.inflate(layout, container, false);
    }

    public static ViewStateSwitcher createStandardSwitcher(Activity activity, View targetView) {
        ViewStateSwitcher result = new ViewStateSwitcher(activity, targetView);
        result.addViewState(STATE_LOADING, R.layout.loading_state_view);
        return result;
    }

    public static ViewStateSwitcher createStandardSwitcher(Activity activity, int targetViewId) {
        ViewStateSwitcher result = new ViewStateSwitcher(activity, targetViewId);
        result.addViewState(STATE_LOADING, R.layout.loading_state_view);
        return result;
    }

    public static TextView addTextState(ViewStateSwitcher viewStateSwitcher, String state, String text) {
        View emptyView = viewStateSwitcher.layoutInflater.inflate(R.layout.empty_state_view, viewStateSwitcher.container, false);
        TextView textView = (TextView) emptyView.findViewById(R.id.emptyText);
        textView.setText(text);
        viewStateSwitcher.addViewState(state, emptyView);
        return textView;
    }

    public static TextView addTextState(ViewStateSwitcher viewStateSwitcher, String state, int text) {
        return addTextState(viewStateSwitcher, state, viewStateSwitcher.activity.getString(text));
    }
}
