package com.example.nearbyplaces

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.nearbyplaces.databinding.ActivityMapsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import javax.security.auth.callback.PasswordCallback
import com.google.android.gms.common.api.GoogleApiClient as GoogleApiClient1

open class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient1.ConnectionCallbacks, GoogleApiClient1.OnConnectionFailedListener, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleApiClient: GoogleApiClient1
    private lateinit var locationRequest: LocationRequest
    private lateinit var lastLocation: Location
    private lateinit var currentUserLocationMarker: Marker
    private var requestUserLocationCode: Int = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLoactionPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    fun onClick(view: View){
        when(view.id){
            R.id.ic_magnify -> {
                val addressField: EditText = findViewById(R.id.location_search)
                val address: String  = addressField.text.toString()
                var addressList: List<Address>
                if(!TextUtils.isEmpty(address)){
                    val geoCoder = Geocoder(this)
                    addressList = geoCoder.getFromLocationName(address, 6) as List<Address>
                    if(addressList != null) {
                        for (i in addressList) {
                            var userAddress = i
                            val latLng = LatLng(userAddress.latitude, userAddress.longitude)
                            mMap.addMarker(MarkerOptions().position(latLng).title(address))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                        }
                    } else{
                        Toast.makeText(this, "Location Not Found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else -> {
                Toast.makeText(this, "Please Writeany Location Name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            buildingGoogleApiCLient()
            mMap.isMyLocationEnabled
            mMap.uiSettings.isMyLocationButtonEnabled
        }
    }

    private fun checkUserLoactionPermission(): Boolean{
        return if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestUserLocationCode)
            } else
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestUserLocationCode)
            false
        } else
            true
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            requestUserLocationCode -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient == null){
                            buildingGoogleApiCLient()
                        }
                        mMap.isMyLocationEnabled
                    }
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    protected fun buildingGoogleApiCLient(){
        synchronized(this) {
            googleApiClient = GoogleApiClient1.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener { this }
                .addApi(LocationServices.API)
                .build()

            googleApiClient.connect()
        }


    }

    override fun onConnected(p0: Bundle?) {
        locationRequest = LocationRequest()
        locationRequest.setInterval(1100)
        locationRequest.setFastestInterval(1100)
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title("user Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
        }

    }
}