package nf28.mediaplace.Models;

import java.util.ArrayList;

public class ShareSetup {

    // CHOIX BIBLIOTHEQUE
    public boolean movie = false;
    public boolean serie = false;
    public boolean book = false;
    public boolean game = false;

    // CHOIX CATEGORIE
    public enum Categorie{
        tous,
        encours,
        planifies,
        termines,
        decote,
        favoris
    }
    public Categorie cat = Categorie.tous;

    // CHOIX INFORMATIONS
    public boolean titre = false;
    public boolean real = false;
    public boolean cover = false;
    public boolean note = false;
    public boolean statut = false;
    public boolean date = false;

    // AUTRE
    public String biblioName = "Ma Bibliothèque";

    public ShareSetup() {}
    public ShareSetup(ShareSetup s) {
        this.movie = s.movie;
        this.serie = s.serie;
        this.book = s.book;
        this.game = s.game;
        this.titre = s.titre;
        this.real = s.real;
        this.cover = s.cover;
        this.note = s.note;
        this.statut = s.statut;
        this.date = s.date;
        this.cat = s.cat;
    }

    // INFORMATIONS
    public static ArrayList<String> getListeInfos(){
        ArrayList<String> liste = new ArrayList<String>();
        liste.add("Titre");
        liste.add("Réalisateur");
        liste.add("Jaquette");
        liste.add("Note");
        liste.add("Statut");
        liste.add("Date");
        return liste;
    }


}
