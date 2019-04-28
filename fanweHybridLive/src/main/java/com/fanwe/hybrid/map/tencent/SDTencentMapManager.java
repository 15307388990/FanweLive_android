package com.fanwe.hybrid.map.tencent;

import android.app.Application;
import android.text.TextUtils;

import com.fanwe.library.utils.SDTypeParseUtil;
import com.tencent.lbssearch.object.Location;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.geolocation.TencentLocationUtils;
import com.tencent.mapsdk.raster.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SDTencentMapManager implements TencentLocationListener
{

    private static final SDTencentMapManager sInstance = new SDTencentMapManager();

    private TencentLocationManager mLocationManager;
    private TencentLocation mLocation;
    private boolean mIsLocationStarted;
    private Map<TencentLocationListener, TencentLocationListenerInfo> mMapListener = new HashMap<>();

    private SDTencentMapManager()
    {
    }

    public static SDTencentMapManager getInstance()
    {
        return sInstance;
    }

    public void init(Application application)
    {
        this.mLocationManager = TencentLocationManager.getInstance(application);
    }

    public TencentLocation getLocation()
    {
        return mLocation;
    }

    /**
     * 获得地址
     *
     * @return
     */
    public String getAddress()
    {
        String address = null;
        if (mLocation != null)
        {
            address = mLocation.getAddress();
        }
        return address;
    }

    /**
     * 获得城市
     *
     * @return
     */
    public String getCity()
    {
        String city = null;
        if (mLocation != null)
        {
            city = mLocation.getCity();
        }
        return city;
    }

    public String getCityShort()
    {
        String city = getCity();
        if (!TextUtils.isEmpty(city))
        {
            if (city.contains("市"))
            {
                city = city.replace("市", "");
            }
        }
        return city;
    }

    /**
     * 获得区域
     *
     * @return
     */
    public String getDistrict()
    {
        String district = null;
        if (mLocation != null)
        {
            district = mLocation.getDistrict();
        }
        return district;
    }

    /**
     * 获得省
     *
     * @return
     */
    public String getProvince()
    {
        if (mLocation != null)
        {
            return mLocation.getProvince();
        } else
        {
            return null;
        }
    }

    public String getDistrictShort()
    {
        String district = getDistrict();
        if (!TextUtils.isEmpty(district))
        {
            if (district.contains("区"))
            {
                district = district.replace("区", "");
            }
        }
        return district;
    }

    public boolean hasLocationSuccess()
    {
        return hasLocationSuccess(mLocation);
    }

    public boolean hasLocationSuccess(TencentLocation location)
    {
        return this.mLocation != null;
    }

    /**
     * 纬度(ypoint)
     *
     * @return
     */
    public double getLatitude()
    {
        double latitude = 0;
        if (mLocation != null)
        {
            latitude = mLocation.getLatitude();
        }
        return latitude;
    }

    /**
     * 经度(xpoint)
     *
     * @return
     */
    public double getLongitude()
    {
        double longitude = 0;
        if (mLocation != null)
        {
            longitude = mLocation.getLongitude();
        }
        return longitude;
    }

    /**
     * 获得当前位置的LatLng
     *
     * @return
     */
    public LatLng getLatLngCurrent()
    {
        LatLng ll = null;
        double lat = getLatitude();
        double lon = getLongitude();
        if (lat != 0 && lon != 0)
        {
            ll = new LatLng(lat, lon);
        }
        return ll;
    }

    /**
     * 获得经纬度之间的距离
     *
     * @param llStart
     * @param llEnd
     * @return
     */
    public double getDistance(LatLng llStart, LatLng llEnd)
    {
        if (llStart != null && llEnd != null)
        {
            double latStart = llStart.getLatitude();
            double lngStart = llStart.getLongitude();
            double latEnd = llEnd.getLatitude();
            double lngEnd = llEnd.getLongitude();
            return TencentLocationUtils.distanceBetween(latStart, lngStart, latEnd, lngEnd);
        } else
        {
            return 0;
        }
    }

    /**
     * 获得经纬度到当前定位的距离
     *
     * @param llEnd
     * @return
     */
    public double getDistanceFromMyLocation(LatLng llEnd)
    {
        return getDistance(getLatLngCurrent(), llEnd);
    }

    /**
     * 获得我的位置和传进来的经纬度间的距离
     *
     * @param latitude  纬度(Ypoint)
     * @param longitude 经度(Xpoint)
     * @return
     */
    public double getDistanceFromMyLocation(double latitude, double longitude)
    {
        return getDistance(getLatLngCurrent(), new LatLng(latitude, longitude));
    }

    /**
     * 获得我的位置和传进来的经纬度间的距离
     *
     * @param latitude  纬度(Ypoint)
     * @param longitude 经度(Xpoint)
     * @return
     */
    public double getDistanceFromMyLocation(String latitude, String longitude)
    {
        return getDistance(getLatLngCurrent(), new LatLng(SDTypeParseUtil.getDouble(latitude, 0), SDTypeParseUtil.getDouble(longitude, 0)));
    }

    public Location latLngToLocation(LatLng ll)
    {
        Location location = null;
        if (ll != null)
        {
            location = new Location();
            location.lat = (float) ll.getLatitude();
            location.lng = (float) ll.getLongitude();
        }
        return location;
    }

    public List<LatLng> locationToLatLng(List<Location> listLocation)
    {
        List<LatLng> listLatLng = new ArrayList<LatLng>();
        for (Location location : listLocation)
        {
            listLatLng.add(new LatLng(location.lat, location.lng));
        }
        return listLatLng;
    }

    public LatLng locationToLatLng(Location location)
    {
        LatLng latLng = null;
        if (location != null)
        {
            latLng = new LatLng(location.lat, location.lng);
        }
        return latLng;
    }

    private boolean isLocationLegal(TencentLocation location)
    {
        return location != null
                && !"Unknown".equals(location.getProvince())
                && !"Unknown".equals(location.getCity());
    }

    /**
     * 定位回调监听
     *
     * @param location
     * @param error
     * @param reason
     */
    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason)
    {
        if (isLocationLegal(location) && error == TencentLocation.ERROR_OK)
        {
            this.mLocation = location;
        }

        synchronized (sInstance)
        {
            // 通知监听
            Iterator<Map.Entry<TencentLocationListener, TencentLocationListenerInfo>> it = mMapListener.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<TencentLocationListener, TencentLocationListenerInfo> item = it.next();
                TencentLocationListener listener = item.getKey();
                TencentLocationListenerInfo listenerInfo = item.getValue();

                listener.onLocationChanged(location, error, reason);
                if (listenerInfo.locationAllTheTime)
                {
                    // 需要一直定位，不移除
                } else
                {
                    it.remove();
                }
            }
            shouldStopLocation();
        }
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc)
    {
        synchronized (sInstance)
        {
            // 通知监听
            Iterator<Map.Entry<TencentLocationListener, TencentLocationListenerInfo>> it = mMapListener.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<TencentLocationListener, TencentLocationListenerInfo> item = it.next();
                TencentLocationListener listener = item.getKey();
                listener.onStatusUpdate(name, status, desc);
            }
        }
    }

    private void shouldStopLocation()
    {
        if (mMapListener.isEmpty())
        {
            stopLocation();
        }
    }

    /**
     * 创建默认的定位请求对象
     *
     * @return
     */
    public static TencentLocationRequest createDefaultLocationRequest()
    {
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(3 * 1000);
        request.setAllowCache(false);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);// default
        return request;
    }


    /**
     * 开始定位
     *
     * @param listener
     */
    public void startLocation(TencentLocationListener listener)
    {
        startLocation(false, listener);
    }


    /**
     * 开始定位
     *
     * @param locationAllTheTime true-一直定位
     * @param listener
     */
    public synchronized void startLocation(boolean locationAllTheTime, TencentLocationListener listener)
    {
        if (listener == null)
        {
            listener = mEmptyListener;
        }

        TencentLocationListenerInfo listenerInfo = new TencentLocationListenerInfo();
        listenerInfo.listener = listener;
        listenerInfo.locationAllTheTime = locationAllTheTime;
        mMapListener.put(listener, listenerInfo);

        TencentLocationRequest request = createDefaultLocationRequest();

        if (!mIsLocationStarted)
        {
            mLocationManager.requestLocationUpdates(request, this);
            mIsLocationStarted = true;
        }
    }

    /**
     * 空操作的监听对象
     */
    private TencentLocationListener mEmptyListener = new TencentLocationListener()
    {
        @Override
        public void onLocationChanged(TencentLocation tencentLocation, int i, String s)
        {

        }

        @Override
        public void onStatusUpdate(String s, int i, String s1)
        {

        }
    };

    /**
     * 停止定位
     */
    public void stopLocation()
    {
        if (mIsLocationStarted)
        {
            mLocationManager.removeUpdates(this);
            mIsLocationStarted = false;
        }

        // 不清空所有监听对象，调用者需要手动取消监听
    }

    /**
     * 定位是否启动
     *
     * @return
     */
    public boolean isLocationStarted()
    {
        return mIsLocationStarted;
    }

    /**
     * 取消监听对象
     *
     * @param listener
     */
    public synchronized void unRegisterLocationListener(TencentLocationListener listener)
    {
        if (listener == null)
        {
            return;
        }

        mMapListener.remove(listener);
        shouldStopLocation();
    }

    private class TencentLocationListenerInfo
    {
        public TencentLocationListener listener;
        public boolean locationAllTheTime;
    }

}
