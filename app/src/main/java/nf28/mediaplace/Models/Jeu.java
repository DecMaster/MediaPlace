package nf28.mediaplace.Models;

import android.util.Pair;

import java.net.URI;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nf28.mediaplace.Controllers.Toolbox;

public class Jeu extends Oeuvre {

    // PROPRIETES FIXES
    private Duration Duree;
    private ArrayList<String> editeurs = new ArrayList<String>();
    private ArrayList<String> developpeurs = new ArrayList<String>();
    private ArrayList<String> plateformes = new ArrayList<String>();
    private String serieDeJeux;
    private ArrayList<String> modesDeJeux = new ArrayList<String>();

    // CONSTRUCTEUR
    public Jeu(String id, String titleVF, String titleVO, String description, Date dateSortie, int noteUtilisateurs, String langueOriginale, ArrayList<String> genres, ArrayList<String> motsCles, ArrayList<Oeuvre> recommandations, TypeStatut statut, int note, String lien, String cheminImage, boolean fav, Duration duree, ArrayList<String> editeurs, ArrayList<String> developpeurs, ArrayList<String> plateformes, String serieDeJeux, ArrayList<String> modesDeJeux) {
        super(id, titleVF, titleVO, description, dateSortie, noteUtilisateurs, langueOriginale, genres, motsCles, recommandations, statut, note, lien, cheminImage, fav);
        Duree = duree;
        this.editeurs = editeurs;
        this.developpeurs = developpeurs;
        this.plateformes = plateformes;
        this.serieDeJeux = serieDeJeux;
        this.modesDeJeux = modesDeJeux;
    }

    // AUTRES CONSTRUCTEUR


    // GET & SET
    public Duration getDuree() {
        return Duree;
    }

    public void setDuree(Duration duree) {
        Duree = duree;
    }

    public ArrayList<String> getEditeurs() {
        return editeurs;
    }

    public void setEditeurs(ArrayList<String> editeurs) {
        this.editeurs = editeurs;
    }

    public ArrayList<String> getDeveloppeurs() {
        return developpeurs;
    }

    public void setDeveloppeurs(ArrayList<String> developpeurs) {
        this.developpeurs = developpeurs;
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
    public List<Pair<String, String>> getDetails() {
        List<Pair<String, String>> res = new LinkedList<>();
        res.add(new Pair<>("Durée", Duree.toString()));
        res.add(new Pair<>("Editeurs", Toolbox.instance.listToString(editeurs)));
        res.add(new Pair<>("Développeurs", Toolbox.instance.listToString(developpeurs)));
        res.add(new Pair<>("Plateformes", Toolbox.instance.listToString(plateformes)));
        res.add(new Pair<>("Franchise", serieDeJeux));
        res.add(new Pair<>("Date", new SimpleDateFormat("dd/mm/yyyy").format(getDateSortie())));
        res.add(new Pair<>("Mode de Jeux", Toolbox.instance.listToString(modesDeJeux)));
        res.add(new Pair<>("Note des utilisateurs", Integer.toString(getNoteUtilisateurs())));
        res.add(new Pair<>("Genre", Toolbox.instance.listToString(getGenres())));
        return res;
    }
}
