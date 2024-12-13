package com.example.dicodingstory.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstory.R
import com.example.dicodingstory.data.ApiResult
import com.example.dicodingstory.databinding.ActivityAddStoryBinding
import com.example.dicodingstory.injection.Injection
import com.example.dicodingstory.MainActivity
import com.example.dicodingstory.utils.UserPreferences
import com.example.dicodingstory.utils.createTempFile
import com.example.dicodingstory.utils.dataStore
import com.example.dicodingstory.utils.reduceFileImage
import com.example.dicodingstory.utils.uriToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private lateinit var userPreferences: UserPreferences
    private var currentPhotoPath: String? = null
    private var imageFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            imageFile = uriToFile(selectedImg, this)
            binding.ivPreviewImage.setImageURI(selectedImg)
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath!!)
            imageFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.ivPreviewImage.setImageBitmap(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewModel
        val repository = Injection.provideRepository(this)
        val factory = AddStoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]

        // Setup UserPreferences
        userPreferences = UserPreferences.getInstance(dataStore)

        // Check and request permissions
        if (allPermissionsGranted()) {
            setupViewModel()
            setupListeners()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Setup UI Listeners
        setupListeners()

        // Observe upload result
        viewModel.uploadResult.observe(this) { result ->
            when (result) {
                is ApiResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonAdd.isEnabled = false
                }
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonAdd.isEnabled = true
                    Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViewModel() {
        val repository = Injection.provideRepository(this)
        val factory = AddStoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]

        userPreferences = UserPreferences.getInstance(dataStore)
    }

    private fun setupListeners() {
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.btnCamera.setOnClickListener { openCamera() }
        binding.buttonAdd.setOnClickListener { checkLoginAndUploadStory() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(this).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.dicodingstory.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(intent)
        }
    }

    private fun checkLoginAndUploadStory() {
        lifecycleScope.launch {
        userPreferences.getUser().collect { userData ->
            if (userData.token.isNotEmpty()) {
                uploadStory(userData.token)
            } else {
                uploadStoryAsGuest()
            }
        }
        }
    }

    private fun uploadStory(token: String) {
        if (imageFile == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.edAddDescription.text.toString()
        if (description.isEmpty()) {
            Toast.makeText(this, "Please add a description", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Reduce file size
                val compressedFile = imageFile?.reduceFileImage()

                // Create request bodies
                val descriptionBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = compressedFile?.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part? = requestImageFile?.let {
                    MultipartBody.Part.createFormData(
                        "photo",
                        compressedFile.name,
                        it
                    )
                }

                imageMultipart?.let { part ->
                    viewModel.uploadStory("Bearer $token", descriptionBody, part)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddStoryActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadStoryAsGuest() {
        if (imageFile == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.edAddDescription.text.toString()
        if (description.isEmpty()) {
            Toast.makeText(this, "Please add a description", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Reduce file size
                val compressedFile = imageFile?.reduceFileImage()

                // Create request bodies
                val descriptionBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = compressedFile?.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part? = requestImageFile?.let {
                    MultipartBody.Part.createFormData(
                        "photo",
                        compressedFile.name,
                        it
                    )
                }

                imageMultipart?.let { part ->
                    viewModel.uploadStoryAsGuest(descriptionBody, part)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddStoryActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(baseContext, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val permissionsMap = permissions.mapIndexed { index, permission ->
                permission to (grantResults[index] == PackageManager.PERMISSION_GRANTED)
            }.toMap()

            permissionsMap.forEach { (permission, granted) ->
                Log.d("PermissionsResult", "$permission granted: $granted")
            }

            if (allPermissionsGranted()) {
                setupViewModel()
                setupListeners()
            } else {
                Toast.makeText(this, "Permissions not granted. Cannot add story.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
