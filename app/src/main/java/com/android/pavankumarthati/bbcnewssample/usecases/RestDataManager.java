package com.android.pavankumarthati.bbcnewssample.usecases;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.pavankumarthati.bbcnewssample.usecases.apicontracts.NewsApi;
import com.android.pavankumarthati.bbcnewssample.usecases.models.News;
import com.android.pavankumarthati.bbcnewssample.usecases.models.NewsRequestModel;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pavankumar.thati on 1/14/18.
 */

public class RestDataManager {

  private NewsApiService newsApiService;
  private Retrofit retrofitInstance;
  private static final String BASE_URL = "https://bbc-news.herokuapp.com/";

  public RestDataManager() {
    retrofitInstance = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .client(new OkHttpClient.Builder().build())
        .build();
    newsApiService = NewsApiService.create(retrofitInstance);
  }

  public Observable<ResponseBody> getNews(@Nullable String maxId, @Nullable String id, @Nullable int limit) {
    NewsRequestModel newsRequestModel = new NewsRequestModel();
    newsRequestModel.id = id;
    newsRequestModel.maxId = maxId;
    newsRequestModel.limit = limit;
    return newsApiService.getNews(newsRequestModel);
  }


}
