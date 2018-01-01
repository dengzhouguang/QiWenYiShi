package com.dzg.readclient.mvp.usecase;

import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.model.Joke;
import com.dzg.readclient.mvp.usecase.base.UseCase;
import com.dzg.readclient.repository.interfaces.Repository;

import rx.Observable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class GetAll extends UseCase<GetAll.RequestValues, GetAll.ResponseValue> {
    private final Repository mRepository;

    public GetAll(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.getList(requestValues.newOrHotFlag, requestValues.type, requestValues.offset, requestValues.count));
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private int newOrHotFlag, type, offset, count;

        public RequestValues(int newOrHotFlag, int type, int offset, int count) {
            this.newOrHotFlag = newOrHotFlag;
            this.type = type;
            this.offset = offset;
            this.count = count;
        }

        public RequestValues(int newOrHotFlag, int count) {
            this.newOrHotFlag = newOrHotFlag;
            this.type = Joke.TYPE_QUSHI;
            this.offset = 0;
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
