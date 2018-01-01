package com.dzg.readclient.injector.module;

import com.dzg.readclient.ReadClientApp;
import com.dzg.readclient.commons.Constants;
import com.dzg.readclient.injector.scope.PerApplication;
import com.dzg.readclient.repository.RepositoryImpl;
import com.dzg.readclient.repository.interfaces.Repository;
import com.dzg.readclient.utils.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/6/22.
 */
@Module
public class NetworkModule {
    private final ReadClientApp mReadClient;
    public NetworkModule(ReadClientApp app){
        mReadClient=app;
    }
    @Provides
    @PerApplication
    Repository provideRepository(@Named("JokeRetrofit")Retrofit jokeRepo){
        return new RepositoryImpl(mReadClient,jokeRepo);}
    @Provides
    @Named("JokeRetrofit")
    @PerApplication
    Retrofit provideJokeRetrofit(){
        String endpointUrl = Constants.BASE_API_URL;
        Gson gson = new GsonBuilder().create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new Cache(FileUtil.getHttpCacheDir(ReadClientApp.getContext()), Constants.HTTP_CACHE_SIZE))
                .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(Constants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpointUrl)
                .client(client)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }
}
