//package com.dicoding.freshfind.ui
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.ComponentActivity
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCaptureException
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.content.ContextCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.dicoding.freshfind.R
//import com.dicoding.freshfind.databinding.ActivityCameraXactivityBinding
//import com.dicoding.freshfind.ui.HomeSearchActivity
//import java.io.File
//
//class CameraXActivity : ComponentActivity() {
//    private lateinit var binding: ActivityCameraXactivityBinding
//    companion object {
//        private const val TAG = "CameraXActivity"
//    }
//    private var imageCapture: ImageCapture? = null
//
//    // Request camera permission at runtime (needed for Android 6.0 and above)
//    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//        if (isGranted) {
//            startCamera()  // If permission granted, start the camera
//        } else {
//            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityCameraXactivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Handling window insets for edge-to-edge display
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Request camera permission when activity starts
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
//            startCamera()
//        } else {
//            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
//        }
//
//        // Capture button click listener
//        binding.captureButton.setOnClickListener {
//            val photoFile = File(getExternalFilesDir(null), "photo.jpg")
//            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//            imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                }
//
//                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                    val intent = Intent(this@CameraXActivity, HomeSearchActivity::class.java)
//                    intent.putExtra("image_path", photoFile.absolutePath)  // Kirimkan path gambar
//                    startActivity(intent)
//                }
//            })
//        }
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            // Set up ImageCapture
//            imageCapture = ImageCapture.Builder().build()
//            val preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//            }
//
//            // Bind camera lifecycle and use ImageCapture
//            cameraProvider.bindToLifecycle(
//                this,
//                cameraSelector,
//                preview,
//                imageCapture
//            )
//        }, ContextCompat.getMainExecutor(this))
//    }
//}
