package com.android.pavankumarthati.bbcnewssample.usecases.apicontracts;

import com.android.pavankumarthati.bbcnewssample.usecases.models.News;
import io.reactivex.Observable;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pavankumar.thati on 1/14/18.
 */


public interface NewsApi {

  @GET("/api/news")
  public Observable<ResponseBody> getNews(@Query("max_id") String maxId, @Query("id") String id, @Query("limit") int limit);

}
