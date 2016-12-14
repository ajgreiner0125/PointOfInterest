package com.example.pointofinterest2;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Alex on 11/17/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.example.pointofinterest2/databases/";

    private static String DB_NAME = "data.sqlite";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private ArrayList<Place> places;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    //fill the places arraylist with all the places in the databse
    public ArrayList<Place> grabPlaces(){

        String sql = "SELECT * FROM DATA3";
        Cursor cursor = myDataBase.rawQuery(sql, null);

        places = new ArrayList<Place>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Place place = new Place();
                place.setPrimaryCategory(cursor.getString(0));
                place.setSubCategories(cursor.getString(1));
                place.setRating(cursor.getString(2));
                place.setName(cursor.getString(3));
                place.setPhotoHref(cursor.getString(4));
                place.setDescription(cursor.getString(5));
                place.setAddress0(cursor.getString(6));
                place.setAddress1(cursor.getString(7));
                place.setAddress2(cursor.getString(8));
                place.setAddress3(cursor.getString(9));
                if (!(cursor.getString(10).equals("null") || cursor.getString(11).equals("null"))) {
                    place.setLatLng(Double.parseDouble(cursor.getString(10)), Double.parseDouble(cursor.getString(11)));
                }
                // Adding place to list
                if ((place.lat != null) && (place.lng != null)) {
                    places.add(place);
                }
            } while (cursor.moveToNext());
        }

        return places;
    }



    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}
