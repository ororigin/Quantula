package xyz.ororigin.quantula.researchGUI.fragments;

import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.FrameLayout;
import icyllis.modernui.widget.TextView;
import xyz.ororigin.quantula.researchGUI.components.techtree.TechTreeCanvas;

import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TechTreeFragment extends Fragment {

    private TechTreeCanvas mTechTreeCanvas;
    private TextView mPositionText;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable DataSet savedInstanceState) {
        var context = requireContext();

        // 使用 FrameLayout 作为容器
        FrameLayout containerLayout = new FrameLayout(context);

        // 科技树画布
        mTechTreeCanvas = new TechTreeCanvas(context);
        mTechTreeCanvas.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        // 设置位置变化监听，用于更新导航栏和显示位置信息
        mTechTreeCanvas.setOnPositionChangeListener(new TechTreeCanvas.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(float x, float y) {
                // 通知父Fragment更新导航栏
                if (getParentFragment() instanceof ResearchMainFragment) {
                    ((ResearchMainFragment) getParentFragment()).updateNavigationPosition(Math.abs(x));
                }

                // 更新位置文本显示
                if (mPositionText != null) {
                    mPositionText.setText(String.format("滚动位置: X=%.0f, Y=%.0f", x, y));
                }
            }
        });

        containerLayout.addView(mTechTreeCanvas);

        // 叠加的位置信息文本视图
        mPositionText = new TextView(context);
        mPositionText.setText("滚动位置: X=0, Y=0");
        mPositionText.setTextColor(0xFFFFFFFF);
        mPositionText.setTextSize(14);

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                WRAP_CONTENT, WRAP_CONTENT
        );
        textParams.gravity = Gravity.TOP | Gravity.START;
        textParams.setMargins(dp(10), dp(10), 0, 0);
        mPositionText.setLayoutParams(textParams);

        containerLayout.addView(mPositionText);

        return containerLayout;
    }

    private int dp(int value) {
        return (int) (value * requireContext().getResources().getDisplayMetrics().density);
    }
}