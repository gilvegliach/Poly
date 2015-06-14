package it.gilvegliach.poly.sample;


/**
 * @author Gil
 */
public class PolyComponentWrapperHandWritten implements ActivityComponent {
    private final ActivityComponent mComponent;

    public PolyComponentWrapperHandWritten(ActivityComponent component) {
        mComponent = component;
    }

    @Override
    public void inject(SizeActivity activity) {
        mComponent.inject(activity);
    }

    @Override
    public void inject(ColorActivity activity) {
        mComponent.inject(activity);
    }

    public void inject(Object o) {
        if (o instanceof SizeActivity) {
            mComponent.inject((SizeActivity) o);
        } else if (o instanceof ColorActivity) {
            mComponent.inject((ColorActivity) o);
        } else {
            throw new AssertionError("Object not recognized");
        }
    }
}

