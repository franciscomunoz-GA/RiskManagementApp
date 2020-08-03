package com.systramer.risk;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.systramer.risk.Utilidades.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EncuestaClientes extends AppCompatActivity {
    //DIALOG
    String Impacto, Probabilidad;
    Spinner TImpacto, TProbabilidad;
    Button Guardar, Cerrar;

    String IdUsuario;
    String IdCliente;
    String IdArea;
    String Titulo;

    ProgressBar progressBar;
    ConexionSQLiteHelper conexionSQLiteHelper;
    ListView ListViewRiesgos;
    List<SitioInteresRiesgos> list;
    private RequestQueue requestQueue;
    ImageView imageView;
    TextView textView;
    private Snackbar snackbar;
    ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_clientes);
        Intent intent = getIntent();
        IdUsuario = intent.getStringExtra("IdUsuario");
        IdCliente = intent.getStringExtra("IdCliente");
        IdArea    = intent.getStringExtra("IdArea");
        Titulo    = intent.getStringExtra("Titulo");
        imageView = (ImageView) findViewById(R.id.imageView2);
        textView = (TextView) findViewById(R.id.textView3);
        layout = (ConstraintLayout) findViewById(R.id.container);
        ListViewRiesgos = findViewById(R.id.ListaRiesgos);
        progressBar = findViewById(R.id.progressBarRiesgos);
        progressBar.setVisibility(View.VISIBLE);
        conexionSQLiteHelper = new ConexionSQLiteHelper(this, "bd_encuestas", null, 3);

        MostrarLista(Integer.parseInt(IdCliente), Integer.parseInt(IdArea));
    }
    public void MostrarLista(final int IdCliente, final  int IdArea){
        SQLiteDatabase select = conexionSQLiteHelper.getReadableDatabase();

        String[] parameters = { String.valueOf(IdArea) };
        String[] campos = {
                Utilidades.IdClienteAreasRiesgo,
                Utilidades.FKIdClienteArea,
                Utilidades.NombreClienteRiesgo,
                Utilidades.ClienteImpacto,
                Utilidades.ClienteProbabilidad,
                Utilidades.ClienteRespondido
        };

        //Cursor cursor = select.query(Utilidades.TablaClienteAreasRiesgos,campos, Utilidades.FKIdClienteArea+"=?", parameters, null, null, null);
        Cursor cursor = select.rawQuery("SELECT Id, IdCliente, Nombre, Impacto, Probabilidad, Respondido FROM ClienteAreasRiesgos WHERE IdCliente = " + IdCliente + " AND IdClienteArea = " + IdArea, null);
        list = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                int Id            = cursor.getInt(0);
                String Nombre     = cursor.getString(2);
                int Impacto       = cursor.getInt(3);
                int Probabilidad  = cursor.getInt(4);
                String Respondido = cursor.getString(5);
                int Imagen;
                if(Impacto > 0 && Probabilidad > 0){
                    Imagen = R.drawable.baseline_done_black_18dp;
                }
                else{
                    Imagen = R.drawable.baseline_cancel_black_18dp;
                }

                list.add(new SitioInteresRiesgos(Id, Imagen, Nombre, Impacto, Probabilidad, Respondido));

            }while (cursor.moveToNext());
            cursor.close();
        }
        else{
            Toast.makeText(getBaseContext(), "No hay riesgos por mostrar",Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
        SitioInteresAdapter adapter = new SitioInteresAdapter(getApplicationContext(),list);
        ListViewRiesgos.setAdapter(adapter);

        ListViewRiesgos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SitioInteresRiesgos Riesgo = list.get(position);
                if(Riesgo.Respondido.equals("Pendiente")){
                    OpenDialog(Riesgo.Id, Riesgo.Riesgo, IdCliente, IdArea);
                }
                else{
                    snackbar.make(layout, "El riesgo ya fue evaluado", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    public void OpenDialog(final int Id, final String Riesgo, final int IdCliente, final int IdArea){
        final Dialog dialog = new Dialog(EncuestaClientes.this, R.style.Dialog);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View custom_dialog = layoutInflater.inflate(R.layout.dialog_valores,null);
        custom_dialog.setElevation(2);

        TImpacto = custom_dialog.findViewById(R.id.Impacto);
        TProbabilidad = custom_dialog.findViewById(R.id.Probabilidad);

        Guardar = custom_dialog.findViewById(R.id.btn_Guardar);
        Cerrar = custom_dialog.findViewById(R.id.btn_Cerrar);

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Impacto = TImpacto.getSelectedItem().toString();
                Probabilidad = TProbabilidad.getSelectedItem().toString();
                //TODO CONSUMIR API REST

                //Crear JSON para armar el parametro
                final JSONObject Parametros = new JSONObject();
                JSONObject Riesgo = new JSONObject();
                try {
                    Riesgo.put("IdNombreARiesgo", Id);
                    Riesgo.put("Probabilidad", Probabilidad);
                    Riesgo.put("Impacto", Impacto);
                    JSONArray Riesgos = new JSONArray();
                    Riesgos.put(Riesgo);
                    JSONObject Area = new JSONObject();
                    try {
                        Area.put("IdNombreArea", IdArea);
                        Area.put("Riesgos", Riesgos);
                        JSONArray Areas = new JSONArray();
                        Areas.put(Area);

                        try {
                            Parametros.put("IdEncuesta", IdCliente);
                            Parametros.put("Tipo", 2);
                            Parametros.put("IdUsuario", IdUsuario);
                            Parametros.put("Areas", Areas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String savedata = Parametros.toString();
                String URL = "https://risk-management-backend-dot-systramer.uc.r.appspot.com/public/api/Responder/Encuesta";
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            JSONObject obj = new JSONObject(response);
                            String Mensaje = obj.getString("Message");
                            if(Mensaje.equals("Consulta Exitosa")){
                                String Data = obj.getString("Data");
                                if(Data == "true"){
                                    int Resultado = ModificarRegistro(Id, IdArea, IdCliente, Integer.parseInt(Probabilidad), Integer.parseInt(Impacto));
                                    if(Resultado > 0 ){
                                        MostrarLista(IdCliente, IdArea);
                                        dialog.dismiss();
                                    }
                                }

                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public String getBodyContentType(){ return "application/json; charset=utf-8";}
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return savedata == null ? null: savedata.getBytes("utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        Guardar = custom_dialog.findViewById(R.id.btn_Guardar);
        Cerrar = custom_dialog.findViewById(R.id.btn_Cerrar);
        dialog.setContentView(custom_dialog);

        dialog.setTitle(Riesgo);

        dialog.show();
        /*
        final Dialog dialog = new Dialog(EncuestaClientes.this, R.style.Dialog);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View custom_dialog = layoutInflater.inflate(R.layout.dialog,null);
        custom_dialog.setElevation(2);


        //crear dropdown

        TImpacto = custom_dialog.findViewById(R.id.Impacto);
        TProbabilidad = custom_dialog.findViewById(R.id.Probabilidad);

        TImpacto.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "10")});
        TProbabilidad.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "10")});

        Guardar = custom_dialog.findViewById(R.id.btn_Guardar);
        Cerrar = custom_dialog.findViewById(R.id.btn_Cerrar);

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Impacto = TImpacto.getText().toString();
                Probabilidad = TProbabilidad.getText().toString();
                //TODO CONSUMIR API REST

                //Crear JSON para armar el parametro
                final JSONObject Parametros = new JSONObject();
                JSONObject Riesgo = new JSONObject();
                try {
                    Riesgo.put("IdNombreARiesgo", Id);
                    Riesgo.put("Probabilidad", Probabilidad);
                    Riesgo.put("Impacto", Impacto);
                    JSONArray Riesgos = new JSONArray();
                    Riesgos.put(Riesgo);
                    JSONObject Area = new JSONObject();
                    try {
                        Area.put("IdNombreArea", IdArea);
                        Area.put("Riesgos", Riesgos);
                        JSONArray Areas = new JSONArray();
                        Areas.put(Area);

                        try {
                            Parametros.put("IdEncuesta", IdCliente);
                            Parametros.put("Tipo", 2);
                            Parametros.put("IdUsuario", IdUsuario);
                            Parametros.put("Areas", Areas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String savedata = Parametros.toString();
                String URL = "http://cuenta-cuentas.com/backend/public/api/Responder/Encuesta";
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            JSONObject obj = new JSONObject(response);
                            String Mensaje = obj.getString("Message");
                            if(Mensaje.equals("Consulta Exitosa")){
                                String Data = obj.getString("Data");
                                if(Data == "true"){
                                    int Resultado = ModificarRegistro(Id, IdArea, IdCliente, Integer.parseInt(Probabilidad), Integer.parseInt(Impacto));
                                    if(Resultado > 0 ){
                                        MostrarLista(IdCliente, IdArea);
                                        dialog.dismiss();
                                    }
                                }

                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public String getBodyContentType(){ return "application/json; charset=utf-8";}
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return savedata == null ? null: savedata.getBytes("utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setContentView(custom_dialog);

        dialog.setTitle(Riesgo);

        dialog.show();
        */
    }
    public int ModificarRegistro(int idRiesgo, int idArea, int idCliente, int probabilidad, int impacto){
        int Formula = Integer.sum(impacto, probabilidad)/2;
        String Evaluacion = "";
        //Guardar en base de datos SQLite
        SQLiteDatabase update = conexionSQLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Utilidades.ClienteImpacto, impacto);
        values.put(Utilidades.ClienteProbabilidad, probabilidad);

        if(Formula == 1 || Formula == 2){Evaluacion = "No significativo";}
        if(Formula == 3 || Formula == 4){Evaluacion = "Menor";}
        if(Formula == 5 || Formula == 6){Evaluacion = "Crítico";}
        if(Formula == 7 || Formula == 8){Evaluacion = "Mayor";}
        if(Formula == 9 || Formula == 10){Evaluacion = "Catastrófico";}
        values.put(Utilidades.ClienteRespondido, Evaluacion);
        update.execSQL("UPDATE ClienteAreasRiesgos SET Impacto = "+impacto+", Probabilidad = "+probabilidad+", Respondido = '"+Evaluacion+"'  WHERE Id = "+idRiesgo + " AND IdCliente = " + IdCliente + " AND IdClienteArea = " +idArea);
        //int Resultado = update.update(Utilidades.TablaClienteAreasRiesgos, values, Utilidades.IdClienteAreasRiesgo+"=?", new String[]{String.valueOf(idRiesgo)});

        return 1;
    }
}