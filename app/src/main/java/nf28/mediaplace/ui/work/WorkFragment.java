package nf28.mediaplace.ui.work;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import nf28.mediaplace.Adapters.OeuvreAdapter;
import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Jeu;
import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.Models.Serie;
import nf28.mediaplace.R;

public class WorkFragment extends Fragment {

    private Class workClass;
    private Oeuvre work;
    private NavController nav;
    PopupWindow popUp;
    private View root;

    //Eléments de la vue
    private ImageView imgView;
    private TableLayout detailsTable;
    private TextView desc;
    private Button readMore;
    private FrameLayout descContainer;
    private GridView gridView;
    private TextView textView;
    private Button bAddBiblio;
    private Spinner state;
    private EditText note;
    private EditText nb;
    private EditText link;
    private Button bFavorite;
    private FloatingActionButton bAddImage;
    private FloatingActionButton bFloating;
    private FloatingActionButton removeAct;
    private FloatingActionButton shareAct;
    private FloatingActionButton playAct;

    private void ClearFocus(EditText edit){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        root.findViewById(R.id.myDatasTitle).requestFocus();
    }

    public static class DescFragment extends DialogFragment {
        String Desc;

        static DescFragment newInstance(String text) {
            DescFragment f = new DescFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("Desc", text);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Desc = getArguments().getString("Desc");

            // Pick a style based on the num.
            int style = DialogFragment.STYLE_NORMAL, theme = 0;
            setStyle(style, theme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_desc, container, false);
            View tv = v.findViewById(R.id.desc);
            ((TextView)tv).setText(Desc);
            return v;
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_work, container, false);
        nav = NavHostFragment.findNavController(this);

        // LOCK PORTRAIT
        if(Toolbox.lockPortraitOrientation){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      // Force l'orientation en portrait
        }

        popUp = new PopupWindow(container);

        Bundle bundle = getArguments();
        work = (Oeuvre)bundle.getSerializable("currentWork");

        workClass = work.getClass();
        Toolbox.instance.actionBar.setTitle(work.getTitleVF());
        Toolbox.instance.actionBar.setBackgroundDrawable(new ColorDrawable((Integer) Toolbox.instance.tabsColor.get(workClass)));

        imgView = root.findViewById(R.id.image_poster);
        detailsTable = root.findViewById(R.id.table_details);
        desc = root.findViewById(R.id.text_description);
        readMore = root.findViewById(R.id.desc_readMore);
        descContainer = root.findViewById(R.id.container_description);
        gridView = root.findViewById(R.id.grid_recommendations);
        textView = root.findViewById(R.id.text_work_noRecommendations);
        bAddBiblio = root.findViewById(R.id.button_addInBiblio);

        //Informations modifiables
        state = root.findViewById(R.id.editState);
        note = root.findViewById(R.id.editNote);
        nb = root.findViewById(R.id.editNumber);
        link = root.findViewById(R.id.editLink);
        bFavorite = root.findViewById(R.id.button_favorite);
        bAddImage = root.findViewById(R.id.button_add_image);

        //Boutons flottants
        bFloating = root.findViewById(R.id.floating_button);
        removeAct = root.findViewById(R.id.floatingActionButton1);
        shareAct = root.findViewById(R.id.floatingActionButton2);
        playAct = root.findViewById(R.id.floatingActionButton3);


        imgView.setImageResource(R.mipmap.placeholder);
        //Affichage de l'image
        if(work.getCheminImage() != null){
            if(work.isImageOnline()){
                Picasso.get().load(work.getCheminImage()).fit().placeholder(R.mipmap.placeholder).into(imgView);
            }
            else{
                String path = work.getCheminImage();
                File img = new File(path);
                if(img.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(new File(path).getAbsolutePath());
                    imgView.setImageBitmap(myBitmap);
                }
            }
        }

        //Affichage des détails
        int index = 0;
        for (Pair<String, String> entry : work.getDetails()) {
            LinearLayout ly = (LinearLayout)((TableRow)detailsTable.getChildAt(index/3)).getChildAt(index%3);
            ((TextView)ly.getChildAt(0)).setText(entry.first);
            ((TextView)ly.getChildAt(1)).setText(entry.second);
            index++;
        }

        //Affichage description
        desc.setText(work.getDescription());

