package com.example.mohamed.myapp;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class showCamera extends SurfaceView implements SurfaceHolder.Callback {
    Camera camera ;
    SurfaceHolder holder ;
    public showCamera(Context context , Camera camera) {
        super(context);
        this.camera = camera ;
        holder = getHolder() ;
        holder.addCallback(this);

    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Camera.Parameters parms = camera.getParameters() ;
        List<Camera.Size> sizes = parms.getSupportedPictureSizes();
        int height =0, width =0;
        for(Camera.Size size :sizes)
        {
            int temp = size.height;
            if(temp>height)
            {
                height=size.height;
                width=size.width;
            }


        }
        //change orientation
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            parms.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            parms.setRotation(90);

        } else {
            parms.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            parms.setRotation(0);
        }
        parms.setPictureSize(width,height);
        camera.setParameters (parms);
        try {
            camera.setPreviewDisplay(holder);

            camera.startPreview();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
