package com.example.kelplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listaCanciones;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listaCanciones = findViewById(R.id.listCanciones);
        runtimePermission();
    }

    public void runtimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        mostrarCanciones();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> buscarMusica (File archivo)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] archivos = archivo.listFiles();
        //Revisamos si el archivo .mp3 es una carpeta o un soloarchivo de sonido.
        for(File unArchivo : archivos)
        {
            if(unArchivo.isDirectory() && !unArchivo.isHidden())
            {
                arrayList.addAll(buscarMusica(unArchivo));
            }
            else
            {
                if(unArchivo.getName().endsWith(".mp3") || unArchivo.getName().endsWith(".wav"))
                {
                    arrayList.add(unArchivo);
                }
            }
        }
        return arrayList;
    }



    void mostrarCanciones()
    {
        final ArrayList<File> misCanciones = buscarMusica(Environment.getExternalStorageDirectory());
        items = new String[misCanciones.size()];
        for (int i = 0; i<misCanciones.size(); i++)
        {
            items[i] = misCanciones.get(i).getName().replace(".mp3","");
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listaCanciones.setAdapter(adaptador);

        miAdaptador miAdaptador = new miAdaptador();
        listaCanciones.setAdapter(miAdaptador);


        listaCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nomCancion = (String) listaCanciones.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), Reproductor.class)
                        .putExtra("canciones", misCanciones)
                        .putExtra("cancion",nomCancion)
                        .putExtra("posicion",i));
            }
        });

    }

    class miAdaptador extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vieww = getLayoutInflater().inflate(R.layout.formato_lista_canciones,null);
            TextView txtCancion = vieww.findViewById(R.id.txtCancion);
            txtCancion.setSelected(true);
            txtCancion.setText(items[i]);
            return vieww;

        }
    }
}