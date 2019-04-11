package com.hy.data.cache;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hy.data.entity.People;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by huangyong on 2019/1/23
 */
@Dao
public interface PeopleDao {
    @Insert
    void insert(People people);

    //Flowable在数据表有更新时会自动查询数据，因此这里需要使用Single
    @Query(("select * from people"))
    Single<List<People>> queryPeopleList();
}
