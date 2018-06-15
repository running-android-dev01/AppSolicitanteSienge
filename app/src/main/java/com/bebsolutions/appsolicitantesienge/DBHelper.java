package com.bebsolutions.appsolicitantesienge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Objects;

public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DBHelper.class.getName();

    private static final String DATABASE_NAME = "solicitacao";
    private static final int DATABASE_VERSION = 1;

    private Dao<Solicitacao, Integer> solicitacaoDao = null;

    public DBHelper(Context context) {
        super(context, Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DBHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Solicitacao.class);
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Cant create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        int i = oldVersion + 1;
        while (i <= newVersion) {
            switch (i) {
                case 1:
                    try {
                        TableUtils.createTable(connectionSource, Solicitacao.class);
                    } catch (Exception e) {
                        Log.e(TAG, "ERRO", e);
                    }
                    break;
            }
            i++;
        }


    }


    public Dao<Solicitacao, Integer> getSolicitacaoDao() throws SQLException {
        if (solicitacaoDao == null) {
            solicitacaoDao = getDao(Solicitacao.class);
        }
        return solicitacaoDao;
    }

    @Override
    public void close() {
        super.close();

        solicitacaoDao = null;
    }

}
