package com.sunfusheng.gank.widget.RecyclerViewLayout;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.sunfusheng.gank.R;
import com.sunfusheng.gank.widget.MultiType.ItemViewProvider;
import com.sunfusheng.gank.widget.MultiType.MultiTypeAdapter;
import com.sunfusheng.gank.widget.SwipeRefreshLayout.SwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2017/1/15.
 */
public class MultiTypeRecyclerView extends FrameLayout {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerRefreshLayout)
    SwipeRefreshLayout recyclerRefreshLayout;
    @BindView(R.id.error_stub)
    ViewStub errorStub;
    @BindView(R.id.empty_stub)
    ViewStub emptyStub;

    View loadingView;
    View errorView;
    View emptyView;

    private OnClickListener errorViewClickListener;
    private OnClickListener emptyViewClickListener;
    private OnRequestListener onRequestListener;
    private OnScrollListener onScrollListener;

    private LoadingStateDelegate loadingStateDelegate;

    private LinearLayoutManager linearLayoutManager;
    private MultiTypeAdapter multiTypeAdapter;
    private int lastItemCount;

    public MultiTypeRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public MultiTypeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTypeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_recyclerview, this);
        ButterKnife.bind(this, view);

        loadingView = ButterKnife.findById(view, R.id.loading_view);
        loadingStateDelegate = new LoadingStateDelegate(recyclerView, loadingView, errorStub, emptyStub);

        multiTypeAdapter = new MultiTypeAdapter();
        multiTypeAdapter.applyGlobalMultiTypePool();

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(multiTypeAdapter);
    }

    private void initListener() {
        recyclerRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (onRequestListener != null) {
                    onRequestListener.onRefresh();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getLayoutManager() instanceof  LinearLayoutManager) {
                    int itemCount = linearLayoutManager.getItemCount();
                    int lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                    if (itemCount != lastItemCount && lastPosition == itemCount - 1 && onRequestListener != null) {
                        lastItemCount = itemCount;
                        onRequestListener.onLoadingMore();
                    }
                }

                if (onScrollListener != null) {
                    onScrollListener.onScrolled(recyclerView, dx, dy);
                }
            }
        });
    }

    public void setLoadingState(@LoadingStateDelegate.STATE int state) {
        recyclerRefreshLayout.setRefreshing(false);
        if (state == LoadingStateDelegate.STATE.ERROR) {
            if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
                loadingStateDelegate.setViewState(LoadingStateDelegate.STATE.SUCCEED);
            } else {
                emptyView = loadingStateDelegate.setViewState(LoadingStateDelegate.STATE.ERROR);
                setErrorViewClickListener(errorViewClickListener);
            }
        } else if (state == LoadingStateDelegate.STATE.EMPTY) {
            emptyView = loadingStateDelegate.setViewState(LoadingStateDelegate.STATE.EMPTY);
            setEmptyViewClickListener(emptyViewClickListener);
        } else {
            loadingStateDelegate.setViewState(state);
        }
    }

    public void setErrorViewClickListener(OnClickListener errorLayoutClickListener) {
        this.errorViewClickListener = errorLayoutClickListener;
        if (errorView != null) {
            errorView.setOnClickListener(errorLayoutClickListener);
        }
    }

    public void setEmptyViewClickListener(OnClickListener emptyViewClickListener) {
        this.emptyViewClickListener = emptyViewClickListener;
        if (emptyView != null) {
            emptyView.setOnClickListener(emptyViewClickListener);
        }
    }

    public void setEmptyView(int layoutId) {
        if (emptyStub != null && layoutId != -1) {
            emptyStub.setLayoutResource(layoutId);
        }
    }

    public void setErrorView(int layoutId) {
        if (errorStub != null && layoutId != -1) {
            errorStub.setLayoutResource(layoutId);
        }
    }

    public void setData(@Nullable List<?> items) {
        multiTypeAdapter.setItems(items);
    }

    public void register(@NonNull Class<?> clazz, @NonNull ItemViewProvider provider) {
        multiTypeAdapter.register(clazz, provider);
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    public MultiTypeAdapter getMultiTypeAdapter() {
        return multiTypeAdapter;
    }

    public void setOnRequestListener(OnRequestListener onRequestListener) {
        this.onRequestListener = onRequestListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnRequestListener {
        void onRefresh();
        void onLoadingMore();
    }

    public interface OnScrollListener {
        void onScrollStateChanged(RecyclerView recyclerView, int newState);
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }
}
