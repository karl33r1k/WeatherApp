//retrieve wweather data from API
// data from external APi and return it
//display this data to the user

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    //fetch weather data for given locaiton
    public static JSONObject getWeatherData(String locationName){
        //location coordinates using geolocation API
        JSONArray locationdata = getLocationData(locationName);

        //extract latitude and longitudde data
        JSONObject location = (JSONObject) locationdata.get(0);
        double latitude = (Double) location.get("latitude");
        double longitude = (Double) location.get("longitude");

        //build API request URL iwth location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try{
            // call api and get response
            HttpURLConnection connection = fetchApiResponse(urlString);
            //check for response status
            // 200 - means that the connection was success
            if (connection.getResponseCode() != 200){
                System.out.println("Error: could not connect");
                return null;
            }
            //Store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()){
                //read and store into the String builder
                resultJson.append(scanner.nextLine());
            }

            scanner.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));
            //retrieve hourly data
            JSONObject hourly = (JSONObject) jsonObject.get("hourly");

            //need current hour data -> get index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // get temp
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (Double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //get humidity
            JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build the weather json data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weatherCondition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            return weatherData;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //convert weathercode to something readable (WMO weather interpretation codes)
    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        //Clear
        if (weathercode == 0L) {
            weatherCondition = "Clear";
        }
        //Cloudy
        else if (weathercode <= 3L && weathercode > 0L) {
            weatherCondition = "Cloudy";
        }
        //Rain
        else if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L) ) {
            weatherCondition = "Rain";
        //Sunny
        } else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;


    }

    private static int findIndexOfCurrentTime(JSONArray timelist) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timelist.size(); i++) {
            String time = (String) timelist.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        LocalDateTime currentDate = LocalDateTime.now();
        //format date to be 2023-09-02T00:00 (in the API is in this format)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        //Format and print the current date and time
        String formattedDateTime = currentDate.format(formatter);
        return formattedDateTime;
    }

    public static JSONArray getLocationData(String locationName) {
        //repalce any whitespace in locaiton to + adhere to API reuqest format
        // nt: ...//open-meteo.com/en/docs/geocoding-api#name=new+york
        locationName = locationName.replaceAll(" ","+");

        //build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName +
                "&count=10&language=en&format=json";
        try{
            HttpURLConnection connection = fetchApiResponse(urlString);
            //check response status
            // 200 means successful (HTTP STATUS CODES)
            if (connection.getResponseCode() != 200){
                System.out.println("Error: COuld not connect to API");
                return null;
            }
            else {
                StringBuilder resultJson = new StringBuilder();
                //Scanner to read JSON daata that is returned from our API call
                Scanner scanner = new Scanner(connection.getInputStream());
                //read and store resulting JSON data into our string builder
                while (scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();
                //close url connection
                connection.disconnect();
                //parse the JSON string into JSOn obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));
                //get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObject.get("results");
                return locationData;




            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //Could not find location
        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //set request method to GET (tüübid mida saab teha: GET, PUT, DELETE, POST)
            connection.setRequestMethod("GET");
            //connect to our API
            connection.connect();
            return connection;
        }catch (IOException e){
            e.printStackTrace();
        }
        //could not make connection
        return null;
    }
}
