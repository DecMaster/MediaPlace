package nf28.mediaplace.Adapters;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Jeu;
import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Oeuvre;
import nf28.mediaplace.Models.Serie;
import nf28.mediaplace.R;
import nf28.mediaplace.ui.search.SearchFragment;
import nf28.mediaplace.ui.share.ShareFragment;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    protected ArrayList<Oeuvre> dataSet;
    private OnItemClickListener mListener;

    public SearchAdapter(ArrayList<Oeuvre> dataSet) {
        this.dataSet = dataSet;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewDate;
        TextView author;
        TextView description;
        ImageView imageViewIcon;
        FloatingActionButton floatingActionButton;
        TextView ajoute;
        @SuppressLint("RestrictedApi")
        public MyViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            this.author = (TextView) itemView.findViewById(R.id.textViewAuthor);
            this.description = (TextView) itemView.findViewById(R.id.textViewDescription);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.floatingActionButton = (FloatingActionButton) itemView.findViewById(R.id.fab);
            this.ajoute = (TextView) itemView.findViewById(R.id.ajoute);
            this.floatingActionButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }



    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        view.setOnClickListener(SearchFragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view, mListener);
        return myViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        TextView textViewDate = holder.textViewDate;
        TextView textViewAuthor = holder.author;
        TextView textViewDescription = holder.description;
        ImageView imageView = holder.imageViewIcon;
        FloatingActionButton floatingActionButton = holder.floatingActionButton;
        TextView textAjoute = holder.ajoute;
        if(Toolbox.instance.userBiblio.getOeuvre(dataSet.get(listPosition).getId()) != null){
            hide(holder.itemView);
        }
        else{
            show(holder.itemView);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

        // TITRE
        textViewName.setText(dataSet.get(listPosition).getTitleVF());

        // REALISATEUR / AUTEUR
        String chaine = "";
        if(dataSet.get(listPosition) instanceof Film){
            Film o = (Film) dataSet.get(listPosition);
            chaine += Toolbox.instance.listToString(o.getRealisateurs());
            floatingActionButton.setSupportBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.activity.getResources().getColor(R.color.colorMovieBlue)));
            textAjoute.setTextColor(Toolbox.instance.activity.getResources().getColor(R.color.colorMovieBlue));
        }
        if(dataSet.get(listPosition) instanceof Serie){
            Serie o = (Serie) dataSet.get(listPosition);
            chaine += Toolbox.instance.listToString(o.getRealisateurs());
            floatingActionButton.setSupportBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.activity.getResources().getColor(R.color.colorShowGreen)));
            textAjoute.setTextColor(Toolbox.instance.activity.getResources().getColor(R.color.colorShowGreen));
        }
        if(dataSet.get(listPosition) instanceof Livre){
            Livre o = (Livre) dataSet.get(listPosition);
            chaine += Toolbox.instance.listToString(o.getAuteurs());
            floatingActionButton.setSupportBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.activity.getResources().getColor(R.color.colorBookYellow)));
            textAjoute.setTextColor(Toolbox.instance.activity.getResources().getColor(R.color.colorBookYellow));
        }
        if(dataSet.get(listPosition) instanceof Jeu){
            Jeu o = (Jeu) dataSet.get(listPosition);
            chaine += Toolbox.instance.listToString(o.getDeveloppeurs());
            floatingActionButton.setSupportBackgroundTintList(ColorStateList.valueOf(Toolbox.instance.activity.getResources().getColor(R.color.colorVideoGamesRed)));
            textAjoute.setTextColor(Toolbox.instance.activity.getResources().getColor(R.color.colorVideoGamesRed));
        }
        if(chaine.equals("N/A")){ chaine = ""; }
        textViewAuthor.setText(chaine);


        // DATE
        if(chaine.isEmpty()){
            textViewDate.setText(dateFormat.format(dataSet.get(listPosition).getDateSortie()));
        }
        else{
            textViewDate.setText(dateFormat.format(dataSet.get(listPosition).getDateSortie()) + ",");
        }

        // DESCRIPTION
        textViewDescription.setText(dataSet.get(listPosition).getDescription());
        if(dataSet.get(listPosition).isImageOnline()){
            Picasso.get().load(dataSet.get(listPosition).getCheminImage()).fit().placeholder(R.mipmap.placeholder).into(imageView);
        }
        else{
            String path = dataSet.get(listPosition).getCheminImage();
            File img = new File(path);
            if(img.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(new File(path).getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public Oeuvre getItem(int index) {
        return dataSet.get(index);
    }

    @SuppressLint("RestrictedApi")
    public void hide(View view) {
        view.findViewById(R.id.fab).setVisibility(View.GONE);
        view.findViewById(R.id.ajoute).setVisibility(View.VISIBLE);
    }
    @SuppressLint("RestrictedApi")
    public void show(View view) {
        view.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        view.findViewById(R.id.ajoute).setVisibility(View.GONE);
    }
}
