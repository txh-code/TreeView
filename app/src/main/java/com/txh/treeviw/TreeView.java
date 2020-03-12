package com.txh.treeviw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class TreeView<T> extends ViewGroup implements NodeView.InvalidateInterface<T> {


    private final Paint mPaint;
    private final Path mPath;
    private Context mContext;
    private int leafHight;
    private int xOffset = dpToPx(getContext(), 40);
    private int yOffset = dpToPx(getContext(), 10);

    private NodeModel<T> rootNode;
    private int maxWidth;
    private int maxHeight;
    private int mCount;


    public TreeView(Context context) {
        this(context, null);
    }

    public TreeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dpToPx(mContext, 2));
        mPaint.setColor(getResources().getColor(R.color.bgColor));

        mPath = new Path();
        mPath.reset();
        setClickable(true);

    }


    public void initData(NodeModel<T> data) {
        rootNode = data;
        pressStack(data);
        popStckData();
        mCount = data.getLeafCount();
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = getChildCount();
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }

        if (rootNode == null) return;

        leafHight = getChildAt(0).getMeasuredHeight() + yOffset;
        maxHeight = leafHight * (mCount + 2);
        setRootLayout(rootNode);//

        pressQueue(rootNode);
        while (!queue.isEmpty()) {
            NodeModel<T> nodeMode = queue.poll();
            setNodeLayout(nodeMode);
        }

        setTreeViewLayout(maxWidth, maxHeight);

    }


    private void setRootLayout(NodeModel<T> rootNode) {
        NodeView rootView = findViewWithTag(rootNode.getTag());
        int l = xOffset;
        int t = (maxHeight - rootView.getMeasuredHeight()) / 2;
        int r = l + rootView.getMeasuredWidth();
        int b = t + rootView.getMeasuredHeight();
        rootView.layout(l, t, r, b);
    }

    private void setNodeLayout(NodeModel<T> parentNode) {
        NodeView parentView = findViewWithTag(parentNode.getTag());
        int zCount = parentNode.getLeafCount();

        LinkedList<NodeModel<T>> childs = parentNode.getChilds();
        if (childs.size() == 0) return;

        int parentT = parentView.getTop();
        int hh = zCount * leafHight;
        int t = parentT - hh / 2;
        int l = xOffset + parentView.getRight();
        for (int i = 0; i < childs.size(); i++) {
            NodeModel node = childs.get(i);
            int count = node.getLeafCount();
            NodeView nodeView = findViewWithTag(node.getTag());
            t = t + count * leafHight / 2;
            int r = l + nodeView.getMeasuredWidth();
            int b = t + nodeView.getMeasuredHeight();
            nodeView.layout(l, t, r, b);
            t = t + count * leafHight / 2;
            maxWidth = maxWidth > r ? maxWidth : (r + leafHight);
        }


    }

    private void setTreeViewLayout(int w, int h) {
        //重置View的大小
        LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = h;
        layoutParams.width = w;
        this.setLayoutParams(layoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (rootNode != null) {
            drawTreeLine(canvas, rootNode);
        }

    }

    /**
     * 绘制树形的连线
     */
    private void drawTreeLine(Canvas canvas, NodeModel<T> root) {
        NodeView fatherView = findViewWithTag(root.getTag());
        if (fatherView != null) {
            LinkedList<NodeModel<T>> childNodes = root.getChilds();
            for (NodeModel<T> node : childNodes) {
                View view = findViewWithTag(node.getTag());
                if (view.getVisibility() == VISIBLE)
                    lineToView(canvas, fatherView, view);
                //递归
                drawTreeLine(canvas, node);
            }
        }
    }


    /**
     * 数据压栈
     */
    Stack<NodeModel<T>> stack = new Stack<>();

    private void pressStack(NodeModel<T> node) {
        stack.add(node);
        LinkedList<NodeModel<T>> child = node.getChilds();
        for (NodeModel<T> item : child) {
            pressStack(item);
        }
    }

    private void popStckData() {
        while (!stack.isEmpty()) {
            NodeModel<T> pop = stack.pop();
            if (pop.getChilds() == null || pop.getChilds().size() == 0 || !pop.isExplosion()) {
                pop.setLeafCount(1);
            } else {
                int count = 0;
                for (NodeModel<T> item : pop.getChilds()) {
                    count += item.getLeafCount();
                }
                pop.setLeafCount(count);
            }
            addView(pop);
        }
    }

    /**
     * 数据进队列
     */
    Queue<NodeModel<T>> queue = new ArrayDeque<>();

    private void pressQueue(NodeModel<T> node) {
        queue.add(node);
        LinkedList<NodeModel<T>> child = node.getChilds();
        for (NodeModel<T> item : child) {
            pressQueue(item);
        }

    }


    private void addView(NodeModel<T> pop) {
        long time = System.currentTimeMillis();
        NodeView<T> view = new NodeView<>(getContext());
        pop.setTag(time);
        view.setTag(time);
        view.setNodeModel(pop);
        view.setData(pop.getValue().toString(), pop.getChilds().size());
        view.setInterFace(this);
        this.addView(view);
    }


    private void lineToView(Canvas canvas, View from, View to) {
        int top = from.getTop();
        int formY = top + from.getMeasuredHeight() / 2;
        int formX = from.getRight();
        int top1 = to.getTop();
        int toY = top1 + to.getMeasuredHeight() / 2;
        int toX = to.getLeft();
        mPath.reset();
        mPath.moveTo(formX, formY);
//        mPath.quadTo(toX - Utils.dpToPx(mContext, 20), toY, toX, toY);
        mPath.cubicTo(formX + dpToPx(mContext, 10), formY, toX - dpToPx(mContext, 20), toY, toX, toY);
        canvas.drawPath(mPath, mPaint);
    }


    @Override
    public void refresh(NodeModel<T> node) {

        boolean explosion = node.isExplosion();
        pressQueue(node);
        while (!queue.isEmpty()) {
            NodeModel<T> pop = queue.poll();
            pop.setExplosion(explosion);
            NodeView view = findViewWithTag(pop.getTag());
            view.setVisibility(explosion ? VISIBLE : GONE);
            calculatingLeafCount(pop);
            view.intNumber(pop.getChilds().size());
        }
        findViewWithTag(node.getTag()).setVisibility(VISIBLE);


        pressStack(rootNode);
        while (!stack.isEmpty()) {
            NodeModel<T> pop = stack.pop();
            calculatingLeafCount(pop);
        }

        invalidate();
    }


    private void calculatingLeafCount(NodeModel<T> pop) {
        if (pop.getChilds().size() == 0 || !pop.isExplosion()) {
            pop.setLeafCount(1);
        } else {
            int count = 0;
            for (NodeModel<T> item : pop.getChilds()) {
                count += item.getLeafCount();
            }
            pop.setLeafCount(count);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // 记录触摸点坐标
//                lastX = x;
//                lastY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                // 计算偏移量
//                int offsetX = x - lastX;
//                int offsetY = y - lastY;
//
//                layout(getLeft() + offsetX,
//                        getTop() + offsetY,
//                        getRight() + offsetX,
//                        getBottom() + offsetY);
//                break;
//        }
//        return true;
//    }


    public void reset() {
        setScaleX(1);
        setScaleY(1);
        setTranslationX(0);
        setTranslationY(0);
        setRotation(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moveType = 1;
                actionX = event.getRawX();
                actionY = event.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                moveType = 2;
                spacing = getSpacing(event);
                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveType == 1) {
                    translationX = translationX + event.getRawX() - actionX;
                    translationY = translationY + event.getRawY() - actionY;
                    setTranslationX(translationX);
                    setTranslationY(translationY);
                    actionX = event.getRawX();
                    actionY = event.getRawY();
                } else if (moveType == 2) {
                    scale = scale * getSpacing(event) / spacing;
                    setScaleX(scale);
                    setScaleY(scale);
                    rotation = rotation + getDegree(event) - degree;
                    if (rotation > 360) {
                        rotation = rotation - 360;
                    }
                    if (rotation < -360) {
                        rotation = rotation + 360;
                    }
                    setRotation(rotation);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 0;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    // 属性变量
    private float translationX; // 移动X
    private float translationY; // 移动Y
    private float scale = 1; // 伸缩比例
    private float rotation; // 旋转角度

    // 移动过程中临时变量
    private float actionX;
    private float actionY;
    private float spacing;
    private float degree;
    private int moveType; // 0=未选择，1=拖动，2=缩放


    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private int dpToPx(Context context, float value) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }
}

