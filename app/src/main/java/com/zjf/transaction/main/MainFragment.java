package com.zjf.transaction.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zjf.transaction.R;
import com.zjf.transaction.app.AppConfig;
import com.zjf.transaction.base.BaseAdapter;
import com.zjf.transaction.base.BaseConstant;
import com.zjf.transaction.base.BaseFragment;
import com.zjf.transaction.base.DataResult;
import com.zjf.transaction.main.api.impl.MainApiImpl;
import com.zjf.transaction.main.model.Commodity;
import com.zjf.transaction.util.LogUtil;
import com.zjf.transaction.util.ScreenUtil;
import com.zjf.transaction.widget.SearchEditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhengjiafeng on 2019/3/13
 *
 * @author 郑佳锋 zhengjiafeng@bytedance.com
 */
public class MainFragment extends BaseFragment {

    private static final int DEFAULT_PAGE_NUM = 1;

    private List<Commodity> commodityList = new ArrayList<>();
    private BaseAdapter<Commodity> adapter;
    private Disposable disposable, searchDisposable;
    private int pageNum = DEFAULT_PAGE_NUM;
    private int searchPageNum = DEFAULT_PAGE_NUM;
    private Receiver receiver = new Receiver();
    private SearchEditText etSearch;
    private ViewGroup searchLayout;


    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BaseConstant.ACTION_MAIN.equals(intent.getAction())) {
                ArrayList<String> commodityIdList = intent.getBundleExtra(BaseConstant.KEY_MAIN_BUNDLE)
                        .getStringArrayList(BaseConstant.KEY_MAIN_DELETE);
                if (commodityIdList == null) {
                    return;
                }
                if (!commodityIdList.isEmpty()) {
                    List<Commodity> list = adapter.getDataList();
                    Iterator<Commodity> iterator = list.iterator();
                    for (int i = 0; i < commodityIdList.size(); i++) {
                        final String id = commodityIdList.get(i);
                        while (iterator.hasNext()) {
                            Commodity commodity = iterator.next();
                            if (id.equals(commodity.getId())) {
                                iterator.remove();
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public View onCreateContent(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        AppConfig.getLocalBroadcastManager().registerReceiver(receiver, new IntentFilter(BaseConstant.ACTION_MAIN));
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchLayout = view.findViewById(R.id.layout_top);
        searchLayout.setPadding(0, ScreenUtil.getStatusBarHeight(), 0, 0); //下移状态栏的高度
        etSearch = searchLayout.findViewById(R.id.et_search);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final String searchText = etSearch.getText().toString();
                if (!searchText.isEmpty()) {
                    searchDisposable = MainApiImpl.getByName(searchText, searchPageNum)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<DataResult<List<Commodity>>>() {
                                @Override
                                public void accept(DataResult<List<Commodity>> listDataResult) throws Exception {
                                    if (listDataResult.code == DataResult.CODE_SUCCESS) {
                                        adapter.setDataList(listDataResult.data);
                                        disposable = null;
                                        LogUtil.d("search commodity success");
                                    } else {
                                        LogUtil.e("search commodity failed, msg -> %s", listDataResult.msg);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    LogUtil.e("search commodity error, throwable -> %s", throwable.getMessage());
                                }
                            });
                }
                ScreenUtil.hideSoftInput(getActivity(), v.getWindowToken());
                return true;
            }
        });

        etSearch.setOnClearTextListener(new SearchEditText.OnClearTextListener() {
            @Override
            public void onClearTextListener() {
                if (searchDisposable == null) {
                    disposable = initData();
                }
            }
        });
        final ImageView ivPublish = searchLayout.findViewById(R.id.iv_publish);
        ivPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishActivity.start(getContext(), PublishActivity.class);
            }
        });

        RefreshLayout refreshLayout = view.findViewById(R.id.layout_refresh);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@androidx.annotation.NonNull final RefreshLayout refreshLayout) {
                if (disposable != null && searchDisposable == null) {
                    refreshData(refreshLayout);
                } else if (searchDisposable != null && disposable == null) {
                    refreshSearchData(refreshLayout);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@androidx.annotation.NonNull final RefreshLayout refreshLayout) {
                if (disposable != null && searchDisposable == null) {
                    loadMoreData(refreshLayout);
                } else if (disposable == null && searchDisposable != null) {
                    loadMoreSearchData(refreshLayout);
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new MainAdapter();
        adapter.setDataList(commodityList);
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);
        MainPageDecoration decoration = new MainPageDecoration();
        recyclerView.addItemDecoration(decoration);
    }

    private void loadMoreSearchData(final RefreshLayout refreshLayout) {
        MainApiImpl.getByName(etSearch.getText().toString(), searchPageNum + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataResult<List<Commodity>>>() {
                    @Override
                    public void accept(DataResult<List<Commodity>> listDataResult) throws Exception {
                        if (listDataResult.code == DataResult.CODE_SUCCESS) {
                            LogUtil.d("load more success when search");
                            adapter.appendDataList(listDataResult.data);
                            searchPageNum++;
                            refreshLayout.finishLoadMore(true);
                        } else {
                            LogUtil.e("load more failed when search, msg -> %s", listDataResult.msg);
                            refreshLayout.finishLoadMore(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e("load more failed when search, thrwoable -> %s", throwable.getMessage());
                        refreshLayout.finishLoadMore(false);
                    }
                });
    }

    private void loadMoreData(@NonNull final RefreshLayout refreshLayout) {
        MainApiImpl.getAllCommodity(pageNum + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataResult<List<Commodity>>>() {
                    @Override
                    public void accept(DataResult<List<Commodity>> listDataResult) throws Exception {
                        if (listDataResult.code == DataResult.CODE_SUCCESS) {
                            LogUtil.d("load more success");
                            adapter.appendDataList(listDataResult.data);
                            pageNum++;
                            refreshLayout.finishLoadMore(true);
                        } else {
                            LogUtil.e("load more failed, msg -> %s", listDataResult.msg);
                            refreshLayout.finishLoadMore(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e("load more failed, thrwoable -> %s", throwable.getMessage());
                        refreshLayout.finishLoadMore(false);
                    }
                });
    }

    private void refreshSearchData(final RefreshLayout refreshLayout) {
        MainApiImpl.getByName(etSearch.getText().toString(), DEFAULT_PAGE_NUM)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataResult<List<Commodity>>>() {
                    @Override
                    public void accept(DataResult<List<Commodity>> listDataResult) throws Exception {
                        if (listDataResult.code == DataResult.CODE_SUCCESS) {
                            LogUtil.d("refresh commodity when search success");
                            adapter.setDataList(listDataResult.data);
                            searchPageNum = DEFAULT_PAGE_NUM;
                            refreshLayout.finishRefresh(true);
                        } else {
                            LogUtil.e("refresh commodity when search failed, msg -> %s", listDataResult.msg);
                            refreshLayout.finishRefresh(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e("refresh commodity when search error, throwable -> %s", throwable.getMessage());
                        refreshLayout.finishRefresh(false);
                    }
                });
    }

    private void refreshData(@NonNull final RefreshLayout refreshLayout) {
        MainApiImpl.getAllCommodity(DEFAULT_PAGE_NUM)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataResult<List<Commodity>>>() {
                    @Override
                    public void accept(DataResult<List<Commodity>> listDataResult) throws Exception {
                        if (listDataResult.code == DataResult.CODE_SUCCESS) {
                            LogUtil.d("refresh success");
                            adapter.setDataList(listDataResult.data);
                            pageNum = DEFAULT_PAGE_NUM;
                            refreshLayout.finishRefresh(true);
                        } else {
                            LogUtil.e("refresh failed, msg -> %s", listDataResult.msg);
                            refreshLayout.finishRefresh(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e("refresh failed, throwable -> %s", throwable.getMessage());
                        refreshLayout.finishRefresh(false);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        searchLayout.requestFocus();
        if (disposable == null) {
            disposable = initData();
        }
    }

    public Disposable initData() {
        return MainApiImpl.getAllCommodity(DEFAULT_PAGE_NUM)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataResult<List<Commodity>>>() {
                    @Override
                    public void accept(DataResult<List<Commodity>> listDataResult) throws Exception {
                        if (listDataResult.code == DataResult.CODE_SUCCESS) {
                            adapter.setDataList(listDataResult.data);
                            pageNum = DEFAULT_PAGE_NUM;
                            searchDisposable = null;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e("throwable -> %s", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppConfig.getLocalBroadcastManager().unregisterReceiver(receiver);
    }
}
