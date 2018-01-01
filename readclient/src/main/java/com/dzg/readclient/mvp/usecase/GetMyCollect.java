package com.dzg.readclient.mvp.usecase;

import android.content.Context;

import com.dzg.readclient.dao.CollectDAO;
import com.dzg.readclient.mvp.model.Collect;
import com.dzg.readclient.mvp.usecase.base.UseCase;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public class GetMyCollect extends UseCase<GetMyCollect.RequestValues, GetMyCollect.ResponseValue> {
    Context mContext;

    public GetMyCollect(Context context) {
        this.mContext = context;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(new CollectDAO(mContext).getCollects(requestValues.userId));
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private int userId;

        public RequestValues(int userId) {
            this.userId = userId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Collect> collect;

        public ResponseValue(List<Collect> collect) {
            this.collect = collect;
        }

        public List<Collect> getCollects() {
            return collect;
        }
    }

}
