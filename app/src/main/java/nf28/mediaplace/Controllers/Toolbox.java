package nf28.mediaplace.Controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.FileOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import nf28.mediaplace.MainActivity;
import nf28.mediaplace.Models.Bibliotheques;
import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Jeu;
import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.Models.Serie;
import nf28.mediaplace.Models.ShareSetup;
import nf28.mediaplace.ui.share.ShareFragment;

public class Toolbox {

    // SINGLETON
    public static final Toolbox instance = new Toolbox();

    // FONCTIONNALITES
    public static boolean toggleAnimations = true;
    public static boolean lockPortraitOrientation = true;

    // DONNEES
    public Bibliotheques userBiblio;
    public int CHANGE_WORK_IMAGE_FROM_GALLERY = 7;

    // PROPRIETES
    public String appPath;
    public ActionBar actionBar;
    public MainActivity activity;
    public Toolbar toolbar;
    public Map<Class, Integer> tabsColor = new HashMap<>();
    private Map<Class, String> tabsFilePath = new HashMap<>();
    private Map<Class, String> tabsImgFilePath = new HashMap<>();

    // METHODES
    private Toolbox() {}

    public void InitSampleData() {

        // INIT
        Date fakeDate = null;
        Duration fakeDuree = null;
        String fakeURI = null;
        String fakeImage = null;
        ArrayList<String> fakeGenres = new ArrayList<String>();
        ArrayList<String> fakeMotsCles = new ArrayList<String>();
        ArrayList<Oeuvre> fakeRecommand = new ArrayList<Oeuvre>();
        ArrayList<String> fakeReal = new ArrayList<String>();
        ArrayList<String> fakeDiffuseur = new ArrayList<String>();
        try{
            fakeDate = new SimpleDateFormat("dd/MM/yyyy").parse("26/02/1993");
            //fakeDuree = new SimpleDateFormat("hh:mm:ss").parse("01:30:06");
            
            fakeURI = "https://www.google.fr/";
            fakeImage = Uri.parse("android.resource://schemas.android.com/mipmap/placeholder.png").toString();

            fakeGenres.add("Genre1");
            fakeGenres.add("Genre2");
            fakeMotsCles.add("MotCles1");
            fakeMotsCles.add("MotCles2");
            fakeReal.add("Realisateur1");
            fakeReal.add("Realisateur2");
            fakeDiffuseur.add("Diffuseur1");
            fakeDiffuseur.add("Diffuseur2");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // CREATION DES BIBLIO
        userBiblio = new Bibliotheques();
        tabsFilePath.put(Film.class, "biblioFilms.json");
        tabsFilePath.put(Jeu.class, "biblioJeux.json");
        tabsFilePath.put(Livre.class, "biblioLivres.json");
        tabsFilePath.put(Serie.class, "biblioSeries.json");

        tabsImgFilePath.put(Film.class, "Medias/Films/");
        tabsImgFilePath.put(Jeu.class, "Medias/Jeux/");
        tabsImgFilePath.put(Livre.class, "Medias/Livres/");
        tabsImgFilePath.put(Serie.class, "Medias/Series/");

        // CREATION DES FILMS
        try{
            userBiblio.loadAll();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getBiblioPath(Class T){

        if(tabsFilePath.containsKey(T)){
            return appPath + tabsFilePath.get(T);
        }
        return appPath;
    }

    public String getNewImgPath(Oeuvre work){
        if(tabsFilePath.containsKey(work.getClass())){
            String folder = appPath + tabsFilePath.get(work.getClass());
            return (folder + work.getTitleVF() + work.getDateSortie() + ".png").replace(' ', '-');
        }
        return appPath + "error.png";
    }

    public String listToString(List<?> list) {
        int i = 0;
        String result = "";
        if(list.size() != 0){
            for (i = 0; i < list.size() - 1; i++) {
                result += list.get(i) + ", ";
                if(result.length() + list.get(i+1).toString().length() > 30){
                    result += "...";
                    return result;
                }
            }
            result += list.get(i);
        }
        else{
            result += "N/A";
        }
        return result;
    }

    public void playWork(Oeuvre work, Fragment frag){
        String link = work.getLien();
        if(link == null || link.isEmpty()){
            Toast.makeText(frag.getContext(),"Vous n'avez pas saisi de lien !", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            Intent i = new Intent((Intent.ACTION_VIEW));
            i.setData(Uri.parse(link));
            frag.startActivity(i);
        }
        catch(Exception e){
            Toast.makeText(frag.getContext(),"Le lien saisi n'est pas valide", Toast.LENGTH_SHORT).show();
        }
    }
}
