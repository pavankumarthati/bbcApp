package com.android.pavankumarthati.bbcnewssample.usecases;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.pavankumarthati.bbcnewssample.usecases.models.News;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pavankumar.thati on 1/3/18.
 */

public class DataManager {

  private final CacheDataManager cacheDataManager;
  RestDataManager restDataManager;

  public DataManager(@NonNull RestDataManager restDataManager, @NonNull CacheDataManager cacheDataManager) {
    this.restDataManager = checkNotNull(restDataManager);
    this.cacheDataManager = checkNotNull(cacheDataManager);
  }

  public Observable<NewsModel> getOneTimeNews(int limit) {
    return Observable.concat(cacheDataManager.getCachedData()
            .flatMap(file -> {
              FileInputStream fis = null;
              ObjectInputStream ois = null;
              try {
                if (file != null) {
                  fis = new FileInputStream(file);
                  ois = new ObjectInputStream(fis);
                  NewsModel newsModel = (NewsModel) ois.readObject();
                  return Observable.just(newsModel);
                } else {
                  return Observable.empty();
                }
              } catch (FileNotFoundException e) {
                return Observable.empty();
              } finally {
                if (ois != null) {
                  ois.close();
                }
              }
            }).filter(o -> !o.isDataOutdated())
        , restDataManager.getNews(null, null, limit)
            .flatMap(responseBody -> {
              InputStream is = responseBody.byteStream();
              try {
                if (is != null) {
                  List<News> newsList = new Gson().fromJson(new InputStreamReader(is),
                      new TypeToken<List<News>>() {
                      }.getType());
                  if (newsList != null && newsList.size() > 0) {
                    NewsModel newsModel = new NewsModel(newsList.get(0).getId(),
                        newsList.get(newsList.size() - 1).getId(), newsList);
                    newsModel.timeInMillis = new Date().getTime();
                    cacheDataManager.writeData(newsModel);
                    return Observable.just(newsModel);
                  }
                }
                return Observable.empty();
              } finally {
                if (is != null) {
                  is.close();
                }
              }
            })
    )
        .subscribeOn(Schedulers.io())
        .take(1);
  }

  public Observable<NewsModel> getNextNews(@Nullable String maxId, @Nullable String id, int limit) {
      return restDataManager.getNews(maxId, id, limit)
          .flatMap(responseBody -> {
            InputStream is = responseBody.byteStream();
            try {
              if (is != null) {
                List<News> newsList = new Gson().fromJson(new InputStreamReader(is),
                    new TypeToken<List<News>>() {
                    }.getType());
                if (newsList == null || newsList.size() == 0) {
                  return Observable.empty();
                }
                NewsModel newsModel = new NewsModel(maxId, id, newsList);
                newsModel.timeInMillis = new Date().getTime();
                return Observable.just(newsModel);
              }
              return Observable.empty();
            } finally {
              if (is != null) {
                is.close();
              }
            }
          }).subscribeOn(Schedulers.io());
    }

  public static class NewsModel implements Serializable {
    private static final long serialVersionUID = 1980L;
    @SerializedName("max_id")
    private String maxId;
    @SerializedName("id")
    private String id;
    @SerializedName("news")
    private List<News> news;
    @SerializedName("time_in_millis")
    private long timeInMillis;

    public NewsModel(@Nullable String maxId, @Nullable String id, @Nullable List<News> news) {
      this.maxId = maxId;
      this.id = id;
      this.news = news;
    }

    public String getMaxId() {
      return maxId;
    }

    public String getId() {
      return id;
    }

    public List<News> getNews() {
      return news;
    }

    public boolean isDataOutdated() {
      Date now = new Date();
      long diffFromNowInHours = TimeUnit.MILLISECONDS.toHours(now.getTime() - timeInMillis);
      return diffFromNowInHours > 12;
    }

  }

}
