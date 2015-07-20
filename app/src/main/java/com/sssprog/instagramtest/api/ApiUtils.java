package com.sssprog.instagramtest.api;

import com.j256.ormlite.misc.TransactionManager;
import com.sssprog.instagramtest.api.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.concurrent.Callable;

public class ApiUtils {

    public static void executeWithRuntimeException(ApiTask task) {
        try {
            task.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface ApiTask {
        void execute() throws Exception;
    }

    public static <T> T callInTransaction(DatabaseHelper database, Callable<T> task) {
        try {
            return TransactionManager.callInTransaction(database.getConnectionSource(), task);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
