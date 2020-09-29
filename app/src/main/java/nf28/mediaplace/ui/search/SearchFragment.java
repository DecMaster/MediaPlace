package nf28.mediaplace.ui.search;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;
import java.util.Objects;

import nf28.mediaplace.Adapters.SearchAdapter;
import nf28.mediaplace.AsyncTask.LoadGoogleBooksResults;
import nf28.mediaplace.AsyncTask.LoadIGDBresults;
import nf28.mediaplace.AsyncTask.LoadOMDBMovieResults;
import nf28.mediaplace.AsyncTask.LoadOMDBSeriesResults;
import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.R;
import nf28.mediaplace.ui.share.ShareFragment;

public class SearchFragment extends Fragment {
    public static ProgressDialog dialogGames = null;
    public static ProgressDialog dialogSeries = null;
    public static ProgressDialog dialogMovies = null;
    public static ProgressDialog dialogBooks = null;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private TabLayout tabLayout;
    private TextView textNoConnection;
    private TextView textLoupe;
    public static TextView textNoResult;
    public static RecyclerView.Adapter searchAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<Oeuvre> data;
    private ArrayList<Oeuvre> dataMovies = new ArrayList<>();
    private ArrayList<Oeuvre> dataSeries = new ArrayList<>();
    private ArrayList<Oeuvre> dataBooks = new ArrayList<>();
    private ArrayList<Oeuvre> dataGames = new ArrayList<>();
    private String lastQuery = "";
    public static View.OnClickListener myOnClickListener;
    private boolean firstSearchDone = false;

    public static int selectedTabIndex = 0;

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;
        private NavController nav;

        private MyOnClickListener(Context context, NavController nav) {
            this.context = context;
            this.nav = nav;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            System.out.println("yoloy");
            int itemPosition = recyclerView.getChildLayoutPosition(v);
            Oeuvre oeuvre = ((SearchAdapter) recyclerView.getAdapter()).getItem(itemPosition);
            Oeuvre oeuvreInBiblio = Toolbox.instance.userBiblio.getOeuvre(oeuvre.getId());
            if(oeuvreInBiblio != null){
                oeuvre = oeuvreInBiblio;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("currentWork", oeuvre);
            nav.navigate(R.id.nav_work, bundle);
        }

    }


    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        ShareFragment.setup = null;     // On réinitialise les options de partage statiques

