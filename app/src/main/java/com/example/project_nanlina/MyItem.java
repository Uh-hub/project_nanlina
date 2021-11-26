package com.example.project_nanlina;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
//마커 클러스터하려고
public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;

    public MyItem(double lat, double lng, String title){
        mPosition = new LatLng(lat, lng);
        mTitle = title;
    }
    @Override
    public LatLng getPosition(){
        return mPosition;
    }
    @Override
    public String getTitle(){
        return mTitle;
    }
    @Override
    public String getSnippet(){
        return null;
    }
}
