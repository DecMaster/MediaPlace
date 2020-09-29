package nf28.mediaplace.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import nf28.mediaplace.Models.ShareSetup;
import nf28.mediaplace.R;
import nf28.mediaplace.ui.share.ShareFragment;

public class ShareInfoAdapter extends BaseAdapter {
    // REFERENCES
    private final Context mContext;
    private final ArrayList<String> infos;

    // 1 - Constructeur pour instancier l'adapter
    public ShareInfoAdapter(Context context, ArrayList<String> str) {
        this.mContext = context;
        this.infos = str;
    }

    // 2 - Nombre de cellules à créer dans le GridView
    @Override
    public int getCount() {
        return infos.size();
    }

    // 3 - Pas nécéssaire, on return 0
    @Override
    public long getItemId(int pos) {
        return 0;
    }

    // 4 - Permet de récupérer un item
    @Override
    public Object getItem(int pos) {
        return infos.get(pos);
    }

    // 5 - Formattage de la vue par cellule
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // 1 - Film à afficher (Modele)
        final String listeInfos = infos.get(pos);

        // 2 - Formattage d'une cellule (Vue)
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.share_info_item, null);
        }

        // RECUP ELEMENTS
        final TextView texte = convertView.findViewById(R.id.share_info_texte);
        final TextView subtexte = convertView.findViewById(R.id.share_info_subtexte);
        final ImageView check = convertView.findViewById(R.id.share_info_check);
        final RelativeLayout layout = convertView.findViewById(R.id.share_info_layout);

        // MAJ ELEMENTS
        texte.setText(infos.get(pos));
        check.setVisibility(View.INVISIBLE);
        int c1 = mContext.getResources().getColor(R.color.colorButtonDarkGrey);
        ColorDrawable colorOFF = new ColorDrawable(c1);
        int c2 = mContext.getResources().getColor(R.color.colorLightGrey);
        ColorDrawable colorON = new ColorDrawable(c2);
        layout.setBackground(colorOFF);

        // RECOVER + INDIC
        switch(pos){
            case 0 :
                subtexte.setVisibility(View.VISIBLE);
                if(ShareFragment.setup.titre == true){
                    layout.setBackground(colorON);
                    check.setVisibility(View.VISIBLE);
                }
                break;
            case 1 :
                subtexte.setVisibility(View.VISIBLE);
                if(ShareFragment.setup.real == true){
                    layout.setBackground(colorON);
                    check.setVisibility(View.VISIBLE);
                }
                break;
            case 2 :
                subtexte.setVisibility(View.VISIBLE);
                if(ShareFragment.setup.cover == true){
                    layout.setBackground(colorON);
                    check.setVisibility(View.VISIBLE);
                }
                break;
            case 3 :
                subtexte.setVisibility(View.GONE);
                if(ShareFragment.setup.note == true){
                    layout.setBackground(colorON);
                    check.setVisibility(View.VISIBLE);
                }
                break;
            case 4 :
                subtexte.setVisibility(View.GONE);
                if(ShareFragment.setup.statut == true){
                    layout.setBackground(colorON);
                    check.setVisibility(View.VISIBLE);
                }
                break;
            case 5 :
                subtexte.setVisibility(View.GONE);
                if(ShareFragment.setup.date == true){
                    layout.setBackground(colorON);
                    check.setVisibility(View.VISIBLE);
                }
                break;
            default:
                subtexte.setVisibility(View.GONE);
                break;
        }

        // LISTENERS
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ENABLE
                if(check.getVisibility() == View.INVISIBLE){
                    layout.setBackground(colorON);
                    check.setVisibility(View.VISIBLE);
                }

                // DISABLE
                else{
                    layout.setBackground(colorOFF);
                    check.setVisibility(View.INVISIBLE);
                }

                // CHANGE SETUP
                ShareSetup setup = ShareFragment.setup;
                CharSequence text = texte.getText();
                if(text.equals(setup.getListeInfos().get(0))){
                    setup.titre = !setup.titre;
                }
                else if(text.equals(setup.getListeInfos().get(1))){
                    setup.real = !setup.real;
                }
                else if(text.equals(setup.getListeInfos().get(2))){
                    setup.cover = !setup.cover;
                }
                else if(text.equals(setup.getListeInfos().get(3))){
                    setup.note = !setup.note;
                }
                else if(text.equals(setup.getListeInfos().get(4))){
                    setup.statut = !setup.statut;
                }
                else if(text.equals(setup.getListeInfos().get(5))){
                    setup.date = !setup.date;
                }
            }
        });

        return convertView;
    }
}
