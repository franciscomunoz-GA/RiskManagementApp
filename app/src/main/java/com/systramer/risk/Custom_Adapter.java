package com.systramer.risk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Custom_Adapter extends BaseAdapter {

    Context context;
    List<Cita> list;

    public Custom_Adapter(Context context, List<Cita> list) {
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
        TextView TextViewDescripcion;
        TextView TextViewFecha;
        TextView TextViewHora;
        Cita c = list.get(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_citas,null);
        }
        icono               = convertView.findViewById(R.id.icon);
        TextViewTitulo      = convertView.findViewById(R.id.Titulo);
        TextViewDescripcion = convertView.findViewById(R.id.Descripcion);
        TextViewFecha       = convertView.findViewById(R.id.Fecha);
        TextViewHora        = convertView.findViewById(R.id.Hora);

        icono.setImageResource(c.Imagen);
        TextViewTitulo.setText(c.Titulo);
        TextViewDescripcion.setText(c.Descripcion);
        TextViewFecha.setText(c.Fecha);
        TextViewHora.setText(c.Hora);

        return convertView;
    }
}
