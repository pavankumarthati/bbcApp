package com.android.pavankumarthati.bbcnewssample.fragments;

import static com.android.pavankumarthati.bbcnewssample.presenters.NewsViewModel.LIMIT;
import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.pavankumarthati.bbcnewssample.R;
import com.android.pavankumarthati.bbcnewssample.presenters.NewsContract;
import com.android.pavankumarthati.bbcnewssample.presenters.NewsViewModel;
import com.android.pavankumarthati.bbcnewssample.usecases.models.News;
import com.squareup.picasso.Picasso;
import com.thefinestartist.finestwebview.FinestWebView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.DisposableSubscriber;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavankumar.thati on 12/29/17.
 */

public class NewsListFragment extends Fragment implements NewsContract.View, OnRefreshListener {

  RecyclerView newsListRv;
  NewsAdapter newsAdapter;
  Context mContext;
  NewsViewModel mNewsViewModel;
  private DisposableSubscriber<List<News>> mNewsDisposable;
  private SwipeRefreshLayout swipeRefreshLayout;
  private boolean isRefreshing;
  private boolean firstFetch = true;
  private boolean paginating;

  public static NewsListFragment getInstance(@NonNull NewsViewModel newsViewModel) {
    checkNotNull(newsViewModel);
    NewsListFragment newsListFragment = new NewsListFragment();
    newsListFragment.setViewModel(newsViewModel);
    return newsListFragment;
  }

