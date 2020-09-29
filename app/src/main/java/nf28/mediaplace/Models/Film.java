package nf28.mediaplace.Models;

import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nf28.mediaplace.Controllers.Toolbox;

public class Film extends Oeuvre {

    // PROPRIETES FIXES
    private Duration Duree;
    private ArrayList<String> realisateurs = new ArrayList<String>();
    private ArrayList<String> diffuseurs = new ArrayList<String>();
    private float budget;
    private float recette;

    // CONSTRUCTEUR PAR DEFAUT
    public Film(String id, String titleVF, String titleVO, String description, Date dateSortie, int noteUtilisateurs, String langueOriginale, ArrayList<String> genres, ArrayList<String> motsCles, ArrayList<Oeuvre> recommandations, TypeStatut statut, int note, String lien, String cheminImage, boolean fav, Duration duree, ArrayList<String> realisateurs, ArrayList<String> diffuseurs, float budget, float recette) {
        super(id, titleVF, titleVO, description, dateSortie, noteUtilisateurs, langueOriginale, genres, motsCles, recommandations, statut, note, lien, cheminImage, fav);
        Duree = duree;
        this.realisateurs = realisateurs;
        this.diffuseurs = diffuseurs;
        this.budget = budget;
        this.recette = recette;
    }

    // AUTRES CONSTRUCTEUR


    // GET & SET
    public Duration getDuree() {
        return Duree;
    }

    public void setDuree(Duration duree) {
        Duree = duree;
    }

    public ArrayList<String> getRealisateurs() {
        return realisateurs;
    }

    public void setRealisateurs(ArrayList<String> realisateurs) {
        this.realisateurs = realisateurs;
    }

    public ArrayList<String> getDiffuseurs() {
        return diffuseurs;
    }

    public void setDiffuseurs(ArrayList<String> diffuseurs) {
        this.diffuseurs = diffuseurs;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public float getRecette() {
        return recette;
    }

    public void setRecette(float recette) {
        this.recette = recette;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Pair<String, String>> getDetails() {
        List<Pair<String, String>> res = new LinkedList<>();
        res.add(new Pair<>("Durée", LocalTime.MIDNIGHT.plus(Duree).format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        res.add(new Pair<>("Réalisateur", Toolbox.instance.listToString(realisateurs)));
        res.add(new Pair<>("Diffuseur", Toolbox.instance.listToString(diffuseurs)));
        res.add(new Pair<>("Budget", Float.toString(budget)));
        res.add(new Pair<>("Recette", Float.toString(recette)));
        res.add(new Pair<>("Date", new SimpleDateFormat("dd/mm/yyyy").format(getDateSortie())));
        res.add(new Pair<>("Note des utilisateurs", Integer.toString(getNoteUtilisateurs())));
        res.add(new Pair<>("Note des critiques", "" /*ToDo*/));
        res.add(new Pair<>("Genre", Toolbox.instance.listToString(getGenres())));
        return res;
    }
}
