package nf28.mediaplace.Models;

import android.util.Pair;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nf28.mediaplace.Controllers.Toolbox;

public class Livre extends Oeuvre {

    // PROPRIETES
    private ArrayList<String> auteurs = new ArrayList<String>();

    // CONSTRUCTEUR
    public Livre(String id, String titleVF, String titleVO, String description, Date dateSortie, int noteUtilisateurs, String langueOriginale, ArrayList<String> genres, ArrayList<String> motsCles, ArrayList<Oeuvre> recommandations, TypeStatut statut, int note, String lien, String cheminImage, boolean fav, ArrayList<String> auteurs) {
        super(id, titleVF, titleVO, description, dateSortie, noteUtilisateurs, langueOriginale, genres, motsCles, recommandations, statut, note, lien, cheminImage, fav);
        this.auteurs = auteurs;
    }

    // A IMPLEMENTER
    public ArrayList<String> getAuteurs() {
        return auteurs;
    }

    @Override
    public List<Pair<String, String>> getDetails() {
        List<Pair<String, String>> res = new LinkedList<>();
        res.add(new Pair<>("Nombre de pages", "" /*ToDo*/));
        res.add(new Pair<>("Auteur", Toolbox.instance.listToString(auteurs)));
        res.add(new Pair<>("Date", new SimpleDateFormat("dd/mm/yyyy").format(getDateSortie())));
        res.add(new Pair<>("Langue originale", getLangueOriginale()));
        res.add(new Pair<>("Titre original", getTitleVO()));
        res.add(new Pair<>("Note des utilisateurs", Integer.toString(getNoteUtilisateurs())));
        res.add(new Pair<>("Genre", Toolbox.instance.listToString(getGenres())));
        res.add(new Pair<>("", ""));
        res.add(new Pair<>("", ""));
        return res;
    }
}
