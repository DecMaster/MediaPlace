package nf28.mediaplace.Models;
import java.util.ArrayList;

// Le T sera ici soit : Film, Jeux, Serie ou Livre

public class Bibliotheque<T extends Oeuvre> {

    // PROPRIETES
    private ArrayList<T> listeOeuvres;

    // CONSTRUCTEUR
    public Bibliotheque() {
        this.listeOeuvres = new ArrayList<T>();
    }
    public Bibliotheque(ArrayList<T> listeOeuvres) {
        this.listeOeuvres = listeOeuvres;
    }

    // GET & SET
    public ArrayList<T> getListeOeuvres() {
        return listeOeuvres;
    }

    public void setListeOeuvres(ArrayList<T> listeOeuvres) {
        this.listeOeuvres = listeOeuvres;
    }
}