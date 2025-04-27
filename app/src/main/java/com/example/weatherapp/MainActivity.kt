package com.example.weatherapp

import WeatherChatbot
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.ChatAdapter
import com.example.weatherapp.api.RetrofitClient
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.ChatMessage
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.util.WeatherAnimationUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = ArrayList<ChatMessage>()
    private val weatherChatbot = WeatherChatbot()

    // Replace this with your actual API key
    private val apiKey = "7b8bf95f3771c21cf4a4323a49226d09"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView for chat
        chatAdapter = ChatAdapter(chatMessages)
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = chatAdapter
        }

        // Add initial bot message
        addBotMessage("Hello! I'm your weather assistant. Fetch weather data and then ask me anything about current weather conditions.")

        // Set up click listeners
        binding.sendButton.setOnClickListener {
            val message = binding.chatEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                addUserMessage(message)
                processUserMessage(message)
                binding.chatEditText.text.clear()
            }
        }

        binding.searchFab.setOnClickListener {
            showSearchDialog()
        }

        // For demo purposes, fetch San Francisco weather on app start
        fetchWeatherByCoordinates(13.1769, 80.09787)
    }

    private fun addUserMessage(message: String) {
        val chatMessage = ChatMessage(message, true)
        chatMessages.add(chatMessage)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun addBotMessage(message: String) {
        val chatMessage = ChatMessage(message, false)
        chatMessages.add(chatMessage)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun processUserMessage(message: String) {
        val response = weatherChatbot.generateResponse(message)
        addBotMessage(response)
    }

    private fun showSearchDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search)

        val searchTypeRadioGroup = dialog.findViewById<RadioGroup>(R.id.searchTypeRadioGroup)
        val coordinatesRadioButton = dialog.findViewById<RadioButton>(R.id.coordinatesRadioButton)
        val cityRadioButton = dialog.findViewById<RadioButton>(R.id.cityRadioButton)
        val coordinatesLayout = dialog.findViewById<LinearLayout>(R.id.coordinatesLayout)
        val cityEditText = dialog.findViewById<EditText>(R.id.cityEditText)
        val latitudeEditText = dialog.findViewById<EditText>(R.id.latitudeEditText)
        val longitudeEditText = dialog.findViewById<EditText>(R.id.longitudeEditText)
        val searchButton = dialog.findViewById<Button>(R.id.searchButton)

        searchTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.coordinatesRadioButton -> {
                    coordinatesLayout.visibility = View.VISIBLE
                    cityEditText.visibility = View.GONE
                }
                R.id.cityRadioButton -> {
                    coordinatesLayout.visibility = View.GONE
                    cityEditText.visibility = View.VISIBLE
                }
            }
        }

        searchButton.setOnClickListener {
            if (coordinatesRadioButton.isChecked) {
                val latText = latitudeEditText.text.toString()
                val lonText = longitudeEditText.text.toString()
                if (latText.isNotEmpty() && lonText.isNotEmpty()) {
                    try {
                        val latitude = latText.toDouble()
                        val longitude = lonText.toDouble()
                        fetchWeatherByCoordinates(latitude, longitude)
                        dialog.dismiss()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Please enter valid coordinates", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter both latitude and longitude", Toast.LENGTH_SHORT).show()
                }
            } else {
                val city = cityEditText.text.toString()
                if (city.isNotEmpty()) {
                    fetchWeatherByCity(city)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun fetchWeatherByCoordinates(latitude: Double, longitude: Double) {
        val call = RetrofitClient.instance.getCurrentWeatherByCoordinates(latitude, longitude, apiKey)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        updateUI(it)
                        weatherChatbot.updateWeatherData(it)
                        addBotMessage("Weather data for ${it.name} has been updated. Feel free to ask me about current conditions!")
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch weather data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchWeatherByCity(cityName: String) {
        val call = RetrofitClient.instance.getCurrentWeatherByCity(cityName, apiKey)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        updateUI(it)
                        weatherChatbot.updateWeatherData(it)
                        addBotMessage("Weather data for ${it.name} has been updated. Feel free to ask me about current conditions!")
                    }
                } else {
                    Toast.makeText(this@MainActivity, "City not found or error in response", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch weather data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        binding.apply {
            locationTextView.text = weatherResponse.name + ", " + weatherResponse.sys.country
            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault())
            dateTimeTextView.text = dateFormat.format(Date())

            temperatureTextView.text = "${weatherResponse.main.temp.toInt()}°C"
            weatherDescriptionTextView.text = weatherResponse.weather.firstOrNull()?.description?.capitalize()

            humidityTextView.text = "${weatherResponse.main.humidity}%"
            windSpeedTextView.text = "${weatherResponse.wind.speed} m/s"
            pressureTextView.text = "${weatherResponse.main.pressure} hPa"
            visibilityTextView.text = "${weatherResponse.visibility / 1000} km"

            // Animate weather icon based on condition
            weatherResponse.weather.firstOrNull()?.let { weather ->
                WeatherAnimationUtil.animateWeatherIcon(weatherIconImageView, weather.main)
            }
        }
    }
}