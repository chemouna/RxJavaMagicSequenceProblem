package com.mounacheikhna.magicsequence;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

  private static final String MAGIC_SEQUENCE = "ABBABA";
  @Bind(R.id.buttonA) Button buttonA;
  @Bind(R.id.buttonB) Button buttonB;
  @Bind(R.id.result) TextView resultView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    final Observable<String> obsButtonA = RxView.clicks(buttonA).map(new Func1<Void, String>() {
      @Override public String call(Void aVoid) {
        return "A";
      }
    });
    final Observable<String> obsButtonB = RxView.clicks(buttonB).map(new Func1<Void, String>() {
      @Override public String call(Void aVoid) {
        return "B";
      }
    });

    final Observable<String> obsButtons = Observable.merge(obsButtonA, obsButtonB);
    obsButtons.buffer(6, 1)
        .timeout(5000, TimeUnit.SECONDS, Observable.just(new ArrayList<String>()))
        .repeat()
        .doOnNext(new Action1<List<String>>() {
          @Override public void call(List<String> strings) {
            resultView.setText(concatStringsWSep(strings, ""));
          }
        })
        .filter(new Func1<List<String>, Boolean>() {
          @Override public Boolean call(List<String> strings) {
            return concatStringsWSep(strings, "").equals(MAGIC_SEQUENCE);
          }
        })
        .subscribe(new Action1<List<String>>() {
          @Override public void call(List<String> strings) {
            resultView.setText("BRAVO! Magic Sequence entered.");
          }
        });
  }

  public static String concatStringsWSep(Iterable<String> strings, String separator) {
    StringBuilder sb = new StringBuilder();
    String sep = "";
    for(String s: strings) {
      sb.append(sep).append(s);
      sep = separator;
    }
    return sb.toString();
  }

}
