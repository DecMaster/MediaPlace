package nf28.mediaplace.AsyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;

import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Oeuvre;

public class SaveImageTask extends AsyncTask<String, Void, String> {
    private Oeuvre work;

    public SaveImageTask(Oeuvre o) {
        this.work = o;
    }

    protected String doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bitmap = null;
        String newPath = null;
        try {
            newPath = Toolbox.instance.getNewImgPath(work);
            InputStream in = new java.net.URL(urldisplay).openStream();
            bitmap = BitmapFactory.decodeStream(in);
            FileOutputStream fos = new FileOutputStream(newPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return newPath;
    }

    protected void onPostExecute(String result) {
        work.setCheminImage(result, false);
    }
}
