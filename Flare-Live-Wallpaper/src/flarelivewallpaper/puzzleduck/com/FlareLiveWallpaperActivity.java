package flarelivewallpaper.puzzleduck.com;

import android.app.Activity;
import android.os.Bundle;

public class FlareLiveWallpaperActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}



//target LWP manifest:

//<?xml version="1.0" encoding="utf-8"?>
//<!--
///* 
//**
//** Copyright 2009, The Android Open Source Project
//**
//** Licensed under the Apache License, Version 2.0 (the "License");
//** you may not use this file except in compliance with the License.
//** You may obtain a copy of the License at
//**
//**     http://www.apache.org/licenses/LICENSE-2.0
//**
//** Unless required by applicable law or agreed to in writing, software
//** distributed under the License is distributed on an "AS IS" BASIS,
//** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//** See the License for the specific language governing permissions and
//** limitations under the License.
//*/
//-->
//<manifest
//    android:versionName="2" 
//    package="com.puzzleduck.targetLiveWallpaper" 
//    android:versionCode="2" 
//    xmlns:android="http://schemas.android.com/apk/res/android">
//    <uses-sdk android:targetSdkVersion="9" 
//        android:minSdkVersion="7"/>
//    <uses-feature android:name="android.software.live_wallpaper" />
//
//    
//    
//    <application
//        android:label="@string/target_wallpaper_application_label"
//        android:icon="@drawable/target_lwp_icon" 
//        android:debuggable="false"
//        android:enabled="true">
//   		        
//        
//        <activity android:label="@string/target_wallpaper_activity_label" 
//        	android:name=".TargetLiveActivity" 
//        	android:icon="@drawable/target_lwp_icon"
//        	 android:enabled="true"
//        	 android:exported="true"
//        	 android:process="com.puzzleduck.targetLiveWallpaper.TargetLiveActivity">
//            <intent-filter>
//                <action android:name="android.intent.action.MAIN" />
//                <category android:name="android.intent.category.LAUNCHER" />
//            </intent-filter>
//    
//        </activity>
//
//        <service
//            android:label="@string/target_wallpaper_service_label"
//            android:permission="android.permission.BIND_WALLPAPER"
//            android:name="TargetLiveWallpaper">
//            <intent-filter>
//                <action android:name="android.service.wallpaper.WallpaperService" />
//            </intent-filter>
//            <meta-data android:name="android.service.wallpaper" android:resource="@xml/target_lwp" />
//        </service>
//        <activity
//            android:label="@string/target_settings"
//            android:name="TargetLiveWallpaperSettings"
//            android:theme="@android:style/Theme.Light.WallpaperSettings"
//            android:exported="true"
//            android:layout="@layout/activity">
//        </activity>
//        
//        
//
//    </application>
//
//</manifest>


