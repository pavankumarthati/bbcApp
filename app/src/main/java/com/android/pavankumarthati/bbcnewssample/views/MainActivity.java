package com.android.pavankumarthati.bbcnewssample.views;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.pavankumarthati.bbcnewssample.BBCApplication;
import com.android.pavankumarthati.bbcnewssample.R;
import com.android.pavankumarthati.bbcnewssample.fragments.NewsListFragment;
import com.android.pavankumarthati.bbcnewssample.presenters.NewsViewModel;

public class MainActivity extends AppCompatActivity {

  NewsViewModel mNewsViewModel;
  NewsListFragment mNewsListFragment;
  private static final String NEWS_FRAGMENT_TAG = "NewsListFragment";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    showNewsFragment();
  }

  private void showNewsFragment() {
    mNewsViewModel = new NewsViewModel(((BBCApplication)getApplication()).getDataManager());
    Fragment existingNewsListFragment = getSupportFragmentManager().findFragmentByTag(NEWS_FRAGMENT_TAG);
    if (existingNewsListFragment == null) {
      mNewsListFragment = NewsListFragment.getInstance(mNewsViewModel);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.fragmentContainer, mNewsListFragment, NEWS_FRAGMENT_TAG)
          .commit();
    }
  }
}
