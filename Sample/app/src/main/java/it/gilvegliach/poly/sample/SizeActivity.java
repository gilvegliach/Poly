package it.gilvegliach.poly.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;


public class SizeActivity extends BaseActivity
        implements View.OnClickListener {

    @Inject
    SizeProcessor mTextProcessor;

    TextView mText;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size);
        bindViews();
        setupViews();
    }

    private void bindViews() {
        mText = (TextView) findViewById(R.id.text);
        mButton = (Button) findViewById(R.id.start_activity);
    }

    private void setupViews() {
        mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextProcessor.getSize());
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ColorActivity.class);
        startActivity(intent);
    }
}