  private void setViewModel(NewsViewModel newsViewModel) {
    this.mNewsViewModel = newsViewModel;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mContext = context;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    newsAdapter = new NewsAdapter();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.news_list_frag, container, false);
    swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
    newsListRv = rootView.findViewById(R.id.newsListRv);
    newsListRv.setHasFixedSize(true);
    newsListRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    newsListRv.setAdapter(newsAdapter);
    newsListRv.addOnItemTouchListener(newsAdapter);
    newsListRv.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleCount = recyclerView.getLayoutManager().getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int firstVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (!paginating && !newsAdapter.isLastPage) {
          if (firstVisibleItemPosition + visibleCount >= totalItemCount
              && totalItemCount >= LIMIT
              && firstVisibleItemPosition >= 0) {
            paginating = true;
            reconnectWithSubject();
            mNewsViewModel.fetchOldNews();
          }
        }
      }
    });
    swipeRefreshLayout.setOnRefreshListener(this);
    mNewsViewModel.fetchOneTimeNews();
  }

  @Override
  public void onResume() {
    super.onResume();
    subscribeForNews();
  }

  private void subscribeForNews() {
    mNewsDisposable = mNewsViewModel.getNewsSubject()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new NewsObserver());
  }

  @Override
  public void onPause() {
    super.onPause();
    unsubscribeOnNews();
  }

  private void unsubscribeOnNews() {
    mNewsDisposable.dispose();
  }

  @Override
  public void onRefresh() {
    reconnectWithSubject();
    isRefreshing = true;
    mNewsViewModel.fetchLatestNews();
  }

  public void reconnectWithSubject() {
    mNewsDisposable = mNewsViewModel.createNewsSubject()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new NewsObserver());
  }


  public class NewsObserver extends DisposableSubscriber<List<News>> implements Observer<List<News>> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(List<News> news) {
      if (news != null) {
        if (firstFetch) {
          firstFetch = false;
          newsAdapter.addAll(news);
          if (news.size() >= LIMIT) {
            newsAdapter.addFooter();
          } else {
            newsAdapter.isLastPage = true;
          }
        } else if (isRefreshing) {
          isRefreshing = false;
          swipeRefreshLayout.setRefreshing(false);
          newsAdapter.addFront(news);
          if (newsAdapter.getItemCount() >= LIMIT && !newsAdapter.isFotterAdded) {
            newsAdapter.addFooter();
          }
        } else if (paginating) {
          paginating = false;
          newsAdapter.removeFooter();
          newsAdapter.addAll(news);
          if (news.size() >= LIMIT) {
            newsAdapter.addFooter();
          } else {
            newsAdapter.isLastPage = true;
          }
        }
      }
    }

    @Override
    public void onError(Throwable e) {
      dispose();
      if (isRefreshing) {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
      }

      if (paginating) {
        paginating = false;
        if (newsAdapter.isFotterAdded) {
          newsAdapter.removeFooter();
        }
      }
    }

    @Override
    public void onComplete() {
      dispose();
      if (isRefreshing) {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
      }

      if (paginating) {
        paginating = false;
        if (newsAdapter.isFotterAdded) {
          newsAdapter.removeFooter();
        }
      }
    }
  }

  public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemTouchListener {

    ArrayList<News> items;
    GestureDetector gestureDetector;
    private static final int FOOTER = 1;
    private static final int ITEM = 0;
    private boolean isFotterAdded;
    public boolean isLastPage;


    public NewsAdapter() {
      this.items = new ArrayList<>();
      gestureDetector = new GestureDetector(NewsListFragment.this.getContext(), new SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
          return true;
        }

      });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView;
      switch (viewType) {
        case ITEM:
          itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
          return new NewsItemViewHolder(itemView);
        case FOOTER:
          itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_item, parent, false);
          return new LoadMoreItemsViewHolder(itemView);
        default:
          throw new RuntimeException("No view found with this type");
      }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      int viewType = getItemViewType(position);
      switch (viewType) {
        case ITEM:
          NewsItemViewHolder viewHolder = (NewsItemViewHolder) holder;
          viewHolder.titleTv.setText(items.get(position).getTitle());
          Picasso.with(viewHolder.thumbnailIv.getContext()).load(items.get(position).getThumbnail()).into(viewHolder.thumbnailIv);
          viewHolder.description.setText(items.get(position).getDescription());
      }
    }

    @Override
    public int getItemViewType(int position) {
      return isLastPosition(position) && isFotterAdded ? FOOTER : ITEM;
    }

    public void addFooter() {
      isFotterAdded = true;
      add(new News());
    }

    public void removeFooter() {
      isFotterAdded = false;
      int position = items.size() - 1;
      items.remove(position);
      notifyItemRemoved(position);
    }

    private boolean isLastPosition(int position) {
      return items.size() - 1 == position;
    }

    public void add(News item) {
      items.add(item);
      notifyItemInserted(items.size() - 1);
    }

    @Override
    public int getItemCount() {
      System.out.println("item count " + items.size());
      return items == null || items.size() == 0 ? 0 : items.size();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
      View childView = rv.findChildViewUnder(e.getX(), e.getY());
      if (childView != null && gestureDetector.onTouchEvent(e)) {
        News news = items.get(rv.getChildAdapterPosition(childView));
        new FinestWebView.Builder(getActivity()).titleDefault(news.getTitle())
            .show(news.getLink());
      }
      return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public void addFront(List<News> newsList) {
      items.addAll(0, newsList);
      notifyItemRangeInserted(0, newsList.size());
    }

    public void addAll(List<News> newsList) {
      items.addAll(newsList);
      notifyDataSetChanged();
    }
  }

  private static class NewsItemViewHolder extends RecyclerView.ViewHolder {

    TextView description;
    TextView titleTv;
    ImageView thumbnailIv;

    public NewsItemViewHolder(View itemView) {
      super(itemView);
      this.titleTv = itemView.findViewById(R.id.newsTitleView);
      this.thumbnailIv = itemView.findViewById(R.id.newsInsetImv);
      this.description = itemView.findViewById(R.id.description);
    }
  }

  public interface OnClickListener {
    void onClick(View view, int position);
  }


  private static class LoadMoreItemsViewHolder extends RecyclerView.ViewHolder {

    ProgressBar progressBar;

    public LoadMoreItemsViewHolder(View itemView) {
      super(itemView);
      this.progressBar = itemView.findViewById(R.id.progressBar);
      this.progressBar.setIndeterminate(true);
    }
  }

}
