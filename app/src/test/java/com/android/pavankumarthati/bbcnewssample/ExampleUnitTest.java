package com.android.pavankumarthati.bbcnewssample;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

  @Test
  public void addition_isCorrect() throws Exception {
    Observable<Integer> empty = Observable.create(
        (ObservableOnSubscribe<List<Integer>>) emitter -> {
          emitter.onNext(Arrays.asList(10,11,12,13));
          emitter.onComplete();
        } ).flatMap(new Function<List<Integer>, ObservableSource<Integer>>() {
      @Override
      public ObservableSource<Integer> apply(List<Integer> integers) throws Exception {
        return Observable.fromIterable(integers);
      }
    });

    Observable<Integer> list = Observable.just(1, 2, 3, 4);
    Observable.concat(empty, list)
        .take(1)
        .subscribe(i -> {
          System.out.println(" value " + i);
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {
            System.out.println("exception " + throwable);
          }
        });
  }
}