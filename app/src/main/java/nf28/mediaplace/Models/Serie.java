package nf28.mediaplace.Models;

import android.util.Pair;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nf28.mediaplace.Controllers.Toolbox;

public class Serie extends Oeuvre {

    // PROPRIETES MODIFIABLE
    private int nbEpisodesVus = 0;

    // PROPRIETES FIXES
    private Duration Duree;
    private int nbEpisodesTotal;
    private ArrayList<String> realisateurs = new ArrayList<String>();
    private ArrayList<String> diffuseurs = new ArrayList<String>();
    private int nbSaisons;
    private Date dateDerniereDiffusion;

    // CONSTRUCTEUR
    public Serie(String id, String titleVF, String titleVO, String description, Date dateSortie, int noteUtilisateurs, String langueOriginale, ArrayList<String> genres, ArrayList<String> motsCles, ArrayList<Oeuvre> recommandations, TypeStatut statut, int note, String lien, String cheminImage, boolean fav, int nbEpisodesVus, Duration duree, int nbEpisodesTotal, ArrayList<String> realisateurs, ArrayList<String> diffuseurs, int nbSaisons, Date dateDerniereDiffusion) {
        super(id, titleVF, titleVO, description, dateSortie, noteUtilisateurs, langueOriginale, genres, motsCles, recommandations, statut, note, lien, cheminImage, fav);
        this.nbEpisodesVus = nbEpisodesVus;
        Duree = duree;
        this.nbEpisodesTotal = nbEpisodesTotal;
        this.realisateurs = realisateurs;
        this.diffuseurs = diffuseurs;
        this.nbSaisons = nbSaisons;
        this.dateDerniereDiffusion = dateDerniereDiffusion;
    }

    // AUTRES CONSTRUCTEUR


    // GET & SET
    public int getNbEpisodesVus() {
        return nbEpisodesVus;
    }

    public void setNbEpisodesVus(int nbEpisodesVus) {
        this.nbEpisodesVus = nbEpisodesVus;
    }

    public Duration getDuree() {
        return Duree;
    }

    public void setDuree(Duration duree) {
        Duree = duree;
    }

    public int getNbEpisodesTotal() {
        return nbEpisodesTotal;
    }

    public void setNbEpisodesTotal(int nbEpisodesTotal) {
        this.nbEpisodesTotal = nbEpisodesTotal;
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

    public int getNbSaisons() {
        return nbSaisons;
    }

    public void setNbSaisons(int nbSaisons) {
        this.nbSaisons = nbSaisons;
    }

    public Date getDateDerniereDiffusion() {
        return dateDerniereDiffusion;
    }

    public void setDateDerniereDiffusion(Date dateDerniereDiffusion) {
        this.dateDerniereDiffusion = dateDerniereDiffusion;
    }

    @Override
    public List<Pair<String, String>> getDetails() {
        List<Pair<String, String>> res = new LinkedList<>();
        res.add(new Pair<>("Durée moyenne d'un épisode", Duree.toString()));
        res.add(new Pair<>("Réalisateur", Toolbox.instance.listToString(realisateurs)));
        res.add(new Pair<>("Diffuseur", Toolbox.instance.listToString(diffuseurs)));
        res.add(new Pair<>("Nombre de saisons", Integer.toString(nbSaisons)));
        res.add(new Pair<>("Note des utilisateurs", Integer.toString(getNoteUtilisateurs())));
        res.add(new Pair<>("Note des critiques", "" /*ToDo*/));
        res.add(new Pair<>("Genre", Toolbox.instance.listToString(getGenres())));
        res.add(new Pair<>("Date de première diffusion", new SimpleDateFormat("dd/mm/yyyy").format(getDateSortie())));
        res.add(new Pair<>("Date de dernière diffusion", new SimpleDateFormat("dd/mm/yyyy").format(dateDerniereDiffusion)));
        return res;
    }
}
