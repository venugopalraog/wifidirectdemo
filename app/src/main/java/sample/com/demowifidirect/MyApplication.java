package sample.com.demowifidirect;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Vgubbala on 9/25/15.
 */
public class MyApplication extends Application {
    private static Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public static Bus getBus() {
        return bus;
    }
}
