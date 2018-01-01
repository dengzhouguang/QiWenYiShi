package com.dzg.readclient.mvp.usecase;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.commons.ResponseInfo;
import com.dzg.readclient.mvp.usecase.base.UseCase;
import com.dzg.readclient.repository.interfaces.Repository;

import rx.Observable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class GetQuShiDetail extends UseCase<GetQuShiDetail.RequestValues, GetQuShiDetail.ResponseValue> {
    private final Repository mRepository;

    public GetQuShiDetail(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        if (requestValues.action == Constants.GETJOKEBYID)
            return new ResponseValue(mRepository.getJokeById(requestValues.jokeId));
        else if (requestValues.action == Constants.GETCOMMENTBYJOKEID)
            return new ResponseValue(mRepository.getComments(requestValues.jokeId, requestValues.offset, requestValues.count));
        else if (requestValues.action == Constants.ADDCOMMENT)
            return new ResponseValue(mRepository.postComment(requestValues.jokeId,
                    ReadClientApp.getInstance().currentUser.getId(),
                    ReadClientApp.getInstance().currentUser.getPortraitUrl(),
                    ReadClientApp.getInstance().currentUser.getUserNike(),
                    requestValues.content));
        return null;
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private int action, jokeId, offset, count;
        private String content;

        public RequestValues(int action, int jokeId, int offset, int count) {
            this.action = action;
            this.jokeId = jokeId;
            this.offset = offset;
            this.count = count;
        }

        public RequestValues(int action, int jokeId) {
            this.action = action;
            this.jokeId = jokeId;
        }

        public RequestValues(int action, int jokeId, String content) {
            this.action = action;
            this.jokeId = jokeId;
            this.content = content;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<ResponseInfo> responseInfo;

        public ResponseValue(Observable<ResponseInfo> responseInfo) {
            this.responseInfo = responseInfo;
        }

        public Observable<ResponseInfo> getResponseInfo() {
            return responseInfo;
        }
    }

}
