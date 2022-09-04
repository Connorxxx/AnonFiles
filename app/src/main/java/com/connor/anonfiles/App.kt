package com.connor.anonfiles

import android.app.Application
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.connor.anonfiles.converter.SerializationConverter
import com.connor.anonfiles.di.appModule
import com.drake.brv.utils.BRV
import com.drake.engine.base.Engine
import com.drake.net.NetConfig
import com.drake.net.cookie.PersistentCookieJar
import com.drake.net.interceptor.LogRecordInterceptor
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDebug
import com.tencent.mmkv.MMKV
import okhttp3.Cache
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        MMKV.initialize(this)
        Engine.initialize(this)
        BRV.modelId = BR.m

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }

        NetConfig.initialize("https://api.anonfiles.com/", this) {

            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)

            cache(Cache(cacheDir, 1024 * 1204 * 128))

            setDebug(BuildConfig.DEBUG)

            addInterceptor(LogRecordInterceptor(BuildConfig.DEBUG))

            cookieJar(PersistentCookieJar(this@App))

            if (BuildConfig.DEBUG) {
                addInterceptor(
                    ChuckerInterceptor.Builder(this@App)
                        .collector(ChuckerCollector(this@App))
                        .maxContentLength(250000L)
                        .redactHeaders(emptySet())
                        .alwaysReadResponseBody(false)
                        .build()
                )
            }

            setConverter(SerializationConverter())
        }
    }
}