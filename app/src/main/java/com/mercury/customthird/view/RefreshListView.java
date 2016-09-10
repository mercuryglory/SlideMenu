package com.mercury.customthird.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercury.customthird.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mercury on 2016/7/20.
 */
public class RefreshListView extends ListView {

    private View mHeaderView;
    private int  mMeasuredHeight;
    private int  mDownY;
    private final int DOWN_PULL       = 0;
    private final int RELEASE_REFRESH = 1;
    private final int REFRESHING      = 2;

    private int currentState = DOWN_PULL;   //用来记录当前的状态，默认是下拉刷新
    private ImageView       mIv_arrow;
    private ProgressBar     mPb;
    private TextView        mTv_state;
    private TextView        mTv_time;
    private RotateAnimation mUpAnimation;
    private RotateAnimation mDownAnimation;
    private OnFreshListener mOnFreshListener;

    private boolean isLoadMore = false;
    private View mFooterView;
    private int  mFooterViewHeight;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化头布局
        initHeaderView();
        //初始化脚布局
        initFooterView();
        //初始化动画操作
        initAnimation();

        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // System.out.println("view = [" + view + "], scrollState = [" + scrollState + "]");
                int lastVisiblePosition = RefreshListView.this.getLastVisiblePosition();
                if (scrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL &&
                        lastVisiblePosition == getCount() - 1 && !isLoadMore) {

                    System.out.println("加载更多");
                    isLoadMore = true;

                    mFooterView.setPadding(0, 0, 0, 0);
                    RefreshListView.this.setSelection(getCount() - 1);

                    if (mOnFreshListener != null) {
                        mOnFreshListener.onLoadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });

    }

    private void initAnimation() {
        mUpAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation
                .RELATIVE_TO_SELF, 0.5f);
        mUpAnimation.setDuration(500);
        mUpAnimation.setFillAfter(true);
        mDownAnimation = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation
                .RELATIVE_TO_SELF, 0.5f);
        mDownAnimation.setDuration(500);
        mDownAnimation.setFillAfter(true);

    }

    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.view_footer, null);
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
        this.addFooterView(mFooterView);

    }

    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.view_header, null);
        mIv_arrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        mPb = (ProgressBar) mHeaderView.findViewById(R.id.pb);
        mTv_state = (TextView) mHeaderView.findViewById(R.id.tv_state);
        mTv_time = (TextView) mHeaderView.findViewById(R.id.tv_time);

        //        int height = mHeaderView.getHeight();
        //        System.out.println("height:"+height);
        //让系统测量
        mHeaderView.measure(0, 0);
        mMeasuredHeight = mHeaderView.getMeasuredHeight();
        //        System.out.println("mMeasuredHeight:"+mMeasuredHeight);
        mHeaderView.setPadding(0, -mMeasuredHeight, 0, 0);
        this.addHeaderView(mHeaderView);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == REFRESHING) {
                    break;
                }

                int moveY = (int) ev.getY();
                int diffY = moveY - mDownY;
                int paddingTop = -mMeasuredHeight + diffY;
                int firstVisiblePosition = this.getFirstVisiblePosition();
                if (paddingTop > -mMeasuredHeight && firstVisiblePosition == 0) {
                    if (paddingTop > 0 && currentState == DOWN_PULL) {
                        // System.out.println("松开刷新");
                        currentState = RELEASE_REFRESH;
                        updateViewByState();
                    } else if (paddingTop <= 0 && currentState == RELEASE_REFRESH) {
                        //  System.out.println("下拉刷新");
                        currentState = DOWN_PULL;
                        updateViewByState();
                    }

                    //设置给头布局
                    mHeaderView.setPadding(0, paddingTop, 0, 0);
                    // System.out.println("paddingTop: " + paddingTop);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == DOWN_PULL) {
                    mHeaderView.setPadding(0, -mMeasuredHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {
                    mHeaderView.setPadding(0, 0, 0, 0);
                    //进入正在刷新状态
                    currentState = REFRESHING;
                    updateViewByState();

                    //将下拉刷新操作回调给主界面完成
                    if (mOnFreshListener != null) {
                        mOnFreshListener.onDownPull();
                    }
                }
                break;
            default:
                break;
        }
        //将触摸事件交给listview进行上下滑动操作
        return super.onTouchEvent(ev);
    }

    //根据当前的状态来刷新界面
    private void updateViewByState() {
        switch (currentState) {
            case DOWN_PULL:
                System.out.println(123123123);
                mIv_arrow.startAnimation(mDownAnimation);
                mTv_state.setText("下拉刷新");
                break;
            case RELEASE_REFRESH:
                mIv_arrow.startAnimation(mUpAnimation);
                mTv_state.setText("松开刷新");
                break;
            case REFRESHING:
                mTv_state.setText("正在刷新");
                mIv_arrow.clearAnimation();
                mIv_arrow.setVisibility(View.INVISIBLE);
                mPb.setVisibility(VISIBLE);
                mHeaderView.setPadding(0, 0, 0, 0);
                break;

            default:
                break;

        }
    }

    public void onFinish() {
        if (isLoadMore) {
            mFooterView.setPadding(0, 0, 0, -mFooterViewHeight);
            isLoadMore = false;
        }else{
            currentState = DOWN_PULL;
            mTv_state.setText("下拉刷新");
            mHeaderView.setPadding(0, -mMeasuredHeight, 0, 0);
            mPb.setVisibility(INVISIBLE);
            mIv_arrow.setVisibility(VISIBLE);
            mTv_time.setText("最新刷新时间:" + getCurrentTime());
        }

    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public interface OnFreshListener {
        public void onDownPull();

        public void onLoadMore();
    }

    public void setOnFreshListener(OnFreshListener listener) {
        this.mOnFreshListener = listener;
    }
}
