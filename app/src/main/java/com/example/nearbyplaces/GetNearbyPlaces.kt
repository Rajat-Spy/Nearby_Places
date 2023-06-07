package com.example.nearbyplaces

import android.os.AsyncTask
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GetNearbyPlaces: AsyncTask<Object, String, String>() {

    private lateinit var googlePlaceData: String
    private lateinit var mMap: GoogleMap
    private lateinit var url: String


    override fun doInBackground(vararg p0: Object?): String {
        mMap = p0[0] as GoogleMap
        url = p0[1] as String
        lateinit var downloadUrl: DownloadUrl
        googlePlaceData = downloadUrl.readTheUrl(url)
        return googlePlaceData
    }

    override fun onPostExecute(result: String) {
        var nearbyPlacesList: List<HashMap<String, String>>? = null
        lateinit var dataParser: DataParser
        nearbyPlacesList = dataParser.parse(result)

        displayNearByPlaces(nearbyPlacesList)

    }
    private fun displayNearByPlaces(nearbyPlacesList: List<HashMap<String, String>>){
        for(i in nearbyPlacesList.indices){
            var googleNearbyPlace : HashMap<String, String> = nearbyPlacesList.get(i)
            val nameOfPlace = googleNearbyPlace["place_name"]
            val vicinity = googleNearbyPlace["vicinity"]
            val lat: Double = googleNearbyPlace["lat"]?.toDouble()!!
            val lng: Double = googleNearbyPlace["lng"]?.toDouble()!!
            val latLng = LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(latLng).title("$nameOfPlace : $vicinity"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
        }
    }
}