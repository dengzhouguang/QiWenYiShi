package com.dzg.readclient.mvp.usecase.base;

import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.repository.interfaces.Repository;

import rx.Observable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class Backup extends UseCase<Backup.RequestValues, Backup.ResponseValue> {
    private final Repository mRepository;

    public Backup(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        switch (requestValues.action) {
            case Constants.GETALL:
                return new ResponseValue(mRepository.getList(requestValues.newOrHotFlag, requestValues.type, requestValues.offset, requestValues.count));
            case Constants.GETJOKEBYID:
                return new ResponseValue(mRepository.getJokeById(requestValues.jokeId));
            case Constants.GETCOMMENTS:
                return new ResponseValue(mRepository.getComments(requestValues.qiushiId, requestValues.offset, requestValues.count));
            default:
                throw new RuntimeException("wrong action type");
        }
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private int newOrHotFlag, type, offset, count, action, jokeId, qiushiId;

        public RequestValues(int action, int newOrHotFlag, int type, int offset, int count) {
            this.newOrHotFlag = newOrHotFlag;
            this.type = type;
            this.offset = offset;
            this.count = count;
            this.action = action;
        }

        public RequestValues(int action, int jokeId) {
            this.action = action;
            this.jokeId = jokeId;
        }

        public RequestValues(int action, int qiushiId, int offset, int count) {
            this.action = action;
            this.qiushiId = qiushiId;
            this.offset = offset;
            this.count = count;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<ResponseInfo> mList;

        public ResponseValue(Observable<ResponseInfo> jokeList) {
            mList = jokeList;
        }

        public Observable<ResponseInfo> getList() {
            return mList;
        }
    }

}
