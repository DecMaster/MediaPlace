package nf28.mediaplace.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.R;
import nf28.mediaplace.ui.share.ShareFragment;

public class HomeFragment extends Fragment {

    protected NavController nav;
    protected View root;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        nav = NavHostFragment.findNavController(this);
        ShareFragment.setup = null;                                             // On réinitialise les options de partage statiques

        // LOCK PORTRAIT
        if(Toolbox.lockPortraitOrientation){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      // Force l'orientation en portrait
        }

        // CHANGEMENT COULEUR HEADER
        int headerColor = getResources().getColor(R.color.colorVioletHeader);
        ColorDrawable color = new ColorDrawable(headerColor);
        Toolbox.instance.actionBar.setBackgroundDrawable(color);

        // CHANGEMENT COULEUR STATUT BAR
        Window window = this.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(headerColor);

        //Récupération des widgets
        EditText searchBar = (EditText) root.findViewById(R.id.search_bar);
        ImageButton searchButton = (ImageButton) root.findViewById(R.id.search_button);
        ImageButton movieButton = (ImageButton) root.findViewById(R.id.movie_button);
        ImageButton tvShowButton = (ImageButton) root.findViewById(R.id.tvshow_button);
        ImageButton bookButton = (ImageButton) root.findViewById(R.id.book_button);
        ImageButton videoGameButton = (ImageButton) root.findViewById(R.id.videogames_button);

        //Recherche
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Récupération du texte entré par l'utilisateur
                String keyword = searchBar.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("keyword", keyword);
                nav.navigate(R.id.nav_search, bundle); //Envoyé au fragment de la recherche
            }
        });
        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override //Equivalence bouton Done / Bouton fleche pour le lancement de la recherche
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchButton.callOnClick();
                    return true;
                }
                return false;
            }
        });



        //Boutons vers biblio
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.nav_movies);
            }
        });
        tvShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.nav_series);
            }
        });
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.nav_books);
            }
        });
        videoGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.nav_videoGames);
            }
        });

        return root;
    }


    @Override
    public void onPause() {
        super.onPause(); //Cache clavier quand on change de fragment
        showSoftwareKeyboard(false);
    }

    protected void showSoftwareKeyboard(boolean showKeyboard){ //Méthode d'affichage du clavier
        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(root.getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
