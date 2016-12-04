package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by danie on 03/12/2016.
 */

public class DBHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dbsuelos.db";
    private Context context;

    private static DBHelper instance = null;

    public static DBHelper getInstance(Context context){
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE gravita ("
                + "Id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Url VARCHAR(150) NOT NULL,"
                + "Suelo Varchar(100) NOT NULL,"
                + "Fecha VARCHAR(50) NOT NULL,"
                + "Latitud REAL NOT NULL,"
                + "Longitud REAL NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No hay operaciones
    }

    public void inserta(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("gravita",null,values);
        db.close();
    }

    public ArrayList<Floor> getAll(){
        ArrayList<Floor> salida = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM gravita",null);
        if(c.moveToFirst()){
            do{
                salida.add(new Floor(c.getFloat(4),c.getFloat(5),c.getString(3),c.getString(2),c.getString(1),c.getInt(0)));
            } while (c.moveToNext());
        }
        db.close();
        return salida;
    }

    public void elimina(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("gravita","Id = " + id, null);
        db.close();
    }

}
