package com.example.lab_week_05

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.lab_week_05.model.ImageData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : AppCompatActivity() {

    // Retrofit instance dengan Moshi + Logging Interceptor
    private val retrofit by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // CatApiService instance
    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    // Views
    private lateinit var breedLabel: TextView
    private lateinit var apiResponseView: TextView
    private lateinit var imageResultView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // inisialisasi views
        breedLabel = findViewById(R.id.breed_label)
        apiResponseView = findViewById(R.id.api_response)
        imageResultView = findViewById(R.id.image_result)

        // panggil API function saat activity dibuat
        getCatImageResponse()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // fungsi untuk ambil data dari Cat API
    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
                apiResponseView.text = "Failure: ${t.message}"
            }

            override fun onResponse(
                call: Call<List<ImageData>>,
                response: Response<List<ImageData>>
            ) {
                if (response.isSuccessful) {
                    val imageList = response.body()
                    val firstImage = imageList?.firstOrNull()

                    // Ambil nama breed pertama atau kosongkan
                    val breedName = firstImage?.breeds?.firstOrNull()?.name.orEmpty()
                    apiResponseView.text = breedName

                    // Load image ke ImageView pakai Glide
                    val imageUrl = firstImage?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@MainActivity)
                            .load(imageUrl)
                            .centerCrop()
                            .placeholder(android.R.color.darker_gray)
                            .into(imageResultView)
                    }
                } else {
                    val error = response.errorBody()?.string().orEmpty()
                    Log.e(MAIN_ACTIVITY, "Failed to get response\n$error")
                    apiResponseView.text = "Error: $error"
                }
            }
        })
    }

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}