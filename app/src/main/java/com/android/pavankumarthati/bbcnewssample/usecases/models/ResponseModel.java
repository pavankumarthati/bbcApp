package com.android.pavankumarthati.bbcnewssample.usecases.models;

import android.support.annotation.Nullable;

/**
 * Created by pavankumar.thati on 1/14/18.
 */

public class ResponseModel extends Exception {
  private int code;
  private String reason;
  private Throwable error;

  public ResponseModel(@Nullable int code, @Nullable String reason, @Nullable Throwable error) {
    this.code = code;
    this.reason = reason;
    this.error = error;
  }

}
