package nf28.mediaplace.ui.share;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import nf28.mediaplace.Adapters.ShareInfoAdapter;
import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.ShareSetup;
import nf28.mediaplace.R;

public class ShareFragment extends Fragment {

    // REFERENCES
    EditText nomBiblio;
    ImageButton movieButton;
    ImageButton serieButton;
    ImageButton bookButton;
    ImageButton gameButton;
    ImageButton check1;
    ImageButton check2;
    ImageButton check3;
    ImageButton check4;
    GridView gridView;
    Button shareButton;
    Spinner spinnerShare;

    // PROPRIETES
    private String[] listeStatuts;
    protected NavController nav;
    public static ShareSetup setup = null;

    // METHODES
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_share, container, false);

        // LOCK PORTRAIT
        if(Toolbox.lockPortraitOrientation){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      // Force l'orientation en portrait
        }

        // INIT
        nav = NavHostFragment.findNavController(this);

        // CHANGEMENT COULEUR HEADER
        int headerColor = getResources().getColor(R.color.colorVioletHeader);
        ColorDrawable color = new ColorDrawable(headerColor);
        Toolbox.instance.actionBar.setBackgroundDrawable(color);

        // CHANGEMENT COULEUR STATUT BAR
        Window window = this.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(headerColor);

        // RECUP ELEMENTS
        nomBiblio = root.findViewById(R.id.share_nomBiblio_text);
        shareButton = root.findViewById(R.id.share_button);
        movieButton = root.findViewById(R.id.share_movie_button);
        serieButton = root.findViewById(R.id.share_tvshow_button);
        bookButton = root.findViewById(R.id.share_book_button);
        gameButton = root.findViewById(R.id.share_videogames_button);
        check1 = root.findViewById(R.id.share_check1);
        check2 = root.findViewById(R.id.share_check2);
        check3 = root.findViewById(R.id.share_check3);
        check4 = root.findViewById(R.id.share_check4);

        // BOUTONS CHECK = INVISIBLE
        check1.setVisibility(View.INVISIBLE);
        check2.setVisibility(View.INVISIBLE);
        check3.setVisibility(View.INVISIBLE);
        check4.setVisibility(View.INVISIBLE);

        // CONFIG SPINNER
        spinnerShare = root.findViewById(R.id.spinner_share);
        listeStatuts = new String[]{"Toutes",
                getResources().getString(R.string.onglet_encours), getResources().getString(R.string.onglet_planifie),
                getResources().getString(R.string.onglet_termines), getResources().getString(R.string.onglet_abandonne), getResources().getString(R.string.onglet_favoris)};
        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.share_white_spinner, listeStatuts);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShare.setAdapter(aa);

        // SETUP GRIDVIEW
        gridView = root.findViewById(R.id.grid_share);
        ShareInfoAdapter itemsAdapter = new ShareInfoAdapter(this.getContext(), ShareSetup.getListeInfos());
        gridView.setAdapter(itemsAdapter);
        gridView.setVerticalScrollBarEnabled(false);

        // RECOVER LAST SETUP
        if(setup != null){
            RecoverLastSetup();
        }
        else{
            setup = new ShareSetup();
        }

        // LISTENERS
        InitListeners();

        return root;
    }

    private void InitListeners(){

        // BIBLIO BUTTONS
        View.OnClickListener MovieListener = new View.OnClickListener() {
            public void onClick(View v)
            {
                // ENABLE
                if(setup.movie == false){
                    setup.movie = true;
                    movieButton.setImageResource(R.drawable.share_moviebutton_on);
                    check1.setVisibility(View.VISIBLE);
                }
                // DISABLE
                else{
                    setup.movie = false;
                    movieButton.setImageResource(R.drawable.share_moviebutton_off);
                    check1.setVisibility(View.INVISIBLE);
                }
            }
        };
        View.OnClickListener SerieListener = new View.OnClickListener() {
            public void onClick(View v)
            {
                // ENABLE
                if(setup.serie == false){
                    setup.serie = true;
                    serieButton.setImageResource(R.drawable.share_seriesbutton_on);
                    check2.setVisibility(View.VISIBLE);
                }
                // DISABLE
                else{
                    setup.serie = false;
                    serieButton.setImageResource(R.drawable.share_seriesbutton_off);
                    check2.setVisibility(View.INVISIBLE);
                }
            }
        };
        View.OnClickListener BookListener = new View.OnClickListener() {
            public void onClick(View v)
            {
                // ENABLE
                if(setup.book == false){
                    setup.book = true;
                    bookButton.setImageResource(R.drawable.share_bookbutton_on);
                    check3.setVisibility(View.VISIBLE);
                }
                // DISABLE
                else{
                    setup.book = false;
                    bookButton.setImageResource(R.drawable.share_bookbutton_off);
                    check3.setVisibility(View.INVISIBLE);
                }
            }
        };
        View.OnClickListener GameListener = new View.OnClickListener() {
            public void onClick(View v)
            {
                // ENABLE
                if(setup.game == false){
                    setup.game = true;
                    gameButton.setImageResource(R.drawable.share_gamebutton_on);
                    check4.setVisibility(View.VISIBLE);
                }
                // DISABLE
                else{
                    setup.game = false;
                    gameButton.setImageResource(R.drawable.share_gamebutton_off);
                    check4.setVisibility(View.INVISIBLE);
                }
            }
        };

        View.OnClickListener createPreview = new View.OnClickListener() {

            public void onClick(View v) {

                // SETUP VALIDE : Chargement de la Preview
                if(CheckValidSetup()){
                    // LOAD
                    nav.navigate(R.id.nav_sharePreview);
                }
                // SETUP NON VALIDE : Affichage Message
                else{
                    Toast.makeText(getContext(),R.string.share_setupNonValide, Toast.LENGTH_SHORT).show();
                }
            }
        };

        nomBiblio.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    setup.biblioName = s.toString();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        spinnerShare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    // TOUS
                    case 0 :
                        setup.cat = ShareSetup.Categorie.tous;
                        break;
                    // EN COURS
                    case 1 :
                        setup.cat = ShareSetup.Categorie.encours;
                        break;
                    // PLANIFIES
                    case 2 :
                        setup.cat = ShareSetup.Categorie.planifies;
                        break;
                    // TERMINES
                    case 3 :
                        setup.cat = ShareSetup.Categorie.termines;
                        break;
                    // DE COTE
                    case 4 :
                        setup.cat = ShareSetup.Categorie.decote;
                        break;
                    // FAVORIS
                    case 5 :
                        setup.cat = ShareSetup.Categorie.favoris;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        movieButton.setOnClickListener(MovieListener);
        serieButton.setOnClickListener(SerieListener);
        bookButton.setOnClickListener(BookListener);
        gameButton.setOnClickListener(GameListener);
        check1.setOnClickListener(MovieListener);
        check2.setOnClickListener(SerieListener);
        check3.setOnClickListener(BookListener);
        check4.setOnClickListener(GameListener);
        shareButton.setOnClickListener(createPreview);
    }

    // Permet de vérifier si le partage peut être effectué (au moins une bibliothèque et une information sélectionnée)
    private boolean CheckValidSetup(){

        if(
                (setup.date == false && setup.cover == false && setup.titre == false && setup.statut == false && setup.note == false && setup.real == false) ||
                        (setup.movie == false && setup.book == false && setup.serie == false && setup.game == false)) {

            return false;
        }
        else{
            return true;
        }
    }

    private void RecoverLastSetup(){

        // BIBLIO BUTTONS
        if(setup.movie == true){
            movieButton.setImageResource(R.drawable.share_moviebutton_on);
            check1.setVisibility(View.VISIBLE);
        }
        if(setup.serie == true){
            serieButton.setImageResource(R.drawable.share_seriesbutton_on);
            check2.setVisibility(View.VISIBLE);
        }
        if(setup.book == true){
            bookButton.setImageResource(R.drawable.share_bookbutton_on);
            check3.setVisibility(View.VISIBLE);
        }
        if(setup.game == true){
            gameButton.setImageResource(R.drawable.share_gamebutton_on);
            check4.setVisibility(View.VISIBLE);
        }
    }

}
