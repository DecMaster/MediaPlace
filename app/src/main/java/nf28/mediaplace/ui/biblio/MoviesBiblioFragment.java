package nf28.mediaplace.ui.biblio;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import nf28.mediaplace.Adapters.OeuvreAdapter;
import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.R;

public class MoviesBiblioFragment extends BiblioFragment {

    // PROPRIETES
    private ArrayList<Film> listeOeuvreActuelle;
    private ColorDrawable headerColor;

    // METHODES
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState);
        mContext = this.getContext();

        // CHANGEMENT TEXTE & GRILLE
        gridView = root.findViewById(R.id.grid_biblio);

        // TRI PAR DEFAUT : Sinon on garde l'option de tri précédente
        if(triActuel == null){
            triActuel = OptionsTri.alphabet;
        }

        // ADAPTATION DU CONTENU
        listeOeuvreActuelle = (ArrayList<Film>) Toolbox.instance.userBiblio.getBiblioFilms().getListeOeuvres();
        TrierListe(triActuel);
        OeuvreAdapter<Film> oeuvreAdapter = new OeuvreAdapter(this.getContext(), listeOeuvreActuelle);
        gridView.setAdapter(oeuvreAdapter);
        adapter = oeuvreAdapter;
        messageBiblioVide.setText(R.string.biblio_noMovie);
        gridView.setEmptyView(messageBiblioVide);    // Affiche le message si la bibliothèque est vide
        gridView.setOnItemClickListener(BiblioToWorkAction);

        // CHANGEMENT COULEUR HEADER
        int color = getResources().getColor(R.color.colorMovieBlue);
        headerColor = new ColorDrawable(color);
        Toolbox.instance.actionBar.setBackgroundDrawable(headerColor);

        //CHANGEMENT DE L'ICONE EN FILIGRANE
        ImageView iconFiligran = (ImageView) root.findViewById(R.id.biblio_filigram);
        iconFiligran.setImageResource(R.drawable.movie_icon);

        // CHANGEMENT COULEUR STATUT BAR
        Window window = this.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);

        // SETUP MESSAGE RECHERCHE
        if(listeOeuvreActuelle.size() == 0){
            messageRecherche.setVisibility(View.GONE);
        }
        else {
            messageRecherche.setVisibility(View.VISIBLE);
            AdaptTextForSearch(messageRecherche, R.color.colorMovieBlue, R.drawable.ic_search_biblio_movie);
        }

        // SETUP TEXTE BIBLIO VIDE
        messageBiblioVide.setText(R.string.biblio_noMovie);
        AdaptTextForSearch(messageBiblioVide, R.color.colorMovieBlue, R.drawable.ic_search_biblio_movie);

        // CHANGEMENT ONGLETS
        mTabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_movie));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // RECUP LISTE ACTUELLE
                ArrayList<Film> listeNonFiltree = (ArrayList<Film>) Toolbox.instance.userBiblio.getBiblioFilms().getListeOeuvres();
                messageRecherche.setVisibility(View.VISIBLE);

                // RECUP STRING
                String tous = getResources().getString(R.string.onglet_tous);
                String termines = getResources().getString(R.string.onglet_termines);
                String encours = getResources().getString(R.string.onglet_encours);
                String abandonnes = getResources().getString(R.string.onglet_abandonne);
                String planifies = getResources().getString(R.string.onglet_planifie);
                String favoris = getResources().getString(R.string.onglet_favoris);


                // CHOIX DU FILTRE (PREDICATE)
                Predicate<Film> statut = null;
                if(tab.getText().equals(tous) || listeNonFiltree.size() == 0){
                    OeuvreAdapter<Film> oeuvreAdapterFiltre = new OeuvreAdapter(mContext, listeNonFiltree);
                    listeOeuvreActuelle = listeNonFiltree;
                    gridView.setAdapter(oeuvreAdapterFiltre);
                    if(listeNonFiltree.size() == 0){
                        messageRecherche.setVisibility(View.GONE);
                    }
                }
                else{
                    String messageVide = "Vous n'avez aucun film ";
                    if(tab.getText().equals(termines)){
                        messageVide += "terminé";
                        statut = x -> x.getStatut() == Oeuvre.TypeStatut.Termine;
                        messageBiblioVide.setText(messageVide);
                    }
                    if(tab.getText().equals(encours)){
                        messageVide += "en cours";
                        statut = x -> x.getStatut() == Oeuvre.TypeStatut.EnCours;
                        messageBiblioVide.setText(messageVide);
                    }
                    if(tab.getText().equals(abandonnes)){
                        messageVide += "de côté";
                        statut = x -> x.getStatut() == Oeuvre.TypeStatut.Abandonne;
                        messageBiblioVide.setText(messageVide);
                    }
                    if(tab.getText().equals(planifies)){
                        messageVide += "planifié";
                        statut = x -> x.getStatut() == Oeuvre.TypeStatut.Planifie;
                        messageBiblioVide.setText(messageVide);
                    }
                    if(tab.getText().equals(favoris)){
                        messageVide += "favori";
                        statut = x -> x.isFavori() == true;
                        messageBiblioVide.setText(messageVide);
                    }

                    // FILTRAGE
                    if(statut != null){
                        ArrayList<Film> listeFiltree = (ArrayList<Film>) listeNonFiltree.stream().filter(statut).collect(Collectors.toList());
                        // APPLICATION
                        OeuvreAdapter<Film> oeuvreAdapterFiltre = new OeuvreAdapter(mContext, listeFiltree);
                        listeOeuvreActuelle = listeFiltree;
                        gridView.setAdapter(oeuvreAdapterFiltre);

                        // MAJ MESSAGE RECHERCHE
                        if(listeFiltree.size() == 0){
                            messageRecherche.setVisibility(View.GONE);
                        }

                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

    public void TrierListe(OptionsTri option){

        // SORT
        switch (option){
            case alphabet :
                Collections.sort(listeOeuvreActuelle, (u1, u2) -> u1.getTitleVF().compareTo(u2.getTitleVF()));
                break;
            case alphabet_inverse :
                Collections.sort(listeOeuvreActuelle, (u1, u2) -> u1.getTitleVF().compareTo(u2.getTitleVF()));
                Collections.reverse(listeOeuvreActuelle);
                break;
            case date_ajout:
                Collections.sort(listeOeuvreActuelle, (u1, u2) -> u1.getDateAjout().compareTo(u2.getDateAjout()));
                Collections.reverse(listeOeuvreActuelle);
                break;
            case date_sortie:
                Collections.sort(listeOeuvreActuelle, (u1, u2) -> u1.getDateSortie().compareTo(u2.getDateSortie()));
                Collections.reverse(listeOeuvreActuelle);
                break;
            case note:
                Collections.sort(listeOeuvreActuelle, (u1, u2) -> Integer.compare(u1.getNote(), u2.getNote()));
                Collections.reverse(listeOeuvreActuelle);
                break;
        }

        // RELOAD
        triActuel = option;
        OeuvreAdapter<Film> oeuvreAdapter = new OeuvreAdapter(mContext, listeOeuvreActuelle);
        gridView.setAdapter(oeuvreAdapter);
    }
}
