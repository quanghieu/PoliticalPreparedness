package com.example.android.politicalpreparedness.representative

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_representative.*
import java.util.*


class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
        private val REQUEST_ACCESS_FINE_LOCATION = 1
        private val REQUEST_TURN_DEVICE_LOCATION_ON = 2
        private val KEY_PROGRESS = "KEY_PROGRESS"
        val states = arrayOf(
            "Alaska",
            "Alabama",
            "Arkansas",
            "American Samoa",
            "Arizona",
            "California",
            "Colorado",
            "Connecticut",
            "District of Columbia",
            "Delaware",
            "Florida",
            "Georgia",
            "Guam",
            "Hawaii",
            "Iowa",
            "Idaho",
            "Illinois",
            "Indiana",
            "Kansas",
            "Kentucky",
            "Louisiana",
            "Massachusetts",
            "Maryland",
            "Maine",
            "Michigan",
            "Minnesota",
            "Missouri",
            "Mississippi",
            "Montana",
            "North Carolina",
            "North Dakota",
            "Nebraska",
            "New Hampshire",
            "New Jersey",
            "New Mexico",
            "Nevada",
            "New York",
            "Ohio",
            "Oklahoma",
            "Oregon",
            "Pennsylvania",
            "Puerto Rico",
            "Rhode Island",
            "South Carolina",
            "South Dakota",
            "Tennessee",
            "Texas",
            "Utah",
            "Virginia",
            "Virgin Islands",
            "Vermont",
            "Washington",
            "Wisconsin",
            "West Virginia",
            "Wyoming"
        )
    }

    //TODO: Declare ViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    private val viewModel: RepresentativeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater)

        binding.state.adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, states)
        //TODO: Define and assign Representative adapter
        val representativeAdapter = RepresentativeListAdapter()
        binding.representativeList.adapter = representativeAdapter
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.representativeList
        viewModel.listOfRepresentative.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val representativeTitleHeight = binding.representativeTitle.height
                val params = recyclerView.layoutParams
                Log.d("HIEU", "Full screen ${Resources.getSystem().displayMetrics.heightPixels}, title ${representativeTitleHeight}, toolbar hight ${getToolBarHeight()}")
                params.height = Resources.getSystem().displayMetrics.heightPixels - (2 * representativeTitleHeight) - getToolBarHeight()
                recyclerView.layoutParams = params
            }
            representativeAdapter.submitList(it)
        })

//        viewModel.status.observe(viewLifecycleOwner, Observer {
//            loadingStatus(binding.loadingAnim, it)
//        })


        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        binding.buttonLocation.setOnClickListener {
            onBtnLocationClick()
        }

        val motionLayout = binding.motionLayout




        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val offset = recyclerView.computeVerticalScrollOffset().toFloat()
                val range = recyclerView.computeVerticalScrollRange().toFloat()
                val extent = recyclerView.computeVerticalScrollExtent().toFloat()
                if (range == 0f)
                    return
                val progress = (offset / (range - extent))
                Log.d("HIEU", "offset ${offset}, range is ${range}, extent is ${extent}")
                Log.d("HIEU", "progress is set ${progress}")
                motionLayout.progress = progress
            }
        })
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentatives(binding)
        }
        return binding.root
    }

    open fun getToolBarHeight(): Int {
        val attrs = intArrayOf(R.attr.actionBarSize)
        val ta = context!!.obtainStyledAttributes(attrs)
        val toolBarHeight = ta.getDimensionPixelSize(0, -1)
        ta.recycle()
        return toolBarHeight
    }

    private fun onBtnLocationClick() {
        if (checkLocationPermissions()) {
            checkDeviceLocationSettingsAndGetLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //TODO: Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Snackbar.make(
                    binding.root,
                    "You need to grant location permission to use this feature",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                checkDeviceLocationSettingsAndGetLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            if (resultCode != Activity.RESULT_OK) {
                Snackbar.make(
                    binding.root,
                    "You need to enable device location to use this feature",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                getLocation()
            }
        }

    }

    private fun checkDeviceLocationSettingsAndGetLocation(
        resolve: Boolean = true
    ) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(context!!)
        val locationSettingResponseTask = settingsClient.checkLocationSettings(builder.build())
        locationSettingResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("HIEU", "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Log.d("HIEU", "Location is not enabled")
                Snackbar.make(
                    binding.root,
                    "You need to enable device location to use this feature", Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
        locationSettingResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("HIEU", "Complete Location Settings Listener")
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        if (ActivityCompat.checkSelfPermission(
                context!!,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        Toast.makeText(context!!, "Getting Location", Toast.LENGTH_SHORT).show()
        val fusedLocationClient = FusedLocationProviderClient(context!!)
        val locationResult = fusedLocationClient.lastLocation
        locationResult.addOnCompleteListener(activity!!, object : OnCompleteListener<Location> {
            override fun onComplete(task: Task<Location>) {
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) {
                        val address = geoCodeLocation(location)
                        fillAddress(address)
                        viewModel.getRepresentatives(binding)
                    } else {
                        val locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult?) {
                                locationResult ?: return
                                val address = geoCodeLocation(locationResult.lastLocation)
                                fillAddress(address)
                                viewModel.getRepresentatives(binding)
                            }
                        }
                        fusedLocationClient.requestLocationUpdates(
                            LocationRequest(),
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }
                }
            }

        })
    }

    private fun fillAddress(address: Address) {
        binding.addressLine1.setText(address.line1)
        binding.addressLine2.setText(address.line2)
        binding.city.setText(address.city)
        binding.zip.setText(address.zip)
        val position = states.indices.firstOrNull {
            states[it] == address.state
        }
        binding.state.setSelection(position ?: 1)
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}