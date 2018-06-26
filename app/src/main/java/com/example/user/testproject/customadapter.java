package com.example.user.testproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class customadapter implements GoogleMap.InfoWindowAdapter{

    private final View window;
    private Context context;
    public customadapter(Context context) {
        this.context = context;
        window= LayoutInflater.from(context).inflate(R.layout.custominfo,null);
    }
    private void renderWindowText(Marker marker,View view){
        String title=marker.getTitle();
        TextView tvTitle=(TextView)view.findViewById(R.id.title);
        if(!title.equals("")){
           tvTitle.setText(title);
        }
        String snippet=marker.getSnippet();
        TextView snip=(TextView)view.findViewById(R.id.snippet);
        if(!snippet.equals("")){
            snip.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,window);
        return window;
    }
}
