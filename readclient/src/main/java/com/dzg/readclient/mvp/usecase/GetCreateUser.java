package com.dzg.readclient.mvp.usecase;

import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.usecase.base.UseCase;
import com.dzg.readclient.repository.interfaces.Repository;

import okhttp3.MultipartBody;
import rx.Observable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class GetCreateUser extends UseCase<GetCreateUser.RequestValues, GetCreateUser.ResponseValue> {
    private final Repository mRepository;

    public GetCreateUser(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {

        if (requestValues.action == Constants.SETNICKANDSEX)
            return new ResponseValue(mRepository.setNickAndSex(requestValues.userIdstr, requestValues.nickName, requestValues.sex));
        else if (requestValues.action == Constants.UPLOADPORTRAIT)
            return new ResponseValue(mRepository.uploadPortrait(requestValues.fileToUpload, requestValues.userId));
        return null;
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private int action;
        private String userIdstr, nickName, sex;
        private MultipartBody.Part fileToUpload, userId;

        public RequestValues(int action, String userIdstr, String nickName, String sex) {
            this.action = action;
            this.userIdstr = userIdstr;
            this.nickName = nickName;
            this.sex = sex;
        }

        public RequestValues(int action, MultipartBody.Part fileToUpload, MultipartBody.Part userId) {
            this.action = action;
            this.fileToUpload = fileToUpload;
            this.userId = userId;
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
