package com.example.cantonese.drawView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.os.Environment;
import android.widget.Toast;

import com.example.cantonese.R;
import com.example.cantonese.SecondActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


//this view contains the drawing surface
public class drawView extends View {

    int WIDTH = 150;
    int HEIGHT = 150;
    int total_saves = 0;
    Bitmap bitmap;
    Paint paint;
    Path path;
    Path last_path;
    Canvas canvas;
    Context context;
    private ArrayList<Path> paths;
    float mX, mY; // previous coordinates
    static final float TOL = 2;

    public drawView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        path = new Path();
        paths = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        last_path = new Path();
    }
    //when the view is created (when the user clicks "Draw"), this runs
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // create a bitmap for the canvas, with the passed size
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(ContextCompat.getColor(context, R.color.whiteColor));

        //create the canvas
        canvas = new Canvas(bitmap);

    }
    //when the user puts their finger on the drawing surface, this is the first point they touch
    public void startTouch(float x, float y){

        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    //as the user moves their finger, edit the current path
    public void moveTouch(float x, float y){

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOL || dy >= TOL){
            path.quadTo(mX, mY, (x + mX)/2, (y+ mY)/2);
            mX = x;
            mY = y;
        }
    }
    //when user lifts finger
    public void upTouch(){

        path.lineTo(mX, mY);
        canvas.drawPath(path, paint);
        paths.add(path);
        path = new Path();

    }

    //
    public void clearCanvas(){
        paths.clear();
        invalidate(); //updates the canvas with the new paths

    }

    //clear the previous path
    public void undoLast(){

        if(paths.size() > 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }
    //when save button is pressed (WIP)
    public void onSave(){
        total_saves += 1; //for naming the saved picture

        String save_num = String.valueOf(total_saves);
        String fname = "save-" + save_num + ".png";

        CharSequence name = save_num;
        String extStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String saveDir = "Saves";
        File pictureFolder = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        File picture = new File(pictureFolder, fname);
        try {
            Toast save = Toast.makeText(context, name, Toast.LENGTH_SHORT);
            save.show();
            OutputStream out = new FileOutputStream(picture);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);
        for (Path p : paths){
            canvas.drawPath(p, paint);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    //for every time user moves finger
    public boolean onTouchEvent(MotionEvent event){

        float x = event.getX();
        float y = event.getY();
        //call appropriate func depending on touch type
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }
}





















