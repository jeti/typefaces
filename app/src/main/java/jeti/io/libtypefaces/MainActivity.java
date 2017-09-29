package jeti.io.libtypefaces;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import jeti.io.typefaces.Typefaces;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("And there were three little bears, sitting on chairs. Two little kittens and a pair of mittens.");
        Typefaces.set(textView, R.raw.pacifico);
        setContentView(textView);
    }
}
