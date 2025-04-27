import com.example.weatherapp.model.WeatherResponse

class WeatherChatbot {
    private var currentWeather: WeatherResponse? = null

    fun updateWeatherData(weatherResponse: WeatherResponse) {
        currentWeather = weatherResponse
    }

    fun generateResponse(userMessage: String): String {
        val message = userMessage.lowercase()

        if (currentWeather == null) {
            return "I don't have any weather data yet. Please fetch weather data first."
        }

        return when {
            message.contains("temperature") || message.contains("how hot") || message.contains("how cold") -> {
                val temp = currentWeather?.main?.temp
                "The current temperature in ${currentWeather?.name} is ${temp}°C."
            }
            message.contains("humidity") -> {
                val humidity = currentWeather?.main?.humidity
                "The humidity in ${currentWeather?.name} is $humidity%."
            }
            message.contains("wind") -> {
                val windSpeed = currentWeather?.wind?.speed
                "The wind speed in ${currentWeather?.name} is $windSpeed m/s."
            }
            message.contains("pressure") -> {
                val pressure = currentWeather?.main?.pressure
                "The atmospheric pressure in ${currentWeather?.name} is $pressure hPa."
            }
            message.contains("weather") || message.contains("condition") -> {
                val description = currentWeather?.weather?.firstOrNull()?.description
                "The current weather condition in ${currentWeather?.name} is $description."
            }
            message.contains("visibility") -> {
                val visibility = currentWeather?.visibility
                "The visibility in ${currentWeather?.name} is ${visibility?.div(1000)} km."
            }
            message.contains("raining") || message.contains("rain") -> {
                val condition = currentWeather?.weather?.firstOrNull()?.main?.lowercase()
                if (condition?.contains("rain") == true) {
                    "Yes, it's currently raining in ${currentWeather?.name}."
                } else {
                    "No, it's not raining in ${currentWeather?.name} right now."
                }
            }
            message.contains("cloudy") || message.contains("clouds") -> {
                val cloudiness = currentWeather?.clouds?.all
                "Cloud coverage in ${currentWeather?.name} is $cloudiness%."
            }
            message.contains("sunrise") -> {
                val sunriseTime = currentWeather?.sys?.sunrise?.let { java.util.Date(it * 1000L) }
                "Sunrise in ${currentWeather?.name} is at $sunriseTime."
            }
            message.contains("sunset") -> {
                val sunsetTime = currentWeather?.sys?.sunset?.let { java.util.Date(it * 1000L) }
                "Sunset in ${currentWeather?.name} is at $sunsetTime."
            }
            message.contains("hello") || message.contains("hi") -> {
                "Hello! How can I help you with weather information today?"
            }
            message.contains("thank") -> {
                "You're welcome! Feel free to ask if you need more weather information."
            }
            message.contains("forecast") || message.contains("tomorrow") -> {
                "I can only provide current weather data. For forecasts, you would need to use the forecast API."
            }
            else -> {
                "I'm sorry, I don't understand that question. You can ask me about temperature, humidity, wind, pressure, visibility, or general weather conditions."
            }
        }
    }
}