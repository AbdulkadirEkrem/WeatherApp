    package com.abdulkadirekrem.weatherappapi.ui

    import android.os.Bundle
    import android.util.Log
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.abdulkadirekrem.weatherappapi.api.WeatherApiClient
    import com.abdulkadirekrem.weatherappapi.databinding.ActivityMainBinding
    import com.abdulkadirekrem.weatherappapi.model.WeatherResponse
    import com.bumptech.glide.Glide
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class MainActivity : AppCompatActivity() {

        private lateinit var binding:ActivityMainBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.getWeatherButton.setOnClickListener {
                val cityName = binding.cityEditText.text.toString()
                if (cityName.isNotEmpty()) {
                    getWeatherData(cityName)
                } else {
                    Toast.makeText(this, "Lütfen bir şehir adı girin", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun getWeatherData(cityName: String) {
            val apiKey = "Your API Key" // API anahtarınızı buraya girin

            WeatherApiClient.weatherApiService.getWeather(cityName, apiKey).enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        updateUI(weatherResponse)
                    } else {
                        // Hata mesajını logcat'te göster
                        val errorMessage = response.errorBody()?.string()
                        Log.e("API Hatası", "Hata Kodu: ${response.code()}, Hata Mesajı: $errorMessage")

                        // Kullanıcıya hata mesajı göster
                        Toast.makeText(this@MainActivity, "Hava durumu bilgileri alınamadı. Hata kodu: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Ağ hatası", Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun updateUI(weatherResponse: WeatherResponse?) {

            if (weatherResponse != null) {
                binding.cityTextView.text = weatherResponse.name
                binding.temperatureTextView.text = "Sıcaklık: ${weatherResponse.main.temp}°C"
                binding.humidityTextView.text = "Nem: %${weatherResponse.main.humidity}"
               // binding.descriptionTextView.text = weatherResponse.weather[0].description

                val iconUrl = "https://openweathermap.org/img/w/${weatherResponse.weather[0].icon}.png"
                Glide.with(this).load(iconUrl).into(binding.weatherIconImageView)
            }
        }
    }