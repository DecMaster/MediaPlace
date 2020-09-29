package nf28.mediaplace.Models.API;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nf28.mediaplace.Models.Jeu;
import nf28.mediaplace.Models.Oeuvre;

public class IGDBGame {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String title;
    @SerializedName("first_release_date")
    private String releaseDate;
    @SerializedName("rating")
    private String userRating;
    @SerializedName("summary")
    private String description;


    @SerializedName("genres")
    @JsonAdapter(GenresDeserializer.class)
    private ArrayList<String> genres = new ArrayList<String>();
    @SerializedName("keywords")
    @JsonAdapter(KeywordsDeserializer.class)
    private ArrayList<String> motsCles = new ArrayList<String>();
    @SerializedName("similar_games")
    @JsonAdapter(SimilarDeserializer.class)
    private ArrayList<String> recommandations = new ArrayList<String>();

    // PROPRIETES MODIFIABLES
    @SerializedName("url")
    private String lien;
    @SerializedName("cover")
    @JsonAdapter(ImageDeserializer.class)
    private String cheminImage;

    @SerializedName("involved_companies")
    @JsonAdapter(CompaniesDeserializer.class)
    private ArrayList<String> editeurs = new ArrayList<String>();

    @SerializedName("platforms")
    @JsonAdapter(PlatformsDeserializer.class)
    private ArrayList<String> plateformes = new ArrayList<String>();
    @SerializedName("collection")
    @JsonAdapter(CollectionDeserializer.class)
    private String serieDeJeux;
    @SerializedName("game_modes")
    @JsonAdapter(GamemodesDeserializer.class)
    private ArrayList<String> modesDeJeux = new ArrayList<String>();

    public IGDBGame() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<String> getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(ArrayList<String> motsCles) {
        this.motsCles = motsCles;
    }

    public ArrayList<String> getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(ArrayList<String> recommandations) {
        this.recommandations = recommandations;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public void setCheminImage(String cheminImage) {
        this.cheminImage = cheminImage;
    }

    public ArrayList<String> getEditeurs() {
        return editeurs;
    }

    public void setEditeurs(ArrayList<String> editeurs) {
        this.editeurs = editeurs;
    }

    public ArrayList<String> getPlateformes() {
        return plateformes;
    }

    public void setPlateformes(ArrayList<String> plateformes) {
        this.plateformes = plateformes;
    }

    public String getSerieDeJeux() {
        return serieDeJeux;
    }

    public void setSerieDeJeux(String serieDeJeux) {
        this.serieDeJeux = serieDeJeux;
    }

    public ArrayList<String> getModesDeJeux() {
        return modesDeJeux;
    }

    public void setModesDeJeux(ArrayList<String> modesDeJeux) {
        this.modesDeJeux = modesDeJeux;
    }


    @Override
    public String toString() {
        return "IGDBGame{" +
                "title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", userRating='" + userRating + '\'' +
                ", description='" + description + '\'' +
                ", genres=" + genres +
                ", motsCles=" + motsCles +
                ", recommandations=" + recommandations +
                ", lien='" + lien + '\'' +
                ", cheminImage='" + cheminImage + '\'' +
                ", editeurs=" + editeurs +
                ", plateformes=" + plateformes +
                ", serieDeJeux='" + serieDeJeux + '\'' +
                ", modesDeJeux=" + modesDeJeux +
                '}';
    }


    public static class GenresDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement json : jsonArray) {
                result.add(((JsonObject) json).get("name").getAsString());
            }
            return result;
        }
    }

    public static class KeywordsDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement json : jsonArray) {
                result.add(((JsonObject) json).get("name").getAsString());
            }
            return result;
        }
    }


    public static class SimilarDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement json : jsonArray) {
                result.add(((JsonObject) json).get("name").getAsString());
            }
            return result;
        }
    }

    public static class ImageDeserializer implements JsonDeserializer<String> {

        @Override
        public String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return jsonElement.getAsJsonObject().get("url").getAsString();
        }
    }

    public static class CompaniesDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement json : jsonArray) {
                result.add(((JsonObject) json).get("company").getAsJsonObject().get("name").getAsString());
            }
            return result;
        }
    }

    public static class PlatformsDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement json : jsonArray) {
                result.add(((JsonObject) json).get("name").getAsString());
            }
            return result;
        }
    }

    public static class CollectionDeserializer implements JsonDeserializer<String> {

        @Override
        public String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return jsonElement.getAsJsonObject().get("name").getAsString();
        }
    }

    public static class GamemodesDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement json : jsonArray) {
                result.add(((JsonObject) json).get("name").getAsString());
            }
            return result;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Oeuvre toJeu() throws URISyntaxException {

        int rating = StringUtils.isBlank(this.getUserRating()) ? 0 : (int) Math.round(Double.parseDouble(this.getUserRating()));
        int date = StringUtils.isBlank(this.getReleaseDate()) ? 0 : (int) Math.round(Double.parseDouble(this.getReleaseDate()));
        String link = StringUtils.isBlank(this.getLien()) ? "" : this.getLien();
        String image = StringUtils.isBlank(this.getCheminImage()) ? "https://www.google.com" : "https:" + this.getCheminImage();

        return new Jeu(this.getId(),this.getTitle(), this.getTitle(), this.getDescription(), new Date((long)date*1000L), rating, "", this.getGenres(), this.getMotsCles(), new ArrayList<>(), Oeuvre.TypeStatut.EnCours, -1, link, image, false, Duration.ZERO, this.getEditeurs(), this.getEditeurs(), this.getPlateformes(), this.getSerieDeJeux(), this.getModesDeJeux());
    }

}
