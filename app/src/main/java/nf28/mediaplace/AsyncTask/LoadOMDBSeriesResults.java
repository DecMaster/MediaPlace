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

public class LoadOMDBSeriesResults extends AsyncTask<String, Void, List<Oeuvre>> {

    private ProgressDialog dialog = SearchFragment.dialogSeries;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected List<Oeuvre> doInBackground(String... strings) {
        List<Oeuvre> series = new ArrayList<>();
        try {
            series = APIReader.getAllSeries(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return series;
    }

    public interface AsyncResponse {
        void processFinish(List<Oeuvre> output);
    }

    public AsyncResponse delegate = null;

    public LoadOMDBSeriesResults(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(List<Oeuvre> result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // AFFICHAGE AUCUN RESULTAT
        if(SearchFragment.selectedTabIndex == 3 && result.size() == 0){
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
