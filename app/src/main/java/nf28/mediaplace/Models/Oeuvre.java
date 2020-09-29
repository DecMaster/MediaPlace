package nf28.mediaplace.Models;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nf28.mediaplace.AsyncTask.SaveImageTask;
import nf28.mediaplace.Controllers.Toolbox;

public abstract class Oeuvre implements Serializable {

    // ENUM
    public enum TypeStatut{
        Termine("Terminé"),
        EnCours("En cours"),
        Abandonne("De côté"),
        Planifie("Planifié");

        private String NomStatut;
        private TypeStatut(String nom){
            NomStatut = nom;
        }

        @Override
        public String toString(){
            return NomStatut;
        }
    }

    // PROPRIETES FIXES
    private String id;
    private String titleVF;
    private String titleVO;
    private String description;
    private Date dateSortie;
    private Date dateAjout = Calendar.getInstance().getTime();
    private int noteUtilisateurs;
    private String langueOriginale;
    private ArrayList<String> genres = new ArrayList<String>();
    private ArrayList<String> motsCles = new ArrayList<String>();
    private transient ArrayList<Oeuvre> recommandations = null;

    // PROPRIETES MODIFIABLES
    private boolean inBiblio = false;
    private TypeStatut statut = TypeStatut.EnCours;
    private int note = -1;
    private String lien = null;
    private String cheminImage = null;
    private boolean OnlineImage = true;

    private boolean favori;

    // CONSTRUCTEUR

    public Oeuvre(String id, String titleVF, String titleVO, String description, Date dateSortie, int noteUtilisateurs, String langueOriginale, ArrayList<String> genres, ArrayList<String> motsCles, ArrayList<Oeuvre> recommandations, TypeStatut statut, int note, String lien, String cheminImage, boolean fav) {
        this.id=id;
        this.titleVF = titleVF;
        this.titleVO = titleVO;
        this.description = description;
        this.dateSortie = dateSortie;
        this.noteUtilisateurs = noteUtilisateurs;
        this.langueOriginale = langueOriginale;
        this.genres = genres;
        this.motsCles = motsCles;
        this.recommandations = recommandations;
        this.statut = statut;
        this.note = note;
        this.lien = lien;
        this.cheminImage = cheminImage;
        this.favori = fav;
    }

    public Oeuvre(){
    }

    // GET & SET

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

    public Date getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(Date dateS) {
        this.dateSortie = dateS;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateA) {
        this.dateAjout = dateA;
    }

    public int getNoteUtilisateurs() {
        return noteUtilisateurs;
    }

    public void setNoteUtilisateurs(int noteUtilisateurs) {
        this.noteUtilisateurs = noteUtilisateurs;
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

    public ArrayList<String> getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(ArrayList<String> motsCles) {
        this.motsCles = motsCles;
    }

    public ArrayList<Oeuvre> getRecommandations() {
        if(recommandations == null){
            setRecommandations();
        }
        return recommandations;
    }

    private void setRecommandations() {
        recommandations = new ArrayList<>();
        recommandations.add(this);
        recommandations.add(this);
        recommandations.add(this);
    }

    public TypeStatut getStatut() {
        return statut;
    }

    public void setStatut(TypeStatut statut) {
        this.statut = statut;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
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

    public void setCheminImage(String cheminImage, boolean onlineImage) {
        this.cheminImage = cheminImage;
        this.OnlineImage = onlineImage;
    }

    public String getId() {
        return id;
    }

    public boolean isFavori() {
        return favori;
    }

    public boolean isInBiblio(){
        return inBiblio;
    }

    public boolean isImageOnline(){
        return OnlineImage;
    }

    public void addInBiblio(){
        inBiblio = true;
        dateAjout = Calendar.getInstance().getTime();
        if(isImageOnline()){
            new SaveImageTask(this).execute(getCheminImage());
        }
        Toolbox.instance.userBiblio.getBiblio(this.getClass()).getListeOeuvres().add(this);
    }

    public void removeFromBiblio(){
        Bibliotheque b = Toolbox.instance.userBiblio.getBiblio(this.getClass());
        if(inBiblio){
            b.getListeOeuvres().remove(this);
            inBiblio = false;
        }
    }

    public abstract List<Pair<String, String>> getDetails();

    public void setFavori(boolean favori) {
        this.favori = favori;
    }

    public boolean changeFavori() {
        this.favori = !this.favori;
        return favori;
    }

}
