package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for [Manifest.permission.ACCESS_FINE_LOCATION] and [Manifest.permission.ACCESS_COARSE_LOCATION]
 * are requested at run time. If either permission is not granted, the Activity is finished with an error message.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLocClient()

        

    }

    private lateinit var fusedLocClient: FusedLocationProviderClient
    // use it to request location updates and get the latest location

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap //initialise map
        // Position for reference
        addMarkersHospital()

        getCurrentLocation()
    }
    private fun setupLocClient() {
        fusedLocClient =
            LocationServices.getFusedLocationProviderClient(this)
    }

    // prompt the user to grant/deny access
    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION)
    }

    companion object {
        private const val REQUEST_LOCATION = 1 //request code to identify specific permission request
        private const val TAG = "MapsActivity" // for debugging
    }

    private fun addMarkersHospital() {
        // Agregamos los hospitales a mano (solo por esta entrega)
        val enmanuel = LatLng(-33.491521753615245, -70.616164983397)
        map.addMarker(MarkerOptions().position(enmanuel).title("Centro Medico Enmanuel y Cia"))

        val galm = LatLng(-33.493245474913344, -70.61480592251705)
        map.addMarker(MarkerOptions().position(galm).title("GALM Macul"))

        val centrosj = LatLng(-33.49702815888123, -70.6149152530082)
        map.addMarker(MarkerOptions().position(centrosj).title("Centro Medico San Joaquín"))

        val clinicauc = LatLng(-33.49512197009499, -70.60762109403308)
        map.addMarker(MarkerOptions().position(clinicauc).title("Clinica UC"))

        val buenasalud = LatLng(-33.48820226926813, -70.59770345582156)
        map.addMarker(MarkerOptions().position(buenasalud).title("Clínica Hogar Buena Salud"))

        val sanjuan = LatLng(-33.485181069119996, -70.59305125801616)
        map.addMarker(MarkerOptions().position(sanjuan).title("Orden Hospitalaria Hnos de San Juan de Dios"))

        val hogarancianos = LatLng(-33.4906790594855, -70.59125335846853)
        map.addMarker(MarkerOptions().position(hogarancianos).title("Hogar De Ancianos El Atardecer"))
    }

    private fun getCurrentLocation() {
        // Check if the ACCESS_FINE_LOCATION permission was granted before requesting a location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // call requestLocPermissions() if permission isn't granted
            requestLocPermissions()
        } else {

            fusedLocClient.lastLocation.addOnCompleteListener {
                // lastLocation is a task running in the background
                val location = it.result //obtain location
                //reference to the database
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val ref: DatabaseReference = database.getReference("test")
                if (location != null) {

                    val latLng = LatLng(location.latitude, location.longitude)
                    // create a marker at the exact location
                    map.addMarker(
                        MarkerOptions().position(latLng)
                        .title("You are currently here!"))
                    // create an object that will specify how the camera will be updated
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                    map.moveCamera(update)
                    //Save the location data to the database
                    ref.setValue(location)
                } else {
                    // if location is null , log an error message
                    Log.e(TAG, "No location found")
                }



            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //check if the request code matches the REQUEST_LOCATION
        if (requestCode == REQUEST_LOCATION)
        {
            //check if grantResults contains PERMISSION_GRANTED.If it does, call getCurrentLocation()
            if (grantResults.size == 1 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                //if it doesn`t log an error message
                Log.e(TAG, "Location permission has been denied")
            }
        }
    }

}