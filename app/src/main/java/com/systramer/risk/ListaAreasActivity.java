package com.systramer.risk;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.systramer.risk.Utilidades.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ListaAreasActivity extends AppCompatActivity {
    ProgressBar progressBar;

    String IdUsuario;
    String IdCita;
    String Tipo;
    ConexionSQLiteHelper conexionSQLiteHelper;
    ListView ListViewAreas;
    List<Cita> list;
    private RequestQueue requestQueue;
    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_areas);

        Intent intent = getIntent();
        IdUsuario = intent.getStringExtra("IdUsuario");
        IdCita    = intent.getStringExtra("IdCita");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView2);
        textView = (TextView) findViewById(R.id.textView3);

        ListViewAreas = findViewById(R.id.ListaAreas);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        conexionSQLiteHelper = new ConexionSQLiteHelper(this, "bd_encuestas", null, 3);

        TraerInformacion();
    }
    private void TraerInformacion(){
        progressBar.setVisibility(View.VISIBLE);

        final JSONObject Parametros = new JSONObject();
        try {
            Parametros.put("Id", IdCita);
            Parametros.put("Tipo", 2);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final String savedata = Parametros.toString();
        String URL = "http://cuenta-cuentas.com/backend/public/api/Seleccionar/EncuestaD";
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
                        JSONArray Citas = new JSONArray(Data);
                        for (int i = 0; i < Citas.length(); i++) {
                            JSONObject cliente = Citas.getJSONObject(i);

                            String IdCliente = InsertarRegistro(Utilidades.TablaClientes, cliente);
                            if(IdCliente == ""){
                                IdCliente = InsertarRegistro(Utilidades.TablaClientes, cliente);
                            }
                            String Areas = cliente.getString("Areas");
                            JSONArray ListaAreas = new JSONArray(Areas);
                            for (int j = 0; j < ListaAreas.length(); j++) {
                                JSONObject Area = ListaAreas.getJSONObject(j);

                                Area.put("IdCliente", Integer.parseInt(IdCliente));

                                String IdArea = InsertarRegistro(Utilidades.TablaClienteAreas, Area);
                                if(IdArea == ""){
                                    IdArea = InsertarRegistro(Utilidades.TablaClienteAreas, Area);
                                }
                                String Riesgos = Area.getString("Riesgos");
                                JSONArray ListaRiesgos = new JSONArray(Riesgos);
                                for (int k = 0; k < ListaRiesgos.length(); k++){
                                    JSONObject Riesgo = ListaRiesgos.getJSONObject(k);
                                    Riesgo.put("IdArea", Integer.parseInt(IdArea));
                                    Riesgo.put("IdCliente", Integer.parseInt(IdCliente));
                                    InsertarRegistro(Utilidades.TablaClienteAreasRiesgos, Riesgo);
                                }
                            }
                            MostrarLista(Integer.parseInt(IdCliente));
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
    private String InsertarRegistro(String Tabla, JSONObject Parametros){
        SQLiteDatabase select = conexionSQLiteHelper.getReadableDatabase();
        SQLiteDatabase insert = conexionSQLiteHelper.getWritableDatabase();
        String Resultado = "";
        ContentValues values = new ContentValues();
        switch (Tabla){
            case Utilidades.TablaClientes:
                try {

                    int IdEncuesta = Parametros.getInt("IdEncuesta");
                    String Titulo  = Parametros.getString("Titulo");

                    String[] parameters = { String.valueOf(IdEncuesta) };
                    String[] campos = { Utilidades.IdCliente };

                    //Cursor cursor = select.query(Tabla,campos, Utilidades.IdCliente+"=?", parameters, null, null, null);
                    Cursor cursor = select.rawQuery("SELECT * FROM Clientes WHERE Id = " + IdEncuesta, null);
                    cursor.moveToFirst();
                    try {
                        return String.valueOf(cursor.getInt(0));
                    }
                    catch (Exception e){

                        values.put(Utilidades.IdCliente, IdEncuesta);
                        values.put(Utilidades.NombreCliente, Titulo);
                        long r = insert.insert(Tabla, Utilidades.IdCliente, values);

                        InsertarRegistro(Tabla, Parametros);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Utilidades.TablaClienteAreas:
                try {
                    int IdArea    = Parametros.getInt("IdNombreArea");
                    int IdCliente = Parametros.getInt("IdCliente");
                    String Nombre = Parametros.getString("NombreArea");
                    String Area   = Parametros.getString("Area");
                    String[] parameters = { String.valueOf(IdArea) };
                    String[] campos = { Utilidades.IdClienteArea};

                    //Cursor cursor = select.query(Tabla,campos, Utilidades.IdClienteArea+"=?", parameters, null, null, null);
                    Cursor cursor = select.rawQuery("SELECT * FROM ClienteAreas WHERE Id = " + IdArea + " AND IdCliente = " + IdCliente, null);
                    cursor.moveToFirst();
                    try {
                        return String.valueOf(cursor.getInt(0));
                    }
                    catch (Exception e){

                        values.put(Utilidades.IdClienteArea, IdArea);
                        values.put(Utilidades.FKIdCliente, IdCliente);
                        values.put(Utilidades.NombreClienteArea, Nombre);
                        values.put(Utilidades.Area, Area);
                        insert.insert(Tabla, Utilidades.IdSitioInteresRiesgo, values);

                        InsertarRegistro(Tabla, Parametros);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Utilidades.TablaClienteAreasRiesgos:
                try {
                    int IdRiesgo    = Parametros.getInt("IdNombreARiesgo");
                    int IdCliente = Parametros.getInt("IdCliente");
                    int IdArea = Parametros.getInt("IdArea");

                    String Nombre = Parametros.getString("Riesgo");

                    String[] parameters = { String.valueOf(IdRiesgo) };
                    String[] campos = { Utilidades.IdClienteAreasRiesgo};

                    //Cursor cursor = select.query(Tabla,campos, Utilidades.IdClienteAreasRiesgo+"=?", parameters, null, null, null);
                    Cursor cursor = select.rawQuery("SELECT * FROM ClienteAreasRiesgos" +
                                                         " WHERE Id = " + IdRiesgo +
                                                         " AND IdCliente = " + IdCliente +
                                                         " AND IdClienteArea = " + IdArea, null);
                    cursor.moveToFirst();
                    try {
                        return String.valueOf(cursor.getString(0));
                    }
                    catch (Exception e){

                        values.put(Utilidades.IdClienteAreasRiesgo, IdRiesgo);
                        values.put(Utilidades.FKIdClienteArea, IdCliente);
                        values.put(Utilidades.FKIdClienteArea2, IdArea);
                        values.put(Utilidades.NombreClienteRiesgo, Nombre);
                        values.put(Utilidades.ClienteProbabilidad, 0);
                        values.put(Utilidades.ClienteImpacto, 0);
                        values.put(Utilidades.ClienteRespondido, "Pendiente");
                        insert.insert(Tabla, Utilidades.IdClienteAreasRiesgo, values);
                        return "";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                Resultado = "0";
                break;
        };
        //
        return Resultado;
    }
    public void MostrarLista(final int IdCliente){
        SQLiteDatabase select = conexionSQLiteHelper.getReadableDatabase();

        String[] parameters = { String.valueOf(IdCliente) };
        String[] campos = {
                Utilidades.IdClienteArea,
                Utilidades.FKIdCliente,
                Utilidades.NombreClienteArea,
                Utilidades.Area
        };

        Cursor cursor = select.query(Utilidades.TablaClienteAreas,campos, Utilidades.FKIdCliente+"=?", parameters, null, null, null);

        list = new ArrayList<Cita>();
        if(cursor.moveToFirst()){
            do {
                int IdArea    = cursor.getInt(0);
                String Nombre = cursor.getString(2);
                String Area   = cursor.getString(3);
                int Imagen;
                Imagen = R.drawable.baseline_list_alt_black_18dp;

                list.add(new Cita(IdArea, Imagen,2,"Cliente",Area, "", "", Nombre));

            }while (cursor.moveToNext());
            cursor.close();
        }
        else{
            Toast.makeText(getBaseContext(), "No hay riesgos por mostrar",Toast.LENGTH_LONG).show();

        }
        Custom_Adapter adapter = new Custom_Adapter(getApplicationContext(),list);
        ListViewAreas.setAdapter(adapter);

        ListViewAreas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cita area = list.get(position);
                Intent intent = new Intent(ListaAreasActivity.this, EncuestaClientes.class);
                intent.putExtra("IdUsuario", IdUsuario);
                intent.putExtra("IdCliente", IdCita);
                intent.putExtra("IdArea", String.valueOf(area.Id));
                intent.putExtra("Titulo", "Riesgos de "+area.Titulo);
                startActivity(intent);
            }
        });
    }
}