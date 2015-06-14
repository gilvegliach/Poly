package it.gilvegliach.poly.sample;

import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;


public class ColorActivity extends BaseActivity  {

    @Inject
    ColorProcessor mColorProcessor;

    TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        bindViews();
        setupViews();
    }

    private void bindViews() {
        mText = (TextView) findViewById(R.id.text);
    }

    private void setupViews() {
        mText.setTextColor(mColorProcessor.getColor());
    }
}
