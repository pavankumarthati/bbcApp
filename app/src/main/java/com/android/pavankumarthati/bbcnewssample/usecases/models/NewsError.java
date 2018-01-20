package com.android.pavankumarthati.bbcnewssample.usecases.models;

/**
 * Created by pavankumar.thati on 1/14/18.
 */

public class NewsError extends Exception {
  private String reason;
  private int code;
  private String reasonToShow;

  public NewsError() {

  }

  public NewsError(String reason, int code) {
    this.reason = reason;
    this.code = code;
  }

  public void setReasonToShow(String reasonToShow) {
    this.reasonToShow = reasonToShow;
  }

  public String getReasonToShow() {
    return this.reasonToShow;
  }
}
