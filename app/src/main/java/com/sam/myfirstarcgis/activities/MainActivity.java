package com.sam.myfirstarcgis.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.io.EsriSecurityException;
import com.esri.core.io.OnSelfSignedCertificateListener;
import com.esri.core.io.SelfSignedCertificateHandler;
import com.esri.core.io.UserCredentials;
import com.sam.myfirstarcgis.Utils;

import com.sam.myfirstarcgis.R;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    private static final int INTERNET_PERMISSION = 1;

    private static final String TILED_WORLD_STREETS_URL = "https://www.arcgis.com/home/webmap/viewer.html?useExisting=1&layers=144ab9ff2f2b42d09640b5f8307fb85d";
    //private static final String TILED_WORLD_STREETS_URL = "http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer";
    //private static final String DYNAMIC_USA_HIGHWAY_URL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Specialty/ESRI_StateCityHighway_USA/MapServer";



    MapView mMapView = null;
    ArcGISDynamicMapServiceLayer mStreetsLayer = null;
    ArcGISTiledMapServiceLayer fl = null;

    //ArcGISFeatureLayer fl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trySomething();

        // Retrieve the MapView from XML layout.
        mMapView = (MapView) findViewById(R.id.map);

        SelfSignedCertificateHandler.setOnSelfSignedCertificateListener(
                new OnSelfSignedCertificateListener() {
                    @Override
                    public boolean checkServerTrusted(X509Certificate[] chain, String authType) {
                        try {
                            chain[0].checkValidity();
                        } catch (Exception e) {
                            return true;
                        }

                        return true;
                    }
                });

        // Add a base map layer to the MapView.
        fl = new ArcGISTiledMapServiceLayer(TILED_WORLD_STREETS_URL);
        mMapView.addLayer(fl);

        // Create a second layer, this time using a US Highways and Streets map service.
        // Set an instance variable to this layer so it can be used from within
        // a long press listener.
       /* mStreetsLayer = new ArcGISDynamicMapServiceLayer(
                DYNAMIC_USA_HIGHWAY_URL);

        mStreetsLayer.setOpacity(0.5f);

        // Add the dynamic layer to the MapView.
        mMapView.addLayer(mStreetsLayer);*/

        /*
        fl = new ArcGISFeatureLayer(DYNAMIC_myURL, ArcGISFeatureLayer.MODE.ONDEMAND);
        mMapView.addLayer(fl);*/

        // Handle status change event on MapView
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, OnStatusChangedListener.STATUS status) {
                // Check if a layer is failed to be loaded due to security
                if (status == OnStatusChangedListener.STATUS.LAYER_LOADING_FAILED) {
                    if ((status.getError()) instanceof EsriSecurityException) {
                        EsriSecurityException securityEx = (EsriSecurityException) status.getError();
                        if (securityEx.getCode() == EsriSecurityException.AUTHENTICATION_FAILED)

                            Toast.makeText(mMapView.getContext(), "Authentication Failed!",
                                    Toast.LENGTH_SHORT).show();
                        else if (securityEx.getCode() == EsriSecurityException.TOKEN_INVALID)
                            Toast.makeText(mMapView.getContext(), "Invalid Token!", Toast.LENGTH_SHORT).show();
                        else if (securityEx.getCode() == EsriSecurityException.TOKEN_SERVICE_NOT_FOUND)
                            Toast.makeText(mMapView.getContext(), "Token Service Not Found!",
                                    Toast.LENGTH_SHORT).show();
                        else if (securityEx.getCode() == EsriSecurityException.UNTRUSTED_SERVER_CERTIFICATE)
                            Toast.makeText(mMapView.getContext(), "Untrusted Host!", Toast.LENGTH_SHORT).show();

                        if (source instanceof ArcGISFeatureLayer) {
                            // Set user credential through username and password
                            UserCredentials creds = new UserCredentials();
                            creds.setUserAccount("username", "password");
                            fl.reinitializeLayer(creds);
                        }
                    }
                }
            }
        });


    }

    private void trySomething() {
        if (Utils.hasStoragePermission(getApplicationContext())) {
            initializeSomething();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == INTERNET_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSomething();
            } else {
                Utils.showToast(getApplicationContext(), R.string.no_permissions);
                finish();
            }
        }
    }

    private void initializeSomething() {
        Utils.showToast(getApplicationContext(), R.string.init_sth);
    }


}
