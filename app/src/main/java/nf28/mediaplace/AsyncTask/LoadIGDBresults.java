package nf28.mediaplace.AsyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import nf28.mediaplace.Controllers.APIReader;

import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.R;
import nf28.mediaplace.ui.search.SearchFragment;


public class LoadIGDBresults extends AsyncTask<String, Void, List<Oeuvre>> {

    private ProgressDialog dialog = SearchFragment.dialogGames;

    public interface AsyncResponse {
        void processFinish(List<Oeuvre> output);
    }

    public AsyncResponse delegate = null;

    public LoadIGDBresults(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected List<Oeuvre> doInBackground(String... strings) {
        List<Oeuvre> games = new ArrayList<>();

        try {
            games = APIReader.getAllGames(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return games;
    }

    @Override
    protected void onPostExecute(List<Oeuvre> result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // AFFICHAGE AUCUN RESULTAT
        if(SearchFragment.selectedTabIndex == 1 && result.size() == 0){
            SearchFragment.textNoResult.setVisibility(View.VISIBLE);
        }

        delegate.processFinish(result);
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage(Toolbox.instance.activity.getResources().getString(R.string.veuillez_patienter));
        this.dialog.show();
    }
}
