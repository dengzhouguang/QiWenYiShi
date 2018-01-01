package com.dzg.readclient.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.dzg.readclient.mvp.model.Collect;
import com.dzg.readclient.mvp.model.DingOrCai;

import com.dzg.readclient.greendao.CollectDao;
import com.dzg.readclient.greendao.DingOrCaiDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig collectDaoConfig;
    private final DaoConfig dingOrCaiDaoConfig;

    private final CollectDao collectDao;
    private final DingOrCaiDao dingOrCaiDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        collectDaoConfig = daoConfigMap.get(CollectDao.class).clone();
        collectDaoConfig.initIdentityScope(type);

        dingOrCaiDaoConfig = daoConfigMap.get(DingOrCaiDao.class).clone();
        dingOrCaiDaoConfig.initIdentityScope(type);

        collectDao = new CollectDao(collectDaoConfig, this);
        dingOrCaiDao = new DingOrCaiDao(dingOrCaiDaoConfig, this);

        registerDao(Collect.class, collectDao);
        registerDao(DingOrCai.class, dingOrCaiDao);
    }
    
    public void clear() {
        collectDaoConfig.getIdentityScope().clear();
        dingOrCaiDaoConfig.getIdentityScope().clear();
    }

    public CollectDao getCollectDao() {
        return collectDao;
    }

    public DingOrCaiDao getDingOrCaiDao() {
        return dingOrCaiDao;
    }

}
