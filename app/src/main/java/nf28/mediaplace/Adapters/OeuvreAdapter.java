package nf28.mediaplace.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.squareup.picasso.Picasso;

import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.R;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

// T est soit Film, Livre, Serie ou JeuVideo

public class OeuvreAdapter<T extends Oeuvre> extends BaseAdapter {

    public enum AdapterType{
        Biblio,
        Recommendation
    }

    // REFERENCES
    private final Context mContext;
    private final ArrayList<T> oeuvres;

    // PROPRIETES
    private int ALPHA_NOTE = 175;   // Opacité du rectangle note "10/10"
    private final AdapterType type;

    // 1 - Constructeur pour instancier l'adapter
    public OeuvreAdapter(Context context, ArrayList<T> oe) {
        this.mContext = context;
        this.oeuvres = oe;
        this.type = AdapterType.Biblio;
    }
    public OeuvreAdapter(Context context, ArrayList<T> oe, AdapterType at) {
        this.mContext = context;
        this.oeuvres = oe;
        this.type = at;
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
    public T getItem(int pos) {
        return oeuvres.get(pos);
    }

    // 5 - Formattage de la vue par cellule
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // 1 - Film à afficher (Modele)
        final T oeuvre = oeuvres.get(pos);

        // 2 - Formattage d'une cellule (Vue)
        if (convertView == null) {
            // CONSTRUCTION VUE ADAPTEE
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            switch (type){
                case Biblio:
                    convertView = layoutInflater.inflate(R.layout.biblio_item, null);
                    break;
                case Recommendation:
                    convertView = layoutInflater.inflate(R.layout.recommendation_item, null);
                    break;
            }

            // ANIMATION
            if(Toolbox.toggleAnimations){
                // Pour changer les propriétés de l'animation, il faut modifier l'XML dans "res"
                Animation anim = AnimationUtils.loadAnimation(mContext,R.anim.biblio_item_anim);
                anim.setStartOffset(pos * 20);
                convertView.setAnimation(anim);
                anim.start();
            }

            // 3 - Choix du type d'oeuvre à adapter
            // Récupération des éléments visuels
            final ImageView cover = convertView.findViewById(R.id.item_cover);
            final TextView title = convertView.findViewById(R.id.item_title);
            final TextView subtitle = convertView.findViewById(R.id.biblio_item_subtitle);
            final View rectangleNote = convertView.findViewById(R.id.item_rectangleNote);
            final TextView note = convertView.findViewById(R.id.item_note);

            // Lien entre les éléments de la vue et les éléments de l'item
            cover.setImageResource(R.mipmap.placeholder_biblio);
            title.setText(oeuvre.getTitleVF());
            if(note != null){
                note.setText(oeuvre.getNote() >= 0 ? (String.valueOf(oeuvre.getNote()) + "/10") : "N/A");
            }
            if(oeuvre.getCheminImage() != null){
                if(oeuvre.isImageOnline()){
                    Picasso.get().load(oeuvre.getCheminImage()).fit().placeholder(R.mipmap.placeholder).into(cover);
                }
                else{
                    String path = oeuvre.getCheminImage();
                    File img = new File(path);
                    if(img.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(new File(path).getAbsolutePath());
                        cover.setImageBitmap(myBitmap);
                    }
                }
            }

            if(type == AdapterType.Biblio){
                LocalDate localDate = oeuvre.getDateSortie().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                subtitle.setText(String.valueOf(localDate.getYear()));

                // Changement de la couleur de l'encadré "10/10"
                ColorDrawable color = new ColorDrawable(Toolbox.instance.tabsColor.get(oeuvre.getClass()));
                color.setAlpha(ALPHA_NOTE);
                rectangleNote.setBackground(color);
            }

            // Renvoie de la vue adaptée
            return convertView;
        }

        // ERREUR : Vue vide
        return convertView;
    }
}
