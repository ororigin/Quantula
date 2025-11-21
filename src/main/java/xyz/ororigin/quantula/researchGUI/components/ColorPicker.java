package xyz.ororigin.quantula.researchGUI.components;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.widget.LinearLayout;

/**
 * 颜色选择器组件
 */
public class ColorPicker extends LinearLayout {

    private static final int[] PRESET_COLORS = {
            0xFF4285F4, // 蓝色
            0xFFEA4335, // 红色
            0xFFFBBC05, // 黄色
            0xFF34A853, // 绿色
            0xFF8B5CF6, // 紫色
            0xFF06B6D4, // 青色
            0xFFF97316, // 橙色
            0xFFEC4899, // 粉色
            0xFF6B7280, // 灰色
            0xFF84CC16, // 青绿色
    };

    private int mSelectedColor;
    private OnColorSelectedListener mListener;
    private View mSelectedColorView;

    public ColorPicker(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        // 添加内边距
        setPadding(dp(8), dp(8), dp(8), dp(8));

        // 设置最小尺寸
        setMinimumHeight(dp(60));
        setMinimumWidth(dp(200));

        // 默认选择第一个颜色
        mSelectedColor = PRESET_COLORS[0];

        // 创建颜色选项
        for (int i = 0; i < PRESET_COLORS.length; i++) {
            addColorOption(PRESET_COLORS[i], i == 0);
        }

    }

    private void addColorOption(int color, boolean selected) {
        // 使用自定义的ColorCircleView
        var colorView = new ColorCircleView(getContext(), color, selected);
        int size = dp(32);
        var params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));

        colorView.setOnClickListener(v -> {
            selectColor(color, colorView);
        });

        if (selected) {
            mSelectedColorView = colorView;
        }

        addView(colorView, params);
    }

    private void selectColor(int color, View colorView) {
        mSelectedColor = color;

        // 更新之前选中的视图
        if (mSelectedColorView != null && mSelectedColorView instanceof ColorCircleView) {
            ((ColorCircleView) mSelectedColorView).setSelected(false);
        }

        // 更新新选中的视图
        if (colorView instanceof ColorCircleView) {
            ((ColorCircleView) colorView).setSelected(true);
        }

        mSelectedColorView = colorView;

        // 通知监听器
        if (mListener != null) {
            mListener.onColorSelected(Color4I.rgb(color));
        }

    }

    public Color4I getSelectedColor() {
        return Color4I.rgb(mSelectedColor);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        mListener = listener;
    }

    private int dp(int value) {
        return (int) (value * getContext().getResources().getDisplayMetrics().density);
    }

    public interface OnColorSelectedListener {
        void onColorSelected(Color4I color);
    }

    /**
     * 自定义颜色圆形视图
     */
    private static class ColorCircleView extends View {
        private final int mColor;
        private final Paint mPaint;
        private final Paint mFillPaint;
        private boolean mSelected;

        public ColorCircleView(Context context, int color, boolean selected) {
            super(context);
            this.mColor = color;
            this.mSelected = selected;

            // 创建两个Paint对象：一个用于边框，一个用于填充
            this.mPaint = Paint.obtain();
            this.mFillPaint = Paint.obtain();

            setClickable(true);
            setFocusable(true);

        }

        public void setSelected(boolean selected) {
            if (this.mSelected != selected) {
                this.mSelected = selected;
                invalidate();
            }
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            if (width == 0 || height == 0) {
                return;
            }

            int centerX = width / 2;
            int centerY = height / 2;

            // 计算圆的半径
            int radius = Math.min(width, height) / 2 - dp(2);

            // 首先绘制填充颜色
            mFillPaint.setColor(mColor);
            mFillPaint.setStyle(Paint.FILL);
            mFillPaint.setAntiAlias(true);
            canvas.drawCircle(centerX, centerY, radius - dp(1), mFillPaint);

            // 然后绘制边框
            if (mSelected) {
                mPaint.setColor(0xFFFFFFFF); // 白色边框
                mPaint.setStyle(Paint.STROKE);
                mPaint.setStrokeWidth(dp(3));
                mPaint.setAntiAlias(true);
                canvas.drawCircle(centerX, centerY, radius, mPaint);
            } else {
                // 未选中状态的细边框
                mPaint.setColor(0x80000000); // 半透明黑色边框
                mPaint.setStyle(Paint.STROKE);
                mPaint.setStrokeWidth(dp(1));
                mPaint.setAntiAlias(true);
                canvas.drawCircle(centerX, centerY, radius, mPaint);
            }

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int size = dp(32);
            setMeasuredDimension(size, size);
        }

        private int dp(int value) {
            return (int) (value * getContext().getResources().getDisplayMetrics().density);
        }
    }
}