package com.hy.data.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.hy.data.entity.People;

/**
 * Created by huangyong on 2019/1/17
 * Tb_group表数据库
 */
@Database(entities = {People.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract PeopleDao peopleDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "RoomTest.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
