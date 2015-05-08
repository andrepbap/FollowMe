package com.followme.adapter;

import com.followme.R;
import com.followme.app.AppController;
import com.followme.model.Grupo;
 
import java.util.List;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
 
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Grupo> grupoItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
 
    public CustomListAdapter(Activity activity, List<Grupo> grupoItems) {
        this.activity = activity;
        this.grupoItems = grupoItems;
    }
 
    @Override
    public int getCount() {
        return grupoItems.size();
    }
 
    @Override
    public Object getItem(int location) {
        return grupoItems.get(location);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);
 
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView nome = (TextView) convertView.findViewById(R.id.nome_grupo);
        TextView descricao = (TextView) convertView.findViewById(R.id.descricao);
        TextView admin = (TextView) convertView.findViewById(R.id.admin);
        TextView id = (TextView) convertView.findViewById(R.id.id_grupo);
 
        // getting grupo data for the row
        Grupo g = grupoItems.get(position);
 
        // thumbnail image
        thumbNail.setImageUrl(g.getFoto_patch(), imageLoader);
         
        // Nome
        nome.setText(g.getNome());
         
        // Descrição
        descricao.setText(g.getDescricao());
        
        // admin
        admin.setText(g.getAdmin().getEmail());
         
        // Id
        id.setText(String.valueOf(g.getId()));
 
        return convertView;
    }
 
}