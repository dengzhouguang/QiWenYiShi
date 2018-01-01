package com.dzg.readclient.mvp.usecase;

import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.usecase.base.UseCase;
import com.dzg.readclient.repository.interfaces.Repository;

import rx.Observable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class GetLogin extends UseCase<GetLogin.RequestValues, GetLogin.ResponseValue> {
    private final Repository mRepository;

    public GetLogin(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        if (requestValues.action == Constants.REGIST)
            return new ResponseValue(mRepository.regist(requestValues.phone, requestValues.password));
        else if (requestValues.action == Constants.LOGIN)
            return new ResponseValue(mRepository.login(requestValues.phone, requestValues.password));
        else if (requestValues.action == Constants.CHECKUSEREXEIST)
            return new ResponseValue(mRepository.isExit(requestValues.phone));
        return null;
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private int action;
        private String phone, password;

        public RequestValues(int action, String phone, String password) {
            this.action = action;
            this.phone = phone;
            this.password = password;
        }

        public RequestValues(int action, String phone) {
            this.action = action;
            this.phone = phone;
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
