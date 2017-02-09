package db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

/**
 * Created by chengkai on 2017/2/7.
 */

public class DBFactory {

    private static DBFactory INSTANCE = new DBFactory();

    private static String dbPath;

    private SQLiteDatabase sqLiteDatabase;

    private DBFactory() {
        dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "default.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
    }

    public synchronized <T extends BaseDao<M>, M> T getDBHelper(Class<T> baseDaoClass, Class<M> entityClass){
        BaseDao baseDao = null;
        try {
            baseDao = baseDaoClass.newInstance();
            baseDao.init(entityClass, sqLiteDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return ((T) baseDao);
    }

    public void setDBPath(String dbPath){
        this.dbPath = dbPath;
    }

    public static DBFactory getInstance() {
        return INSTANCE;
    }
}
