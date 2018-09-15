package com.outlook.gonzasosa.apps.touchingcontrols;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;


import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MyTouchEvent";

    //variables draw
    private Canvas canvas;
    private Paint paint;

    //Primer y segundo punto para dibujar
    private float down_x=0,down_y=0,move_x=0,move_y=0;

    //RGB inicial
    public static int color_red=0,color_blue=0,color_green=0;

    //Variables rotate
    //variable sin dedo
    private static final int PointerNull = -1;
    //recta 1
    private float x1, y1, x2, y2;

    //indice de dedo 1 y 2
    private int finger1, finger2;


    //control para rotar
    int rotate=0;

    Bitmap mutable;
    ImageView imageView;
    Bitmap original;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.miToolbar);
        toolbar.setTitle("Draw");
        setSupportActionBar(toolbar);

        imageView = findViewById (R.id.imageView1);


        //Se obtiene el bitmap del imageView cargado
        original = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        // Como no se puede usar el bitmap original se tiene que hacer una copia para poder editar
        mutable = original.copy(Bitmap.Config.ARGB_8888,true);

        //Se crea un lienzo para poder trabajar sobre el
        canvas = new Canvas(mutable);

        //Creamos un objeto de tipo paint para poder dibujar y se le establecen opciones de estética
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        //En esta linea es donde se va a recibir como parametro de la otra actividad el color elegido por el usuario
        paint.setARGB(255,color_red,color_green,color_blue);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20);

        //Se dibuja el nuevo bitmap
        imageView.setImageBitmap(mutable);

        imageView.setOnTouchListener ((view, motionEvent) -> {
            //Como el image view es mas grande que la imagen, había un problema con las coordenadas x ,y
            //Se desfasaban y es por eso que se tiene que usar una Matriz

            paint.setARGB(255,color_red,color_green,color_blue);
            Matrix inverse = new Matrix();
            imageView.getImageMatrix().invert(inverse);
            float[] pts = {
                    motionEvent.getX(), motionEvent.getY()
            };
            inverse.mapPoints(pts);
            String message = "";

            switch (motionEvent.getActionMasked ()) {
                case MotionEvent.ACTION_DOWN:
                    message = String.format (Locale.US, "Touch DOWN on (%.2f, %.2f)", motionEvent.getX (), motionEvent.getY ());
                    //Cuando sea DOWN se toman x, y actuales de la matriz y rotar esté desactivado
                    if(rotate==0){
                        down_x=(float)Math.floor(pts[0]);
                        down_y=(float)Math.floor(pts[1]) ;
                    }else{
                        //Guardar indice del dedo1
                        finger1 = motionEvent.getPointerId(motionEvent.getActionIndex());
                    }



                    break;
                case MotionEvent.ACTION_UP:
                    //Reset del indice del dedo 1
                    finger1 = PointerNull;
                    break;
                case MotionEvent.ACTION_MOVE:
                    message = String.format (Locale.US, "MOVING on (%.2f, %.2f)", motionEvent.getX (), motionEvent.getY ());
                    //Si rotate esta desactivado solo pinta
                    if(rotate==0) {
                        move_x = (float) Math.floor(pts[0]);
                        move_y = (float) Math.floor(pts[1]);
                        //Si se mueve el dedo y es desigual a la posicion original, se dibuja una linea y se actualizan los down
                        if (move_x != down_x & move_y != down_y) {
                            canvas.drawLine(down_x, down_y, move_x, move_y, paint);
                            down_x = move_x;
                            down_y = move_y;
                            imageView.invalidate();
                        }
                    }
                    //Si rotate esta activado
                    else {
                        //Si dedo 1 y dedo 2 están activos
                        if (finger1 != PointerNull && finger2 != PointerNull) {
                            float a1, b1, a2, b2;
                            //Se obtienen los puntos de la 2da recta
                            a2 = motionEvent.getX(motionEvent.findPointerIndex(finger1));
                            b2 = motionEvent.getY(motionEvent.findPointerIndex(finger1));
                            a1 = motionEvent.getX(motionEvent.findPointerIndex(finger2));
                            b1 = motionEvent.getY(motionEvent.findPointerIndex(finger2));
                            //Se calculan el angulo a partir de las 2 rectas
                            float angle = angleBet2Lines(x1, y1, x2, y2, a1, b1, a2, b2);

                            //Se rota la imagen x grados
                            rotateImage(angle);

                        }
                    }



                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    //Si rotar está activado
                    if(rotate==1){
                        //Obtener el indice del dedo 2
                        finger2 = motionEvent.getPointerId(motionEvent.getActionIndex());
                        //Guardar la primera recta
                        x2 = motionEvent.getX(motionEvent.findPointerIndex(finger1));
                        y2 = motionEvent.getY(motionEvent.findPointerIndex(finger1));
                        x1 = motionEvent.getX(motionEvent.findPointerIndex(finger2));
                        y1 = motionEvent.getY(motionEvent.findPointerIndex(finger2));}
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if(rotate==1){
                        //Si el dedo 2 se levanta reset
                        finger2 = PointerNull;

                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    //Si se cancela, ambos reset
                    finger1 = PointerNull;
                    finger2 = PointerNull;
                    break;
            }

            return true;
        });
    }
    private float angleBet2Lines (float x1, float y1, float x2, float y2, float a1, float b1, float a2, float b2)
    {
        //Angulo de recta 1
        float angle1 = (float) Math.atan2( (y1 - y2), (x1 - x2) );
        //Angulo de recta 2
        float angle2 = (float) Math.atan2( (b1 - b2), (a1 - a2) );
        //Angulo entre 2 rectas mod 360 por el movimiento circular
        float angle = ((float)Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f)
            angle += 360.0f;
        if (angle > 180.f)
            angle -= 360.0f;
        return angle*-1;
    }
    private void rotateImage(double rotate)
    {
        //Se crea un bitmap con las propiedades del mutable
        Bitmap rotateBitmap = Bitmap.createBitmap(mutable.getWidth(), mutable.getHeight(), mutable.getConfig());

        //nuevo lienzo con la propietades del mutable
        canvas = new Canvas(rotateBitmap);

        //Se obtiene la matriz del iv
        Matrix rotateMatrix = imageView.getImageMatrix();

        //Se rota la matriz con los grados obtenidos , tomando como base de giro el centro de la imagen
        rotateMatrix.setRotate((float)rotate, mutable.getWidth()/2, mutable.getHeight()/2);

        //Se va dibujando el bitmap que va rotando
        canvas.drawBitmap(mutable, rotateMatrix, paint);
        imageView.setImageBitmap(rotateBitmap);
        imageView.invalidate();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.edit) {
            Intent otra = new Intent(this,Main2Activity.class);
            startActivity(otra);
        }
        if(item.getItemId()==R.id.rotate) {
            if (rotate==0){
                rotate=1;
                //Si se va a dibujar, se crea un lienzo con mutable y se asigna
                mutable = mutable.copy(Bitmap.Config.ARGB_8888,true);
                canvas = new Canvas(mutable);
                imageView.setImageBitmap(mutable);
                item.setIcon(R.drawable.right);
            } else{
                //Si se desactiva rotar, ahora nuestro mutable va a ser el contenido del iv en bitmap
                rotate=0;
                mutable = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                item.setIcon(R.drawable.rotate);
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
