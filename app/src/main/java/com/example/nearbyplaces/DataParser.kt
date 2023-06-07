package com.example.nearbyplaces

import org.json.JSONArray
import org.json.JSONObject

class DataParser {
    private fun getSinglePlace(googlePlaceJson: JSONObject):HashMap<String, String> {
        var googlePlaceMap : HashMap<String, String> = HashMap()
        var nameOfPlace = "-NA-"
        var vicinity = "-NA-"
        var latitude = ""
        var longitude = ""
        var reference = ""
        if(!googlePlaceJson.isNull("name"))  nameOfPlace = googlePlaceJson.getString("name")
        if(!googlePlaceJson.isNull("vicinity"))  vicinity = googlePlaceJson.getString("vicinity")
        latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat")
        longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng")
        reference = googlePlaceJson.getString("reference")
        googlePlaceMap.put("place_name", nameOfPlace)
        googlePlaceMap.put("vicinity", vicinity)
        googlePlaceMap.put("lat", latitude)
        googlePlaceMap.put("lng", longitude)
        googlePlaceMap.put("reference", reference)

        return googlePlaceMap
    }
    private fun getAllNearbyPlaces(googlePlaceJsonArray: JSONArray): List<HashMap<String, String>> {
        val counter = googlePlaceJsonArray.length()
        val nearbyPlacesList = mutableListOf<HashMap<String, String>>()
        var nearbyPlaceMap : HashMap<String, String>? = null
        for (i in 0 until counter){
            nearbyPlaceMap = getSinglePlace(googlePlaceJsonArray.get(i) as JSONObject)
            nearbyPlacesList.add(nearbyPlaceMap)
        }
        return nearbyPlacesList
    }
    fun parse(jsonData: String):List<HashMap<String, String>>{
        var jsonArray: JSONArray? = null
        val jsonObject = JSONObject(jsonData)
        jsonArray = jsonObject.getJSONArray("results")
        return getAllNearbyPlaces(jsonArray)
    }
}