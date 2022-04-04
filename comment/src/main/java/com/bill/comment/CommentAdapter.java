package com.bill.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bill.baselib.bean.ListBean;
import com.bill.baselib.path.ARouterPath;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Bill on 2022/4/4.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<CommentBean> mData;
    private boolean mIsMyCommentList;

    public CommentAdapter(boolean isMyCommentList) {
        mIsMyCommentList = isMyCommentList;
    }

    public void setData(List<CommentBean> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView nameTv;
        private AppCompatTextView contentTv;
        private AppCompatTextView timeTv;
        private AppCompatTextView originalTv;
        private View originalView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tv_user_name);
            contentTv = itemView.findViewById(R.id.tv_content);
            timeTv = itemView.findViewById(R.id.tv_time);
            originalTv = itemView.findViewById(R.id.tv_original_text);
            originalView = itemView.findViewById(R.id.ll_original);
        }

        private void update(int position) {
            final CommentBean bean = mData.get(position);

            nameTv.setText(bean.name);
            contentTv.setText(bean.content);
            timeTv.setText(bean.time);

            if (mIsMyCommentList) {
                originalView.setVisibility(View.VISIBLE);
                originalTv.setText(bean.original);
            } else {
                originalView.setVisibility(View.GONE);
            }

            originalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListBean listBean = new ListBean();
                    listBean.id = bean.originalId;
                    listBean.title = bean.original;
                    ARouter.getInstance()
                            .build(ARouterPath.PATH_DETAIL)
                            .withObject("bean", listBean)
                            .navigation();
                }
            });

        }

    }

}
