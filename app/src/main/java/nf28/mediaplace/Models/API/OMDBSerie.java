package nf28.mediaplace.Models.API;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.Models.Serie;

public class OMDBSerie {
    @SerializedName("imdbID")
    private String id;
    @SerializedName("Title")
    private String title;
    @SerializedName("Released")
    private String releaseDate;
    @SerializedName("Runtime")
    private String duration;
    @SerializedName("Director")
    private String realisateurs;
    @SerializedName("Plot")
    private String description;
    @SerializedName("Language")
    private String langueOriginelle;
    @SerializedName("imdbRating")
    private String rating;
    @SerializedName("Genre")
    private String genres;
    @SerializedName("Poster")
    private String image;
    @SerializedName("totalSeasons")
    private String seasons;

    public OMDBSerie(String id, String title, String releaseDate, String duration, String realisateurs, String description, String langueOriginelle, String rating, String genres, String image, String seasons) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.realisateurs = realisateurs;
        this.description = description;
        this.langueOriginelle = langueOriginelle;
        this.rating = rating;
        this.genres = genres;
        this.image = image;
        this.seasons = seasons;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeasons() {
        return seasons;
    }

    public void setSeasons(String seasons) {
        this.seasons = seasons;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRealisateurs() {
        return realisateurs;
    }

    public void setRealisateurs(String realisateurs) {
        this.realisateurs = realisateurs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLangueOriginelle() {
        return langueOriginelle;
    }

    public void setLangueOriginelle(String langueOriginelle) {
        this.langueOriginelle = langueOriginelle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Serie toSerie() {
        int userRating = (StringUtils.isBlank(getRating()) || getRating().equalsIgnoreCase("N/A")) ? 0 : (int) Math.round(Double.parseDouble(this.getRating()));
        String image = (StringUtils.isBlank(getImage()) || this.getImage().equalsIgnoreCase("N/A")) ? "https://www.google.com" : this.getImage();
        String title = (StringUtils.isBlank(getTitle()) || getTitle().equalsIgnoreCase("N/A")) ? "" : getTitle();
        Date releaseDate = (StringUtils.isBlank(getReleaseDate()) || getReleaseDate().equalsIgnoreCase("N/A")) ? new Date() : new Date(getReleaseDate());
        Duration duration = (StringUtils.isBlank(getDuration()) || getDuration().equalsIgnoreCase("N/A")) ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(getDuration().replaceAll("[^0-9]", "")));
        ArrayList<String> realisateurs = (StringUtils.isBlank(getRealisateurs()) || getRealisateurs().equalsIgnoreCase("N/A")) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(getRealisateurs().split(",")));
        String description = (StringUtils.isBlank(getDescription()) || getDescription().equalsIgnoreCase("N/A")) ? "" : getDescription();
        String langueOriginelle = (StringUtils.isBlank(getLangueOriginelle()) || getLangueOriginelle().equalsIgnoreCase("N/A")) ? "" : getLangueOriginelle();
        ArrayList<String> genres = (StringUtils.isBlank(getGenres()) || getGenres().equalsIgnoreCase("N/A")) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(getGenres().split(",")));
        int seasons = (StringUtils.isBlank(getSeasons()) || getSeasons().equalsIgnoreCase("N/A")) ? 1 : Integer.parseInt(getSeasons());

        return new Serie(getId(), title, title, description, releaseDate, userRating, langueOriginelle, genres, new ArrayList<String>(), new ArrayList<Oeuvre>(), Oeuvre.TypeStatut.EnCours, -1, "", image, false, 0, duration, 0, realisateurs, new ArrayList<>(), seasons, new Date());
    }
}
