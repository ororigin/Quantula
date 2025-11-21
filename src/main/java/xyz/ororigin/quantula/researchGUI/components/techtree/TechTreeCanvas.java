package xyz.ororigin.quantula.researchGUI.components.techtree;

import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.graphics.drawable.ImageDrawable;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.graphics.text.ShapedText;
import icyllis.modernui.text.TextDirectionHeuristics;
import icyllis.modernui.text.TextPaint;
import icyllis.modernui.text.TextShaper;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.view.View;
import xyz.ororigin.quantula.data.files.DataFileManager;
import xyz.ororigin.quantula.data.files.structure.ResearchNode;

import javax.annotation.Nonnull;
import java.util.*;

public class TechTreeCanvas extends View {

    private static final float CONTENT_WIDTH  = 6000;
    private static final float CONTENT_HEIGHT = 6000;

    private float mScrollX = 0;
    private float mScrollY = 0;
    private float mLastTouchX;
    private float mLastTouchY;
    private boolean mIsDragging;

    private final List<NodeUI> mNodes = new ArrayList<>();
    private boolean mDataLoaded = false;

    // 固定尺寸
    private static final int NODE_WIDTH  = 96;
    private static final int NODE_HEIGHT = 120;
    private static final int ICON_SIZE   = 64;
    private static final int TITLE_SIZE  = 11;

    private final Paint mPaint = Paint.obtain();
    private final TextPaint mTextPaint = new TextPaint(); // 新增：专门用于文本渲染
    private final Rect mTempRect = new Rect();

    private OnPositionChangeListener mPositionListener;

    public TechTreeCanvas(Context context) {
        super(context);
        setWillNotDraw(false);
        setClickable(true);

        // 初始化文本绘制配置
        mTextPaint.setTextAntiAlias(true);
        mTextPaint.setLinearText(true);
        mTextPaint.setColor(0xFFFFFFFF); // 白色文本

        loadResearchDataAsync();
    }

    private void loadResearchDataAsync() {
        post(() -> {
            DataFileManager manager = new DataFileManager();
            List<ResearchNode> rawNodes = manager.getAllResearchNodes();

            mNodes.clear();
            if (rawNodes.isEmpty()) {
                mDataLoaded = true;
                invalidate();
                return;
            }

            Map<String, NodeUI> nodeMap = new HashMap<>();

            for (ResearchNode rn : rawNodes) {
                NodeUI node = new NodeUI(rn);
                mNodes.add(node);
                nodeMap.put(rn.getId(), node);
            }

            for (NodeUI node : mNodes) {
                String[] deps = node.raw.getDependence();
                if (deps != null) {
                    for (String depId : deps) {
                        NodeUI parent = nodeMap.get(depId);
                        if (parent != null) {
                            node.parents.add(parent);
                            parent.children.add(node);
                        }
                    }
                }
            }

            mDataLoaded = true;
            resetView();
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();

        // 背景
        mPaint.setColor(0xFF1E1E1E);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        canvas.save();
        canvas.translate(mScrollX, mScrollY);

        if (!mDataLoaded) {
            drawCenteredText(canvas, "加载中...", CONTENT_WIDTH / 2f, CONTENT_HEIGHT / 2f, 20);
        } else if (mNodes.isEmpty()) {
            drawCenteredText(canvas, "没有任何可用的研究节点", CONTENT_WIDTH / 2f, CONTENT_HEIGHT / 2f - 30, 24);
            drawCenteredText(canvas, "请在 quantula/research.json 中添加节点", CONTENT_WIDTH / 2f, CONTENT_HEIGHT / 2f + 20, 14);
        } else {
            drawConnections(canvas);
            drawNodes(canvas);
        }

        canvas.restore();
    }

    private void drawCenteredText(Canvas canvas, String text, float centerX, float centerY, float textSizePx) {
        if (text.isBlank()) return;

        // 使用 TextPaint 而不是普通的 Paint
        mTextPaint.setTextSize(textSizePx);

        // 使用 TextShaper 排版，获取 ShapedText
        ShapedText shaped = TextShaper.shapeText(
                (CharSequence) text,
                0, text.length(),
                TextDirectionHeuristics.FIRSTSTRONG_LTR,
                mTextPaint  // 使用 TextPaint 而不是强制转换
        );

        // 计算总宽度（advance）
        float totalWidth = shaped.getAdvance();

        // 计算基线（baseline）
        float baselineOffset = -shaped.getAscent();

        float x = centerX - totalWidth / 2f;
        float y = centerY + baselineOffset * 0.75f;

        // 绘制文本
        canvas.drawShapedText(shaped, x, y, mTextPaint);
    }

    private void drawConnections(Canvas canvas) {
        mPaint.setStyle(Paint.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(0xFF555555);

        for (NodeUI node : mNodes) {
            float cx = node.x + NODE_WIDTH / 2f;
            float cy = node.y + NODE_HEIGHT / 2f;

            for (NodeUI parent : node.parents) {
                float px = parent.x + NODE_WIDTH / 2f;
                float py = parent.y + NODE_HEIGHT / 2f;

                canvas.drawLine(px, py, cx, py, mPaint); // 水平
                canvas.drawLine(cx, py, cx, cy, mPaint); // 垂直
            }
        }
    }

    private void drawNodes(Canvas canvas) {
        for (NodeUI node : mNodes) {
            node.draw(canvas, mPaint, mTextPaint, mTempRect);
        }
    }

    @Override
    public boolean onTouchEvent(@Nonnull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                mLastTouchX = x;
                mLastTouchY = y;
                mIsDragging = true;
            }
            case MotionEvent.ACTION_MOVE -> {
                if (mIsDragging) {
                    float dx = x - mLastTouchX;
                    float dy = y - mLastTouchY;
                    mScrollX += dx;
                    mScrollY += dy;

                    mScrollX = Math.max(getWidth() - CONTENT_WIDTH, Math.min(0, mScrollX));
                    mScrollY = Math.max(getHeight() - CONTENT_HEIGHT, Math.min(0, mScrollY));

                    mLastTouchX = x;
                    mLastTouchY = y;
                    invalidate();
                    notifyPositionChanged();
                }
            }
            case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mIsDragging = false;
        }
        return true;
    }

    private void notifyPositionChanged() {
        if (mPositionListener != null) {
            mPositionListener.onPositionChanged(-mScrollX, -mScrollY);
        }
    }

    public void resetView() {
        mScrollX = (getWidth() - CONTENT_WIDTH) / 2f;
        mScrollY = (getHeight() - CONTENT_HEIGHT) / 2f;
        invalidate();
        notifyPositionChanged();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDataLoaded) resetView();
    }

