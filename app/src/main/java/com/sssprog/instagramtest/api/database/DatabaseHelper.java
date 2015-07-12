package com.sssprog.instagramtest.api.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sssprog.instagramtest.App;
import com.sssprog.instagramtest.utils.LogHelper;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = LogHelper.getTag(DatabaseHelper.class);
    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;

    private static final Set<Class<?>> DATA_CLASSES = new HashSet<>(Arrays.asList(new Class<?>[] {
            Post.class,
            Comment.class
    }));

    private static DatabaseHelper instance;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper(App.getInstance());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        createDb();
    }

    private void createDb() {
        try {
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables() throws SQLException {
        for (Class<?> dataClass : DATA_CLASSES) {
            TableUtils.createTable(connectionSource, dataClass);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        LogHelper.i(TAG, "onUpgrade " + oldVersion + " -> " + newVersion);
    }

    public Dao<Post, Long> getPostDao() throws SQLException {
        return getDao(Post.class);
    }

    public Dao<Comment, Long> getCommentDao() throws SQLException {
        return getDao(Comment.class);
    }

    @Override
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        D dao = super.getDao(clazz);
        dao.setObjectCache(true);
        return dao;
    }

    public void clearTable(Class<?> cls) throws SQLException {
        TableUtils.clearTable(connectionSource, cls);
        getDao(cls).clearObjectCache();
    }

}
