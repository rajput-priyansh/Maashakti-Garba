package com.vibs.maashakti

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.vibs.maashakti.api.Status
import com.vibs.maashakti.base.StartActivityCallback
import com.vibs.maashakti.databinding.FragmentSecondBinding
import com.vibs.maashakti.utils.ViewExtensions.capitalizeWords
import com.vibs.weatherdemosdk.base.BaseFragment
import com.vibs.weatherdemosdk.base.PermissionCallback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class SecondFragment : BaseFragment(R.layout.fragment_second) {
    companion object {
        const val NFC_CODE = "NFC_CODE"
    }

    private var _binding: FragmentSecondBinding? = null

    private var photo: File? = null

    private var nfcId = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this)[CardViewModel::class.java]
    }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                val data: Intent = it.data ?: return@registerForActivityResult

                val imageBitmap = data.extras?.get("data") as Bitmap

                val uri: Uri? = if (data.data == null) {
                    getUri()
                } else {
                    data.data
                }

                getAndSetupImage(uri, imageBitmap)
            }
        }


    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                val data: Intent = it.data ?: return@registerForActivityResult

                val imagePath = data.clipData

                imagePath.let { imfPart ->
                    if (imfPart != null) {

                        if (imfPart.itemCount > 1) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.can_not_upload_more_than_one_image),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@let
                        }

                        for (i in 0 until imfPart.itemCount) {

                            val selectedImage = imfPart.getItemAt(i).uri

                            val bitmap =
                                MediaStore.Images.Media.getBitmap(
                                    requireContext().contentResolver,
                                    selectedImage
                                )
                            getAndSetupImage(selectedImage, bitmap)
                        }

                    } else {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                data.data
                            )

                        getAndSetupImage(data.data, bitmap)
                    }
                }
            }
        }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    getAndSetupImage(fileUri)

                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        initUi()

        observer()
    }

    override fun initData() {
        super.initData()
    }

    override fun initUi() {
        super.initUi()
        binding.etGiftBy.visibility = View.GONE

        binding.btnSaveCard.setOnClickListener {
            //check Validation

            if (binding.etFirstName.text.isNullOrEmpty() || binding.etFirstName.text.toString()
                    .trim().isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please enter first name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (binding.etLastName.text.isNullOrEmpty() || binding.etLastName.text.toString()
                    .trim().isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please enter last name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (nfcId.isEmpty()) {
                Toast.makeText(requireContext(), "Please Scan NFC Code", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (!Patterns.PHONE.matcher(binding.etContactNo.text.toString()).matches()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter valid mobile number.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (photo == null) {
                Toast.makeText(
                    requireContext(),
                    "Please add Paler photo.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //Make API call to save Card

            val map: MutableMap<String, RequestBody> = HashMap()
            var image: MultipartBody.Part? = null
            try {
                /*val fileBody: RequestBody? =
                    photo?.asRequestBody("multipart/form-data".toMediaTypeOrNull())*/

                val requestImageFile: RequestBody =
                    photo!!.asRequestBody("image/*".toMediaTypeOrNull())

//                map["image\"; filename=\"" + photo.getName().toString() + "\""] = fileBody

                image = MultipartBody.Part.createFormData(
                    "user_photo_url",
                    photo?.name,
                    requestImageFile
                )

                /*fileBody?.let { it ->
                    map["user_photo_url"] = fileBody
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }

            map["user_nfc_no"] =
                nfcId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            map["registration_from"] =
                "Manual2022".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            map["first_name"] = binding.etFirstName.text.toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            map["last_name"] = binding.etLastName.text.toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            map["middle_name"] = "S".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            map["secret_token"] =
                "mashakti@admin".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            map["user_contact_no"] = binding.etContactNo.text.toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

            if (binding.etGiftBy.visibility == View.VISIBLE && ! binding.etGiftBy.text.isNullOrEmpty()) {
                map["gift_by"] = binding.etGiftBy.text.toString()
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }

            val passType = if (binding.rbPaid.isChecked) "Paid" else "Gift By ${binding.etGiftBy.text?.trim().toString().capitalizeWords()}"

            map["pass_type"] = passType.toRequestBody("multipart/form-data".toMediaTypeOrNull())

            image?.let { img ->
                postCreateCardData(map, img)
            }
        }

        binding.llCamara.setOnClickListener {
            if (requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (hasPermission(Manifest.permission.CAMERA)) {
                    openCamera()
                } else {
                    requestPermission(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), object : PermissionCallback {
                            override fun onPermissionGranted(grantedPermissions: ArrayList<String>) {
                                openCamera()
                            }

                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please Allow Camera Permission.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onPermissionBlocked(blockedPermissions: ArrayList<String>) {
                                //Emoty Boty
                            }

                        })
                }
            } else {
                // Camara not Available
                Toast.makeText(
                    requireContext(),
                    getString(R.string.can_not_connect_to_camera),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.llGallery.setOnClickListener {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openGallery()
            } else {
                requestPermission(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    object : PermissionCallback {
                        override fun onPermissionGranted(grantedPermissions: ArrayList<String>) {
                            openGallery()
                        }

                        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                            Toast.makeText(
                                requireContext(),
                                "Please Allow Gallery Permission.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onPermissionBlocked(blockedPermissions: ArrayList<String>) {
                            //Emoty Boty
                        }

                    })
            }
        }

        binding.btScanCard.setOnClickListener {

            val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

            if (nfcAdapter == null) {
                Toast.makeText(requireContext(), "This device does not support NFC",
                    Toast.LENGTH_SHORT).show()
            } else if (!nfcAdapter.isEnabled) {
                // NFC is available for device but not enabled
                startActivity(Intent("android.settings.NFC_SETTINGS"))
            } else {
                startNewActivityForResult(
                    Intent(requireActivity(), ScanCardAactivity::class.java),
                    object : StartActivityCallback {
                        override fun onActivityResult(result: ActivityResult) {
                            result.data?.let { data ->
                                if (data.hasExtra(NFC_CODE)) {
                                    val id = data.getStringExtra(NFC_CODE)
                                    binding.etScanNfcCode.setText(id)
                                    nfcId = id ?: ""
                                }
                            }
                        }

                    })
            }
        }

        binding.rgType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.rbPaid.id) {
                binding.etGiftBy.visibility = View.GONE
            } else {
                binding.etGiftBy.visibility = View.VISIBLE
            }
        }
    }

    override fun observer() {
        super.observer()

        viewModel.cardResponse.observe(viewLifecycleOwner) {
            if (it?.statusCode == 1) {
                openConfirmationDialogDialog(
                    99,
                    "Success",
                    it.msg ?: "Congratulation, New Card has been Created!",
                    "Okay",
                    "Cancel",
                    false
                )
            } else {
                openConfirmationDialogDialog(
                    99,
                    "Fail",
                    it?.msg ?: "Something went wrong.",
                    "Okay",
                    "Cancel",
                    false
                )
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Use to occur on positive button click
     */
    override fun onDialogPositiveButtonClick(dialogTag: Int) {
        activity?.onBackPressed()
    }

    /**
     * Use to occur on negative button click
     */
    override fun onDialogNegativeButtonClick(dialogTag: Int) {
        activity?.onBackPressed()
    }

    private fun openCamera() {
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.resolveActivity(requireActivity().packageManager)

        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        cameraLauncher.launch(intent)*/

        ImagePicker.with(this)
            .cameraOnly()	//User can only select image from Gallery
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            //  Path: /storage/sdcard0/Android/data/package/files/Pictures/ImagePicker
            .saveDir(File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!, "maashakti"))
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }


    }

    private fun openGallery() {

//        val pickPhoto = Intent()
//
//        pickPhoto.type = "image/*"
//
//        pickPhoto.action = Intent.ACTION_GET_CONTENT
//
//        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//
//        pickPhoto.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//
//        galleryLauncher.launch(Intent.createChooser(pickPhoto, "Select Image"))

        ImagePicker.with(this)
            .galleryOnly()	//User can only select image from Gallery
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .galleryMimeTypes(  //Exclude gif images
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .saveDir(File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!, "maashakti"))
            .createIntent { intent ->
            startForProfileImageResult.launch(intent)
        }

    }


    private fun getAndSetupImage(uri: Uri?, imageBitmap: Bitmap? = null) {

//        binding.ivProfileImage.setImageBitmap(imageBitmap)

        val outputStream = ByteArrayOutputStream()

        /*val angle = getCapturedImageOrientation(uri!!)

        val newBitmap = rotateImage(imageBitmap, angle)*/

//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

        val byteArray = outputStream.toByteArray()

        uri?.let { turi ->

//            val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(turi, "r", null)
            /*parcelFileDescriptor?.let {
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                context?.contentResolver?.getFileName(turi)?.let { name ->
                    Log.d("TESTP", "getAndSetupImage() called name:$name")
                    photo = File(context?.cacheDir, name)
                }
                val outputStream = FileOutputStream(file)
                *//*file.outputStream().use {
                    outputStream
                }*//*

                Log.d("TESTP", "getAndSetupImage() called photo:$photo")
            }*/

            binding.ivProfileImage.setImageURI(uri)
            photo = File(FileUtil.getPath(uri, requireContext()))

            Log.e("TESTP", "getAndSetupImage() called with: camera photo = $photo")

        }
//        val uploadUrl = UploadMedia(uri, imageBitmap, 1, "", "", byteArray)


        /*val request = ImageUploadRequestBody(
            1,
            PreferenceManager.getCRNID(),
            "${PreferenceManager.getCRNID()}",
            "CONCERN_QUERY"
        )*/
    }

    private fun ContentResolver.getFileName(fileUri: Uri): String {

        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }

        return name
    }

    @SuppressLint("Range")
    private fun getUri(): Uri? {
        var uri: Uri? = null
        val cursor =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                requireActivity().contentResolver
                    .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.ImageColumns.ORIENTATION
                        ),
                        MediaStore.Images.Media.DATE_ADDED,
                        null,
                        "date_added ASC"
                    )
            } else {
                requireActivity().contentResolver
                    .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.Media.DATA
                        ),
                        MediaStore.Images.Media.DATE_ADDED,
                        null,
                        "date_added ASC"
                    )
            }

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            do {
                uri =
                    Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))
            } while (cursor.moveToNext())
            cursor.close()
        }

        return uri
    }

    /**
     * Make API call to get verify details
     */
    private fun postCreateCardData(map: Map<String, RequestBody>, image: MultipartBody.Part) {
        viewModel.postCreateCardData(map, image).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.SUCCESS -> {
                    hideProgressDialog()
                    viewModel.setCardResponseData(it.data)
                }
                Status.ERROR -> {
                    Log.d("TESTP", "postCreateCardData() called:${it.message}")
                    viewModel.setCardResponseData(null)
                    hideProgressDialog()
                }
            }
        }
    }
}