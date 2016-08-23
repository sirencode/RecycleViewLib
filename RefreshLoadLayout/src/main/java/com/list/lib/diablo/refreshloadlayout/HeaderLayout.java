package com.list.lib.diablo.refreshloadlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author  qinbaowei
 * @createtime 2015/10/10 09:52
 */
class HeaderLayout extends FrameLayout {
    private View headerView;
    private View headerContent;
    private ImageView arrowImg;
    private ProgressBar progressBar;
    private TextView titleTxt;
    private TextView tipTimeTxt;
    private TextView timeTxt;

    private int status = -1;

    private String dateFormat = "";

    /**
     * this value must be set
     */
    private String keyLastUpdateTime = "";

    private boolean showLastRefreshTime = false;

    /**
     * the time of rll_arrow rotate
     */
    private static final int ARROW_ROTATION_DURATION = 200;


    private ValueAnimator arrowRotationAnim;


    private static final int DURATION_PER_10_PIXEL = 15;

    private ValueAnimator heightAnim;

    private int headerHeight;

    private RefreshLoadMoreLayout.CallBack callBack;

    public HeaderLayout(Context context) {
        super(context);
        initView(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.rll_header_view, this, false);
        addView(view);
        headerView = view.findViewById(R.id.header_ll_root);
        headerContent = view.findViewById(R.id.header_rl_content);
        arrowImg = (ImageView) view.findViewById(R.id.header_iv_arrow);
        progressBar = (ProgressBar) view.findViewById(R.id.header_pb_arrow);
        titleTxt = (TextView) view.findViewById(R.id.header_tv_title);
        tipTimeTxt = (TextView) view.findViewById(R.id.header_tv_tip_time);
        timeTxt = (TextView) view.findViewById(R.id.header_tv_time);
        setStatus(Status.NORMAL);
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        headerHeight = height;
        LayoutParams params = (LayoutParams) headerView.getLayoutParams();
        params.height = headerHeight;
        headerView.setLayoutParams(params);
    }

