package com.dzg.readclient.mvp.usecase;

import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.usecase.base.UseCase;
import com.dzg.readclient.repository.interfaces.Repository;

import rx.Observable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class GetFeedback extends UseCase<GetFeedback.RequestValues, GetFeedback.ResponseValue> {
    private final Repository mRepository;

    public GetFeedback(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.feedback(requestValues.content, requestValues.contact, requestValues.imgUrl));
    }

    public static final class RequestValues implements UseCase.RequestValues {
        String content, contact, imgUrl;

        public RequestValues(String content, String contact, String imgUrl) {
            this.content = content;
            this.contact = contact;
            this.imgUrl = imgUrl;
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
