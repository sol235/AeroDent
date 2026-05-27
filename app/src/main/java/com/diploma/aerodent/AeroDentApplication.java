package com.diploma.aerodent;

import android.app.Application;
import com.diploma.aerodent.data.AppContainer;
import com.diploma.aerodent.ui.ViewModelFactory;

public class AeroDentApplication extends Application {
    public AppContainer appContainer;
    private ViewModelFactory viewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer(this);
        viewModelFactory = new ViewModelFactory(this, appContainer);
    }

    public ViewModelFactory getViewModelFactory() {
        return viewModelFactory;
    }
}
