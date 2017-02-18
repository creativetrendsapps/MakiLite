/**  Copyright (C) 2016-2017  Roman Savchyn/Sunshine Apps
 Code is taken from:
 - Folio for Facebook by creativetrendsapps. Thank you!
 - Simple for Facebook by creativetrendsapps. Thank you!
 - FaceSlim by indywidualny. Thank you!
 - Toffed by JakeLane. Thank you!
 - SlimSocial by  Leonardo Rignanese. Thank you!
 - MaterialFBook by ZeeRooo. Thank you!
 - Simplicity by creativetrendsapps. Thank you!
 Copyright notice must remain here if you're using any part of this code.
 **/
package com.sunshine.makilite.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.sunshine.makilite.R;

public class SlideFinishLayout extends LinearLayout {
    //parent
    private View mParentView;
    private int mEdgeSlop;
    private int mTouchSlop;
	private int mInitX;
    private int mInitY;
    private int mTempX;
    private Scroller mScroller;
    private boolean isFinish = false;
    private boolean isSliding = false;
    private int mViewWidth;
    private ColorDrawable backDrawable;
    private Window window;
    private onSlideFinishListener finishListener;
    public SlideFinishLayout(Context context) {
        this(context, null);
    }

    public SlideFinishLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mEdgeSlop = ViewConfiguration.get(context).getScaledEdgeSlop();

        mScroller = new Scroller(context);

        if (context instanceof Activity) {
            window = ((Activity) context).getWindow();
            
            backDrawable = new ColorDrawable(getResources().getColor(R.color.black));
            backDrawable.setAlpha((int) (255 * 0.4));
            window.setBackgroundDrawable(backDrawable);
        }

      
        this.setBackgroundResource(R.color.white);
    }

    public void setFinishListener(onSlideFinishListener finishListener) {
        this.finishListener = finishListener;
    }

    public interface onSlideFinishListener {
        public void onSlideFinish();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mParentView = (View) getParent();
            mViewWidth = getWidth();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitX = mTempX = (int) event.getRawX();
                mInitY = (int) event.getRawY();
                if (mInitX <= mEdgeSlop) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                if (!isSliding && moveX - mInitX > mTouchSlop && Math.abs((int) event.getRawY() - mInitY) < mTouchSlop) {
                    isSliding = true;
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                if (moveX > mInitX && isSliding) {
                    mParentView.scrollBy(mTempX - moveX, 0);
                 
                    if (window != null) {
                        int pre = (int) ((((float) moveX / (float) mViewWidth) * 153f) + 102f);
                        backDrawable.setAlpha(255 - pre);
                        window.setBackgroundDrawable(backDrawable);
                    }
                } else if (moveX < mInitX) {
                 
                    mParentView.scrollTo(0, 0);
                }
                mTempX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                isSliding = false;
                if (mParentView.getScrollX() <= -mViewWidth / 2) {
                    isFinish = true;
                    scrollToRight();
                } else {
                    isFinish = false;
                    scrollToOrigin();
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            if (mScroller.isFinished() && isFinish && finishListener != null) {
                finishListener.onSlideFinish();
            }
        }
    }

  
    private void scrollToOrigin() {
        final int delta = mParentView.getScrollX();
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0, Math.abs(delta));
        postInvalidate();
    }

 
    private void scrollToRight() {
        backDrawable.setAlpha(0);
        window.setBackgroundDrawable(backDrawable);
        final int delta = mViewWidth + mParentView.getScrollX();
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0, Math.abs(delta));
        postInvalidate();
    }
}
