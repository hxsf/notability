package me.hxsf.notability.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import me.hxsf.notability.R;
import me.hxsf.notability.draw.Drawer;
import me.hxsf.notability.until.DensityUtil;

/**
 * Created by hxsf on 15－12－10.
 */
public class ArcMenu extends ViewGroup implements OnClickListener {
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;
    final private int deley = 100;
    private final int margin;
    private Position mPosition = Position.RIGHT_BOTTOM;
    private int mRadius;
    private int sRadius;
    /**
     * 菜单的状态
     */
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 菜单的主按钮
     */
    private View mCButton;
    private OnMenuItemClickListener mMenuItemClickListener;

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100, getResources().getDisplayMetrics());
        // 获取自定义属性的值
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArcMenu, defStyle, 0);

        int pos = a.getInt(R.styleable.ArcMenu_position, POS_RIGHT_BOTTOM);
        switch (pos) {
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }
        mRadius = (int) a.getDimension(R.styleable.ArcMenu_radius, TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources().getDisplayMetrics()));
        Log.e("TAG", "position = " + mPosition + " , radius =  " + mRadius);

        a.recycle();
        margin = DensityUtil.dp2px(context, 16f);
        System.out.println("margin: " + margin);
        Toast.makeText(context, "margin: " + margin, Toast.LENGTH_SHORT).show();

    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            // 测量child
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutCButton();
            int width = mCButton.getMeasuredWidth();
            int height = mCButton.getMeasuredHeight();
            int count = 6;
            sRadius = mRadius / 2 + 20;
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i);
                child.setVisibility(View.GONE);
                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();
                // 如果菜单位置在底部 左下，右下
                if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - height - ct - margin;
                }
                // 右上，右下
                if (mPosition == Position.RIGHT_TOP || mPosition == Position.RIGHT_BOTTOM) {
                    cl = getMeasuredWidth() - width - cl - margin;
                }
                child.layout(cl, ct, cl + cWidth, ct + cHeight);
            }
            count = 4;
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(5 + i);
                child.setVisibility(View.GONE);
                int cl = (int) (sRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ct = (int) (sRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();
                // 如果菜单位置在底部 左下，右下
                if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - (height + cHeight) / 2 - ct - margin;
                }
                // 右上，右下
                if (mPosition == Position.RIGHT_TOP || mPosition == Position.RIGHT_BOTTOM) {
                    cl = getMeasuredWidth() - (width + cWidth) / 2 - cl - margin;
                }
                child.layout(cl, ct, cl + cWidth, ct + cHeight);
            }

        }

    }

    /**
     * 定位主菜单按钮
     */
    private void layoutCButton() {
        mCButton = getChildAt(8);
        mCButton.setOnClickListener(this);

        int l = 0;
        int t = 0;

        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();
        Log.d("wwhh", width + " - " + height);
        switch (mPosition) {
            case LEFT_TOP:
                l = margin;
                t = margin;
                break;
            case LEFT_BOTTOM:
                l = margin;
                t = getMeasuredHeight() - height - margin;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width - margin;
                t = margin;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width - margin;
                t = getMeasuredHeight() - height - margin;
                break;
        }
        mCButton.layout(l, t, l + width, t + height);
    }

    @Override
    public void onClick(View v) {
        // mCButton = findViewById(R.id.id_button);
        // if(mCButton == null)
        // {
        // mCButton = getChildAt(0);
        // }

        if (mCurrentStatus == Status.CLOSE) {
            rotateCButton(v, 0f, 135f, deley);
        } else {
            rotateCButton(v, 135f, 0f, deley);
        }


        toggleMenu(deley);

    }

    /**
     * 切换菜单
     */
    public void toggleMenu(int duration) {
        // 为menuItem添加平移动画和旋转动画
        int count = 6;

        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i);
            childView.setVisibility(View.VISIBLE);
            // end 0 , 0
            // start
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            int xflag = 1;
            int yflag = 1;

            if (mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }

            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }

            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;

            // to open
            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);

            } else {
                // to close
                tranAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i * 100) / count);

            tranAnim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }
            });
            // 旋转动画
            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);

            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);

            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemClickListener != null)
                        mMenuItemClickListener.onClick(childView, pos);

                    menuItemAnim(pos - 1);
                    changeStatus();

                }
            });
        }
        count = 4;
        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(5 + i);
            childView.setVisibility(View.VISIBLE);
            // end 0 , 0
            // start
            int cl = (int) (sRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int ct = (int) (sRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            int xflag = 1;
            int yflag = 1;

            if (mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }

            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }

            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;

            // to open
            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);

            } else {
                // to close
                tranAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i * 100) / count);

            tranAnim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }
            });
            // 旋转动画
            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);

            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);

            final int pos = i + 6;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemClickListener != null)
                        mMenuItemClickListener.onClick(childView, pos);

                    menuItemAnim(pos - 1);
                    changeStatus();

                }
            });
        }
        // 切换菜单状态
        changeStatus();
    }

    /**
     * 添加menuItem的点击动画
     *
     * @param
     */
    private void menuItemAnim(int pos) {
        for (int i = 0; i < getChildCount() - 1; i++) {

            View childView = getChildAt(i);
            if (i == pos) {
                childView.startAnimation(scaleBigAnim(deley));
            } else {

                childView.startAnimation(scaleSmallAnim(deley));
            }

            childView.setClickable(false);
            childView.setFocusable(false);

        }
        // TODO sidebar onclick
        Drawer drawer = Drawer.getDrawer();
        Log.v("item", pos+"");
        switch (pos) {
            case 0:
                //color_black
                drawer.changePaintColor(Color.BLACK);
                break;
            case 1:
                //color_red
                drawer.changePaintColor(Color.RED);
                break;
            case 2:
                //color_orange
                drawer.changePaintColor(Color.YELLOW);
                break;
            case 3:
                //color_green
                drawer.changePaintColor(Color.GREEN);
                break;
            case 4:
                //color_blue
                drawer.changePaintColor(Color.rgb(0, 255, 255));
                break;
            case 5:
                //weight_small
                drawer.changePaintSize(2f);
                break;
            case 6:
                //weight_middle
                drawer.changePaintSize(4f);
                break;
            case 7:
                //weight_large
                drawer.changePaintSize(10f);
                break;
            default:
                break;
        }
        rotateCButton(mCButton, 135f, 0f, deley);
    }

    private Animation scaleSmallAnim(int duration) {

        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;

    }

    /**
     * 为当前点击的Item设置变大和透明度降低的动画
     *
     * @param duration
     * @return
     */
    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);

        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;

    }

    /**
     * 切换菜单状态
     */
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
    }

    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }

    private void rotateCButton(View v, float start, float end, int duration) {

        RotateAnimation anim = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 菜单的位置枚举类
     */
    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }


    /**
     * 点击子菜单项的回调接口
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

}
