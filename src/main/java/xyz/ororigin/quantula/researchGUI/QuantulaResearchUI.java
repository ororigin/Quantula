package xyz.ororigin.quantula.researchGUI;


import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ValueAnimator;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.core.Context;
import icyllis.modernui.core.Core;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.fragment.FragmentContainerView;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.FrameLayout;
import icyllis.modernui.widget.ImageView;
import xyz.ororigin.quantula.Config;
import xyz.ororigin.quantula.Quantula;
import xyz.ororigin.quantula.researchGUI.fragments.*;
import xyz.ororigin.quantula.team.TeamManageUtils;

import java.io.File;

import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class QuantulaResearchUI extends Fragment {

    private static boolean sShouldPlayAnimation = Config.displayOpenAnimation;
    private FrameAnimDrawable mFrameAnimDrawable;
    private ImageView mAnimationView;
    private AspectRatioFrameLayout mRootLayout;
    private FragmentContainerView mFragmentContainer;

    // 界面状态
    private enum AppState {
        ANIMATION_PLAYING,
        CHECKING_TEAM_STATUS,
        CREATE_TEAM,
        TECH_TREE,
        RESEARCH_DEVICE,
        TEAM_MANAGEMENT
    }

    private AppState mCurrentState = AppState.ANIMATION_PLAYING;

    public static void setShouldPlayAnimation(boolean playAnimation) {
        sShouldPlayAnimation = playAnimation;
    }

    public static boolean shouldPlayAnimation() {
        return sShouldPlayAnimation;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable DataSet savedInstanceState) {
        var context = requireContext();
        mRootLayout = new AspectRatioFrameLayout(context, 16f / 9f);

        // 1. 动画视图
        mAnimationView = new ImageView(context);
        mFrameAnimDrawable = new FrameAnimDrawable();
        mAnimationView.setImageDrawable(mFrameAnimDrawable);

        var animParams = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        animParams.gravity = Gravity.CENTER;
        mAnimationView.setLayoutParams(animParams);

        // 2. Fragment容器
        mFragmentContainer = new FragmentContainerView(context);
        mFragmentContainer.setId(View.generateViewId());
        mFragmentContainer.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        // 添加到根布局
        mRootLayout.addView(mAnimationView);
        mRootLayout.addView(mFragmentContainer);
        return mRootLayout;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (sShouldPlayAnimation) {
            startEntryAnimation();
        } else {
            // 跳过动画，直接检查状态
            sShouldPlayAnimation = true; // 重置标志，下次正常播放
            checkTeamStatusAndNavigate();
        }
    }

    private void startEntryAnimation() {
        mFrameAnimDrawable.start();
        mFrameAnimDrawable.setAnimationListener(new FrameAnimDrawable.AnimationListener() {
            @Override
            public void onAnimationEnd() {
                checkTeamStatusAndNavigate();
            }
        });
    }

    /**
     * 检查队伍状态并导航到相应界面
     */
    private void checkTeamStatusAndNavigate() {
        mCurrentState = AppState.CHECKING_TEAM_STATUS;

        // 使用异步任务检查队伍状态，避免阻塞UI线程
        new Thread(() -> {
            boolean hasTeam = TeamManageUtils.isInParty();

            Core.getUiThreadExecutor().execute(() -> {
                if (hasTeam) {
                    navigateToResearchMain();
                } else {
                    navigateToCreateTeam();
                }
            });
        }).start(); // 添加 .start() 来启动线程
    }

    /**
     * 导航到创建队伍界面
     */
    public void navigateToCreateTeam() {
        mCurrentState = AppState.CREATE_TEAM;

        try {
            getChildFragmentManager().beginTransaction()
                    .replace(mFragmentContainer.getId(), CreateTeamFragment.class, null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导航到科技树界面
     */
    public void navigateToResearchMain() {
        mCurrentState = AppState.TECH_TREE; // 可以改为 RESEARCH_MAIN
        getChildFragmentManager().beginTransaction()
                .replace(mFragmentContainer.getId(), ResearchMainFragment.class, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * 导航到研究装置界面
     */
    public void navigateToResearchDevice() {
        mCurrentState = AppState.RESEARCH_DEVICE;
        getChildFragmentManager().beginTransaction()
                .replace(mFragmentContainer.getId(), ResearchDeviceFragment.class, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * 导航到队伍管理界面
     */
    public void navigateToTeamManagement() {
        mCurrentState = AppState.TEAM_MANAGEMENT;
        getChildFragmentManager().beginTransaction()
                .replace(mFragmentContainer.getId(), TeamManagementFragment.class, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * 处理队伍创建成功
     */
    public void onTeamCreatedSuccessfully() {
        // 可以在这里添加创建成功后的逻辑，比如显示成功消息
        navigateToResearchMain();
    }


    public static class AspectRatioFrameLayout extends FrameLayout {
        private final float mAspectRatio;

        public AspectRatioFrameLayout(Context context, float aspectRatio) {
            super(context);
            this.mAspectRatio = aspectRatio;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            // 计算16:9的尺寸 - 始终居中
            int layoutWidth, layoutHeight;

            // 计算最佳尺寸，确保16:9比例且不超过屏幕
            if (width / (float) height > mAspectRatio) {
                // 屏幕比16:9更宽，根据高度计算宽度
                layoutHeight = height;
                layoutWidth = (int) (height * mAspectRatio);
            } else {
                // 屏幕比16:9更高，根据宽度计算高度
                layoutWidth = width;
                layoutHeight = (int) (width / mAspectRatio);
            }

            // 创建新的测量规格
            int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY);
            int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY);

            // 测量所有子视图
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(newWidthMeasureSpec, newHeightMeasureSpec);
            }

            // 设置自己的尺寸
            setMeasuredDimension(layoutWidth, layoutHeight);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int parentWidth = ((View) getParent()).getWidth();
            int parentHeight = ((View) getParent()).getHeight();

            // 计算在父容器中的居中位置
            int layoutLeft = (parentWidth - width) / 2;
            int layoutTop = (parentHeight - height) / 2;

            // 布局所有子视图，填满整个16:9区域
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.layout(0, 0, width, height);
            }

            // 设置自己的位置
            setLeft(layoutLeft);
            setTop(layoutTop);
            setRight(layoutLeft + width);
            setBottom(layoutTop + height);

        }
    }

    /**
     * 自定义帧动画Drawable
     */
    public static class FrameAnimDrawable extends Drawable {

        private Image[] mFrames;
        private int mCurrentFrame = 0;
        private ValueAnimator mAnimator;
        private AnimationListener mListener;
        private boolean mIsRunning = false;
        public FrameAnimDrawable() {
            loadFrames();
            setupAnimator();
        }

        private void loadFrames() {
            mFrames = new Image[41];
            for (int i = 0; i < mFrames.length; i++) {
                mFrames[i] = Image.create(Quantula.MODID, "gui/os/open_animation/" + i + ".jpg");
                // 如果图片不存在，使用占位色块
                if (mFrames[i] == null) {
                    mFrames[i] = createPlaceholderImage(i);
                }
            }
        }

        private Image createPlaceholderImage(int frameIndex) {
            // 创建占位图片用于测试
            return null; // 返回null会跳过绘制
        }

        private void setupAnimator() {
            mAnimator = ValueAnimator.ofInt(0, mFrames.length - 1);
            mAnimator.setDuration(2000); // 2秒完成所有帧
            mAnimator.setRepeatCount(0); // 只播放一次

            mAnimator.addUpdateListener(anim -> {
                mCurrentFrame = (int) anim.getAnimatedValue();
                invalidateSelf(); // 触发重绘
            });

            mAnimator.addListener(new AnimatorListener() {
                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    mIsRunning = false;
                    if (mListener != null) {
                        mListener.onAnimationEnd();
                    }
                }
            });
        }

        public void start() {
            if (!mIsRunning && mFrames != null && mFrames.length > 0) {
                mIsRunning = true;
                mCurrentFrame = 0;
                mAnimator.start();
            }
        }

        public void setAnimationListener(AnimationListener listener) {
            mListener = listener;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (mFrames == null || mCurrentFrame >= mFrames.length || mFrames[mCurrentFrame] == null) {
                // 绘制占位背景
                drawPlaceholder(canvas);
                return;
            }

            Rect bounds = getBounds();
            Paint paint = Paint.obtain();

            // 绘制当前帧，居中显示并保持16:9
            Image frame = mFrames[mCurrentFrame];

            // 计算在16:9区域内的最佳显示尺寸
            float frameAspect = (float) frame.getWidth() / frame.getHeight();
            float containerAspect = (float) bounds.width() / bounds.height();

            float scale;
            float drawWidth, drawHeight;
            float drawLeft, drawTop;

            if (frameAspect > containerAspect) {
                // 图片比容器宽，根据宽度缩放
                scale = (float) bounds.width() / frame.getWidth();
                drawWidth = bounds.width();
                drawHeight = frame.getHeight() * scale;
                drawLeft = 0;
                drawTop = (bounds.height() - drawHeight) / 2; // 垂直居中
            } else {
                // 图片比容器高，根据高度缩放
                scale = (float) bounds.height() / frame.getHeight();
                drawWidth = frame.getWidth() * scale;
                drawHeight = bounds.height();
                drawLeft = (bounds.width() - drawWidth) / 2; // 水平居中
                drawTop = 0;
            }

            // 正确的drawImage调用 - 9个参数
            canvas.drawImage(frame,
                    0, 0, frame.getWidth(), frame.getHeight(),  // 源矩形：整个图像
                    drawLeft, drawTop, drawLeft + drawWidth, drawTop + drawHeight,  // 目标矩形
                    paint);
            paint.recycle();
        }

        private void drawPlaceholder(@NonNull Canvas canvas) {
            Rect bounds = getBounds();
            Paint paint = Paint.obtain();

            // 绘制背景
            paint.setColor(0xFF1E1E1E);
            canvas.drawRect(bounds, paint);

            // 绘制边框
            paint.setColor(0xFF444444);
            paint.setStrokeWidth(2f);
            paint.setStyle(Paint.STROKE);
            canvas.drawRect(bounds, paint);

            paint.recycle();
        }

        @Override
        public int getIntrinsicWidth() {
            return 1600; // 16:9 的宽度基准
        }

        @Override
        public int getIntrinsicHeight() {
            return 900; // 16:9 的高度基准
        }

        @Override
        public void setAlpha(int alpha) {
            // 实现透明度支持
        }

        @Override
        public void setColorFilter(@Nullable icyllis.modernui.graphics.ColorFilter colorFilter) {
            // 实现颜色过滤
        }

        public interface AnimationListener {
            void onAnimationEnd();
        }

    }
}