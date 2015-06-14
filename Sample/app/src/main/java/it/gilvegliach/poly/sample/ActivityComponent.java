package it.gilvegliach.poly.sample;

import dagger.Component;

/**
 * @author Gil
 */
@Component
public interface ActivityComponent {
    void inject(SizeActivity activity);
    void inject(ColorActivity activity);
}
