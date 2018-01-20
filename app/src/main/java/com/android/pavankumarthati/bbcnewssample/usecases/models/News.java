package com.android.pavankumarthati.bbcnewssample.usecases.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by pavankumar.thati on 12/29/17.
 */

@IgnoreExtraProperties
public class News implements Serializable {

  @SerializedName("_id")
  String id;
  @SerializedName("title")
  String title;
  @SerializedName("link")
  String link;
  @SerializedName("description")
  String description;
  @SerializedName("pubDate")
  String pubDate;
  @SerializedName("thumbnail")
  String thumbnail;

  public News() {

  }

  public News(String id, String title, String link, String description, String pubDate, String thumbnail) {
    this.id = id;
    this.title = title;
    this.link = link;
    this.description = description;
    this.pubDate = pubDate;
    this.thumbnail = thumbnail;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public String getLink() {
    return link;
  }

  public String getDescription() {
    return description;
  }

  public String getPubDate() {
    return pubDate;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  @Override
  public String toString() {
    return "News{" +
        "id= '" + id + '\'' +
        "title='" + title + '\'' +
        ", link='" + link + '\'' +
        ", description='" + description + '\'' +
        ", pubDate=" + pubDate +
        ", thumbnail='" + thumbnail + '\'' +
        '}';
  }
}
