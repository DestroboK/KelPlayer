package com.example.kelplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class Reproductor extends AppCompatActivity {
    Button btnPlay,btnSiguiente,btnPrevio, btnAdelantar, btnAtrasar;
    TextView txtNomCancion, txtInicio, txtFinal;
    SeekBar progreso;
    BarVisualizer visualizadorBarra;
    int posicion;
    ArrayList<File> canciones;

    String nomCanc;
    public  static  final  String EXTRA_NAME = "cancion";
    static MediaPlayer reproductor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        btnPlay = findViewById(R.id.btnPlay);
        btnAdelantar = findViewById(R.id.btnAdelantar);
        btnAtrasar = findViewById(R.id.btnAtrasar);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnPrevio = findViewById(R.id.btnPrevio);
        txtNomCancion = findViewById(R.id.nomCancion);
        txtInicio = findViewById(R.id.txtInicio);
        txtFinal = findViewById(R.id.txtFinal);
        progreso = findViewById(R.id.seekbarProgreso);
        visualizadorBarra = findViewById(R.id.visualizadorBarra);

        if(reproductor != null)
        {
            reproductor.stop();
            reproductor.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        canciones =  (ArrayList) bundle.getParcelableArrayList("canciones");
        String nomCancion = intent.getStringExtra("cancion");
        posicion = bundle.getInt("posicion", 0);
        txtNomCancion.setSelected(true);

        Uri uri = Uri.parse(canciones.get(posicion).toString());
        nomCancion = canciones.get(posicion).getName();
        txtNomCancion.setText(nomCancion);


        reproductor = MediaPlayer.create(getApplicationContext(), uri);
        reproductor.start();


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reproductor.isPlaying())
                {
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    reproductor.pause();
                }
                else
                    {
                        btnPlay.setBackgroundResource(R.drawable.ic_pause);
                        reproductor.start();
                    }
            }
        });
    }
}