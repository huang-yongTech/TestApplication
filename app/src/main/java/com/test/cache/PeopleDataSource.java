package com.test.cache;

import com.test.entity.People;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huangyong on 2019/1/23
 */
public class PeopleDataSource {
    private PeopleDao peopleDao;

    public PeopleDataSource(PeopleDao peopleDao) {
        this.peopleDao = peopleDao;
    }

    public Completable insert(final People people) {
        return Completable
                .fromAction(new Action() {
                    @Override
                    public void run() {
                        peopleDao.insert(people);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<People>> queryPeopleList() {
        return peopleDao.queryPeopleList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
