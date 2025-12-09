package com.dkproject.compsoe_component

import android.app.Application
import com.naver.maps.map.NaverMapSdk

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }
}