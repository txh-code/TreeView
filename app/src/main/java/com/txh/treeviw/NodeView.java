package com.txh.treeviw;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class NodeView<T> extends FrameLayout {

    public NodeModel<T> nodeModel = null;
    TextView textView;
    TextView number;

    public NodeView(Context context) {
        this(context, null, 0);
    }

    public NodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.node_view, null);
        textView = view.findViewById(R.id.content);
        number = view.findViewById(R.id.number);
        textView.setOnClickListener(v -> {
            nodeModel.setExplosion(!nodeModel.isExplosion());

            if (nodeModel.getChilds().size() == 0 || nodeModel.isExplosion()) {
                number.setVisibility(View.GONE);
            } else {
                number.setVisibility(View.VISIBLE);
            }

            if (interFace != null && nodeModel.getChilds().size() > 0)
                interFace.refresh(nodeModel);
        });
        this.addView(view);

    }

    

    public void setNodeModel(NodeModel<T> nodeModel) {
        this.nodeModel = nodeModel;
    }

    public void setData(String content, int childCount) {
        textView.setText(content);
        intNumber(childCount);
    }

    public void intNumber(int childCount) {
        if (childCount == 0 || nodeModel.isExplosion()) {
            number.setVisibility(View.GONE);
        } else {
            number.setVisibility(View.VISIBLE);
        }

        number.setText(String.valueOf(childCount));
    }

    public InvalidateInterface interFace;

    public void setInterFace(InvalidateInterface interFace) {
        this.interFace = interFace;
    }

    public interface InvalidateInterface<T> {

        void refresh(NodeModel<T> nodeModel);
    }
}