    public void setOnPositionChangeListener(OnPositionChangeListener listener) {
        mPositionListener = listener;
    }

    public interface OnPositionChangeListener {
        void onPositionChanged(float worldX, float worldY);
    }

    // ====================== 节点绘制类 ======================
    private static class NodeUI {
        final ResearchNode raw;
        final float x, y;
        final List<NodeUI> parents = new ArrayList<>();
        final List<NodeUI> children = new ArrayList<>();

        final ImageDrawable iconDrawable;
        final Drawable fallbackDrawable;

        NodeUI(ResearchNode raw) {
            this.raw = raw;
            this.x = raw.getX();
            this.y = raw.getY();

            String iconPath = raw.getIcon();
            Image image = null;
            if (iconPath != null && !iconPath.isEmpty()) {
                try {
                    image = Image.create("quantula", "textures/gui/research/" + iconPath + ".png");
                } catch (Exception ignored) {}
            }

            if (image != null) {
                iconDrawable = new ImageDrawable(image);
            } else {
                iconDrawable = null;
            }

            // 备用图标：蓝色圆角方块
            ShapeDrawable sd = new ShapeDrawable();
            sd.setShape(ShapeDrawable.RECTANGLE);
            sd.setCornerRadius(12);
            sd.setColor(0xFF2D2D2D);
            sd.setStroke(3, 0xFF00DDFF);
            sd.setSize(ICON_SIZE, ICON_SIZE);
            fallbackDrawable = sd;
        }

        void draw(Canvas canvas, Paint paint, TextPaint textPaint, Rect temp) {
            float left   = x;
            float top    = y;
            float right  = x + NODE_WIDTH;
            float bottom = y + NODE_HEIGHT;

            // 卡片背景
            paint.setColor(0xFF2D2D2D);
            canvas.drawRoundRect(left, top, right, bottom, 12, paint);

            // 边框
            paint.setStyle(Paint.STROKE);
            paint.setStrokeWidth(3);
            paint.setColor(0xFF00DDFF);
            canvas.drawRoundRect(left, top, right, bottom, 12, paint);
            paint.setStyle(Paint.FILL);

            // 图标
            float iconL = left + (NODE_WIDTH - ICON_SIZE) / 2f;
            float iconT = top + 12;
            temp.set((int)iconL, (int)iconT, (int)(iconL + ICON_SIZE), (int)(iconT + ICON_SIZE));

            Drawable icon = iconDrawable != null ? iconDrawable : fallbackDrawable;
            icon.setBounds(temp);
            icon.draw(canvas);

            // 标题（居中）- 使用 TextPaint
            String title = raw.getTitle() != null ? raw.getTitle() : "未知节点";

            textPaint.setTextSize(TITLE_SIZE);

            ShapedText shapedTitle = TextShaper.shapeText(
                    (CharSequence) title, 0, title.length(),
                    TextDirectionHeuristics.FIRSTSTRONG_LTR,
                    textPaint  // 使用 TextPaint
            );

            float titleWidth = shapedTitle.getAdvance();
            float titleX = left + (NODE_WIDTH - titleWidth) / 2f;
            float titleY = bottom - 12 - shapedTitle.getAscent();

            canvas.drawShapedText(shapedTitle, titleX, titleY, textPaint);
        }
    }
}