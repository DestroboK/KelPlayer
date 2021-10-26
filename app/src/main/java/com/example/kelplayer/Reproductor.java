package com.example.kelplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView imagenCancion;

    String nomCanc;
    Thread procesoActualizarMusica;
    public  static  final  String EXTRA_NAME = "cancion";
    static MediaPlayer reproductor;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizadorBarra != null)
        {
            visualizadorBarra.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        getSupportActionBar().setTitle("Reproduciendo... ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        imagenCancion = findViewById(R.id.imagenCancion);

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

        procesoActualizarMusica = new Thread()
        {
            @Override
            public void run() {
                int duracion = reproductor.getDuration();
                int posicionactual = 0;

                while (posicionactual < duracion)
                {
                    try
                    {
                        sleep(500);
                        posicionactual = reproductor.getCurrentPosition();
                        progreso.setProgress(posicionactual);
                    }
                    catch(InterruptedException | IllegalStateException error)
                    {
                        error.printStackTrace();
                    }
                }
            }
        };
        progreso.setMax(reproductor.getDuration());
        procesoActualizarMusica.start();
        progreso.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        progreso.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.SRC_IN);

        progreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                reproductor.seekTo(seekBar.getProgress());
            }
        });
        String duracionMaxima = Tiempo(reproductor.getDuration());
        txtFinal.setText(duracionMaxima);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String tiempoActual = Tiempo(reproductor.getCurrentPosition());
                txtInicio.setText(tiempoActual);
                handler.postDelayed(this, delay);
            }
        }, delay);



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
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reproductor.stop();
                reproductor.release();
                posicion = (posicion + 1 ) % canciones.size();
                Uri uri1 = Uri.parse(canciones.get(posicion).toString());
                reproductor = MediaPlayer.create(getApplicationContext(), uri1);
                nomCanc = canciones.get(posicion).getName();
                txtNomCancion.setText(nomCanc);
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                Animar(imagenCancion);
                int audiosessionId = reproductor.getAudioSessionId();
                if(audiosessionId != 1){

                    visualizadorBarra.setAudioSessionId(audiosessionId);
                }
                progreso.setProgress(0);
                progreso.setMax(reproductor.getDuration());
                String duracionMaxima = Tiempo(reproductor.getDuration());
                txtFinal.setText(duracionMaxima);
                reproductor.start();
            }

        });
        btnPrevio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reproductor.stop();
                reproductor.release();
                posicion = ((posicion - 1 )<0) ? ( canciones.size()-1) : (posicion - 1);
                Uri uri1 = Uri.parse(canciones.get(posicion).toString());
                reproductor = MediaPlayer.create(getApplicationContext(), uri1);
                nomCanc = canciones.get(posicion).getName();
                txtNomCancion.setText(nomCanc);
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                Animar(imagenCancion);
                int audiosessionId = reproductor.getAudioSessionId();
                if(audiosessionId != 1){

                    visualizadorBarra.setAudioSessionId(audiosessionId);
                }
                progreso.setProgress(0);
                progreso.setMax(reproductor.getDuration());
                String duracionMaxima = Tiempo(reproductor.getDuration());
                txtFinal.setText(duracionMaxima);

                reproductor.start();
            }

        });



        reproductor.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnSiguiente.performClick();
                }
        });

        btnAdelantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reproductor.isPlaying())
                {
                    reproductor.seekTo(reproductor.getCurrentPosition()+5000);
                }
            }
        });

        btnAtrasar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reproductor.seekTo(reproductor.getCurrentPosition()-5000);
            }
        });


        int audiosessionId = reproductor.getAudioSessionId();
        if(audiosessionId != 1){

            visualizadorBarra.setAudioSessionId(audiosessionId);
        }
    }


    public void Animar(View view)
    {
        ObjectAnimator animador = ObjectAnimator.ofFloat(imagenCancion, "rotation", 0f, 360f);
        animador.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animador);
        animatorSet.start();
    }


    public String Tiempo (int duracion)
    {
        String tiempo = "";
        int minutos = duracion/1000/60;
        int segundos = duracion/1000%60;

        tiempo+=minutos+":";

        if(segundos<10)
        {
            tiempo += "0";
        }

        tiempo += segundos;

        return tiempo;
    }
}