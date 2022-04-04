package com.bill.comment;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bill.baselib.path.ARouterPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@Route(path = ARouterPath.PATH_COMMENT)
public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Autowired(name = "type")
    public int mType; // 评论类型 1：评论列表 2：我的列表
    @Autowired
    public String contentId;  // 内容id

    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ARouter.getInstance().inject(this);
        recyclerView = findViewById(R.id.rv_comment);
        init();
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        CommentAdapter adapter = new CommentAdapter(mType == 2);
        recyclerView.setAdapter(adapter);
        adapter.setData(getData());
    }

    private List<CommentBean> getData() {
        if (mRandom == null)
            mRandom = new Random();

        ArrayList<CommentBean> list = new ArrayList<>(20);
        for (int i = 1; i < 21; i++) {
            CommentBean bean = new CommentBean();
            bean.id = "comment_" + i;
            if (mType == 2) {
                bean.name = "Me";
                int pos = mRandom.nextInt(20) + 1; // [1,20]
                if (pos % 2 == 0)
                    bean.original = "Attention Title " + pos;
                else
                    bean.original = "Recommend Title " + pos;
                bean.originalId = "list_" + pos;
            } else if (mType == 1)
                bean.name = "User_Name_" + i;
            else
                bean.name = "Name_" + i;
            bean.content = "This is a Comment Content " + i;
            bean.time = i + "小时前";
            list.add(bean);
        }
        return list;
    }

}