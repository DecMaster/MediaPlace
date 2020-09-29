package nf28.mediaplace.ui.biblio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import nf28.mediaplace.Adapters.OeuvreAdapter;
import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Oeuvre;
import com.google.android.material.tabs.TabLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nf28.mediaplace.R;
import nf28.mediaplace.Views.NonScrollGridView;
import nf28.mediaplace.ui.share.ShareFragment;

public abstract class BiblioFragment extends Fragment {

    // ENUM
    public enum OptionsTri{
        alphabet,
        alphabet_inverse,
        date_ajout,
        date_sortie,
        note
    }

    // REFERENCES
    protected OptionsTri triActuel;
    protected Context mContext;
    protected OeuvreAdapter adapter;
    protected NonScrollGridView gridView;
    protected View root;
    protected TabLayout mTabLayout;
    protected NavController nav;
    protected TextView messageRecherche;
    protected TextView messageBiblioVide;

    // PROPRIETES
    private int selectedPos;

    protected final AdapterView.OnItemClickListener BiblioToWorkAction =  new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Oeuvre oeuvre = (Oeuvre)parent.getAdapter().getItem(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("currentWork", oeuvre);
            nav.navigate(R.id.nav_work, bundle);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.biblio_item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Oeuvre oeuvre = adapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.biblio_option_regarder:String link = oeuvre.getLien();
                Toolbox.instance.playWork(oeuvre, this);
                return true;
            case R.id.biblio_option_modifier:
                selectedPos = info.position;
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), Toolbox.instance.CHANGE_WORK_IMAGE_FROM_GALLERY);
                return true;
            case R.id.biblio_option_supprimer:
                oeuvre.removeFromBiblio();
                adapter.notifyDataSetChanged();
                gridView.setAdapter(adapter);
                if(adapter.getCount() == 0){
                    messageRecherche.setVisibility(View.GONE);
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tri, menu);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        nav = NavHostFragment.findNavController(this);
        root = inflater.inflate(R.layout.fragment_biblio, container, false);
        setHasOptionsMenu(true);
        ShareFragment.setup = null;     // On réinitialise les options de partage statiques

        // LOCK PORTRAIT
        if(Toolbox.lockPortraitOrientation){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      // Force l'orientation en portrait
        }

        // SETUP GRIDVIEW
        gridView = root.findViewById(R.id.grid_biblio);
        registerForContextMenu(gridView);

        // SETUP ONGLETS
        mTabLayout = root.findViewById(R.id.tabLayout_biblio);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.onglet_tous));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.onglet_encours));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.onglet_planifie));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.onglet_termines));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.onglet_abandonne));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.onglet_favoris));

        // TEXTES
        messageBiblioVide = root.findViewById(R.id.text_biblio_noElements);
        messageRecherche = root.findViewById(R.id.text_biblio_lienRecherche);

        // CREATION
        return root;
    }

    protected void AdaptTextForSearch(TextView txt, int couleur, int icon){
        String message = (String) txt.getText();
        String recherche = getResources().getString(R.string.biblio_recherche) + "  d";
        SpannableStringBuilder ssb = new SpannableStringBuilder(message + " " + recherche);
        ForegroundColorSpan fcolor = new ForegroundColorSpan(getResources().getColor(couleur));
        int oldLenght = message.length();
        int newLenght = oldLenght + recherche.length();
        ImageSpan is = new ImageSpan(getContext(), icon);
        ssb.setSpan(is, newLenght, newLenght + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ssb.setSpan(fcolor, oldLenght, newLenght + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt.setText(ssb);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.nav_search);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Changement d'images
        if(requestCode == Toolbox.instance.CHANGE_WORK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            Oeuvre work = adapter.getItem(selectedPos);
            try {
                String path = Toolbox.instance.getNewImgPath(work);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                FileOutputStream fos = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                work.setCheminImage(path, false);
                adapter.notifyDataSetChanged();
                gridView.setAdapter(adapter);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_tri_alpha:
                TrierListe(OptionsTri.alphabet);
                return true;
            case R.id.option_tri_alpha_inverse:
                TrierListe(OptionsTri.alphabet_inverse);
                return true;
            case R.id.option_tri_date_ajout:
                TrierListe(OptionsTri.date_ajout);
                return true;
            case R.id.option_tri_date_sortie:
                TrierListe(OptionsTri.date_sortie);
                return true;
            case R.id.option_tri_note:
                TrierListe(OptionsTri.note);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected abstract void TrierListe(OptionsTri option);

}
