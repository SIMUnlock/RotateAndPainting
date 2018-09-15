package com.outlook.gonzasosa.apps.touchingcontrols;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

//Se implement el Listener del SeekBar
public class Main2Activity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    ImageView iv;
    ShapeDrawable sd;
    Display display;
    Point size;
    TextView redText,greenText,blueText;
    int width, height;
    private SeekBar seekBarRed, seekBarGreen, seekBarBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Se crean variables de los TextView para escribir sobre ellos Ej. Rojo  123
        redText=(TextView) findViewById(R.id.textRedCant);
        redText.setText(""+MainActivity.color_red);
        greenText=(TextView) findViewById(R.id.textGreenCant);
        greenText.setText(""+MainActivity.color_green);
        blueText=(TextView) findViewById(R.id.textBlueCant);
        blueText.setText(""+MainActivity.color_blue);


        //Se crea la Toolbar
        Toolbar toolbar = findViewById(R.id.miToolbar2);
        toolbar.setTitle("Change Color");
        setSupportActionBar(toolbar);


        //Se declara una figura
        sd = new ShapeDrawable(new OvalShape());

        //Se crea un objeto display para obtener el tamaño de la pantalla
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        //Se dibuja el circulo en base al tamaño de la pantalla
        sd.setIntrinsicHeight(height / 2);
        sd.setIntrinsicWidth(width);
        sd.getPaint().setARGB(255, MainActivity.color_red, MainActivity.color_green, MainActivity.color_blue);
        iv = (ImageView) findViewById(R.id.imageView2);
        iv.setBackground(sd);

        //Se crean los SeekBar y se establecen los valores RGB antiguos
        seekBarRed = (SeekBar) findViewById(R.id.seekBarRed);
        seekBarRed.setProgress(MainActivity.color_red);
        seekBarGreen = (SeekBar) findViewById(R.id.seekBarGreen);
        seekBarGreen.setProgress(MainActivity.color_green);
        seekBarBlue = (SeekBar) findViewById(R.id.seekBarBlue);
        seekBarBlue.setProgress(MainActivity.color_blue);


        //Se crean los Listener para cada que cambien los valores RGB
        seekBarRed.setOnSeekBarChangeListener(this);
        seekBarGreen.setOnSeekBarChangeListener(this);
        seekBarBlue.setOnSeekBarChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.back) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //Mientrasa el progreso de las barras se esten moviendo
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //Switch de las barras, actualización de variables de la Actividad 1
        switch (seekBar.getId()){
            case R.id.seekBarRed:
                MainActivity.color_red = seekBar.getProgress();
                redText.setText(""+seekBar.getProgress());
                break;
            case R.id.seekBarGreen:
                MainActivity.color_green = seekBar.getProgress();
                greenText.setText(""+seekBar.getProgress());
                break;
            case R.id.seekBarBlue:
                MainActivity.color_blue = seekBar.getProgress();
                blueText.setText(""+seekBar.getProgress());
                break;
        }

        //Se redibuja el circulo con los nuevos valores
        sd.setIntrinsicHeight(height / 2);
        sd.setIntrinsicWidth(width);
        sd.getPaint().setARGB(255, MainActivity.color_red, MainActivity.color_green, MainActivity.color_blue);
        iv.setBackground(sd);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
