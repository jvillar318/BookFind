package com.example.android.bookfind;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

/**
 * Helper methods related to requesting and receiving Book data from google books.
 */
public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }
    /**
     * Query the Books dataset
     * @return (List<BooksList>) object to represent a single book.
     */
    public static List<Book> fetchDataFromInternet(String requestUrl)throws IOException{
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpConnection(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in input stream in use it methods", e);
        }
        List<Book>books = extractDataFromJSON(jsonResponse);

        return books;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpConnection(URL url)throws IOException{
        String jsonResponse = "";
        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromInputStream(InputStream inputStream)throws IOException{
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
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractDataFromJSON(String bookJSON)throws IOException{

        if(TextUtils.isEmpty(bookJSON))
            return null;
        // Create an empty ArrayList that we can start adding Books to
        List<Book> listOfBooks = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++){
                JSONObject currentBook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String title = volumeInfo.getString("title");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                String author = "";
                if (volumeInfo.has("authors")){
                    for (int x = 0; x < authors.length(); x++) {
                        author += authors.getString(x) + "\n";
                    }
                }else {
                    author = "Not Available";
                }
                String publisher;
                if (volumeInfo.has("publisher")){
                    publisher = volumeInfo.getString("publisher");
                }else {
                    publisher = "Not Available";
                }
                String publishedDate;
                if (volumeInfo.has("publishedDate")){
                    publishedDate = volumeInfo.getString("publishedDate");
                } else {
                    publishedDate = "Not Available";
                }
                int ratingBar;
                if (volumeInfo.has("averageRating")) {
                    ratingBar = volumeInfo.getInt("averageRating");
                }else {
                    ratingBar = 0;
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imageLinks.getString("smallThumbnail");
                Bitmap image = getBitmapFromUrl(imageUrl);
                Bitmap resizedImage = getResizedBitmap(image, 200, 125);


                String description;
                if (volumeInfo.has("description")){
                    description = volumeInfo.getString("description");
                }else {
                    description = "Not Available";
                }
                String url = volumeInfo.getString("previewLink");

                Book book = new Book(title, author, publisher, publishedDate, ratingBar, resizedImage, description, url);
                listOfBooks.add(book);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Book JSON results", e);
        }

        // Return the list of Books
        return listOfBooks;

    }
    private static Bitmap getBitmapFromUrl(String imgUrl)throws IOException{
        URL url = createUrl(imgUrl);

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200){
                inputStream = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error in image response Code: " + httpURLConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Error while fetch the image", e);
        }finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return bitmap;
    }

    private static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }
}