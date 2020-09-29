package nf28.mediaplace.Models.API;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Oeuvre;

public class GoogleBook {

    private String id;
    @SerializedName("title")
    private String titleVF;

    private String titleVO;
    @SerializedName("description")
    private String description;

    @SerializedName("publishedDate")
    private String dateSortie;
    @SerializedName("language")
    private String langueOriginale;
    @SerializedName("categories")
    @JsonAdapter(GenresDeserializer.class)
    private ArrayList<String> genres;

    // PROPRIETES MODIFIABLES

    @SerializedName("infoLink")
    private String lien;

    @SerializedName("imageLinks")
    @JsonAdapter(ImageDeserializer.class)
    private String cheminImage;

    @SerializedName("authors")
    @JsonAdapter(AuteurDeserializer.class)
    private ArrayList<String> auteurs;


    private static class GenresDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonArray jsonArray = json.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray) {
                result.add(jsonElement.getAsString());
            }
            return result;
        }
    }


    private static class ImageDeserializer implements JsonDeserializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.getAsJsonObject().get("smallThumbnail").getAsString();
        }
    }

    private static class AuteurDeserializer implements JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonArray jsonArray = json.getAsJsonArray();
            List<String> result = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray) {
                result.add(jsonElement.getAsString());
            }
            return result;
        }
    }

    public GoogleBook() {
    }

    public GoogleBook(String id, String titleVF, String titleVO, String description, String dateSortie, String langueOriginale, ArrayList<String> genres, String lien, String cheminImage, ArrayList<String> auteurs) {
        this.id = id;
        this.titleVF = titleVF;
        this.titleVO = titleVO;
        this.description = description;
        this.dateSortie = dateSortie;
        this.langueOriginale = langueOriginale;
        this.genres = genres;
        this.lien = lien;
        this.cheminImage = cheminImage;
        this.auteurs = auteurs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitleVF() {
        return titleVF;
    }

    public void setTitleVF(String titleVF) {
        this.titleVF = titleVF;
    }

    public String getTitleVO() {
        return titleVO;
    }

    public void setTitleVO(String titleVO) {
        this.titleVO = titleVO;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(String dateSortie) {
        this.dateSortie = dateSortie;
    }

    public String getLangueOriginale() {
        return langueOriginale;
    }

    public void setLangueOriginale(String langueOriginale) {
        this.langueOriginale = langueOriginale;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
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

    public ArrayList<String> getAuteurs() {
        return auteurs;
    }

    public void setAuteurs(ArrayList<String> auteurs) {
        this.auteurs = auteurs;
    }

    @Override
    public String toString() {
        return "GoogleBook{" +
                "titleVF='" + titleVF + '\'' +
                ", titleVO='" + titleVO + '\'' +
                ", description='" + description + '\'' +
                ", dateSortie='" + dateSortie + '\'' +
                ", langueOriginale='" + langueOriginale + '\'' +
                ", genres=" + genres +
                ", lien='" + lien + '\'' +
                ", cheminImage='" + cheminImage + '\'' +
                ", auteurs=" + auteurs +
                '}';
    }

    public Livre toLivre() {
        String titleVF = StringUtils.isBlank(this.getTitleVF()) ? "" : this.getTitleVF();
        String titleVO = titleVF;
        String description = StringUtils.isBlank(this.getDescription()) ? "" : this.getDescription();
        Date dateSortie = new Date();
        try {
            dateSortie = StringUtils.isBlank(this.getDateSortie()) ? new Date() : DateUtils.parseDate(getDateSortie(), "yyyy", "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ssXXX", "yyyy-MM");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String langueOriginale = StringUtils.isBlank(this.getLangueOriginale()) ? "" : this.getLangueOriginale();
        ArrayList<String> genres = (getGenres() == null || this.getGenres().isEmpty()) ? new ArrayList<>() : this.getGenres();
        String lien = StringUtils.isBlank(this.getLien()) ? "" : this.getLien();
        String cheminImage = StringUtils.isBlank(this.getCheminImage()) ? "https://www.google.com" : this.getCheminImage();
        ArrayList<String> auteurs = (getAuteurs() == null || this.getAuteurs().isEmpty()) ? new ArrayList<>() : this.getAuteurs();


        return new Livre(getId(),titleVF, titleVO, description, dateSortie, 5, langueOriginale, genres, new ArrayList<>(), new ArrayList<>(), Oeuvre.TypeStatut.EnCours, -1, lien, cheminImage, false, auteurs);
    }
}
