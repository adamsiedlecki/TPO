/**
 *
 *  @author Siedlecki Adam S25300
 *
 */

package zad1;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStream;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Service {

    private final String country;

    private static final String OPEN_WEATHER_API_KEY = "";
    private static final String EXCHANGE_RATE_API_KEY = "2167d3a13fe39f20524bca69"; // by 10 minutes mail

    public Service(String country) {
        this.country = country;
    }

    public String getWeather(String city) {
        Cords cords = getCords(city);
        return getContent(String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric", cords.getLat(), cords.getLng(), OPEN_WEATHER_API_KEY));
    }

    public Double getRateFor(String currency) {
        String response = getContent("https://open.er-api.com/v6/latest/" + getCurrencyCode(getCountryCode(country)));
        JSONObject obj = (JSONObject) JSONValue.parse(response);
        JSONObject array = (JSONObject) obj.get("rates");
        if (array.get(currency.toUpperCase()) instanceof Long) {
            return Double.valueOf((long) array.get(currency.toUpperCase()));
        }
        return (double) array.get(currency.toUpperCase());
    }

    public Double getNBPRate() {
        JSONArray rates = getNbpRates("A");
        rates.addAll(getNbpRates("B"));
        String currencyCode = getCurrencyCode(getCountryCode(country));

        if (currencyCode.equals("PLN")) {
            return 1.0; // nbp nie notuje zł względem zł
        }

        try {
            return (Double) rates.stream()
                    .filter(rate -> ((JSONObject)rate).get("code").equals(currencyCode))
                    .map(rate -> ((JSONObject)rate).get("mid"))
                    .findFirst()
                    .orElseGet(() -> new RuntimeException("cannot find currency rate from NBP: " + currencyCode));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getWeatherPretty(String city) {
        String weatherJson = getWeather(city);
        JSONObject obj = (JSONObject) JSONValue.parse(weatherJson);
        JSONObject current = (JSONObject) obj.get("main");
        String temp = String.valueOf(current.get("temp") );
        long pressure = (long) current.get("pressure");

        return String.format("Temperature: %s \n Pressure: %s", temp, pressure);
    }

    private JSONArray getNbpRates(String table) {
        String response = getContent(String.format("http://api.nbp.pl/api/exchangerates/tables/%s/", table));
        JSONArray ar = (JSONArray) JSONValue.parse(response);
        JSONObject ob = (JSONObject) ar.get(0);
        return  (JSONArray) ob.get("rates");
    }

    private String getCurrencyCode(String countryCode) {
        Locale locale = new Locale("", countryCode);
        return Currency.getInstance(locale).getCurrencyCode();
    }

    private Cords getCords(String city) {
        String response = getContent(String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s,%s&limit=1&appid=%s", city, getCountryCode(country), OPEN_WEATHER_API_KEY));
        Object obj = JSONValue.parse(response);
        JSONArray array = (JSONArray) obj;
        if (array.isEmpty()) {
            throw new RuntimeException("There is no such city in such country: " + country +" " + city);
        }
        JSONObject entry = (JSONObject) array.get(0);
        return new Cords((double) entry.get("lat"), (double) entry.get("lon"));
    }

    private String getCountryCode(String countryName) {
        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
            countries.put(l.getDisplayCountry(Locale.US), iso);
        }
        return countries.get(countryName);
    }

    private String getContent(String urlString) {
        try{
            URL url = new URL(urlString);
            try (InputStream is = url.openStream()) {
                int c;
                StringBuilder sb = new StringBuilder();
                while ((c = is.read()) != -1)
                    sb.append((char)c);

                return sb.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
