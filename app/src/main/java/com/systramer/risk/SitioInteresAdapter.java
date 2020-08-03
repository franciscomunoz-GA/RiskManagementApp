package com.systramer.risk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SitioInteresAdapter extends BaseAdapter {
    Context context;
    List<SitioInteresRiesgos> list;

    public SitioInteresAdapter(Context context, List<SitioInteresRiesgos> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView icono;
        TextView TextViewTitulo;
        TextView TextViewImpacto;
        TextView TextViewProbabilidad;
        TextView TextViewRespondido;
        SitioInteresRiesgos s = list.get(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_riesgos,null);
        }
        icono                = convertView.findViewById(R.id.icon);
        TextViewTitulo       = convertView.findViewById(R.id.NombreRiesgo);
        TextViewImpacto      = convertView.findViewById(R.id.Impacto);
        TextViewProbabilidad = convertView.findViewById(R.id.Probabilidad);
        TextViewRespondido   = convertView.findViewById(R.id.Respondido);

        icono.setImageResource(s.Imagen);
        TextViewTitulo.setText(s.Riesgo);
        TextViewImpacto.setText(String.valueOf(s.Impacto));
        TextViewProbabilidad.setText(String.valueOf(s.Probabilidad));
        TextViewRespondido.setText(s.Respondido);
        return convertView;
    }
}
