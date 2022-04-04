package com.bill.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bill.baselib.bean.ListBean;
import com.bill.baselib.path.ARouterPath;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Bill on 2022/4/3.
 */

@Route(path = ARouterPath.PATH_LIST)
public class ListFragment extends Fragment implements OnListItemClickListener {

    public static final String TAG = "ListFragment";

    private RecyclerView recyclerView;

    //    @Autowired(name = "type")
    public String mType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.rv_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // fragment里使用Autowired注解，然后调用inject方法时，主要一定要传参数，否则会报错，getArguments()空指针
        // 这里用下面方式自己获取判空，用ARouter的方式最好别用Autowired注解获取里，避免不小心空指针了
//        ARouter.getInstance().inject(this);
        if (getArguments() != null)
            mType = getArguments().getString("type");
        init();
    }

    private void init() {
        Context context = getActivity();
        if (context == null) return;

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        ListAdapter adapter = new ListAdapter();
        adapter.setOnListItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.setData(getData());
    }

    private List<ListBean> getData() {
        String prefix = "";
        if ("recommend".equals(mType))
            prefix = "Recommend ";
        else if ("attention".equals(mType))
            prefix = "Attention ";

        ArrayList<ListBean> list = new ArrayList<>(20);
        for (int i = 1; i < 21; i++) {
            ListBean bean = new ListBean();
            bean.id = "list_" + i;
            bean.title = prefix + "Title " + i;
            list.add(bean);
        }
        return list;
    }

    @Override
    public void onClick(ListBean bean) {
        ARouter.getInstance()
                .build(ARouterPath.PATH_DETAIL)
                .withObject("bean", bean)
                .navigation();
    }
}
