import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets the weather forecast data from an external API by fetch and parse.
 */
class WeatherForecast {

    /**
     * The main method that will fetch and parse the weather data.
     * @param args the arguments for setting location cords and temp unit
     */
    public static void main(String[] args) {
        double latitude = 39.1653;
        double longitude = -86.5264;
        String u = "fahrenheit";

        if(args.length >= 6){
            try{
                latitude = Double.parseDouble(args[1]);
                longitude = Double.parseDouble(args[3]);
                u = args[5].equals("C") ? "metric" : "imperial";
            }catch(NumberFormatException e){
                System.out.println("Invalid latitude or longitude values");
                return;
            }
        }
        String urlFormattedString = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m&temperature_unit=%s&timezone=auto",
                latitude,
                longitude,
                u
        );

        try{
            URL url = new URL(urlFormattedString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int response = conn.getResponseCode();
            if(response != 200){
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                System.out.println("Error response: " + errorResponse.toString());
                throw new IOException("Failed to fetch data: HTTP error code " + response);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonAns = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                jsonAns.append(line);
            }
            reader.close();

            parseForecast(jsonAns.toString(), u);
        }catch(IOException e){
            System.out.println("Error while fetching weather data: " + e.getMessage());
        }
    }

    /**
     * Goes through the JSON string that has the weather forecast data.
     * Organizes and displays the forecast in 3 hour increments for 7 days.
     * @param json the string response from the API
     * @param u the unit of temperature, metric or imperial.
     */
    public static void parseForecast(String json, String u){
        JsonElement element = JsonParser.parseString(json);
        JsonObject object = element.getAsJsonObject();
        JsonObject hourly = object.getAsJsonObject("hourly");
        JsonArray tempData = hourly.getAsJsonArray("temperature_2m");
        JsonArray timeData = hourly.getAsJsonArray("time");


        Map<String, List<String>> forecastsByDay = new LinkedHashMap<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < tempData.size(); i += 3) {
            float temp = tempData.get(i).getAsFloat();
            LocalDateTime dateTime = LocalDateTime.parse(timeData.get(i).getAsString());
            String dayKey = dateTime.format(dateFormatter);
            String forecastEntry = String.format("%s: %.1f°F", dateTime.format(timeFormatter), temp);

            forecastsByDay.computeIfAbsent(dayKey, k -> new ArrayList<>()).add(forecastEntry);
        }

        System.out.println("Bloomington 7-Day Forecast in Fahrenheit:");
        for (Map.Entry<String, List<String>> entry : forecastsByDay.entrySet()) {
            System.out.println("Forecast for " + entry.getKey() + ":");
            for (String forecast : entry.getValue()) {
                System.out.println(forecast);

            }
        }
    }
}