package com.jah.bateria_musica_lv;

import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ImageView imgPlatilloIzq, imgBombo, imgPlatilloDer, imgPlay, imgPause, imgStop;
    SeekBar sbBarra;
    ListView lvCanciones;
    SoundPool soundPool;
    TextView lblTiempo, lblDuracion;
    MediaPlayer mediaPlayer;
    String[] canciones;
    int[] sonidos;
    ArrayAdapter <String> adapter;
    int[] arrCanciones;
    int cancionReproducida;
    Handler handler = new Handler();
    Runnable handlerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initComponents();
        imgPlatilloIzq.setOnClickListener(view -> reproducirSonido(sonidos[1]));
        imgPlatilloDer.setOnClickListener(view -> reproducirSonido(sonidos[1]));
        imgBombo.setOnClickListener(view -> reproducirSonido(sonidos[0]));
        lvCanciones.setOnItemClickListener((adapterView, view, posicion, l) -> {
            reproducirCancion(arrCanciones[posicion]);
            cancionReproducida = posicion;
        });
        imgPlay.setOnClickListener(view -> play());
        imgPause.setOnClickListener(view -> pause());
        imgStop.setOnClickListener(view -> stop(cancionReproducida));
        sbBarra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void stop(int cancionReproducida) {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, arrCanciones[cancionReproducida]);
            sbBarra.setProgress(0);
            lblTiempo.setText("00:00");
        }
    }

    private void pause() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    private void play() {
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    private void reproducirCancion(int arrCancion) {
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this, arrCancion);
            mediaPlayer.start();
        }else if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, arrCancion);
            mediaPlayer.start();
        }else if(mediaPlayer != null && !mediaPlayer.isPlaying()){
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, arrCancion);
            mediaPlayer.start();
        }
        actualizarTiempo(mediaPlayer.getDuration(), lblDuracion);
        startTimer();
    }

    private void reproducirSonido(int sonido) {
        soundPool.play(sonido, 1, 1, 0, 0, 1);
    }

    public  void startTimer() {
        handlerTask = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int position = mediaPlayer.getCurrentPosition();
                    sbBarra.setProgress(position);
                    sbBarra.setMax(mediaPlayer.getDuration());
                    actualizarTiempo(mediaPlayer.getCurrentPosition(), lblTiempo);
                    if(mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() - 1000 && cancionReproducida < arrCanciones.length - 1){
                        reproducirCancion(arrCanciones[++cancionReproducida]);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(handlerTask);
    }

    private void actualizarTiempo(int tiempo, TextView lblModificacion) {
        String formattedTime = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(tiempo),
                TimeUnit.MILLISECONDS.toSeconds(tiempo) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tiempo)));
        lblModificacion.setText(formattedTime);
    }

    private void initComponents() {
        lblTiempo = findViewById(R.id.lblTiempo);
        lblDuracion = findViewById(R.id.lblDuracion);
        imgPlatilloIzq = findViewById(R.id.imgPlatilloIzq);
        imgPlatilloIzq.setImageResource(R.drawable.platilloizq);
        imgBombo = findViewById(R.id.imgBombo);
        imgBombo.setImageResource(R.drawable.bombo);
        imgPlatilloDer = findViewById(R.id.imgPlatilloDer);
        imgPlatilloDer.setImageResource(R.drawable.platilloder);
        imgPlay = findViewById(R.id.imgPlay);
        imgPause = findViewById(R.id.imgPause);
        imgStop = findViewById(R.id.imgStop);
        sbBarra = findViewById(R.id.sbBarra);
        sbBarra.setProgress(0);
        lvCanciones = findViewById(R.id.lvCanciones);
        mediaPlayer = new MediaPlayer();
        soundPool = new SoundPool.Builder().setMaxStreams(3).build();

        canciones = new String[]{
                "Cant hear it now", "heavy is the crown", "hellfire", "Enemy", "ma meilleure ennemie"
                ,"Playground", "Paint the town blue", "remember me", "sucker", "the line", "To ashes and blood"
                ,"wasteland"
        };

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, canciones);
        lvCanciones.setAdapter(adapter);

        sonidos = new int[]{
                soundPool.load(this, R.raw.bombo, 1),
                soundPool.load(this, R.raw.platillo, 1)
        };

        arrCanciones = new int[]{
                R.raw.can_t_hear_it_now, R.raw.heavy_is_the_crown, R.raw.hellfire_, R.raw.imagine_dragons,
                R.raw.ma_meilleure_ennemie, R.raw.playground, R.raw.paint_the_town_blue, R.raw.remember_me,
                R.raw.sucker, R.raw.the_line, R.raw.to_ashes_and_blood, R.raw.wasteland
        };
    }
}