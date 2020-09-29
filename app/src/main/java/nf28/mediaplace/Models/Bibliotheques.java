package nf28.mediaplace.Models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import nf28.mediaplace.Controllers.Toolbox;

public class Bibliotheques {

    // PROPRIETES
    private Bibliotheque<Livre> biblioLivres;
    private Bibliotheque<Film> biblioFilms;
    private Bibliotheque<Serie> biblioSeries;
    private Bibliotheque<Jeu> biblioJeux;

    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.create();


    // CONSTRUCTEUR
    public Bibliotheques() {
        this.biblioLivres = new Bibliotheque<>();
        this.biblioFilms = new Bibliotheque<>();
        this.biblioSeries = new Bibliotheque<>();
        this.biblioJeux = new Bibliotheque<>();
    }

    public Bibliotheques(Bibliotheque biblioLivres, Bibliotheque biblioFilms, Bibliotheque biblioSeries, Bibliotheque biblioJeux) {
        this.biblioLivres = biblioLivres;
        this.biblioFilms = biblioFilms;
        this.biblioSeries = biblioSeries;
        this.biblioJeux = biblioJeux;
    }

    // GET & SET
    public Bibliotheque getBiblioLivres() {
        return biblioLivres;
    }

    public void setBiblioLivres(Bibliotheque biblioLivres) {
        this.biblioLivres = biblioLivres;
    }

    public Bibliotheque getBiblioFilms() {
        return biblioFilms;
    }

    public void setBiblioFilms(Bibliotheque biblioFilms) {
        this.biblioFilms = biblioFilms;
    }

    public Bibliotheque getBiblioSeries() {
        return biblioSeries;
    }

    public void setBiblioSeries(Bibliotheque biblioSeries) {
        this.biblioSeries = biblioSeries;
    }

    public Bibliotheque getBiblioJeux() {
        return biblioJeux;
    }

    public void setBiblioJeux(Bibliotheque biblioJeux) {
        this.biblioJeux = biblioJeux;
    }

    public Bibliotheque getBiblio(Class oeuvreType){
        return oeuvreType == Film.class ? getBiblioFilms()
                : oeuvreType ==  Jeu.class ? getBiblioJeux()
                : oeuvreType ==  Livre.class ? getBiblioLivres()
                : oeuvreType ==  Serie.class ? getBiblioSeries()
                : null;
    }

    public void load(Class oeuvreType) {
        try{
            InputStream iS = new FileInputStream(Toolbox.instance.getBiblioPath(oeuvreType));
            InputStreamReader iSR = new InputStreamReader(iS);
            BufferedReader bR = new BufferedReader(iSR);

            Type listType = null;
            if(oeuvreType.equals(Film.class))
                listType = new TypeToken<ArrayList<Film>>(){}.getType();
            if(oeuvreType.equals(Livre.class))
                listType = new TypeToken<ArrayList<Livre>>(){}.getType();
            if(oeuvreType.equals(Jeu.class))
                listType = new TypeToken<ArrayList<Jeu>>(){}.getType();
            if(oeuvreType.equals(Serie.class))
                listType = new TypeToken<ArrayList<Serie>>(){}.getType();

            ArrayList<Oeuvre> list = gson.fromJson(bR, listType);
            if(list != null){
                getBiblio(oeuvreType).setListeOeuvres(list);
            }
        } catch (FileNotFoundException e) {
            System.out.println("La bibliothèque " + oeuvreType.toString() + " n'a pas encore été sauvegardée.");
        }

    }

    public void save(Class oeuvreType) {
        FileOutputStream fOS = null;
        try {
            fOS = new FileOutputStream(Toolbox.instance.getBiblioPath(oeuvreType));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Erreur de sauvegarde - le fichier de la bibliothèque " + oeuvreType.toString() + " est introuvable.");
            return;
        }

        Bibliotheque biblio = getBiblio(oeuvreType);
        try {
            fOS.write(gson.toJson(biblio.getListeOeuvres()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'écriture JSON de la bibliothèque " + oeuvreType.toString());
        }
    }

    public void loadAll(){
        this.load(Film.class);
        this.load(Livre.class);
        this.load(Jeu.class);
        this.load(Serie.class);
    }

    public void saveAll(){
        save(Film.class);
        save(Livre.class);
        save(Jeu.class);
        save(Serie.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Oeuvre getOeuvre(String id){
        Oeuvre work;
        work = biblioLivres.getListeOeuvres().stream().filter(x -> x.getId() != null && x.getId().equals(id)).findFirst().orElse(null);
        if(work != null)
            return work;

        work = biblioFilms.getListeOeuvres().stream().filter(x -> x.getId() != null && x.getId().equals(id)).findFirst().orElse(null);
        if(work != null)
            return work;

        work = biblioJeux.getListeOeuvres().stream().filter(x -> x.getId() != null && x.getId().equals(id)).findFirst().orElse(null);
        if(work != null)
            return work;

        work = biblioSeries.getListeOeuvres().stream().filter(x -> x.getId() != null && x.getId().equals(id)).findFirst().orElse(null);
        return work;
    }
}
