package com.bj.yatu.twodimension.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by admin on 2017/4/5.
 */

public class GPSInfoProvider {
    LocationManager manager;
    //单例
    private static GPSInfoProvider mGPSInfoProvider;
    private static Context context;
    //单例
    private static MyLoactionListener listener;
    //1.私有化构造方法

    private GPSInfoProvider() {
    }

    ;

    //2. 提供一个静态的方法 可以返回他的一个实例   保证代码必须执行完成 放在 synchronized  单态的GPSInfoProvider
    public static synchronized GPSInfoProvider getInstance(Context context) {
        if (mGPSInfoProvider == null) {
            mGPSInfoProvider = new GPSInfoProvider();
            GPSInfoProvider.context = context;
        }
        return mGPSInfoProvider;
    }


    // 获取gps 信息
    public String getLocation() {
        //获取与位置相关的服务  服务都是通过上下文获取出来的
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //获取所有的定位方式
        //manager.getAllProviders(); // gps //wifi //
        //选择一种目前状态下最好的定位方式
        String provider = getProvider(manager);
        // 注册位置的监听器
        /**
         *provider 定位方式 用什么设备定位  基站 网络 GPS AGPS
         *时间 gps 多长时间重新获取一下位置  最小为1分钟
         *位置 最短位移 位置改变多少 重新获取一下位置
         *listener 位置发生改变时 对应的回调方法
         */
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        manager.requestLocationUpdates(provider, 1000, 1, getListener());

        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String location = sp.getString("location", "");
        return location;
    }




    // 停止gps监听
    public void stopGPSListener(){
        //参数为LocationListener
        manager.removeUpdates(getListener());
    }

    //返回Listener实例
    private synchronized MyLoactionListener getListener(){
        if(listener==null){
            listener = new MyLoactionListener();
        }
        return listener;
    }

    private class MyLoactionListener implements LocationListener {

        /**
         * 当手机位置发生改变的时候 调用的方法
         */
        public void onLocationChanged(Location location) {
            String latitude =location.getLatitude()+""; //weidu
            String longtitude =location.getLongitude()+""; //jingdu
            SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("location", latitude+" - "+ longtitude);
            editor.commit(); //最后一次获取到的位置信息 存放到sharedpreference里面
        }

        /**
         * 某一个设备的状态发生改变的时候 调用 可用->不可用  不可用->可用                   GPS是否可用
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * 某个设备被打开   GPS被打开
         */
        public void onProviderEnabled(String provider) {

        }

        /**某个设备被禁用     GPS被禁用
         *
         */
        public void onProviderDisabled(String provider) {

        }

    }

    /**
     *
     * @param manager 位置管理服务
     * @return 最好的位置提供者    // gps //wifi //
     */
    private String getProvider(LocationManager manager){
        //一组查询条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //获取精准位置、
        criteria.setAltitudeRequired(false);//对海拔不敏感
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);//耗电量中等
        criteria.setSpeedRequired(true);//速度变化敏感
        criteria.setCostAllowed(true);//产生开销  通信费用
        //返回最好的位置提供者   true 表示只返回当前已经打开的定位设备
        return  manager.getBestProvider(criteria, true);
    }
}
