package com.android.pavankumarthati.bbcnewssample.presenters;

import com.android.pavankumarthati.bbcnewssample.BaseViewModel;
import com.android.pavankumarthati.bbcnewssample.BaseView;

/**
 * Created by pavankumar.thati on 1/3/18.
 */

public interface NewsContract {

  public static interface View extends BaseView<ViewModel> {

  }

  public static interface ViewModel extends BaseViewModel {

  }

}