    private void setHeightAnim(final int toHeight) {
        if (null != heightAnim && heightAnim.isRunning()) {
            heightAnim.cancel();
        }
        int duration = Math.abs(getHeaderHeight() - toHeight) / 10 * DURATION_PER_10_PIXEL;
        if (0 == duration) {
            setHeaderHeight(toHeight); //can't get real height immediately when set height
            if (0 == toHeight) {
                setStatusValue(Status.NORMAL);
            } else if (getHeaderContentHeight() == toHeight) {
                setStatusValue(Status.REFRESH);
            }
        } else {
            heightAnim = ValueAnimator.ofInt(getHeaderHeight(), toHeight);
            heightAnim.setDuration(duration);
            heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            heightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    final Integer animValue = (Integer) valueAnimator.getAnimatedValue();
                    setHeaderHeight(animValue);
                    if (animValue == toHeight) { //animator finised
                        if (0 == toHeight) {
                            setStatusValue(Status.NORMAL);
                        } else if (getHeaderContentHeight() == toHeight) {
                            setStatusValue(Status.REFRESH);
                        }
                    }
                }
            });
            heightAnim.start();
        }
    }

    public int getHeaderContentHeight() {
        return headerContent.getMeasuredHeight();
    }

    private void setStatusValue(int status) {
        this.status = status;
    }

    public void setStatus(int status) {
        if (this.status == status) {
            return;
        }

        int oldStatus = this.status;
        this.status = status;

        switch (this.status) {
            case Status.NORMAL:
                onNormalStatus(oldStatus);
                break;
            case Status.PULL_DOWN:
                onPullDownStatus(oldStatus);
                break;
            case Status.CAN_RELEASE:
                onCanRefreshStatus();
                break;
            case Status.REFRESH:
                onRefreshStatus();
                break;
            case Status.BACK_NORMAL:
                onBackNormalStatus();
                break;
            case Status.BACK_REFRESH:
                onBackRefreshStatus();
                break;
            case Status.AUTO_REFRESH:
                onAutoRefreshStatus();
                break;
            default:
                break;
        }
    }


    private void onNormalStatus(int oldStatus) {
        onPullDownStatus(oldStatus);
    }


    private void onPullDownStatus(int oldStatus) {
        progressBar.setVisibility(View.GONE);
        arrowImg.setVisibility(View.VISIBLE);
        titleTxt.setText(R.string.rll_header_hint_normal);
        String lastUpdateTime = getLastUpdateTime();
        if (TextUtils.isEmpty(lastUpdateTime)) {
            tipTimeTxt.setVisibility(View.GONE);
            timeTxt.setVisibility(View.GONE);
        } else {
            tipTimeTxt.setVisibility(View.VISIBLE);
            timeTxt.setVisibility(View.VISIBLE);
            timeTxt.setText(lastUpdateTime);
        }
        switch (oldStatus) {
            case Status.NORMAL:
                arrowImg.setRotation(0);
                break;
            case Status.CAN_RELEASE:
                rotateArrow(-180, 0);
                break;
            default:
                break;
        }
    }

    private void onCanRefreshStatus() {
        titleTxt.setText(R.string.rll_header_hint_ready);
        rotateArrow(0, -180);
    }

    private void rotateArrow(float from, float to) {
        if (null != arrowRotationAnim && arrowRotationAnim.isRunning()) {
            arrowRotationAnim.cancel();
        }
        arrowImg.setRotation(from);
        arrowRotationAnim = ValueAnimator.ofFloat(from, to);
        arrowRotationAnim.setDuration(ARROW_ROTATION_DURATION);
        arrowRotationAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float angel = (Float) animation.getAnimatedValue();
                arrowImg.setRotation(angel);
            }
        });
        arrowRotationAnim.start();
    }

    private void onRefreshStatus() {
        titleTxt.setText(R.string.rll_header_hint_loading);
        arrowImg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        saveLastUpdateTime(System.currentTimeMillis());
        setHeightAnim(getHeaderContentHeight());
        if (null != getCallBack()) {
            getCallBack().onRefresh();
        }
    }

    private void onBackNormalStatus() {
        setHeightAnim(0);
    }

    private void onBackRefreshStatus() {
        setHeightAnim(getHeaderContentHeight());
    }

    private void onAutoRefreshStatus() {
        String lastUpdateTime = getLastUpdateTime();
        if (TextUtils.isEmpty(lastUpdateTime)) {
            tipTimeTxt.setVisibility(View.GONE);
            timeTxt.setVisibility(View.GONE);
        } else {
            tipTimeTxt.setVisibility(View.VISIBLE);
            timeTxt.setVisibility(View.VISIBLE);
            timeTxt.setText(lastUpdateTime);
        }
        onRefreshStatus();
    }

    private static final long MINUTE_PER = 60 * 1000;
    private static final long HOUR_PER = 60 * MINUTE_PER;
    private static final long DAY_PER = 24 * HOUR_PER;

    private String getLastUpdateTime() {
        long lastUpdateTime = loadLastUpdateTime();
        if (INVALID_LAST_UPDATE_TIME == lastUpdateTime || !isShowLastRefreshTime()) {
            return "";
        }
        Date lastUpdateDate = new Date(lastUpdateTime);
        long nowTime = System.currentTimeMillis();
        if (TextUtils.isEmpty(getDateFormat())) {
            long disTime = nowTime - lastUpdateTime;
            long days = disTime / DAY_PER;
            long hours = disTime / HOUR_PER;
            long minutes = disTime / MINUTE_PER;
            if (0 == days) {
                if (0 == hours) {
                    if (0 == minutes) {
                        return getContext().getString(R.string.rll_header_time_justnow);
                    } else {
                        return minutes + getContext().getString(R.string.rll_header_time_minutes);
                    }
                } else {
                    return hours + getContext().getString(R.string.rll_header_time_hours);
                }
            } else {
                return days + getContext().getString(R.string.rll_header_time_days);
            }
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getDateFormat());
            return simpleDateFormat.format(lastUpdateDate);
        }
    }

    private static final String PREFERENCE_NAME = "RefreshLoadMoreLayout_HeaderLayout";
    private static final int INVALID_LAST_UPDATE_TIME = -1;

    private void saveLastUpdateTime(long time) {
        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putLong(getKeyLastUpdateTime(), time).commit();
    }

    private long loadLastUpdateTime() {
        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(getKeyLastUpdateTime(), INVALID_LAST_UPDATE_TIME);
    }

    public int getStatus() {
        return status;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        if (!TextUtils.isEmpty(dateFormat)) {
            this.dateFormat = dateFormat;
        }
    }

    public boolean isShowLastRefreshTime() {
        return showLastRefreshTime;
    }

    public void setIsShowLastRefreshTime(boolean isShowLastRefreshTime) {
        this.showLastRefreshTime = isShowLastRefreshTime;
    }

    public String getKeyLastUpdateTime() {
        return keyLastUpdateTime;
    }

    public void setKeyLastUpdateTime(String keyLastUpdateTime) {
        this.keyLastUpdateTime = keyLastUpdateTime;
    }

    public static class Status {

        public static final int NORMAL = 0;

        public static final int PULL_DOWN = 1;

        public static final int CAN_RELEASE = 2;

        public static final int REFRESH = 3;

        public static final int BACK_NORMAL = 4;

        public static final int BACK_REFRESH = 5;

        public static final int AUTO_REFRESH = 6;
    }

    public RefreshLoadMoreLayout.CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(RefreshLoadMoreLayout.CallBack callBack) {
        this.callBack = callBack;
    }

    public boolean isRefreshing() {
        return status == Status.REFRESH || status == Status.BACK_REFRESH || status == Status.AUTO_REFRESH;
    }
}
