package com.example.jayghodasara.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, LocationListener {
    override fun onConnectionSuspended(p0: Int) {

    }

    lateinit var locationreq:LocationRequest
    private lateinit var mMap: GoogleMap
    lateinit var googleClient:GoogleApiClient
    lateinit var loc:Location
     var marker:Marker?= null
    lateinit var geocoder:Geocoder
     var latlng:LatLng?=null
    lateinit var destination:LatLng

    override fun onConnectionFailed(p0: ConnectionResult) {
      Log.i("failed","in")
    }


    override fun onConnected(p0: Bundle?) {
        locationreq= LocationRequest()
        locationreq.interval = 1000
        Log.i("Called","in onconnected")
        locationreq.fastestInterval = 1000
        locationreq.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if(ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i("check","in onconnected")
            LocationServices.FusedLocationApi.requestLocationUpdates(googleClient,locationreq,this)
       }


    }



    override fun onLocationChanged(location: Location?) {

       loc= location!!
//        if(marker == null){
//            marker!!.remove()
//        }

     latlng= LatLng(location.latitude,location.longitude)

        var markerOptions:MarkerOptions= MarkerOptions()
        markerOptions.position(latlng!!)
        markerOptions.title("My Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        marker=mMap.addMarker(markerOptions)
        Log.i("Move","animated")
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
    }


    fun checklocationpermission():Boolean {

        return if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
            }
            false

        } else
            true

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){

            99->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){


                    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                        if (googleClient != null) {
                            createClient()
                        }
                        mMap.isMyLocationEnabled = true
                    }
                }else{
                    Toast.makeText(applicationContext," Permission Denied",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        geocoder= Geocoder(this)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checklocationpermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn.setOnClickListener(View.OnClickListener {
            var add:String=address.text.toString()

            var list:List<Address> = geocoder.getFromLocationName(add,1)
            var address:Address= list[0]

            var lat= address.latitude
            var lon= address.longitude
            destination= LatLng(lat,lon)

            var markerOptions:MarkerOptions= MarkerOptions()
            markerOptions.title(add)
            markerOptions.position(destination)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

            mMap.addMarker(markerOptions)

            mMap.animateCamera(CameraUpdateFactory.zoomBy(20F))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(destination))

  // var line:Polyline=mMap.addPolyline(PolylineOptions().add(latlng,destination).width(5F).color(Color.RED))

        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Toast.makeText(applicationContext,"OnMapReady",Toast.LENGTH_LONG).show()

     if(ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

         createClient()
         mMap.isMyLocationEnabled=true


     }



    }

    fun createClient(){
        synchronized(this){
            Log.i("Client","created")
            googleClient=GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
            googleClient.connect()
        }
    }
}
