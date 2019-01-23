package com.test.cache;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.test.entity.People;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by huangyong on 2019/1/23
 */
@Dao
public interface PeopleDao {
    @Insert
    void insert(People people);

    @Query(("select * from people"))
    //Flowable在数据表有更新时会自动查询数据
    Single<List<People>> queryPeopleList();
}
