package com.greyogproducts.greyog.lechmap

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.xmlpull.v1.XmlPullParser
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback{


    private lateinit var mMap: GoogleMap

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    private lateinit var fusedLoacationClient: FusedLocationProviderClient

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_menu, menu)
        val viewAsListMI = menu?.findItem(R.id.view_as_list)
        viewAsListMI?.setOnMenuItemClickListener {
            launchListActivity()
            return@setOnMenuItemClickListener false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLoacationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation

            }
        }
        createLocationRequest()

    }

    private class Receiver(val map: GoogleMap, val markers: MutableList<Marker>) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val name = intent?.extras?.getString("name")
            val marker = markers.find { it.title == name }
            marker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(marker?.position))
        }

    }

    private lateinit var lastLocation: Location

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
        mMap.setOnInfoWindowClickListener {
            launchStoneActivity(it)
        }
        val filter = IntentFilter("com.greyogproducts.greyog.lechmap.SET_MAP_FOCUS")
        registerReceiver(Receiver(mMap, markerList), filter)

//        mMap.setOnMarkerClickListener {
//            if (it.isInfoWindowShown) {
//                launchStoneActivity(it)
//            }
//            return@setOnMarkerClickListener false
//        }
        // Add a marker in Sydney and move the camera
//        val lechinkai = LatLng(43.5622, 43.4267)
//        mMap.addMarker(MarkerOptions().position(lechinkai).title("Marker in Lechinkai"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lechinkai, 15.0f))
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
//        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        setUpMap()
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        fusedLoacationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                return@addOnSuccessListener
            }
            lastLocation = location
            val currentLatLng = LatLng(location.latitude, location.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
        }

        loadMarkers()
    }

    private fun launchStoneActivity(it: Marker) {
        val intent = Intent(this, StoneActivity::class.java)
        intent.putExtra("stoneNumber", it.title)
        this.startActivity(intent)
    }

    private fun launchListActivity() {
        val intent = Intent(this, ListActivity::class.java)
//        intent.putExtra("stoneNumber", it.title)
        this.startActivity(intent)

    }

    private val markerList = emptyList<Marker>().toMutableList()

    private fun loadMarkers() {
        val hm = HashMap<String, String>()
        var curName = ""
        val parser = resources.getXml(R.xml.coords)
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
//            println("name: ${parser.name}, text: ${parser.text}, eventType: ${parser.eventType}")
            if (parser.name == "name")
                curName = parser.nextText()
            if (parser.name == "coordinates")
                hm[curName] = parser.nextText()
            parser.next()
        }
//        println(hm)
        hm.keys.forEach {
            val temp = hm[it]?.split(",")
            val lon = temp?.get(0)?.toDouble()
            val lat = temp?.get(1)?.toDouble()
            if (lat != null && lon != null) {
                val ll = LatLng(lat, lon)
//                addStoneMarker(ll, it)
                val marker = mMap.addMarker(MarkerOptions().position(ll).title(it))
                markerList.add(marker)
            }

        }
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            fusedLoacationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private var locationUpdateState: Boolean = true

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this@MapsActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    //ignore
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLoacationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun addStoneMarker(latLng: LatLng, title: String) {
        val conf = Bitmap.Config.ARGB_8888
        val bmp = Bitmap.createBitmap(60, 60, conf)
        val canvas1 = Canvas(bmp)

// paint defines the text color, stroke width and size
        val color = Paint()
        color.textSize = 35f
        color.color = Color.WHITE

// modify canvas

        canvas1.drawBitmap(BitmapFactory.decodeResource(resources,
                R.drawable.marker_only), 0f, 0f, color)
        canvas1.drawText(title, 20f, 20f, color)

// add marker to Map
        mMap.addMarker(MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                // Specifies the anchor to be at a particular point in the marker image.
//                .anchor(0.5f, 1f)
        )
    }

}
