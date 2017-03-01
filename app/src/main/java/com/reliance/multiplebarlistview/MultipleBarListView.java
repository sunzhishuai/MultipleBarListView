package com.reliance.multiplebarlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by sunzhishuai on 17/2/28.
 * E-mail itzhishuaisun@sina.com
 */

public class MultipleBarListView extends ListView implements AbsListView.OnScrollListener, Animation.AnimationListener {
    private MulipleAttrs mAttrs = new MulipleAttrs();
    private View mScrollBarLayout;
    private int widthMeasureSpec, heightMeasureSpec;
    private int mScrollBarOffset;
    private Animation mInAnimation;
    private int ANIMATION_TIME = 700;
    private Animation mOutAnimation;

    public MultipleBarListView(Context context) {
        this(context, null);
    }

    public MultipleBarListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleBarListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        setOnScrollListener(this);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initAttrs(context, attrs, defStyleAttr);
        initBar(context);
        initAnimation(context);

    }

    private void initAnimation(Context context) {

        mInAnimation = AnimationUtils.loadAnimation(context, mAttrs.inAnimation);
        mInAnimation.setDuration(ANIMATION_TIME);

        mOutAnimation = AnimationUtils.loadAnimation(context, mAttrs.outAnimation);
        mOutAnimation.setDuration(ANIMATION_TIME);
        mOutAnimation.setAnimationListener(this);
    }

    private void initBar(Context context) {
        //讲scrollBar 布局 添加到布局中秋
        mScrollBarLayout = LayoutInflater.from(context).inflate(mAttrs.layoutId, this, false);
        mScrollBarLayout.setVisibility(GONE);
        //控件重新布局
        requestLayout();
    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultipleBarListView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int index = typedArray.getIndex(i);
            switch (index) {
                case R.styleable.MultipleBarListView_bar_bg:
                    break;
                case R.styleable.MultipleBarListView_bar_layout:
                    mAttrs.layoutId = typedArray.getResourceId(index, R.layout.barlayout);
                    break;
                case R.styleable.MultipleBarListView_bar_layout_in_animation:
                    mAttrs.inAnimation = typedArray.getResourceId(index, R.anim.barlayout_in_animation);
                    break;
                case R.styleable.MultipleBarListView_bar_layout_out_animation:
                    mAttrs.outAnimation = typedArray.getResourceId(index, R.anim.barlayout_out_animation);
                    break;
            }
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    Handler handler = new Handler();
    private int prePositon = -1;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (listener != null) {
            int scaleBarHeight = computeVerticalScrollExtent();
            int scaleBarOffset = computeVerticalScrollOffset();
            int scaleBarRange = Math.max(computeVerticalScrollRange(), 1);
            int scrollBarheight = scaleBarHeight * getMeasuredHeight() / scaleBarRange;
            mScrollBarOffset = getMeasuredHeight() * scaleBarOffset / scaleBarRange;
            mScrollBarLayout.layout(
                    getMeasuredWidth() - mScrollBarLayout.getMeasuredWidth() - getVerticalScrollbarWidth()
                    , mScrollBarOffset + scrollBarheight - mScrollBarLayout.getMeasuredHeight()
                    , getMeasuredWidth() - getVerticalScrollbarWidth()
                    , mScrollBarOffset + scrollBarheight - mScrollBarLayout.getMeasuredHeight() + mScrollBarLayout.getMeasuredHeight()
            );
            mScrollBarLayout.setVisibility(VISIBLE);

            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView.getBottom() > mScrollBarOffset + scrollBarheight &&
                        childView.getTop() < mScrollBarOffset + scrollBarheight&&prePositon!=firstVisibleItem + i) {
                    prePositon = firstVisibleItem + i;
                    Random random = new Random();
                    int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                    ((TextView) mScrollBarLayout).setTextColor(rgb);
                    listener.onPositionChange(this, firstVisibleItem + i, mScrollBarLayout);
                    measureChild(mScrollBarLayout, widthMeasureSpec, heightMeasureSpec);
                }
            }
        }

    }

    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        boolean b = super.awakenScrollBars(startDelay, invalidate);
        handler.removeCallbacksAndMessages(null);
        Log.e("awakenScrollBars", "awakenScrollBars");
        if (b && mScrollBarLayout.getVisibility() == GONE) {
            mScrollBarLayout.startAnimation(mInAnimation);
            Log.e("mInAnimation", "mInAnimation");
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScrollBarLayout.startAnimation(mOutAnimation);
                    Log.e("mOutAnimation", "mOutAnimation");
                }
            }, startDelay);
        }
        return b;

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mScrollBarLayout.setVisibility(GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private class MulipleAttrs {
        public int layoutId;
        public int inAnimation;
        public int outAnimation;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;
        //测量子布局
        measureChild(mScrollBarLayout, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getAdapter() != null && mScrollBarLayout.getVisibility() == VISIBLE) {
            mScrollBarLayout.layout(
                    getMeasuredWidth() - mScrollBarLayout.getMeasuredWidth() - getVerticalScrollbarWidth()
                    , mScrollBarOffset
                    , getMeasuredWidth() - getVerticalScrollbarWidth()
                    , mScrollBarOffset + mScrollBarLayout.getMeasuredHeight()
            );
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawBarLayout(canvas);
    }

    private void drawBarLayout(Canvas canvas) {
        if (mScrollBarLayout.getVisibility() == VISIBLE)
            drawChild(canvas, mScrollBarLayout, getDrawingTime());
    }

    private OnPositionChangeListener listener;

    public void setOnPositionChangeListener(OnPositionChangeListener listener) {
        this.listener = listener;
    }

    public interface OnPositionChangeListener {
        void onPositionChange(AbsListView parent, int position, View barLayout);
    }
}
