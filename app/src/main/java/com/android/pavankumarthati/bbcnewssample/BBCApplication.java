package com.android.pavankumarthati.bbcnewssample;

import android.app.Application;
import com.android.pavankumarthati.bbcnewssample.usecases.CacheDataManager;
import com.android.pavankumarthati.bbcnewssample.usecases.DataManager;
import com.android.pavankumarthati.bbcnewssample.usecases.RestDataManager;
import java.io.File;

/**
 * Created by pavankumar.thati on 1/14/18.
 */

public class BBCApplication extends Application {

  private static DataManager mDataManager;

  @Override
  public void onCreate() {
    super.onCreate();

    RestDataManager restDataManager = new RestDataManager();
    File cacheDir = getApplicationContext().getCacheDir();
    String cacheFileName = "news.dat";
    CacheDataManager cacheDataManager = new CacheDataManager(cacheDir, cacheFileName);
    mDataManager = new DataManager(restDataManager, cacheDataManager);

  }

  public DataManager getDataManager() {
    return mDataManager;
  }
}
