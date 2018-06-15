package com.bebsolutions.appsolicitantesienge;

import android.app.Application;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class AplicationSolicitante extends Application {
    private DBHelper dbHelper = null;

    public DBHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        }
        return dbHelper;
    }

}
