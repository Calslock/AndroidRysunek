package com.example.zadanie3;

//2020 Karol Buchajczuk
//calslock@github

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class DrawHandle extends View {

    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint drawPaint;
    private Paint canvasPaint;
    //Zmienne static - zachowują stan po obrocie urządzenia
    private static int strokeWidth = 10;    //grubość pędzla
    private static String brushColor = "#000000";   //kolor pędzla
    protected boolean rd = false; //Zmienna do określenia przywrócenia zreskalowanej bitmapy

    public static final int INCREASE = 0;   //zwiększenie rozmiaru pędzla
    public static final int DECREASE = 1;   //zmniejszenie rozmiaru pędzla

    public DrawHandle(Context context){ //Konstruktor
        super(context);
        init();
    }

    //Metoda inicjalizująca bitmapę
    private void init() {
        path = new Path();

        drawPaint = new Paint();
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setColor(Color.parseColor(brushColor));
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //Metoda zmieniająca kolor
    public void setColor(String color){
        brushColor = color;
        drawPaint.setColor(Color.parseColor(brushColor));
    }

    //Metoda czyszcząca ekran
    public void clearView(){
        setDrawingCacheEnabled(false);
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        invalidate();
        setDrawingCacheEnabled(true);
    }

    //Metoda rysująca zakończenie
    private void drawButt(float x, float y){
        path.reset();
        canvas.drawCircle(x, y, strokeWidth, drawPaint);
    }

    //Metoda rysująca linie
    private void drawLine(float x, float y){
        path.lineTo(x,y);
        canvas.drawPath(path, drawPaint);
    }

    //Metoda zmieniająca grubość pędzla
    public int changeStrokeWidth(int i){
        switch(i){
            case INCREASE: if(strokeWidth<25) strokeWidth++; break;
            case DECREASE: if(strokeWidth>1) strokeWidth--; break;
        }
        drawPaint.setStrokeWidth(strokeWidth);
        return strokeWidth;
    }

    //Getter i setter bitmapy
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    //Metoda przeskalowująca rysunek po obrocie
    private Bitmap getRescaledBitmap(Bitmap bitmap, int newWidth, int newHeight){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale((float)newWidth/width, (float)newHeight/height);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    //Metody przysłaniające @Override
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                drawButt(event.getX(), event.getY());
                path.moveTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                drawLine(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawButt(event.getX(), event.getY());
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        //Jeżeli rd nie jest true - pierwsze uruchomienie programu, zainicjalizuj bitmapę
        if(!rd) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        //Jeżeli rd jest true - urządzenie jest po obrocie
        else{
            bitmap = getRescaledBitmap(bitmap, w, h);
            rd = false;
        }
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, canvasPaint);
        canvas.drawPath(path, drawPaint);
    }
}
