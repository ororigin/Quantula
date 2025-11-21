package xyz.ororigin.quantula.researchGUI.components.navigation;

import icyllis.modernui.core.Context;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.SeekBar;
import icyllis.modernui.widget.TextView;

import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class QuickNavigationBar extends LinearLayout {

    private TextView mPositionText;
    private SeekBar mQuickJumpBar;

    public QuickNavigationBar(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        // 位置显示
        mPositionText = new TextView(getContext());
        mPositionText.setText("X: 0.0");
        mPositionText.setTextSize(12);
        mPositionText.setMinWidth(dp(80));
        addView(mPositionText, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        // 快速跳转滑块
        mQuickJumpBar = new SeekBar(getContext());
        mQuickJumpBar.setMax(1000); // 0-1000的范围，便于精度控制
        var seekBarParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1);
        int margin = dp(8);
        seekBarParams.setMargins(margin, 0, margin, 0);
        addView(mQuickJumpBar, seekBarParams);

        // 预设锚点按钮可以后续添加

        // 设置内边距
        int padding = dp(8);
        setPadding(padding, padding, padding, padding);
    }

    public void updatePosition(float xPosition) {
        if (mPositionText != null) {
            mPositionText.setText(String.format("X: %.1f", xPosition));

            // 同时更新滑块位置（假设xPosition范围是0-1000）
            int progress = (int) Math.min(Math.max(xPosition, 0), 1000);
            mQuickJumpBar.setProgress(progress);
        }
    }

    public void setOnJumpListener(OnJumpListener listener) {
        mQuickJumpBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && listener != null) {
                    listener.onPositionChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public interface OnJumpListener {
        void onPositionChanged(float xPosition);
    }
}