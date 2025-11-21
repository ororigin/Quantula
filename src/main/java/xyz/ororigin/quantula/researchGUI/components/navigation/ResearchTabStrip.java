package xyz.ororigin.quantula.researchGUI.components.navigation;

import icyllis.modernui.core.Context;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.RadioButton;
import icyllis.modernui.widget.RadioGroup;

import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ResearchTabStrip extends RadioGroup {

    public static final int TAB_TECH_TREE = 1001;
    public static final int TAB_RESEARCH_DEVICE = 1002;

    private OnTabSelectedListener mListener;

    public ResearchTabStrip(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        // 科技树选项卡
        RadioButton techTreeTab = createTabButton("科技树", TAB_TECH_TREE);
        addView(techTreeTab);

        // 研究装置选项卡
        RadioButton deviceTab = createTabButton("研究装置", TAB_RESEARCH_DEVICE);
        addView(deviceTab);

        // 设置选中变化监听器
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mListener != null) {
                    mListener.onTabSelected(checkedId);
                }
            }
        });

        // 默认选中科技树
        check(TAB_TECH_TREE);
    }

    private RadioButton createTabButton(String text, int id) {
        RadioButton button = new RadioButton(getContext());
        button.setId(id);
        button.setText(text);
        button.setTextSize(14);

        var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int margin = dp(12);
        params.setMargins(margin, margin, margin, margin);
        button.setLayoutParams(params);

        return button;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        mListener = listener;
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int tabId);
    }
}