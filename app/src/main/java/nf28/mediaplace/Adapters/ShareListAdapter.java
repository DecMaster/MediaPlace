package nf28.mediaplace.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Jeu;
import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.Models.Serie;
import nf28.mediaplace.R;
import nf28.mediaplace.ui.share.ShareFragment;

public class ShareListAdapter<T extends Oeuvre> extends BaseAdapter {
    // REFERENCES
    private final Context mContext;
    private final ArrayList<T> oeuvres;

    // 1 - Constructeur pour instancier l'adapter
    public ShareListAdapter(Context context, ArrayList<T> str) {
        this.mContext = context;
        this.oeuvres = str;
    }

    // 2 - Nombre de cellules à créer dans le GridView
    @Override
    public int getCount() {
        return oeuvres.size();
    }

    // 3 - Pas nécéssaire, on return 0
    @Override
    public long getItemId(int pos) {
        return 0;
    }

    // 4 - Permet de récupérer un item
    @Override
    public Object getItem(int pos) {
        return oeuvres.get(pos);
    }

    // 5 - Formattage de la vue par cellule
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // 1 - Oeuvre à afficher (Modele)
        final T oeuvre = oeuvres.get(pos);

        // 2 - Formattage d'une cellule (Vue)
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.share_preview_item, null);
        }

        // 3 - Récupération des éléments d'un item de la liste
        final TextView text = convertView.findViewById(R.id.share_preview_item_text);
        final TextView subtext = convertView.findViewById(R.id.share_preview_item_subtext);
        final ImageView image = convertView.findViewById(R.id.share_preview_item_image);

        // JAQUETTE
        if(!ShareFragment.setup.cover){
            image.setVisibility(View.GONE);
        }
        else{
            if(oeuvre.getCheminImage() != null){
                if(oeuvre.isImageOnline()){
                    Picasso.get().load(oeuvre.getCheminImage()).fit().placeholder(R.mipmap.placeholder).into(image);
                }
                else{
                    String path = oeuvre.getCheminImage();
                    File img = new File(path);
                    if(img.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(new File(path).getAbsolutePath());
                        image.setImageBitmap(myBitmap);
                    }
                }
            }
        }

        // TITRE
        if(ShareFragment.setup.titre){
            text.setText(oeuvre.getTitleVF());
        }
        else{
            text.setVisibility(View.GONE);
        }

        // SOUS-TITRE
        String chaine = "";
        if(ShareFragment.setup.date){
            LocalDate localDate = oeuvre.getDateSortie().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            chaine += String.valueOf(localDate.getYear()) + ", ";
        }
        if(ShareFragment.setup.real){
            if(oeuvre instanceof Film){
                Film o = (Film) oeuvre;
                chaine += Toolbox.instance.listToString(o.getRealisateurs()) + ", ";
            }
            if(oeuvre instanceof Serie){
                Serie o = (Serie) oeuvre;
                chaine += Toolbox.instance.listToString(o.getRealisateurs()) + ", ";
            }
            if(oeuvre instanceof Livre){
                Livre o = (Livre) oeuvre;
                chaine += Toolbox.instance.listToString(o.getAuteurs()) + ", ";
            }
            if(oeuvre instanceof Jeu){
                Jeu o = (Jeu) oeuvre;
                chaine += Toolbox.instance.listToString(o.getDeveloppeurs()) + ", ";
            }
        }
        if(ShareFragment.setup.note){
            chaine += oeuvre.getNote() >= 0 ? (oeuvre.getNote() + "/10, ") : "N/A, ";
        }
        if(ShareFragment.setup.statut){
            chaine += oeuvre.getStatut() + ", ";
        }

        // CORRECTIF FIN DE CHAINE
        if(chaine.endsWith(", ")){
            chaine = chaine.substring(0, chaine.length() - 2);
        }
        if(chaine.endsWith("\n")){
            chaine = chaine.substring(0, chaine.length() - 1);
        }

        if(chaine.isEmpty()){
            subtext.setVisibility(View.GONE);
        }
        else{
            subtext.setText(chaine);
        }

        return convertView;
    }
}
