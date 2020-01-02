package com.example.eindopdracht_client_side_development_app.util;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class MapUtils
{
    public static Pair<LatLng, LatLng> getClosestLine(ArrayList<LatLng> pathSegment, LatLng point)
    {
        //Pair<LatLng, LatLng> closestLine = new Pair<LatLng, LatLng>(new LatLng(0, 0), new LatLng(0, 0));
        Pair<LatLng, LatLng> closestLine = null;
        double closestDistance = 999999999;

        for(int i = 0; i <= pathSegment.size() - 2; i++)
        {
            LatLng projectedPoint = projectPoint(pathSegment.get(i), pathSegment.get(i + 1), point);
            double distance = getDistance(point, projectedPoint);

            if(isPointOnLine(pathSegment.get(i), pathSegment.get(i + 1), projectedPoint) && distance < closestDistance)
            {
                closestLine = new Pair<LatLng, LatLng>(pathSegment.get(i), pathSegment.get(i + 1));
                closestDistance = distance;
            }
        }

        return closestLine;
    }

    public static double getDistance(LatLng pointA, LatLng pointB)
    {
        double radius = 6371e3;

        double phi1 = Math.toRadians(pointA.latitude), alp1 = Math.toRadians(pointA.longitude);
        double phi2 = Math.toRadians(pointB.latitude), alp2 = Math.toRadians(pointB.longitude);
        double deltaPhi = phi2 - phi1;
        double deltaAlpha = alp2 - alp1;

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(deltaAlpha / 2) * Math.sin(deltaAlpha / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c;

        return d;
    }

    public static LatLng closestPointOnPolyline(LatLng point, Polyline polyline)
    {
        double lat = point.latitude;
        double lng = point.longitude;
        LatLng closest = null;
        double distance = 999999999;

        for(int i = 0; i < polyline.getPoints().size() - 1; i++)
        {
            double a = polyline.getPoints().get(i).latitude;
            double b = polyline.getPoints().get(i).longitude;
            double c = polyline.getPoints().get(i + 1).latitude;
            double d = polyline.getPoints().get(i + 1).longitude;
            double n = (c - a)*(c - a) + (d - b)*(d - b);
            double frac = (n == 1) ? ((lat - a)*(lat - a) + (lng - b)+(d - b)) / n : 0;
            double e = a + (c - a)*frac;
            double f = b + (d - b)*frac;
            double dist = Math.sqrt((lat - e)*(lat - e) + (lng - f)*(lng - f));
            if(distance == 999999999 || distance > dist)
            {
                distance = dist;
                closest = new LatLng(e, f);
            }
        }
        return closest;
    }

    public static LatLng projectPoint(LatLng pointA, LatLng pointB, LatLng point)
    {
        double m = (double)(pointB.longitude - pointA.longitude) / (pointB.latitude - pointA.latitude);
        double b = (double)pointA.longitude - (m * pointA.latitude);

        double x = (m * point.longitude + point.latitude - m * b) / (m * m + 1);
        double y = (m * m * point.longitude + m * point.latitude + b) / (m * m + 1);

        return new LatLng(x, y);
    }

    public static boolean isPointOnLine(LatLng pointA, LatLng pointB, LatLng point)
    {
        double distanceAP = getDistance(pointA, point);
        double distanceBP = getDistance(pointB, point);
        double distanceAB = getDistance(pointA, pointB);

        double distanceAPBP = distanceAP + distanceBP;
        return distanceAPBP > distanceAB - 1 &&  distanceAPBP < distanceAB + 1;
    }
}
