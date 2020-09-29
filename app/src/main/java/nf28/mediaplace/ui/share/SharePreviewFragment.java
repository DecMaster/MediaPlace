package nf28.mediaplace.ui.share;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import nf28.mediaplace.Adapters.ShareInfoAdapter;
import nf28.mediaplace.Adapters.ShareListAdapter;
import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Jeu;
import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.Models.Serie;
import nf28.mediaplace.Models.ShareSetup;
import nf28.mediaplace.R;

public class SharePreviewFragment  extends Fragment
{
    // PERMISSIONS
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // REFERENCES
    View root;
    TextView title;
    ListView movieList;
    ListView serieList;
    ListView bookList;
    ListView gameList;
    TextView movieEmptyText;
    TextView serieEmptyText;
    TextView bookEmptyText;
    TextView gameEmptyText;


    // PROPRIETES
    private String FilePath;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_share_preview, container, false);

        // LOCK PORTRAIT
        if(Toolbox.lockPortraitOrientation){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      // Force l'orientation en portrait
        }

        // CHANGEMENT TITRE
        title = root.findViewById(R.id.share_preview_title);
        title.setText(ShareFragment.setup.biblioName);

        // CHANGEMENT COULEUR HEADER
        int headerColor = getResources().getColor(R.color.colorVioletHeader);
        ColorDrawable color = new ColorDrawable(headerColor);
        Toolbox.instance.actionBar.setBackgroundDrawable(color);

        // CHANGEMENT COULEUR STATUT BAR
        Window window = this.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(headerColor);

        // INIT FLOATING BUTTON
        FloatingActionButton backButton = root.findViewById(R.id.share_return);
        FloatingActionButton shareButton = root.findViewById(R.id.share_accept);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BackToShare(v);
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Share(v);
            }
        });

        // SETUP LISTES
        movieList = root.findViewById(R.id.shareMovieHeader_list);
        serieList = root.findViewById(R.id.shareSerieHeader_list);
        bookList = root.findViewById(R.id.shareBookHeader_list);
        gameList = root.findViewById(R.id.shareGameHeader_list);

        movieEmptyText = root.findViewById(R.id.share_preview_noMovie);
        serieEmptyText = root.findViewById(R.id.share_preview_noSerie);
        bookEmptyText = root.findViewById(R.id.share_preview_noBook);
        gameEmptyText = root.findViewById(R.id.share_preview_noGame);

        if(ShareFragment.setup.movie){
            ArrayList<Film> listeOeuvre = Toolbox.instance.userBiblio.getBiblioFilms().getListeOeuvres();
            listeOeuvre = TrierListeFilm(listeOeuvre);
            ShareListAdapter<Film> movieAdapter = new ShareListAdapter(this.getContext(), listeOeuvre);
            movieList.setAdapter(movieAdapter);
            movieList.setEmptyView(movieEmptyText);
        }
        else{
            LinearLayout Header = root.findViewById(R.id.shareMovieHeader);
            Header.setVisibility(View.GONE);
            movieList.setVisibility(View.GONE);
            movieEmptyText.setVisibility(View.GONE);
        }
        if(ShareFragment.setup.serie){
            ArrayList<Serie> listeOeuvre = Toolbox.instance.userBiblio.getBiblioSeries().getListeOeuvres();
            listeOeuvre = TrierListeSerie(listeOeuvre);
            ShareListAdapter<Serie> serieAdapter = new ShareListAdapter(this.getContext(), listeOeuvre);
            serieList.setAdapter(serieAdapter);
            serieList.setEmptyView(serieEmptyText);
        }
        else{
            LinearLayout Header = root.findViewById(R.id.shareSerieHeader);
            Header.setVisibility(View.GONE);
            serieList.setVisibility(View.GONE);
            serieEmptyText.setVisibility(View.GONE);
        }
        if(ShareFragment.setup.book){
            ArrayList<Livre> listeOeuvre = Toolbox.instance.userBiblio.getBiblioLivres().getListeOeuvres();
            listeOeuvre = TrierListeLivre(listeOeuvre);
            ShareListAdapter<Livre> bookAdapter = new ShareListAdapter(this.getContext(), listeOeuvre);
            bookList.setAdapter(bookAdapter);
            bookList.setEmptyView(bookEmptyText);
        }
        else{
            LinearLayout Header = root.findViewById(R.id.shareBookHeader);
            Header.setVisibility(View.GONE);
            bookList.setVisibility(View.GONE);
            bookEmptyText.setVisibility(View.GONE);
        }
        if(ShareFragment.setup.game){
            ArrayList<Jeu> listeOeuvre = Toolbox.instance.userBiblio.getBiblioJeux().getListeOeuvres();
            listeOeuvre = TrierListeJeu(listeOeuvre);
            ShareListAdapter<Jeu> gameAdapter = new ShareListAdapter(this.getContext(), listeOeuvre);
            gameList.setAdapter(gameAdapter);
            gameList.setEmptyView(gameEmptyText);
        }
        else{
            LinearLayout Header = root.findViewById(R.id.shareGameHeader);
            Header.setVisibility(View.GONE);
            gameList.setVisibility(View.GONE);
            gameEmptyText.setVisibility(View.GONE);
        }

        return root;
    }

    // Action floating button Rouge
    public void BackToShare(View v) {
        getActivity().onBackPressed();
    }

    // Action floating button Vert
    public void Share(View v)
    {
        // CREATION PDF
        FilePath = CreateBitmap();

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
        }
    }

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private String CreateBitmap()
    {
        ScrollView scroll = root.findViewById(R.id.share_preview_scroll);
        int totalHeight = scroll.getChildAt(0).getHeight();
        int totalWidth = scroll.getChildAt(0).getWidth();

        Bitmap b = getBitmapFromView(scroll,totalHeight,totalWidth);

        //Save bitmap
        File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MaBiblio.png");
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

    // --- OLD ---

    private String CreatePDF(){

        // Vérifier permissions ecriture/lecture
        verifyStoragePermissions();

        // Verification si l'écriture de fichier sur le téléphone est possible
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(getContext(),R.string.share_stockageNonValide, Toast.LENGTH_SHORT).show();
        }
        else{
            // RECUP VIEW
            View VueATransformer = this.getView();

            // Création du fichier PDF
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MaBiblio.pdf");

            // TRANSFORMER LA VIEW EN IMAGE
            VueATransformer.setDrawingCacheEnabled(true);
            Bitmap screen = Bitmap.createBitmap(VueATransformer.getDrawingCache());
            VueATransformer.setDrawingCacheEnabled(false);

            // ADAPTATION TAILLE BITMAP


            // TRANSFORMER L'IMAGE EN PDF
            try {
                Document  document = new Document();

                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                addImage(document,byteArray);
                document.close();

                // RETOURNE CHEMIN DU PDF CREE
                return pdfFile.getAbsolutePath();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }

    private static void addImage(Document document, byte[] byteArray)
    {
        Image image = null;
        try
        {
            image = Image.getInstance(byteArray);
        }
        catch (BadElementException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try
        {
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    // METHODES DE TRI

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Film> TrierListeFilm(ArrayList<Film> liste){

        // CHOIX OPTION DE TRI
        if(liste.size() != 0){
            Predicate<Film> statut = null;
            switch(ShareFragment.setup.cat){
                case tous:
                    return liste;
                case encours:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.EnCours;
                    break;
                case planifies:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Planifie;
                    break;
                case termines:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Termine;
                    break;
                case decote:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Abandonne;
                    break;
                case favoris:
                    statut = x -> x.isFavori() == true;
                    break;
            }

            // TRI
            ArrayList<Film> listeFiltree = (ArrayList<Film>) liste.stream().filter(statut).collect(Collectors.toList());
            return listeFiltree;
        }
        return liste;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Serie> TrierListeSerie(ArrayList<Serie> liste){

        // CHOIX OPTION DE TRI
        if(liste.size() != 0){
            Predicate<Serie> statut = null;
            switch(ShareFragment.setup.cat){
                case tous:
                    return liste;
                case encours:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.EnCours;
                    break;
                case planifies:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Planifie;
                    break;
                case termines:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Termine;
                    break;
                case decote:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Abandonne;
                    break;
                case favoris:
                    statut = x -> x.isFavori() == true;
                    break;
            }

            // TRI
            ArrayList<Serie> listeFiltree = (ArrayList<Serie>) liste.stream().filter(statut).collect(Collectors.toList());
            return listeFiltree;
        }
        return liste;
    }
    public ArrayList<Livre> TrierListeLivre(ArrayList<Livre> liste){

        // CHOIX OPTION DE TRI
        if(liste.size() != 0){
            Predicate<Livre> statut = null;
            switch(ShareFragment.setup.cat){
                case tous:
                    return liste;
                case encours:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.EnCours;
                    break;
                case planifies:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Planifie;
                    break;
                case termines:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Termine;
                    break;
                case decote:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Abandonne;
                    break;
                case favoris:
                    statut = x -> x.isFavori() == true;
                    break;
            }

            // TRI
            ArrayList<Livre> listeFiltree = (ArrayList<Livre>) liste.stream().filter(statut).collect(Collectors.toList());
            return listeFiltree;
        }
        return liste;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Jeu> TrierListeJeu(ArrayList<Jeu> liste){

        // CHOIX OPTION DE TRI
        if(liste.size() != 0){
            Predicate<Jeu> statut = null;
            switch(ShareFragment.setup.cat){
                case tous:
                    return liste;
                case encours:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.EnCours;
                    break;
                case planifies:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Planifie;
                    break;
                case termines:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Termine;
                    break;
                case decote:
                    statut = x -> x.getStatut() == Oeuvre.TypeStatut.Abandonne;
                    break;
                case favoris:
                    statut = x -> x.isFavori() == true;
                    break;
            }

            // TRI
            ArrayList<Jeu> listeFiltree = (ArrayList<Jeu>) liste.stream().filter(statut).collect(Collectors.toList());
            return listeFiltree;
        }
        return liste;
    }
}
