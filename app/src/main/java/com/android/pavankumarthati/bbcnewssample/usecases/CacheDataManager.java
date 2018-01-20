package com.android.pavankumarthati.bbcnewssample.usecases;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

/**
 * Created by pavankumar.thati on 1/20/18.
 */

public class CacheDataManager {

  File mCachedDir;
  String mCacheFileName;

  public CacheDataManager(@NonNull File cacheDir, @NonNull String cacheFileName) {
    mCachedDir = checkNotNull(cacheDir);
    checkNotNull(cacheFileName);
    mCacheFileName = cacheFileName;
  }

  public Observable<File> getCachedData() {
    return Observable.create(emitter -> {
      FileInputStream fis = null;
      File file = null;
      file = new File(mCachedDir, mCacheFileName);
      emitter.onNext(file);
      emitter.onComplete();
    });
  }


  public void writeData(Object object) {
    File file = null;
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    try {
      file = new File(mCachedDir, mCacheFileName);
      fos = new FileOutputStream(file);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(object);
    } catch (Exception e) {
      System.out.println("exception " + mCacheFileName + " " + e);
    } finally {
      try {
        if (oos != null) {
          oos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void releaseResources() {

  }

}
