package com.list.lib.diablo.refreshloadlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * must be only one child view
 * @author qinbaowei
 */
public class RefreshLoadMoreLayout extends ViewGroup {
    private Context context;

    private HeaderLayout headerLayout;
    private boolean mcanrefresh;

    private FooterLayout footerLayout;
    private boolean mcanloadmore;

    private boolean mmultitask;

    private CallBack callBack;

    private float mpreviousypos;

    public RefreshLoadMoreLayout(Context context) {
        super(context);
        initViews(context);
    }

    public RefreshLoadMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        this.context = context;
        setClickable(true); //make event deliver
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof HeaderLayout || child instanceof FooterLayout) {
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        child.getMeasuredHeightAndState());
            } else {
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        getContentMeasuredHeightState());
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r,
            int b) { //height of child must be smaller than parent
        int childCount = getChildCount();
        View vHeader = null;
        View vFooter = null;
        View vContent = null;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof HeaderLayout) {
                vHeader = child;
                vHeader.layout(0, 0, vHeader.getMeasuredWidth(), vHeader.getMeasuredHeight());
            } else if (child instanceof FooterLayout) {
                vFooter = child;
                int y = getMeasuredHeight() - vFooter.getMeasuredHeight();
                vFooter.layout(0, y, vFooter.getMeasuredWidth(), vFooter.getMeasuredHeight() + y);
            } else {
                vContent = child;
            }
        }
        int y = (null == vHeader ? 0 : vHeader.getMeasuredHeight()) - (null == vFooter ? 0
                : vFooter.getMeasuredHeight());
        vContent.layout(0, y, vContent.getMeasuredWidth(), getMeasuredHeight() + y);
    }

    /**
     * @return force
     */
    private float externForce(int height, int factorHeight) {
        float s1 = (float) height / factorHeight;
        if (s1 >= 1.0f) {
            s1 = 0.4f;
        } else if (s1 >= 0.6 && s1 < 1.0) {
            s1 = 0.6f;
        } else if (s1 >= 0.3 && s1 < 0.6) {
            s1 = 0.8f;
        } else {
            s1 = 1.0f;
        }
        return s1;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mpreviousypos = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float fNowYPos = ev.getRawY();
                float disY = fNowYPos - mpreviousypos;
                if (isHeaderActive()) {
                    disY *= externForce(headerLayout.getHeaderHeight(),
                            headerLayout.getHeaderContentHeight());
                } else if (isFooterActive()) {
                    disY *= externForce(footerLayout.getFooterHeight(),
                            footerLayout.getFooterContentHeight());
                }
                mpreviousypos = fNowYPos;
                if (isPullDown(MotionEvent.ACTION_MOVE, disY)) {
                    headerLayout.setHeaderHeight((int) (headerLayout.getHeaderHeight() + disY));
                    updatePullDownStatus(MotionEvent.ACTION_MOVE);
                    if (headerLayout.getHeaderHeight() > 0) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        return super.dispatchTouchEvent(ev);
                    }
                    return false;
                } else if (isPullUp(MotionEvent.ACTION_MOVE, disY)) {
                    footerLayout.setFooterHeight((int) (footerLayout.getFooterHeight() - disY));
                    updatePullUpStatus(MotionEvent.ACTION_MOVE);
                    if (footerLayout.getFooterHeight() > 0 && !footerLayout.isLoadingStatus()) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        return super.dispatchTouchEvent(ev);
                    }
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isPullDown(MotionEvent.ACTION_UP, 0)) {
                    updatePullDownStatus(MotionEvent.ACTION_UP);
                } else if (isPullUp(MotionEvent.ACTION_UP, 0)) {
                    updatePullUpStatus(MotionEvent.ACTION_UP);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isHeaderActive() {
        return isCanRefresh() && HeaderLayout.Status.NORMAL != headerLayout.getStatus()
                && headerLayout.getHeaderHeight() > 0;
    }

    private boolean isHeaderAutoMove() {
        if (!isCanRefresh()) {
            return false;
        }
        if (HeaderLayout.Status.BACK_REFRESH == headerLayout.getStatus()) {
            return true;
        }
        if (HeaderLayout.Status.BACK_NORMAL == headerLayout.getStatus()) {
            return true;
        }
        if (HeaderLayout.Status.AUTO_REFRESH == headerLayout.getStatus()) {
            return true;
        }
        return false;
    }

    /**
     * @return isContentToTop() == isContentToBottom() ? isFooterActive() : isFooterAutoMove();
     */
    private boolean isLoadMoreActive() {
        return isContentToTop() == isContentToBottom() ? isFooterActive() : isFooterAutoMove();
    }

    private boolean isPullDown(int action, float fDisYPos) {
        if (!isCanRefresh()) {
            return false;
        }
        if (isHeaderAutoMove()) {
            return false;
        }
        if (isLoadMoreActive()) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (fDisYPos > 0) { //move down
                    if (isContentToTop()) { //pull up
                        return true;
                    }
                } else if (fDisYPos < 0) { //move up
                    if (isHeaderActive()) { //back after pull down
                        return true;
                    }
                }
            case MotionEvent.ACTION_UP:
                if (isHeaderActive()) {
                    return true;
                }
            default:
                break;
        }
        return false;
    }

    private boolean isFooterAutoMove() {
        if (!isCanLoadMore()) {
            return false;
        }
        if (FooterLayout.Status.BACK_LOAD == footerLayout.getStatus()) {
            return true;
        }
        if (FooterLayout.Status.BACK_NORMAL == footerLayout.getStatus()) {
            return true;
        }
        return false;
    }

    private boolean isFooterActive() {
        return isCanLoadMore() && FooterLayout.Status.NORMAL != footerLayout.getStatus()
                && footerLayout.getFooterHeight() > 0;
    }

    /**
     * @return isContentToTop() == isContentToBottom() ? isHeaderActive() : isHeaderAutoMove();
     */
    private boolean isPullDownActive() {
        return isContentToTop() == isContentToBottom() ? isHeaderActive() : isHeaderAutoMove();
    }

    private boolean isPullUp(int action, float fDisYPos) {
        if (!isCanLoadMore()) {
            return false;
        }
        if (isFooterAutoMove()) {
            return false;
        }
        if (isPullDownActive()) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (fDisYPos > 0) {//move down
                    if (isFooterActive()) {//back after pull up
                        return true;
                    }
                } else if (fDisYPos < 0) {//move up
                    if (isContentToBottom()) {//pull up
                        return true;
                    }
                }
            case MotionEvent.ACTION_UP:
                if (isFooterActive()) {
                    return true;
                }
            default:
                break;
        }
        return false;
    }

    private void updatePullDownStatus(int action) {

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (HeaderLayout.Status.REFRESH
                        == headerLayout.getStatus()) {//change height not change status when refreshing
                    return;
                }
                if (headerLayout.getHeaderHeight()
                        >= headerLayout.getHeaderContentHeight()) {//can release to refresh
                    headerLayout.setStatus(HeaderLayout.Status.CAN_RELEASE);
                } else {//only pull down
                    headerLayout.setStatus(HeaderLayout.Status.PULL_DOWN);
                }
                break;
            case MotionEvent.ACTION_UP://change status when move,check status on up
                if (HeaderLayout.Status.CAN_RELEASE == headerLayout.getStatus()) {
                    headerLayout.setStatus(HeaderLayout.Status.REFRESH);
                } else if (HeaderLayout.Status.PULL_DOWN == headerLayout.getStatus()) {
                    headerLayout.setStatus(HeaderLayout.Status.BACK_NORMAL);
                } else if (HeaderLayout.Status.REFRESH == headerLayout.getStatus()) {
                    if (headerLayout.getHeaderHeight() > headerLayout.getHeaderContentHeight()) {
                        headerLayout.setStatus(HeaderLayout.Status.BACK_REFRESH);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void updatePullUpStatus(int action) {

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (FooterLayout.Status.LOAD == footerLayout.getStatus()) {
                    return;
                }
                if (footerLayout.getFooterHeight() >= footerLayout.getFooterContentHeight()) {
                    footerLayout.setStatus(FooterLayout.Status.CAN_RELEASE);
                } else {
                    footerLayout.setStatus(FooterLayout.Status.PULL_UP);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (FooterLayout.Status.CAN_RELEASE == footerLayout.getStatus()) {
                    footerLayout.setStatus(FooterLayout.Status.LOAD);
                } else if (FooterLayout.Status.PULL_UP == footerLayout.getStatus()) {
                    footerLayout.setStatus(FooterLayout.Status.BACK_NORMAL);
                } else if (FooterLayout.Status.LOAD == footerLayout.getStatus()) {
                    if (footerLayout.getFooterHeight() > footerLayout.getFooterContentHeight()) {
                        footerLayout.setStatus(FooterLayout.Status.BACK_LOAD);
                    }
                }
                break;
            default:
                break;
        }
    }

    private int getContentMeasuredHeightState() {
        if (getContentView() instanceof ScrollView || getContentView() instanceof RecyclerView
                || getContentView() instanceof AbsListView) {
            return MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        } else if (getContentView() instanceof View) {
            return MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        }
        return 0;
    }

    public boolean isContentToTop() {
        return RefreshLoadMoreUtil.isContentToTop(getContentView());
    }

    public boolean isContentToBottom() {
        return RefreshLoadMoreUtil.isContentToBottom(getContentView());
    }

    public void init(Config config) {
        setCallBack(config.mCallBack);
        setRefreshLayout();
        setCanRefresh(config.mCanRefresh);
        setIsShowLastRefreshTime(config.mShowLastRefreshTime);
        setHeaderKeyLastRefreshTime(config.mKeyLastRefreshTime);
        setHeaderDateFormat(config.mHeaderDateFormat);
        setLoadMoreLayout();
        setCanLoadMore(config.mCanLoadMore);
        setSupportAutoLoadMore(config.mAutoLoadMore);
        setMultiTask(config.mMultiTask);
    }

    public static class Config {
        public CallBack mCallBack;
        public boolean mCanRefresh = true;
        public boolean mShowLastRefreshTime = false;
        public String mKeyLastRefreshTime = "";
        public String mHeaderDateFormat = "yyyy-MM-dd";
        public boolean mCanLoadMore = true;
        public boolean mAutoLoadMore = false;
        public boolean mMultiTask = false;

        public Config(CallBack callBack) {
            this.mCallBack = callBack;
        }

        /**
         * @param b 是否支持下拉刷新
         */
        public Config canRefresh(boolean b) {
            mCanRefresh = b;
            return this;
        }

        /**
         * @param currActivityClass 当前页面activity的类名（作为key保存时间）
         * @param dateFormat 显示上次刷新时间的格式
         */
        public Config showLastRefreshTime(Class currActivityClass, String dateFormat) {
            mShowLastRefreshTime = true;
            mKeyLastRefreshTime = currActivityClass.getSimpleName();
            mHeaderDateFormat = dateFormat;
            return this;
        }

        public Config showLastRefreshTime(Class currActivityClass) {
            return showLastRefreshTime(currActivityClass, "");
        }

        /**
         * @param b 是否支持上拉加载更多
         */
        public Config canLoadMore(boolean b) {
            mCanLoadMore = b;
            return this;
        }

        /**
         * 自动上拉加载更多（默认不自动加载更多）
         */
        public Config autoLoadMore() {
            mAutoLoadMore = true;
            return this;
        }

        /**
         * 刷新和加载更多可同时进行（默认不能同时进行）
         */
        public Config multiTask() {
            mMultiTask = true;
            return this;
        }
    }

    private void setHeaderDateFormat(String dateFormat) {
        headerLayout.setDateFormat(dateFormat);
    }

    private void setHeaderKeyLastRefreshTime(String key) {
        headerLayout.setKeyLastUpdateTime(key);
    }

    private void setIsShowLastRefreshTime(boolean b) {
        headerLayout.setIsShowLastRefreshTime(b);
    }

    public void setCanRefresh(boolean canRefresh) {
        mcanrefresh = canRefresh;
    }

    private void setRefreshLayout() {
        headerLayout = new HeaderLayout(context);
        headerLayout.setCallBack(getCallBack());
        headerLayout.setHeaderHeight(0);
        addView(headerLayout, 0);//header should be the first view
    }

    public boolean isCanRefresh() {
        if (mmultitask) {
            return mcanrefresh;
        } else {
            return mcanrefresh && null != footerLayout && !footerLayout.isLoadingMore();
        }
    }

    public boolean isCanLoadMore() {
        if (mmultitask) {
            return mcanloadmore;
        } else {
            return mcanloadmore && null != headerLayout && !headerLayout.isRefreshing();
        }
    }

    public void setCanLoadMore(boolean canLoadMore) {
        mcanloadmore = canLoadMore;
    }

    private void setLoadMoreLayout() {
        footerLayout = new FooterLayout(context);
        footerLayout.setCallBack(getCallBack());
        footerLayout.setFooterHeight(0);
        addView(footerLayout);
    }

    public void startAutoRefresh() {
        startAutoRefresh(500);
    }

    public void startAutoRefresh(final long delay) {
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isCanRefresh()) {
                                    return;
                                }
                                if (HeaderLayout.Status.NORMAL != headerLayout.getStatus()) {
                                    return;
                                }
                                headerLayout.setStatus(HeaderLayout.Status.AUTO_REFRESH);
                            }
                        }, delay);
                    }
                });
    }

    public void stopRefresh() {
        stopRefresh(true);
    }

    /**
     * @param canRefresh 是否禁用掉下拉刷新功能
     */
    public void stopRefresh(boolean canRefresh) {
        if (!isCanRefresh()) {
            return;
        }
        if (HeaderLayout.Status.BACK_NORMAL == headerLayout.getStatus()) {
            return;
        }
        headerLayout.setStatus(HeaderLayout.Status.BACK_NORMAL);
        setCanRefresh(canRefresh);
    }

    /**
     * 依然可以上拉加载更多
     */
    public void stopLoadMore() {
        stopLoadMore(false, true);
    }

    /**
     * @param noMoreData 是否没有更多数据了。如果为true，上拉的时候会提示’没有更多‘了
     */
    public void stopLoadMoreNoData(boolean noMoreData) {
        stopLoadMore(noMoreData, true);
    }

    /**
     * 如果 canLoadMore=false,则上拉加载更多功能不能使用
     */
    public void stopLoadMore(boolean canLoadMore) {
        stopLoadMore(false, canLoadMore);
    }

    private void stopLoadMore(boolean noMoreData, boolean canLoadMore) {
        if (!isCanLoadMore()) {
            return;
        }
        if (FooterLayout.Status.BACK_NORMAL == footerLayout.getStatus()) {
            return;
        }
        footerLayout.setStatus(FooterLayout.Status.BACK_NORMAL);
        footerLayout.setNoMoreData(noMoreData);
        setCanLoadMore(canLoadMore);
    }

    public CallBack getCallBack() {
        return callBack;
    }

    private void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    private void setSupportAutoLoadMore(boolean b) {
        if (b) {
            if (getContentView() instanceof RecyclerView) {
                ((RecyclerView) getContentView()).addOnScrollListener(
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (dy > 0 && isCanLoadMore()
                                        && RefreshLoadMoreUtil.isContentToBottom(
                                        getContentView())) {
                                    footerLayout.setStatus(FooterLayout.Status.LOAD);
                                }
                            }
                        });
            } else if (getContentView() instanceof AbsListView) {
                ((AbsListView) getContentView()).setOnScrollListener(
                        new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {
                                if (SCROLL_STATE_IDLE == scrollState) {
                                    if (isCanLoadMore() && RefreshLoadMoreUtil.isContentToBottom(
                                            getContentView())) {
                                        footerLayout.setStatus(FooterLayout.Status.LOAD);
                                    }
                                }
                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem,
                                    int visibleItemCount, int totalItemCount) {

                            }
                        });
            }
        }
    }

    private void setMultiTask(boolean b) {
        mmultitask = b;
    }

    public interface CallBack {
        void onRefresh();

        void onLoadMore();
    }

    public View getContentView() {
        return getChildAt(1);
    }
}
