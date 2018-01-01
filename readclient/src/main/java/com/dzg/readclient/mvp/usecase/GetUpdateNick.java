package com.dzg.readclient.mvp.usecase;

import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.usecase.base.UseCase;
import com.dzg.readclient.repository.interfaces.Repository;

import rx.Observable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class GetUpdateNick extends UseCase<GetUpdateNick.RequestValues, GetUpdateNick.ResponseValue> {
    private final Repository mRepository;

    public GetUpdateNick(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.setNickAndSex(requestValues.userIdstr, requestValues.nickName, requestValues.sex));
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private String userIdstr, nickName, sex;

        public RequestValues(String userIdstr, String nickName, String sex) {
            this.userIdstr = userIdstr;
            this.nickName = nickName;
            this.sex = sex;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<ResponseInfo> mResponseInfo;

        public ResponseValue(Observable<ResponseInfo> response) {
            mResponseInfo = response;
        }

        public Observable<ResponseInfo> getResponseInfo() {
            return mResponseInfo;
        }
    }

}
