package com.android.pavankumarthati.bbcnewssample.presenters;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import com.android.pavankumarthati.bbcnewssample.BaseViewModel;
import com.android.pavankumarthati.bbcnewssample.usecases.DataManager;
import com.android.pavankumarthati.bbcnewssample.usecases.DataManager.NewsModel;
import com.android.pavankumarthati.bbcnewssample.usecases.models.News;
import com.android.pavankumarthati.bbcnewssample.usecases.models.NewsError;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.Subject;
import java.util.List;
import retrofit2.HttpException;

/**
 * Created by pavankumar.thati on 1/3/18.
 */

public class NewsViewModel implements BaseViewModel {

  DataManager mDataManager;
  AsyncSubject<List<News>> newsSubject;
  String id;
  String maxId;
  public static final int LIMIT = 10;


  public NewsViewModel(@NonNull DataManager dataManager) {
    mDataManager = checkNotNull(dataManager);
    newsSubject = AsyncSubject.create();
  }

  public Subject<List<News>> getNewsSubject() {
    return newsSubject;
  }

  public void fetchOneTimeNews() {
    mDataManager.getOneTimeNews(LIMIT)
        .doOnNext(newsModel -> {
          if (newsModel != null && newsModel.getNews() != null && newsModel.getNews().size() > 0) {
            List<News> newsList = newsModel.getNews();
            id = newsList.get(newsList.size() - 1).getId();
            maxId = newsList.get(0).getId();
          }
        })
        .flatMap(newsModel -> {
          if (newsModel != null && newsModel.getNews() != null && newsModel.getNews().size() > 0) {
            return Observable.just(newsModel.getNews());
          }
          return Observable.empty();
        })
        .doOnError(this::prepareError)
        .subscribe(newsSubject);
  }

  private NewsError prepareError(Throwable throwable) {
    System.out.println("exception " + throwable);
    if (throwable instanceof HttpException) {
      HttpException httpException = (HttpException) throwable;
      NewsError newsError = new NewsError(httpException.message(), httpException.code());
      newsError.setReasonToShow("Oops! something went wrong");
      return newsError;
    }
    return new NewsError();
  }


  public Subject<List<News>> createNewsSubject() {
    newsSubject = AsyncSubject.create();
    return newsSubject;
  }

  public void fetchLatestNews() {
    mDataManager.getNextNews(null, maxId, LIMIT)
        .doOnNext(newsModel -> {
          maxId = newsModel.getMaxId();
        })
        .flatMap(newsModel -> {
          return Observable.just(newsModel.getNews());
        })
        .doOnError(this::prepareError)
        .subscribe(newsSubject);
  }

  public void fetchOldNews() {
    mDataManager.getNextNews(id, null, LIMIT)
        .doOnNext(newsModel -> {
          if (newsModel != null && newsModel.getNews() != null && newsModel.getNews().size() > 0) {
            List<News> newsList = newsModel.getNews();
            id = newsModel.getNews().get(newsList.size() - 1).getId();
          }
        })
        .flatMap(newsModel -> {
          if (newsModel != null && newsModel.getNews() != null && newsModel.getNews().size() > 0) {
            return Observable.just(newsModel.getNews());
          }
          return Observable.empty();
        })
        .doOnError(this::prepareError)
        .subscribe(newsSubject);
  }

}
