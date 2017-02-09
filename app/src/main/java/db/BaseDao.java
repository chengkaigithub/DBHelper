package db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import db.annotation.DBField;
import db.annotation.DBTable;

/**
 * Created by chengkai on 2017/2/7.
 */

public abstract class BaseDao<T> implements IBaseDao<T> {

    private Class<T> entityClass;

    private SQLiteDatabase sqLiteDatabase;

    private HashMap<String, Field> cacheMap;

    private String tableName;

    private static boolean isInit;

    public synchronized boolean init(Class<T> entityClass, SQLiteDatabase sqLiteDatabase) {

        this.entityClass = entityClass;
        this.sqLiteDatabase = sqLiteDatabase;

        if (!isInit) {
            DBTable annotation = entityClass.getAnnotation(DBTable.class);
            if (annotation != null) {
                tableName = annotation.value();
            } else {
                tableName = entityClass.getSimpleName();
            }
            if (!TextUtils.isEmpty(createTable())) {
                sqLiteDatabase.execSQL(createTable());
            }
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }

        return isInit;
    }

    private void initCacheMap() {
        String sql = "select * from " + this.tableName + " limit 1 , 0";
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            String[] columnNames = cursor.getColumnNames();
            Field[] fields = entityClass.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
            }
            for (String columnName : columnNames) {
                String key = null;
                Field value = null;
                for (Field field : fields) {
                    DBField annotation = field.getAnnotation(DBField.class);
                    if (annotation != null) {
                        key = annotation.value();
                    } else {
                        key = field.getName();
                    }
                    if (columnName.equals(key)) {
                        value = field;
                        break;
                    }
                }
                if (value != null) cacheMap.put(key, value);
            }
        } catch (Exception e) {
        } finally {cursor.close();}

    }

    @Override
    public long insert(T entity) {
        HashMap<String, String> data = entity2Map(entity);
        ContentValues contentValues = map2ContentValues(data);
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }

    private ContentValues map2ContentValues(HashMap<String, String> data) {
        ContentValues contentValues = new ContentValues();
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String values = data.get(key);
            if (values != null) contentValues.put(key, values);
        }
        return contentValues;
    }

    private HashMap<String, String> entity2Map(T entity) {
        HashMap<String, String> map = new HashMap<>();
        Iterator<Field> iterator = cacheMap.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            String key = null;
            String value = null;
            if (field.getAnnotation(DBField.class) != null) {
                key = field.getAnnotation(DBField.class).value();
            } else {
                key = field.getName();
            }
            try {
                if (field.get(entity) == null) {
                    continue;
                }
                value = field.get(entity).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            map.put(key, value);
        }
        return map;
    }

    @Override
    public int delete(T where) {
        HashMap<String, String> map = entity2Map(where);
        Condition condition = new Condition(map);
        int delete = sqLiteDatabase.delete(this.tableName, condition.getWhereClause(), condition.getWhereArgs());
        return delete;
    }
    /**
     * 封装修改语句
     *
     */
    private class Condition {

        private String whereClause;
        private String[] whereArgs;

        public Condition(Map<String, String> whereClause) {

            StringBuilder sb = new StringBuilder("1 = 1 ");
            ArrayList<String> list = new ArrayList();

            Iterator<String> iterator = whereClause.keySet().iterator();

            while (iterator.hasNext()) {
                String key = iterator.next();
                String velue = whereClause.get(key);
                sb.append("and " + key + " = ? ");
                if (velue != null) list.add(velue);
            }

            this.whereClause = sb.toString();
            this.whereArgs = list.toArray(new String[list.size()]);
        }

        public String getWhereClause() {
            return whereClause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }
    }

    @Override
    public int updata(T entity, T where) {
        ContentValues contentValues = map2ContentValues(entity2Map(entity));
        Condition condition = new Condition(entity2Map(where));
        int update = sqLiteDatabase.update(this.tableName, contentValues, condition.getWhereClause(), condition.getWhereArgs());
        return update;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Condition condition = new Condition(entity2Map(where));
        String limitStr = null;
        if (startIndex != null && limit != null) limitStr = startIndex + " , " + limit;
        Cursor cursor = sqLiteDatabase.query(this.tableName, null, condition.getWhereClause(),
                condition.getWhereArgs(), null, null,
                orderBy, limitStr);
        List<T> result = cursor2List(cursor, where);
        if (!cursor.isClosed()) cursor.close();
        return result;
    }

    private List<T> cursor2List(Cursor cursor, T entity) {
        ArrayList list = new ArrayList();
        while (cursor.moveToNext()) {
            Object obj = null;
            try {
                obj = entity.getClass().newInstance();

                Iterator<Map.Entry<String, Field>> iterator = cacheMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    String key = entry.getKey();
                    int columnIndex = cursor.getColumnIndex(key);
                    Field value = entry.getValue();
                    Class<?> type = value.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            value.set(obj, cursor.getString(columnIndex));
                        } else if (type == Double.class) {
                            value.set(obj, cursor.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            value.set(obj, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            value.set(obj, cursor.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            value.set(obj, cursor.getBlob(columnIndex));
                        } else {
                            continue; /** 不支持的类型 */
                        }
                    }
                }
                list.add(obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    protected abstract String createTable();

}
