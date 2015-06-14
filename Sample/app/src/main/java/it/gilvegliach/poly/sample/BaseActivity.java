package it.gilvegliach.poly.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * @author Gil
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComponent component = DaggerActivityComponent.create();
        PolyActivityComponentWrapper wrapper = new PolyActivityComponentWrapper(component);
        wrapper.inject(this);
    }
}
