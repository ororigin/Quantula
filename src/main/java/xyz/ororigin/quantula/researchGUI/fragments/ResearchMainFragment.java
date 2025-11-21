package xyz.ororigin.quantula.researchGUI.fragments;

import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.FrameLayout;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.RadioButton;
import icyllis.modernui.widget.RadioGroup;
import xyz.ororigin.quantula.researchGUI.components.navigation.QuickNavigationBar;
import xyz.ororigin.quantula.researchGUI.components.navigation.ResearchTabStrip;

import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ResearchMainFragment extends Fragment {

    private RadioGroup mTabStrip;
    private FrameLayout mContentContainer;
    private QuickNavigationBar mNavigationBar;
    private String mCurrentFragmentTag;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable DataSet savedInstanceState) {
        var context = requireContext();

        // 创建三层垂直布局
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // 1. 顶部选项卡
        mTabStrip = createTabStrip(context);
        mainLayout.addView(mTabStrip, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        // 2. 中间内容区域 - 修复：设置ID
        mContentContainer = new FrameLayout(context);
        mContentContainer.setId(View.generateViewId()); // 添加这行
        var contentParams = new LinearLayout.LayoutParams(MATCH_PARENT, 0, 1);
        mainLayout.addView(mContentContainer, contentParams);

        // 3. 底部导航栏
        mNavigationBar = new QuickNavigationBar(context);
        mainLayout.addView(mNavigationBar, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        // 默认显示科技树界面
        switchToFragment(TechTreeFragment.class, "techtree");

        return mainLayout;
    }

    private RadioGroup createTabStrip(Context context) {
        ResearchTabStrip tabStrip = new ResearchTabStrip(context);

        // 设置选项卡点击监听 - 修正为正确的方式
        tabStrip.setOnTabSelectedListener(new ResearchTabStrip.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    case ResearchTabStrip.TAB_TECH_TREE:
                        switchToFragment(TechTreeFragment.class, "techtree");
                        break;
                    case ResearchTabStrip.TAB_RESEARCH_DEVICE:
                        switchToFragment(ResearchDeviceFragment.class, "device");
                        break;
                }
            }
        });

        return tabStrip;
    }

    private void switchToFragment(Class<? extends Fragment> fragmentClass, String tag) {
        if (tag.equals(mCurrentFragmentTag)) return;

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        // 隐藏当前Fragment
        Fragment currentFragment = getChildFragmentManager().findFragmentByTag(mCurrentFragmentTag);
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }

        // 显示或添加新Fragment
        Fragment newFragment = getChildFragmentManager().findFragmentByTag(tag);
        if (newFragment == null) {
            try {
                newFragment = fragmentClass.newInstance();
                ft.add(mContentContainer.getId(), newFragment, tag);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create fragment: " + fragmentClass.getSimpleName(), e);
            }
        } else {
            ft.show(newFragment);
        }

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        mCurrentFragmentTag = tag;
    }

    // 提供给子Fragment更新导航栏位置的方法
    public void updateNavigationPosition(float xPosition) {
        if (mNavigationBar != null) {
            mNavigationBar.updatePosition(xPosition);
        }
    }
}