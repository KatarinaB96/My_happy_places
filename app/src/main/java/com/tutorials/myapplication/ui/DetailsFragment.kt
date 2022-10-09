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
import com.tutorials.myapplication.databinding.FragmentDetailsBinding
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

class DetailsFragment : BaseFragment() {

    val calendar: Calendar = Calendar.getInstance()

    private lateinit var locationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    var imageUri: Uri? = null

    companion object {
        private const val CAMERA_REQUEST_CODE = 1
        private const val IMAGE_DIRECTORY = "PlacesImages"
        private const val GALLERY_REQUEST_CODE = 2
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3

        private const val ID = "placeId"
        private const val TITLE = "title"
        private const val IMAGE_KEY = "image"

        private const val DESCRIPTION = "description"
        private const val LOCATION = "location"
        private const val DATE = "date"

        fun createInstance(id: Int, title: String, image: String, description: String, location: String, date: String) =
            DetailsFragment().apply {

                arguments = Bundle().apply {
                    putInt(ID, id)
                    putString(TITLE, title)
                    putString(IMAGE_KEY, image)
                    putString(DESCRIPTION, description)
                    putString(LOCATION, location)
                    putString(DATE, date)
                }

            }
    }

    private val idPlace: Int by lazy { arguments?.getInt(ID) ?: 0 }
    private val image: String by lazy { arguments?.getString(IMAGE_KEY) ?: "" }
    private val title: String by lazy { arguments?.getString(TITLE) ?: "" }
    private val description: String by lazy { arguments?.getString(DESCRIPTION) ?: "" }
    private val location: String by lazy { arguments?.getString(LOCATION) ?: "" }
    private val date: String by lazy { arguments?.getString(DATE) ?: "" }

    private lateinit var binding: FragmentDetailsBinding

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    @Inject
    lateinit var placeViewModel: PlaceViewModel

    @Inject
    lateinit var router: Router

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater)

        binding.etTitle.setText(title)
        binding.etDate.setText(date)
        binding.etDescription.setText(description)
        binding.etLocation.setText(location)

        if (image != "") {
            binding.imagePlace.setImageURI(Uri.parse(image))
        } else {
            binding.imagePlace.setImageResource(R.drawable.ic_baseline_image_24)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        savePlaceToDatabase()
        datePick()


        binding.selectCurrentLocation.setOnClickListener { checkPermissionsForLocation() }
        Places.initialize(
            getActivity()?.getApplicationContext(),
            resources.getString(R.string.google_maps_api_key)
        )
        //        }

        binding.etLocation.setOnClickListener {
            try {
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


        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.addImage.setOnClickListener {
            cameraCheckPermission()
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

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        val wrapper = ContextWrapper(requireActivity().applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {

            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()

            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
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

    private fun datePick() {

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }

        binding.etDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }

    private fun savePlaceToDatabase() {

        binding.updatePlace.setOnClickListener {
            val titleChange = binding.etTitle

            if (titleChange.text.isNullOrEmpty()) {
                binding.titleInput.error = "Please add a title"
            } else {
                binding.titleInput.error = null
            }
            val descriptionChange = binding.etDescription
            val dateChange = binding.etDate.text.toString()

            val locationChange = binding.etLocation

            if (locationChange.text.isNullOrEmpty()) {
                binding.locationInput.error = "Please add a location"
            } else {
                binding.locationInput.error = null
            }

            val saveImage: String

            if (imageUri == null) {
                saveImage = image
            } else {
                saveImage = imageUri.toString()
            }


            if (!titleChange.text.isNullOrEmpty() && !locationChange.text.isNullOrEmpty()) {
                placeViewModel.updatePlace(
                    idPlace,
                    titleChange.text.toString(),
                    saveImage,
                    descriptionChange.text.toString(),
                    locationChange.text.toString(),
                    dateChange
                )
                router.routerPopBack()
            }

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

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissionsForLocation() {

        if (!isLocationEnabled()) {
            Toast.makeText(
                requireContext(),
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
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
}