package xyz.ororigin.quantula.researchGUI.fragments;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.ColorDrawable;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.resources.TypedValue;
import icyllis.modernui.text.Editable;
import icyllis.modernui.text.TextWatcher;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import xyz.ororigin.quantula.Quantula;
import xyz.ororigin.quantula.researchGUI.QuantulaResearchUI;
import xyz.ororigin.quantula.researchGUI.components.ColorPicker;
import xyz.ororigin.quantula.team.TeamManageUtils;

import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 创建队伍界面
 */
public class CreateTeamFragment extends Fragment {

    private QuantulaResearchUI mParentUI;
    private EditText mTeamNameInput;
    private EditText mTeamDescriptionInput;
    private ColorPicker mColorPicker;
    private Button mCreateButton;
    private TextView mErrorText;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable DataSet savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        validateInput();


        if (mColorPicker != null) {
            // 延迟检查，确保布局完成
            mColorPicker.postDelayed(() -> {
                for (int i = 0; i < mColorPicker.getChildCount(); i++) {
                    View child = mColorPicker.getChildAt(i);
                }
                mColorPicker.invalidate();
            }, 100);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 获取父Fragment引用
        if (getParentFragment() instanceof QuantulaResearchUI) {
            mParentUI = (QuantulaResearchUI) getParentFragment();
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable DataSet savedInstanceState) {
        var context = requireContext();

        // 使用FrameLayout作为根布局来支持背景图片
        var rootLayout = new FrameLayout(context);

        // 1. 首先添加背景图片
        var backgroundImage = new ImageView(context);
        // 加载背景图片 - 替换为您的实际图片路径
        var backgroundDrawable = Image.create(Quantula.MODID, "gui/os/create_team/bg.png");
        if (backgroundDrawable != null) {
            backgroundImage.setImageDrawable(new BackgroundDrawable(backgroundDrawable));
        } else {
            // 备用背景颜色
            backgroundImage.setBackground(new ColorDrawable(0xFF1E1E1E));
        }

        var bgParams = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        backgroundImage.setLayoutParams(bgParams);
        rootLayout.addView(backgroundImage);

        // 2. 添加半透明遮罩让内容更清晰
        var overlay = new View(context);
        overlay.setBackground(new ColorDrawable(0x80000000)); // 半透明黑色遮罩
        var overlayParams = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        rootLayout.addView(overlay, overlayParams);

        // 3. 原有的内容布局
        var mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(dp(24), dp(24), dp(24), dp(24));

        // 设置内容布局参数
        var mainLayoutParams = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mainLayoutParams.gravity = Gravity.CENTER;
        mainLayout.setLayoutParams(mainLayoutParams);

        // 添加原有内容
        addTitleSection(mainLayout, context);
        addTeamNameSection(mainLayout, context);
        addTeamDescriptionSection(mainLayout, context);
        addColorPickerSection(mainLayout, context);
        addErrorSection(mainLayout, context);
        addActionButtons(mainLayout, context);

        // 包装在ScrollView中
        var scrollView = new ScrollView(context);
        var scrollParams = new FrameLayout.LayoutParams(
                dp(600), // 固定宽度，让内容区域不会太宽
                WRAP_CONTENT
        );
        scrollParams.gravity = Gravity.CENTER;
        scrollView.setLayoutParams(scrollParams);
        scrollView.addView(mainLayout, new ScrollView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        rootLayout.addView(scrollView);

        return rootLayout;
    }

    private void addTitleSection(LinearLayout parent, Context context) {
        var title = new TextView(context);
        title.setText("          创建文明");
        title.setTextSize(24f);
        title.setTextColor(0xFFE8E8E8);
        title.setGravity(Gravity.CENTER);

        var subtitle = new TextView(context);
        subtitle.setText("创建属于你的文明，开启星辰之旅吧");
        subtitle.setTextSize(14f);
        subtitle.setTextColor(0xFFB0B0B0);
        subtitle.setGravity(Gravity.CENTER);

        var titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        var titleLayoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        titleLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        titleLayoutParams.topMargin = dp(20);
        titleLayoutParams.bottomMargin = dp(30);
        titleLayout.addView(title, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        titleLayout.addView(subtitle, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        var titleContainer = new LinearLayout(context);
        titleContainer.setGravity(Gravity.CENTER);
        titleContainer.addView(titleLayout);
        var inputParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        inputParams.bottomMargin = dp(24);
        parent.addView(titleContainer,
                inputParams);
    }

    private void addTeamNameSection(LinearLayout parent, Context context) {
        var section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        var label = new TextView(context);
        label.setText("队伍名称");
        label.setTextSize(16f);
        label.setTextColor(0xFFE8E8E8);

        mTeamNameInput = new EditText(context);
        mTeamNameInput.setHint("输入队伍名称（最多32个字符）");
        mTeamNameInput.setSingleLine(true);
        mTeamNameInput.setBackground(createInputBackground(context));
        mTeamNameInput.setPadding(dp(12), dp(8), dp(12), dp(8));
        var inputParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        inputParams.bottomMargin = dp(24);

        // 添加文本变化监听
        mTeamNameInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput();
            }
        });

        var inputParams2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        inputParams2.bottomMargin = dp(6);
        section.addView(label,inputParams2);
        section.addView(mTeamNameInput, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        parent.addView(section,
                inputParams);
    }

    private void addTeamDescriptionSection(LinearLayout parent, Context context) {
        var section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        var label = new TextView(context);
        label.setText("队伍描述（可选）");
        label.setTextSize(16f);
        label.setTextColor(0xFFE8E8E8);

        mTeamDescriptionInput = new EditText(context);
        mTeamDescriptionInput.setHint("可以在这里填一些奇奇怪怪的东西");
        mTeamDescriptionInput.setMinLines(3);
        mTeamDescriptionInput.setMaxLines(5);
        mTeamDescriptionInput.setBackground(createInputBackground(context));
        mTeamDescriptionInput.setPadding(dp(12), dp(8), dp(12), dp(8));
        var inputParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        inputParams.bottomMargin = dp(24);
        var inputParams2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        inputParams2.bottomMargin = dp(6);
        section.addView(label,inputParams2);
        section.addView(mTeamDescriptionInput,
                new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        parent.addView(section,
               inputParams);
    }

    private void addColorPickerSection(LinearLayout parent, Context context) {
        var section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        var label = new TextView(context);
        label.setText("队伍颜色");
        label.setTextSize(16f);
        label.setTextColor(0xFFE8E8E8);

        var labelParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        labelParams.bottomMargin = dp(8);
        section.addView(label, labelParams);

        // 创建颜色选择器
        mColorPicker = new ColorPicker(context);

        // 设置布局参数
        var pickerParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        pickerParams.setMargins(0, dp(8), 0, dp(16));
        pickerParams.gravity = Gravity.CENTER_HORIZONTAL;

        // 设置固定高度确保布局稳定
        mColorPicker.setMinimumHeight(dp(60));

        section.addView(mColorPicker, pickerParams);

        parent.addView(section, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

    }

    private void addErrorSection(LinearLayout parent, Context context) {
        mErrorText = new TextView(context);
        mErrorText.setTextSize(14f);
        mErrorText.setTextColor(0xFFFF6B6B);
        mErrorText.setVisibility(View.GONE);

        parent.addView(mErrorText,
                new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    private void addActionButtons(LinearLayout parent, Context context) {
        var buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.END);

        // 取消按钮
//        var cancelButton = new Button(context);
//        cancelButton.setText("取消");
//        cancelButton.setOnClickListener(v -> {
//            requireActivity().onBackPressed();
//        });

        // 创建按钮
        mCreateButton = new Button(context);
        mCreateButton.setText("创建队伍");
        mCreateButton.setOnClickListener(v -> createTeam());
        mCreateButton.setEnabled(false); // 初始禁用

//        buttonLayout.addView(cancelButton,
//                new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        buttonLayout.addView(mCreateButton,
                new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        parent.addView(buttonLayout,
                new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }



    /**
     * 验证输入
     */
    private void validateInput() {
        String teamName = mTeamNameInput.getText().toString().trim();

        if (teamName.isEmpty()) {
            showError("队伍名称不能为空");
            mCreateButton.setEnabled(false);
            return;
        }

        if (teamName.length() > 32) {
            showError("队伍名称不能超过32个字符");
            mCreateButton.setEnabled(false);
            return;
        }

        hideError();
        mCreateButton.setEnabled(true);
    }

    /**
     * 创建队伍
     */
    private void createTeam() {
        String teamName = mTeamNameInput.getText().toString().trim();
        String description = mTeamDescriptionInput.getText().toString().trim();
        Color4I selectedColor = mColorPicker.getSelectedColor();

        // 显示加载状态
        mCreateButton.setText("创建中...");
        mCreateButton.setEnabled(false);

        // 调用队伍管理工具创建队伍
        boolean success = TeamManageUtils.requestCreateTeam(teamName, description, selectedColor);

        if (success) {
            // 创建请求已发送，等待服务端响应
            // 在实际项目中，这里应该监听服务端的响应
            // 暂时假设创建成功，跳转到科技树界面
            if (mParentUI != null) {
                mParentUI.onTeamCreatedSuccessfully();
            }
        } else {
            // 创建失败，恢复按钮状态
            mCreateButton.setText("创建队伍");
            mCreateButton.setEnabled(true);
        }
    }

    private void showError(String message) {
        mErrorText.setText(message);
        mErrorText.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        mErrorText.setVisibility(View.GONE);
    }

    private ShapeDrawable createInputBackground(Context context) {
        ShapeDrawable background = new ShapeDrawable();
        background.setShape(ShapeDrawable.RECTANGLE);
        background.setCornerRadius(dp(8));
        int blueColor = 0xFF6366F1;
        int blueColorBG=0x056366F1;
        var value = new TypedValue();
        context.getTheme().resolveAttribute(icyllis.modernui.R.ns,
                icyllis.modernui.R.attr.colorOutlineVariant, value, true);
        background.setStroke(dp(1), blueColor);

        context.getTheme().resolveAttribute(icyllis.modernui.R.ns,
                icyllis.modernui.R.attr.colorSurfaceContainer, value, true);
        background.setColor(blueColorBG);

        return background;
    }

    private int dp(int value) {
        return (int) (value * requireContext().getResources().getDisplayMetrics().density);
    }

    /**
     * 简单的文本观察者
     */
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    /**
     * 自定义背景Drawable，支持图片填充和缩放
     */
    public static class BackgroundDrawable extends Drawable {
        private final Image mBackgroundImage;
        private final Paint mPaint;

        public BackgroundDrawable(Image backgroundImage) {
            this.mBackgroundImage = backgroundImage;
            this.mPaint = Paint.obtain();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Rect bounds = getBounds();

            if (mBackgroundImage != null) {
                // 计算缩放比例以填充整个区域
                float imageAspect = (float) mBackgroundImage.getWidth() / mBackgroundImage.getHeight();
                float boundsAspect = (float) bounds.width() / bounds.height();

                float scale;
                float drawWidth, drawHeight;
                float drawLeft, drawTop;

                if (imageAspect > boundsAspect) {
                    // 图片比区域宽，根据高度缩放
                    scale = (float) bounds.height() / mBackgroundImage.getHeight();
                    drawWidth = mBackgroundImage.getWidth() * scale;
                    drawHeight = bounds.height();
                    drawLeft = (bounds.width() - drawWidth) / 2;
                    drawTop = 0;
                } else {
                    // 图片比区域高，根据宽度缩放
                    scale = (float) bounds.width() / mBackgroundImage.getWidth();
                    drawWidth = bounds.width();
                    drawHeight = mBackgroundImage.getHeight() * scale;
                    drawLeft = 0;
                    drawTop = (bounds.height() - drawHeight) / 2;
                }

                // 绘制背景图片，填充整个区域
                canvas.drawImage(mBackgroundImage,
                        0, 0, mBackgroundImage.getWidth(), mBackgroundImage.getHeight(),
                        drawLeft, drawTop, drawLeft + drawWidth, drawTop + drawHeight,
                        mPaint);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

    }

}