package nf28.mediaplace.AsyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nf28.mediaplace.Controllers.APIReader;
import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.R;
import nf28.mediaplace.ui.search.SearchFragment;

public class LoadGoogleBooksResults extends AsyncTask<String, Void, List<Oeuvre>> {

    private ProgressDialog dialog = SearchFragment.dialogBooks;

    public interface AsyncResponse {
        void processFinish(List<Oeuvre> output);
    }

    public AsyncResponse delegate = null;

    public LoadGoogleBooksResults(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Oeuvre> doInBackground(String... strings) {
        return APIReader.getAllBooks(strings[0]);
    }

    @Override
    protected void onPostExecute(List<Oeuvre> result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // AFFICHAGE AUCUN RESULTAT
        if(SearchFragment.selectedTabIndex == 2 && result.size() == 0){
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
