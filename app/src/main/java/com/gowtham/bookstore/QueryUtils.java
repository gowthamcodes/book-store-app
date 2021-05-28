package com.gowtham.bookstore;

import android.text.TextUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    public static ArrayList<Book> fetchBookListTask(String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }
        URL url = createUrl(requestUrl);
        String response = null;
        try {
            response = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making HTTP request", e);
        }

        ArrayList<Book> books = extractBooksFromJson(response);
        return books;
    }

    private static ArrayList<Book> extractBooksFromJson(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(response);
            JSONArray items = root.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject bookVolumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");
                String title = bookVolumeInfo.getString("title");
                String author;
                if (bookVolumeInfo.has("authors")) {
                    JSONArray authors = bookVolumeInfo.getJSONArray("authors");
                    if (!bookVolumeInfo.isNull("authors")) {
                        author = (String) authors.get(0);
                    } else {
                        author = "*** unknown author ***";
                    }
                } else {
                    author = "*** missing info of authors ***";
                }

                String year = "N/A";
                if (bookVolumeInfo.has("publishedDate")) {
                    year = bookVolumeInfo.getString("publishedDate");
                }

                String pages = "N/A";
                if (bookVolumeInfo.has("pageCount")) {
                    pages = bookVolumeInfo.getString("pageCount");
                }

                String thumbnail = null;
                if (bookVolumeInfo.has("imageLinks")) {
                    thumbnail = bookVolumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");
                    thumbnail = thumbnail.replace("http://", "https://");
                }

                JSONObject bookSalesInfo = items.getJSONObject(i).getJSONObject("saleInfo");
                String url = null;
                if (bookSalesInfo.has("buyLink")) {
                    url = bookSalesInfo.getString("buyLink");
                }

                JSONObject bookAccessInfo = items.getJSONObject(i).getJSONObject("accessInfo");
                boolean isPdf = bookAccessInfo.getJSONObject("pdf").getBoolean("isAvailable");
                boolean isEpub = bookAccessInfo.getJSONObject("epub").getBoolean("isAvailable");

                books.add(new Book(title, author, year, pages, url, thumbnail, isPdf, isEpub));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the books JSON results", e);
        }
        return books;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        String response = null;
        InputStream inputStream = null;

        final int READ_TIMEOUT = 10000;
        final int CONNECT_TIMEOUT = 15000;
        final int CORRECT_RESPONSE_CODE = 200;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == CORRECT_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                response = readFromInputStream(inputStream);
            } else {
                Log.e(TAG, "Error Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the books JSON results", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the Url", e);
        }
        return url;
    }

}
