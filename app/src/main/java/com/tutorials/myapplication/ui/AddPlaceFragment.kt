package com.tutorials.myapplication.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import coil.load
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.tutorials.myapplication.R
import com.tutorials.myapplication.base.BaseFragment
import com.tutorials.myapplication.databinding.FragmentAddPlaceBinding
import com.tutorials.myapplication.di.fragment.FragmentComponent
import com.tutorials.myapplication.router.Router
import com.tutorials.myapplication.ui.viewmodel.PlaceViewModel
import com.tutorials.myapplication.utils.GetAddressFromLatLng
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddPlaceFragment : BaseFragment() {

    companion object {
        private const val CAMERA_REQUEST_CODE = 1
        private const val IMAGE_DIRECTORY = "PlacesImages"
        private const val GALLERY_REQUEST_CODE = 2
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

    var imageUri: Uri? = null
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentAddPlaceBinding
    val calendar: Calendar = Calendar.getInstance()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var placeViewModel: PlaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPlaceBinding.inflate(layoutInflater)
        if (imageUri == null) {
            binding.imagePlace.setImageResource(R.drawable.ic_baseline_image_24)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datePick()

        //        val sdf = SimpleDateFormat("dd/M/yyyy", Locale.ENGLISH)
        //        val currentDate = sdf.format(Date())

        //        binding.etDate.setText(sdf.format(Calendar.getInstance().time)) //Calendar.getInstance() represents the current time using the current locale and timezone
        //        binding.etDate.setText(currentDate)

        binding.addImage.setOnClickListener {
            cameraCheckPermission()
        }

        binding.imagePlace.setOnClickListener {
            cameraCheckPermission()
        }

        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // * Initialize the places sdk if it is not initialized earlier using the api key.
        //        if (!Places.isInitialized()) {
        Places.initialize(
            getActivity()?.getApplicationContext(),
            resources.getString(R.string.google_maps_api_key)
        )
        //        }

        binding.etLocation.setOnClickListener {
            //            checkPermissionsForLocation()


            try {
//                 These are the list of fields which we required is passed
                val fields = listOf(
                    com.google.android.libraries.places.api.model.Place.Field.ID,
                    com.google.android.libraries.places.api.model.Place.Field.NAME,
                    com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,
                    com.google.android.libraries.places.api.model.Place.Field.ADDRESS
                )
//                 Start the autocomplete intent with a unique request code.
                val intent =
                    Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(requireContext())
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.selectCurrentLocation.setOnClickListener { checkPermissionsForLocation() }



        updateDateInView()
        savePlaceToDatabase()

    }

    private fun checkPermissionsForLocation() {

        if (!isLocationEnabled()) {
            Toast.makeText(
                requireContext(),
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()

            // This will redirect you to settings from where you need to turn on the location provider.
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            // For Getting current location of user please have a look at below link for better understanding
            // https://www.androdocs.com/kotlin/getting-current-location-latitude-longitude-in-android-using-kotlin.html
            Dexter.withActivity(requireActivity())
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report?.areAllPermissionsGranted() == true) {

                            requestNewLocationData()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showDialogForPermission()
                    }
                }).onSameThread()
                .check()

        }
    }

    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            checkPermissionsForLocation()
            return
        }
        locationClient.requestLocationUpdates(
            mLocationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.lastLocation != null) {
                val lastLocation: Location = locationResult.lastLocation!!
                latitude = lastLocation.latitude

                longitude = lastLocation.longitude
            }

            val addressTask =
                GetAddressFromLatLng(requireContext(), latitude, longitude)

            addressTask.setAddressListener(object :
                GetAddressFromLatLng.AddressListener {
                override fun onAddressFound(address: String?) {
                    binding.etLocation.setText(address) // Address is set to the edittext
                }

                override fun onError() {
                    Log.e("Get Address ::", "Something is wrong...")
                }
            })

            addressTask.getAddress()

        }
    }

    private fun datePick() {

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()

        }
        //        updateDateInView()

        binding.etDate.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            dpd.show()
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.DKGRAY)
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

        }

    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }

    private fun savePlaceToDatabase() {

        binding.savePlace.setOnClickListener {
            val title = binding.etTitle

            if (title.text.isNullOrEmpty()) {
                binding.titleInput.error = "Please add a title"
            } else {
                binding.titleInput.error = null
            }
            val description = binding.etDescription
            val date = binding.etDate.text.toString()

            val location = binding.etLocation

            if (location.text.isNullOrEmpty()) {
                binding.locationInput.error = "Please add a location"
            } else {
                binding.locationInput.error = null
            }


            if (!title.text.isNullOrEmpty() && !location.text.isNullOrEmpty()) {
                placeViewModel.addPlaceToDatabase(
                    Place(
                        0,
                        title.text.toString(),
                        description.text.toString(),
                        date,
                        location.text.toString(),
                        imageUri?.toString() ?: ""
                    )
                )
                router.routerPopBack()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap

                    imageUri = saveImageToInternalStorage(bitmap)
                    if (imageUri != null) {
                        binding.imagePlace.setImageBitmap(bitmap)
                    } else {
                        binding.imagePlace.setImageResource(R.drawable.ic_baseline_image_24)
                    }

                }
                GALLERY_REQUEST_CODE -> {
                    binding.imagePlace.load(data?.data)

                    val uri = data?.data

                    imageUri = if (Build.VERSION.SDK_INT > 27) {
                        val source = uri?.let { ImageDecoder.createSource(requireActivity().contentResolver, it) }
                        val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                        bitmap?.let { saveImageToInternalStorage(it) }
                    } else {
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                        saveImageToInternalStorage(selectedImageBitmap)
                    }

                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                    val place: com.google.android.libraries.places.api.model.Place = Autocomplete.getPlaceFromIntent(data!!)

                    binding.etLocation.setText(place.address)
                    latitude = place.latLng!!.latitude
                    longitude = place.latLng!!.longitude
                }
            }

        }
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun cameraCheckPermission() {
        Dexter.withContext(requireContext()).withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {

                                val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                pictureDialog.setTitle("Select Action")
                                val pictureDialogItems =
                                    arrayOf("Select photo from gallery", "Capture photo from camera")
                                pictureDialog.setItems(
                                    pictureDialogItems
                                ) { dialog, which ->
                                    when (which) {
                                        0 -> gallery()
                                        1 -> camera()
                                    }
                                }
                                pictureDialog.show()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                        showDialogForPermission()
                    }
                }
            ).onSameThread().check()
    }

    private fun showDialogForPermission() {
        AlertDialog.Builder(requireContext()).setMessage(
            "It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App settings."
        ).setPositiveButton("Go to settings") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)

                intent.data = uri
                startActivity(intent)

            } catch (e: ActivityNotFoundException) {

            }
        }.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(requireActivity().applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

}