        // LOCK PORTRAIT
        if (Toolbox.lockPortraitOrientation) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      // Force l'orientation en portrait
        }
        dialogGames = new ProgressDialog(getActivity());
        dialogBooks = new ProgressDialog(getActivity());
        dialogSeries = new ProgressDialog(getActivity());
        dialogMovies = new ProgressDialog(getActivity());
        tabLayout = (TabLayout) root.findViewById(R.id.mytabs);

        myOnClickListener = new MyOnClickListener(this.getContext(), NavHostFragment.findNavController(this));

        recyclerView = (RecyclerView) root.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        textNoConnection = root.findViewById(R.id.text_noConnection);
        if(!isNetworkAvailable()){
            textNoConnection.setVisibility(View.VISIBLE);
        }
        else{
            textNoConnection.setVisibility(View.GONE);
        }

        textNoResult = root.findViewById(R.id.text_noResult);
        textNoResult.setVisibility(View.GONE);

        tabLayout.addTab(tabLayout.newTab().setText("Films"));
        tabLayout.addTab(tabLayout.newTab().setText("Jeux-vidéos"));
        tabLayout.addTab(tabLayout.newTab().setText("Livres"));
        tabLayout.addTab(tabLayout.newTab().setText("Séries"));

        // RECOVER LAST TAB
        if(data == null){
            selectedTabIndex = 0;
        }
        data = new ArrayList<Oeuvre>();
        TabLayout.Tab tab = tabLayout.getTabAt(selectedTabIndex);
        tab.select();
        int headerColor = getResources().getColor(R.color.colorMovieBlue);
        switch(selectedTabIndex){
            // FILMS
            default:
                data.addAll(dataMovies);
                headerColor = getResources().getColor(R.color.colorMovieBlue);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_movie));
                break;
            // JEUX
            case 1 :
                data.addAll(dataGames);
                headerColor = getResources().getColor(R.color.colorVideoGamesRed);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_game));
                break;
            // LIVRES
            case 2 :
                data.addAll(dataBooks);
                headerColor = getResources().getColor(R.color.colorBookYellow);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_book));
                break;
            // SERIES
            case 3 :
                data.addAll(dataSeries);
                headerColor = getResources().getColor(R.color.colorShowGreen);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_show));
                break;
        }
        // CHANGEMENT COULEUR HEADER & STATUT BAR
        ColorDrawable color = new ColorDrawable(headerColor);
        Toolbox.instance.actionBar.setBackgroundDrawable(color);
        Window window = this.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(headerColor);

        searchAdapter = new SearchAdapter(data);
        ((SearchAdapter) searchAdapter).setOnItemClickListener(position -> {
            ((SearchAdapter) searchAdapter).getItem(position).addInBiblio();
            if(recyclerView.findViewHolderForAdapterPosition(position)!=null)
                ((SearchAdapter) searchAdapter).hide(Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position)).itemView);
        });

        recyclerView.setAdapter(searchAdapter);

        textLoupe = root.findViewById(R.id.text_cliqueLoupe);
        if(data.size() != 0){
            textLoupe.setVisibility(View.GONE);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                data.clear();
                textNoResult.setVisibility(View.GONE);
                int headerColor = 0;
                if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Films")) {
                    data.addAll(dataMovies);
                    headerColor = getResources().getColor(R.color.colorMovieBlue);
                    tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_movie));

                } else if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Séries")) {
                    data.addAll(dataSeries);
                    headerColor = getResources().getColor(R.color.colorShowGreen);
                    tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_show));

                } else if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Livres")) {
                    data.addAll(dataBooks);
                    headerColor = getResources().getColor(R.color.colorBookYellow);
                    tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_book));

                } else if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Jeux-vidéos")) {
                    data.addAll(dataGames);
                    headerColor = getResources().getColor(R.color.colorVideoGamesRed);
                    tabLayout.setBackground(getResources().getDrawable(R.drawable.tablayout_game));
                }

                // SI AUCUN RESULTAT : Affichage Texte
                if(data.size() == 0){
                    if(firstSearchDone){
                        textNoResult.setVisibility(View.VISIBLE);
                    }
                    else{
                        textLoupe.setVisibility(View.VISIBLE);
                    }
                }

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(headerColor);
                ColorDrawable color = new ColorDrawable(headerColor);
                Toolbox.instance.actionBar.setBackgroundDrawable(color);
                /*FloatingActionButton button = root.findViewById(R.id.floatingActionButton);
                button.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));*/
                selectedTabIndex = tab.getPosition();
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(selectedTabIndex).select();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);


        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }


                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onQueryTextSubmit(String query) {

                    // VIDAGE DONNEES PRECEDENTES
                    VidageDonnees();
                    textNoResult.setVisibility(View.GONE);
                    firstSearchDone = true;
                    textLoupe.setVisibility(View.GONE);

                    // CHECK CONNECTION
                    if(!isNetworkAvailable()){
                        textNoConnection.setVisibility(View.VISIBLE);
                        searchAdapter.notifyDataSetChanged();
                    }
                    else {

                        textNoConnection.setVisibility(View.GONE);

                        // a implémenter pour chercher dans les apis
                        if (!lastQuery.equalsIgnoreCase(query)) {

                            // VIDAGE
                            lastQuery = query;
                            dataGames.clear();
                            dataBooks.clear();
                            dataSeries.clear();
                            dataMovies.clear();

                            // REQUETAGE APIs
                            LoadIGDBresults gameResults = (LoadIGDBresults) new LoadIGDBresults(output -> {
                                dataGames.addAll(output);
                                if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Jeux-vidéos")) {
                                    data.addAll(dataGames);
                                }
                                searchAdapter.notifyDataSetChanged();
                            }).execute(query);
                            LoadOMDBMovieResults movieResults = (LoadOMDBMovieResults) new LoadOMDBMovieResults(output -> {
                                dataMovies.addAll(output);
                                if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Films")) {
                                    data.addAll(dataMovies);
                                }
                                searchAdapter.notifyDataSetChanged();
                            }).execute(query);
                            LoadOMDBSeriesResults seriesResults = (LoadOMDBSeriesResults) new LoadOMDBSeriesResults(output -> {
                                dataSeries.addAll(output);
                                if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Séries")) {
                                    data.addAll(dataSeries);
                                }
                                searchAdapter.notifyDataSetChanged();
                            }).execute(query);
                            LoadGoogleBooksResults booksResults = (LoadGoogleBooksResults) new LoadGoogleBooksResults(output -> {
                                dataBooks.addAll(output);
                                if (Objects.equals(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getText(), "Livres")) {
                                    data.addAll(dataBooks);
                                }
                                searchAdapter.notifyDataSetChanged();
                            }).execute(query);
                        }
                        searchView.clearFocus();

                        return true;
                    }
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        super.onCreateOptionsMenu(menu, inflater);

        //Si fenêtre ouverte depuis la page d'accueil
        Bundle bundle = getArguments();
        if (bundle != null) {
            String homeQuery = bundle.getString("keyword");
            searchView.setQuery(homeQuery, true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void VidageDonnees(){
        data.clear();
        dataGames.clear();
        dataBooks.clear();
        dataSeries.clear();
        dataMovies.clear();
        searchAdapter.notifyDataSetChanged();
        searchView.clearFocus();
    }


}
