package nf28.mediaplace.Controllers;


import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import nf28.mediaplace.Models.API.GoogleBook;
import nf28.mediaplace.Models.API.IGDBGame;
import nf28.mediaplace.Models.API.OMDBMovie;
import nf28.mediaplace.Models.API.OMDBSerie;
import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Oeuvre;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIReader {

    private static final String moviesAndSeriesLink = "https://www.omdbapi.com/";
    private static final String gamesLink = "https://api-v3.igdb.com";
    private static final String gamesKey = "4546d59ae1c6bebc1058d0e049f16ec3";
    private static final String moviesAndSeriesKey = "c0ce614d";
    private static final String booksLink = "https://www.googleapis.com/books/v1/volumes";
    private static final String maxNumResults = "25";


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Oeuvre> getAllGames(String query) throws IOException {

        Vector<Oeuvre> games = new Vector<>();
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(("fields name,first_release_date,rating,summary,genres.name,keywords.name,similar_games.name,url,cover.url,involved_companies.company.name,platforms.name,collection.name,game_modes.name; limit " + maxNumResults + "; search \"+" + query + "\";").getBytes());
        Request request = new Request.Builder()
                .url(gamesLink + "/games")
                .addHeader("user-key", gamesKey)
                .addHeader("Accept", "application/json")
                .post(body)
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.requireNonNull(response).isSuccessful()) {
            String json = null;
            try {
                json = Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            List<IGDBGame> igdbGames = gson.fromJson(json, new TypeToken<List<IGDBGame>>() {
            }.getType());

            for (IGDBGame igdbGame : igdbGames) {
                try {
                    games.add(igdbGame.toJeu());

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        return games;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Oeuvre> getAllMovies(String query) throws IOException {

        Vector<Oeuvre> movies = new Vector<>();
        Vector<OMDBMovie> omdbMovies = new Vector<>();
        List<String> movieTitles = new ArrayList<>();
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(moviesAndSeriesLink + "?apikey=" + moviesAndSeriesKey + "&s=" + query + "&type=movie")
                .addHeader("Accept", "application/json")
                .get()
                .build();
        Response response = httpClient.newCall(request).execute();

        if (Objects.requireNonNull(response).isSuccessful()) {
            String json = null;
            json = Objects.requireNonNull(response.body()).string();


            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String goodResult = obj.get("Response").getAsString();
            if (goodResult.equalsIgnoreCase("True")) {
                JsonArray result = obj.getAsJsonArray("Search");
                for (JsonElement currentJson : result) {
                    movieTitles.add(currentJson.getAsJsonObject().get("Title").getAsString());
                }

            }
            int count = 2;
            while ((count - 1) * 10 < Integer.parseInt(maxNumResults) && goodResult.equalsIgnoreCase("True")) {
                request = new Request.Builder()
                        .url(moviesAndSeriesLink + "?apikey=" + moviesAndSeriesKey + "&s=" + query + "&type=movie&page=" + count)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();
                response = httpClient.newCall(request).execute();

                if (Objects.requireNonNull(response).isSuccessful()) {

                    json = Objects.requireNonNull(response.body()).string();

                    obj = JsonParser.parseString(json).getAsJsonObject();
                    goodResult = obj.get("Response").getAsString();
                    if (goodResult.equalsIgnoreCase("True")) {
                        JsonArray result = obj.getAsJsonArray("Search");
                        for (JsonElement currentJson : result) {
                            movieTitles.add(currentJson.getAsJsonObject().get("Title").getAsString());
                        }

                    }
                    count++;
                }
            }

            for (String currentMovie : movieTitles) {
                request = new Request.Builder()
                        .url(moviesAndSeriesLink + "?apikey=" + moviesAndSeriesKey + "&t=" + currentMovie + "&type=movie")
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                response = httpClient.newCall(request).execute();
                if (Objects.requireNonNull(response).isSuccessful()) {
                    json = Objects.requireNonNull(response.body()).string();
                    Gson gson = new Gson();
                    OMDBMovie parsedMovie = gson.fromJson(json, OMDBMovie.class);
                    omdbMovies.add(parsedMovie);
                }
            }
        }

        for (OMDBMovie m : omdbMovies) {
            movies.add(m.toFilm());
        }
        return movies;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Oeuvre> getAllSeries(String query) throws IOException {

        Vector<Oeuvre> series = new Vector<>();
        Vector<OMDBSerie> omdbSeries = new Vector<>();
        List<String> serieTitles = new ArrayList<>();
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(moviesAndSeriesLink + "?apikey=" + moviesAndSeriesKey + "&s=" + query + "&type=series")
                .addHeader("Accept", "application/json")
                .get()
                .build();
        Response response = httpClient.newCall(request).execute();

        if (Objects.requireNonNull(response).isSuccessful()) {
            String json = null;
            json = Objects.requireNonNull(response.body()).string();


            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String goodResult = obj.get("Response").getAsString();
            if (goodResult.equalsIgnoreCase("True")) {
                JsonArray result = obj.getAsJsonArray("Search");
                for (JsonElement currentJson : result) {
                    serieTitles.add(currentJson.getAsJsonObject().get("Title").getAsString());
                }

            }
            int count = 2;
            while ((count - 1) * 10 < Integer.parseInt(maxNumResults) && goodResult.equalsIgnoreCase("True")) {
                request = new Request.Builder()
                        .url(moviesAndSeriesLink + "?apikey=" + moviesAndSeriesKey + "&s=" + query + "&type=series&page=" + count)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();
                response = httpClient.newCall(request).execute();

                if (Objects.requireNonNull(response).isSuccessful()) {

                    json = Objects.requireNonNull(response.body()).string();

                    obj = JsonParser.parseString(json).getAsJsonObject();
                    goodResult = obj.get("Response").getAsString();
                    if (goodResult.equalsIgnoreCase("True")) {
                        JsonArray result = obj.getAsJsonArray("Search");
                        for (JsonElement currentJson : result) {
                            serieTitles.add(currentJson.getAsJsonObject().get("Title").getAsString());
                        }

                    }
                    count++;
                }
            }

            for (String currentSerie : serieTitles) {
                request = new Request.Builder()
                        .url(moviesAndSeriesLink + "?apikey=" + moviesAndSeriesKey + "&t=" + currentSerie + "&type=series")
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                response = httpClient.newCall(request).execute();
                if (Objects.requireNonNull(response).isSuccessful()) {
                    json = Objects.requireNonNull(response.body()).string();
                    Gson gson = new Gson();
                    OMDBSerie parsedSerie = gson.fromJson(json, OMDBSerie.class);
                    omdbSeries.add(parsedSerie);
                }
            }
        }

        for (OMDBSerie s : omdbSeries) {
            series.add(s.toSerie());
        }
        return series;
    }

    public static List<Oeuvre> getAllBooks(String query) {
        List<Oeuvre> livres = new ArrayList<>();
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(booksLink + "?q=" + query + "&maxResults=" + maxNumResults)
                .addHeader("Accept", "application/json")
                .get()
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.requireNonNull(response).isSuccessful()) {
            String json = null;
            try {
                json = Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            assert json != null;
            JsonArray array = new JsonArray();

            try{ array= JsonParser.parseString(json).getAsJsonObject().get("items").getAsJsonArray();}
            catch(Exception e){ e.printStackTrace(); }

            List<GoogleBook> googleBooks = new ArrayList<>();
            for (JsonElement jsonElement : array) {
                String id = jsonElement.getAsJsonObject().get("id").getAsString();
                jsonElement = jsonElement.getAsJsonObject().get("volumeInfo").getAsJsonObject();
                GoogleBook book = gson.fromJson(jsonElement, GoogleBook.class);
                book.setId(id);
                googleBooks.add(book);
            }

            for (GoogleBook book : googleBooks) {
                livres.add(book.toLivre());
            }

        }
        return livres;

    }

    public static String getMoviesAndSeriesLink() {
        return moviesAndSeriesLink;
    }

    public static String getGamesLink() {
        return gamesLink;
    }

    public static String getBooksLink() {
        return booksLink;
    }

    public static String getGamesKey() {
        return gamesKey;
    }
}
