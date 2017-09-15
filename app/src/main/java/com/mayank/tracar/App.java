package com.mayank.tracar;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mayank on 13-09-2017.
 */

//// TODO: 14-09-2017 add reqyest to async
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/arial.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

    }
}
