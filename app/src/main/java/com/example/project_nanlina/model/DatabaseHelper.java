package com.example.project_nanlina.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    static String NAME = "pm_info5.db";   // to-do*
    static SQLiteDatabase.CursorFactory FACTORY = null;
    static String PACKEGE = "com.example.project_nanlina"; //to-do
    static String DB = "pm_info5.db";   // to-do*
    static int VERSION  = 1;

    public DatabaseHelper(Context context) {
        super(context, NAME, FACTORY, VERSION);
        // TODO Auto-generated constructor stub
        try {
            boolean bResult = isCheckDB(context);  // DB가 있는지?
            Log.i("DatabaseHelper", "DB Check="+bResult);
            if(!bResult) {   // DB가 없으면 복사
                copyDB(context);
            } else{
            }

        } catch (Exception e) {
        }

    }

    // DB가 있나 체크함수
    public boolean isCheckDB(Context mContext){

        String filePath = "/data/data/" + PACKEGE + "/databases/" + DB;
        File file = new File(filePath);

        if (file.exists()) {
            return true;
        }
        return false;
    }

    // DB를 복사하기
    // assets의 /db/xxxx.db 파일을 설치된 프로그램의 내부 DB공간으로 복사하기
    public void copyDB(Context mContext){

        Log.d("DatabaseHelper", "copyDB실행");

        AssetManager manager = mContext.getAssets();
        String folderPath = "/data/data/" + PACKEGE + "/databases";
        String filePath = "/data/data/" + PACKEGE + "/databases/" +DB;
        File folder = new File(folderPath);
        File file = new File(filePath);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            //InputStream is = manager.open("db/" + DB);
            InputStream is = manager.open(DB); //나는 애샛폴더에 바로 DB가 있으니까!
            BufferedInputStream bis = new BufferedInputStream(is);
            if (folder.exists()) {

            } else {
                folder.mkdirs();
            }


            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];

            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();
            bos.close();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("ErrorMessage : ", e.getMessage());
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String QUERY = "CREATE TABLE word (_id INTEGER PRIMARY KEY autoincrement, word_e TEXT , word_k TEXT)";
        // db.execSQL(QUERY);
        Log.e("ehsk", "eee");
        //String QUERY1 = "INSERT INTO word (word_e, word_k ) VALUES(apple , 사과)";
        //db.execSQL(QUERY1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String QUERY = "DROP TABLE IF EXISTS word";
        db.execSQL(QUERY);
        onCreate(db);
    }
}
