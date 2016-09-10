package com.mercury.slidemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * Created by Mercury on 2016/7/21.
 */
public class SlideMenu extends ViewGroup {

    private View mMenuView;
    private View mMainView;
    private int mDownX;

    private final int MAIN_VIEW = 0;
    private final int MENU_VIEW = 1;
    private int currentView = MAIN_VIEW;
    private Scroller mScroller;
    private int mDownX1;
    private int mDownY;
    private LinearLayout ll;
    private ScrollView sv;

    public SlideMenu(Context context) {
        this(context,null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
    }

    /**
     * @param widthMeasureSpec  宽度测量规格
     * @param heightMeasureSpec 高度测量规格
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMenuView = this.getChildAt(0);
        mMenuView.measure(mMenuView.getLayoutParams().width, heightMeasureSpec);
        mMainView = this.getChildAt(1);
        mMainView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mMenuView.layout(-mMenuView.getMeasuredWidth(), 0, 0, b);
        mMainView.layout(l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                System.out.println("mDownXmain:"+mDownX);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                System.out.println("mDownXMOVE:"+mDownX);
                System.out.println("moveX"+moveX);
                int diffX = mDownX - moveX;
                System.out.println("diffX:"+diffX);
                int descX = getScrollX() + diffX;
                System.out.println("descX:" + descX);
                if (descX < -mMenuView.getMeasuredWidth()) {
                    scrollTo(-mMenuView.getMeasuredWidth(),0);
                } else if (descX > 0) {
                    scrollTo(0, 0);
                }else{
                    scrollBy(diffX, 0);
                }
                mDownX=moveX;
                break;
            case MotionEvent.ACTION_UP:
                int center=-mMenuView.getMeasuredWidth()/2;
                if (getScrollX() > center) {
                    currentView = MAIN_VIEW;
                    updateView();
                } else {
                    currentView = MENU_VIEW;
                    updateView();
                }
                break;


        }
        return true;
    }

    private void updateView() {
        int startX = getScrollX();
        int dx=0;
        if (currentView == MAIN_VIEW) {
            dx = 0 - startX;
        } else {
            dx=-mMenuView.getMeasuredWidth()-startX;
        }
        int duration = Math.abs(dx * 5);
        mScroller.startScroll(startX, 0, dx, 0, duration);
        invalidate();

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            scrollTo(currX, 0);
            invalidate();
        }
    }

    public boolean isMenuShow() {
        return currentView==MENU_VIEW;
    }

    public void hideMenu() {
        currentView=MAIN_VIEW;
        updateView();
    }

    public void showMenu() {
        currentView = MENU_VIEW;
        updateView();
    }

    //事件拦截

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX1 = (int) event.getX();
                System.out.println("mDownX1:"+mDownX1);
                mDownY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int moveY = (int) event.getY();

                int diffX = moveX - mDownX1;
                int diffY = moveY - mDownY;
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }
}
