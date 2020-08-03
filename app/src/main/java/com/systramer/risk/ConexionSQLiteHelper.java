package com.systramer.risk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.systramer.risk.Utilidades.Utilidades;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.Clientes);
        db.execSQL(Utilidades.ClienteAreas);
        db.execSQL(Utilidades.ClienteAreasRiesgos);
        db.execSQL(Utilidades.SitioInteres);
        db.execSQL(Utilidades.SitioInteresRiesgos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Clientes");
        db.execSQL("DROP TABLE IF EXISTS ClienteAreas");
        db.execSQL("DROP TABLE IF EXISTS ClienteAreasRiesgos");
        db.execSQL("DROP TABLE IF EXISTS SitioInteres");
        db.execSQL("DROP TABLE IF EXISTS SitioInteresRiesgos");

        onCreate(db);
    }
}
