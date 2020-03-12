package com.txh.treeviw;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TreeViewActivity extends AppCompatActivity  {


    private View reset;
    private TreeView treeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         reset=findViewById(R.id.reset);
         treeView=findViewById(R.id.tree_view);

        reset.setOnClickListener(v -> treeView.reset());

        NodeModel tree = new NodeModel("根节点");

        NodeModel model1 = new NodeModel("孩子1");
        model1.addChild(new NodeModel("1"));
        NodeModel modelll = new NodeModel("2");

        NodeModel child3 = new NodeModel("1");
        child3.addChild(new NodeModel("1"));

        NodeModel child4 = new NodeModel("2");
//        child4.addChild(new NodeModel("1"));
//        child4.addChild(new NodeModel("2"));
//        child4.addChild(new NodeModel("3"));
//        child4.addChild(new NodeModel("4"));
        child3.addChild(child4);

        modelll.addChild(child3);
        modelll.addChild(new NodeModel("2"));
        model1.addChild(modelll);
        model1.addChild(new NodeModel("3"));
        NodeModel model2 = new NodeModel("孩子2");
        model2.addChild(new NodeModel("1"));
        model2.addChild(new NodeModel("2"));
        NodeModel model3 = new NodeModel("孩子3");
        model3.addChild(new NodeModel("1"));
        model3.addChild(new NodeModel("2"));
        NodeModel model4 = new NodeModel("孩子4");
        NodeModel model5 = new NodeModel("孩子5");
        model5.addChild(new NodeModel("1"));
        NodeModel model6 = new NodeModel("孩子6");
        model6.addChild(new NodeModel("1"));
        model6.addChild(new NodeModel("2"));
        model6.addChild(new NodeModel("3"));
        model6.addChild(new NodeModel("4"));
        tree.addChild(model1);
        tree.addChild(model2);
        tree.addChild(model3);
        tree.addChild(model4);
        tree.addChild(model5);
        tree.addChild(model6);
        treeView.initData(tree);





    }



}