        if(desc.length() > 300/*ToDo - Improve*/){
            desc.setLines(5);
            Shader myShader = new LinearGradient(
                    0, 0, 0, readMore.getHeight(),
                    Color.WHITE, Color.BLACK,
                    Shader.TileMode.CLAMP);
            readMore.getPaint().setShader(myShader);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialog = DescFragment.newInstance(work.getDescription());
                    dialog.show(getActivity().getSupportFragmentManager(), "name of fragment here");
                }
            };
            descContainer.setOnClickListener(clickListener);
            readMore.setOnClickListener(clickListener);
        }
        else{
            readMore.setVisibility(LinearLayout.GONE);
        }

        //Gestion des recommandations
        OeuvreAdapter<Oeuvre> oeuvreAdapter = new OeuvreAdapter(this.getContext(), work.getRecommandations(), OeuvreAdapter.AdapterType.Recommendation);
        gridView.setAdapter(oeuvreAdapter);
        textView.setText(R.string.work_noRecommendation);
        gridView.setEmptyView(textView);    // Affiche le message si la bibliothèque est vide

        //Gestion du bouton d'ajout à la bibliothèque
        int id = workClass == Film.class ? R.drawable.film_button :
                workClass == Livre.class ? R.drawable.livre_button :
                        workClass == Jeu.class ? R.drawable.jeu_button :
                                workClass == Serie.class ? R.drawable.serie_button :
                                        R.drawable.rounded_button;
        Drawable butDrawable = getContext().getResources().getDrawable(id);
        bAddBiblio.setBackground(butDrawable);
        bAddBiblio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                work.addInBiblio();
                hideNotInBiblioInfos();
                setUpUpdatableDatas();
            }
        });

        root.findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(shareAct.getVisibility() == FloatingActionButton.VISIBLE){
                    Rect shareRect = new Rect();
                    Rect playRect = new Rect();
                    Rect removeRect = new Rect();
                    shareAct.getGlobalVisibleRect(shareRect);
                    playAct.getGlobalVisibleRect(playRect);
                    removeAct.getGlobalVisibleRect(removeRect);
                    if (!shareRect.contains((int)event.getRawX(), (int)event.getRawY()) && !playRect.contains((int)event.getRawX(), (int)event.getRawY()) && !removeRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        shareAct.setVisibility(FloatingActionButton.GONE);
                        playAct.setVisibility(FloatingActionButton.GONE);
                        removeAct.setVisibility(FloatingActionButton.GONE);
                    }
                }
                return false;
            }
        });

        if(work.isInBiblio()){
            hideNotInBiblioInfos();
            setUpUpdatableDatas();
        }
        else{
            hideBiblioInfos();
        }
        return root;
    }

    //Gestion des données modifiables
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpUpdatableDatas() {

        //Gestion du statut
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.work_white_spinner, Arrays.stream(Oeuvre.TypeStatut.values()).map(x -> x.toString()).collect(Collectors.toList()));
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateAdapter);
        state.setSelection(work.getStatut().ordinal());
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                work.setStatut(Oeuvre.TypeStatut.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Gestion de la  note
        note.setText(work.getNote() < 0 ? "" : work.getNote() + "/10");
        note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    note.setText("");
                }
                else{
                    note.setText(work.getNote() < 0 ? "" : work.getNote() + "/10");
                }
            }
        });
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().contains("/") && !s.toString().equals("")){
                    int val = Integer.parseInt(s.toString());
                    work.setNote(Math.min(val, 10));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        //Gestion du nombre d'épisode
        if(work.getClass() == Serie.class){
            Serie serie = (Serie)work;
            nb.setText(serie.getNbEpisodesVus() + "/" + serie.getNbEpisodesTotal());
            nb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        nb.setText("");
                    }
                    else{
                        nb.setText(serie.getNbEpisodesVus() + "/" + serie.getNbEpisodesTotal());
                    }
                }
            });
            nb.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!s.toString().contains("/") && !s.toString().equals("")){
                        int val = Integer.parseInt(s.toString());
                        serie.setNbEpisodesVus(Math.min(val, serie.getNbEpisodesTotal()));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }
        else{
            ((TableRow)nb.getParent()).setVisibility(LinearLayout.GONE);
        }

        //Gestion du lien
        link.setText(work.getLien().toString());
        link.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                work.setLien(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Gestion du bouton de favori
        Drawable bg = getResources().getDrawable(work.isFavori() ? R.drawable.button_favorite_on : R.drawable.button_favorite_off);
        bFavorite.setBackground(bg);
        bFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable bg = getResources().getDrawable(work.changeFavori() ? R.drawable.button_favorite_on : R.drawable.button_favorite_off);
                bFavorite.setBackground(bg);
            }
        });

        //Gestion du bouton d'ajout d'images
        bAddImage.setBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.tabsColor.get(workClass)));
        bAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), Toolbox.instance.CHANGE_WORK_IMAGE_FROM_GALLERY);
            }
        });

        //Gestion du boutton flottant
        bFloating.setBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.tabsColor.get(workClass)));
        shareAct.setBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.tabsColor.get(workClass)));
        playAct.setBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.tabsColor.get(workClass)));

        bFloating.setVisibility(FloatingActionButton.VISIBLE);
        bFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = FloatingActionButton.VISIBLE;
                if(removeAct.getVisibility() == FloatingActionButton.VISIBLE){
                    visibility = FloatingActionButton.GONE;
                }
                removeAct.setVisibility(visibility);
                shareAct.setVisibility(visibility);
                playAct.setVisibility(visibility);
            }
        });

        // BOUTON SUPPRIMER
        removeAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                work.removeFromBiblio();
                hideBiblioInfos();
            }
        });

        // BOUTON REGARDER
        Fragment frag = this;
        playAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbox.instance.playWork(work, frag);
            }
        });

        // BOUTON PARTAGER
        shareAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCurrentWork();
            }
        });
    }

    private void hideBiblioInfos(){
        root.findViewById(R.id.info_notInBiblio).setVisibility(LinearLayout.VISIBLE);
        root.findViewById(R.id.myDatasTitle).setVisibility(LinearLayout.GONE);
        root.findViewById(R.id.myDatasContent).setVisibility(LinearLayout.GONE);

        bAddImage.setVisibility(FloatingActionButton.GONE);
        bFavorite.setVisibility(Button.GONE);

        bFloating.setVisibility(FloatingActionButton.GONE);
        playAct.setVisibility(FloatingActionButton.GONE);
        removeAct.setVisibility(FloatingActionButton.GONE);
        shareAct.setVisibility(FloatingActionButton.GONE);
    }

    private void hideNotInBiblioInfos(){
        root.findViewById(R.id.info_notInBiblio).setVisibility(LinearLayout.GONE);
        root.findViewById(R.id.myDatasTitle).setVisibility(LinearLayout.VISIBLE);
        root.findViewById(R.id.myDatasContent).setVisibility(LinearLayout.VISIBLE);

        bFloating.setVisibility(FloatingActionButton.VISIBLE);
        bAddImage.setVisibility(FloatingActionButton.VISIBLE);
        bFavorite.setVisibility(Button.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Changement d'images
        if(requestCode == Toolbox.instance.CHANGE_WORK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                String path = Toolbox.instance.getNewImgPath(work);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                FileOutputStream fos = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                work.setCheminImage(path, false);
                imgView.setImageResource(R.mipmap.placeholder);
                imgView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void shareCurrentWork(){

        // RENDRE INVISIBLES BOUTONS
        bFavorite.setVisibility(View.INVISIBLE);
        bAddImage.setVisibility(View.INVISIBLE);

        // CREATION PDF
        String FilePath = CreateBitmap();

        // RECUP PDF
        if(FilePath != null){

            File outputFile = new File(FilePath);
            Uri uri = null;

            // PERMISSION ECRITURE/LECTURE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getContext(), "nf28.mediaplace.provider", outputFile);
            } else {
                uri = Uri.fromFile(outputFile);
            }

            // ENVOI PDF
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("image/png");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(sendIntent, "Partager"));

            // REACTIVER BOUTONS
            bFavorite.setVisibility(View.VISIBLE);
            bAddImage.setVisibility(View.VISIBLE);
        }
    }

    private String CreateBitmap()
    {
        ScrollView scroll = root.findViewById(R.id.scrollView);
        int totalHeight = scroll.getChildAt(0).getHeight();
        int totalWidth = scroll.getChildAt(0).getWidth();

        Bitmap b = getBitmapFromView(scroll,totalHeight,totalWidth);

        //Save bitmap
        File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), work.getTitleVF() + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(image);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), b, "Screen", "screen");
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image.getAbsolutePath();
    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(getResources().getColor(R.color.colorBackground));
        view.draw(canvas);
        return returnedBitmap;
    }
}
