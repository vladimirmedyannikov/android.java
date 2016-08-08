/**
 * 
 */
package ru.mos.elk.netframework.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.android.volley2.Cache;

import org.apache.http.protocol.HTTP;

import java.util.HashMap;

import ru.mos.elk.netframework.request.GeneralRequest;

/**
 * @author Александр Свиридов
 * @28.05.2013
 */
public class DbCache implements Cache{

    private static final String CACHE_TABLE = "cache_table";

    public static final String STUB_CACHE_KEY = "STUB_CACHE_KEY";

	private CacheDataHelper dbHeplper;
	/**
	 * @param context
	 */
	public DbCache(Context context) {
		this.dbHeplper = new CacheDataHelper(context);
	}

	@Override
	public synchronized Entry get(String key) {
		if(key==null) return null;
		else if (STUB_CACHE_KEY.equals(key)) return getStubEntry();
		Entry entry = null;
		
        SQLiteDatabase db = dbHeplper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT data, expire_time, content_type FROM " + CACHE_TABLE + " WHERE key=? AND expire_time>="+System.currentTimeMillis(), new String[]{key});
        if (cursor.moveToNext()) {
        	entry = new Entry();
        	entry.data = cursor.getBlob(0);
        	entry.ttl = entry.softTtl = cursor.getLong(1);
        	entry.responseHeaders = new HashMap<String, String>();
        	entry.responseHeaders.put(HTTP.CONTENT_TYPE, cursor.getString(2));
        	entry.responseHeaders.put(GeneralRequest.CACHE_FLAG, "true");
        }
        cursor.close();
        db.close();
        
		return entry;
	}

	private Entry getStubEntry() {
		Cache.Entry entry = new Entry();
		entry.data = null;
		entry.softTtl = entry.ttl = Long.MAX_VALUE;
		entry.responseHeaders = null;
		
		return entry;
	}

	@Override
	public synchronized void put(String key, Entry entry) {
        SQLiteDatabase db = dbHeplper.getWritableDatabase();
        long id; 
        if ((id = isStored(db,key))!=-1L)
        	update(db ,id, entry);
        else
        	insert(db, key, entry);
        db.close();
	}

	/** returns id of record or -1L if no records found
     * @param key */
    private long isStored(SQLiteDatabase db, String key) {
        long result = -1L;
        Cursor cursor = db.rawQuery("SELECT ID FROM " + CACHE_TABLE + " WHERE key=? LIMIT 1", new String[]{key});
        if (cursor.moveToNext()) {
        	result = cursor.getLong(0);
        }
        cursor.close();
        
        return result;
    }
    
	private void insert(SQLiteDatabase db, String key, Entry entry){
        try {
            db.beginTransaction();
            SQLiteStatement stmt = db.compileStatement("INSERT INTO " + CACHE_TABLE + " (key, data, expire_time, content_type) VALUES (?,?,?,?)");
            stmt.bindString(1, key);
            stmt.bindBlob(2, entry.data);
            stmt.bindLong(3, entry.ttl);
            String contentType = entry.responseHeaders.get(HTTP.CONTENT_TYPE);
            if(contentType==null)
                contentType = entry.responseHeaders.get("content-type");
            stmt.bindString(4, contentType);
            if (stmt.executeInsert() != -1) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
	}
	
	private void update(SQLiteDatabase db, long id, Entry entry){
        ContentValues values = new ContentValues(3);
        values.put("data", entry.data);
        values.put("expire_time", entry.ttl);
        values.put("content_type", entry.responseHeaders.get(HTTP.CONTENT_TYPE));
        try {
            db.beginTransaction();
            if (db.update(CACHE_TABLE, values, "id=?", new String[]{String.valueOf(id)}) != 0) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
	}
	
	@Override
	public void initialize() {
		SQLiteDatabase db = dbHeplper.getReadableDatabase(); //first time initializes db, upgrade it.
		db.close();
	}

	@Override
	public void invalidate(String key, boolean fullExpire) {
        if(fullExpire)
            removePattern(key);
        else
		    remove(key);
	}

    public synchronized void removePattern(String pattern) {
        SQLiteDatabase db = dbHeplper.getWritableDatabase();
        db.delete(CACHE_TABLE, "key like '" + pattern + "%'", null);

        db.close();
    }

	@Override
	public synchronized void remove(String key) {
        SQLiteDatabase db = dbHeplper.getWritableDatabase();
        db.delete(CACHE_TABLE, "key=?", new String[]{key});
        db.close();
	}

	@Override
	public synchronized void clear() {
		SQLiteDatabase db = dbHeplper.getWritableDatabase();
		db.delete(CACHE_TABLE, null, null);
		db.close();
	}
	
	private class CacheDataHelper extends SQLiteOpenHelper{
	    private static final String CREATE_CACHE_TABLE = "CREATE TABLE " + CACHE_TABLE + "(id INTEGER PRIMARY KEY, key TEXT, data BLOB, content_type TEXT, expire_time DATETIME);";
	    
	    private static final int DB_VERSION = 1;
	    private static final String DB_NAME = "cache_data.db";
		
		public CacheDataHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
	        db.execSQL(CREATE_CACHE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
	}
}
