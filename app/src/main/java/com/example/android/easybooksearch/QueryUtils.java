package com.example.android.easybooksearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JOAO on 25-Apr-18.
 */

public class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {
    }

    public static List<Book> fetchData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        // Extract relevant fields from the JSON response and create a list of items
        List<Book> books = extractData(jsonResponse);
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the Google Server and return a list of {@link Book} objects.
     */
    public static List<Book> extractData(String jsonResponse) {
        ArrayList<Book> bookList = new ArrayList<>();
        // Try to parse the JSON Response.
        try {
            //This creates the root JSONObject by calling jsonResponse
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            //this get the objects contained in the a items Array
            if (baseJsonResponse.has("items")) {
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
                //the data are obtained for each item
                for (int i = 0; i < itemsArray.length(); i++) {
                    String authors = ""; //this is the list of authors
                    //here there is pulled out the JSON object at the specified position
                    JSONObject singleItem = itemsArray.getJSONObject(i);
                    //here there is extracted out the JSON object associated with the volumeInfo key.
                    JSONObject volumeInfo = singleItem.getJSONObject("volumeInfo");
                    //here there is the title extracted as string from volumeInfo key
                    String title = volumeInfo.getString("title");
                    //here there are a list of authors extracted as an array from volumeInfo key
                    if (volumeInfo.has("authors")) {
                        JSONArray authorsList = volumeInfo.getJSONArray("authors");
                        if (authorsList.length() > 1) {
                            authors = authorsList.join(", ").replaceAll("\"", "");
                        } else if (authorsList.length() == 1) {
                            authors = authorsList.getString(0);
                        } else if (authorsList.length() == 0) {
                            authors = "";
                        }
                    }

                    //this get the link of image representing the book
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    String smallThumbnail = imageLinks.getString("smallThumbnail");
                    //here it is create a new ItemsList object with all data extracted from JSON
                    Book books = new Book(title, authors, smallThumbnail);
                    //add the ItemsList object to the Array
                    bookList.add(books);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return bookList;
    }
}
