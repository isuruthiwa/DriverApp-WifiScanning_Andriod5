package com.example.driverapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


public class HeadingFinder {

    private Double parseHeading(LatLng CurrentLocation, LatLng preLocation) {
        Double head = SphericalUtil.computeHeading(CurrentLocation, preLocation);
        if (head < 0) {
            return head + 360;
        } else return head;
    }


}
