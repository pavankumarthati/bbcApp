package com.android.pavankumarthati.bbcnewssample.usecases;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import com.android.pavankumarthati.bbcnewssample.usecases.apicontracts.NewsApi;
import com.android.pavankumarthati.bbcnewssample.usecases.models.News;
import com.android.pavankumarthati.bbcnewssample.usecases.models.NewsRequestModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by pavankumar.thati on 1/14/18.
 */

class NewsApiService {

  NewsApi newsApi;
  private NewsApiService(@NonNull NewsApi newsApi) {
    this.newsApi = checkNotNull(newsApi);
  }


  public static NewsApiService create(@NonNull Retrofit retrofit) {
    NewsApi newsApi = retrofit.create(NewsApi.class);
    return new NewsApiService(newsApi);
  }

  public Observable<ResponseBody> getNews(@NonNull NewsRequestModel newsRequestModel) {
    checkNotNull(newsRequestModel);
    return newsApi.getNews(newsRequestModel.maxId, newsRequestModel.id, newsRequestModel.limit);
  }

}
