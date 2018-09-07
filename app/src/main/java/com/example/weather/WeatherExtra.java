package com.example.weather;

import android.content.res.Resources;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;

import com.caverock.androidsvg.SVG;

/**
 * Created by василий on 28.01.2016.
 */
public class WeatherExtra {
    static public Drawable getWeatherIconDrawable(Resources resources, String sWeatherIcon, float Height, float Width) {
        try {
            //SVG svg = SVG.getFromResource(getResources(), R.raw.sunss);
                            /*Picture picture = svg.renderToPicture(rootView.getHeight() / 3, rootView.getWidth() / 3);
                            weatherImg.setImageDrawable();*/
            //LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.layout);
            //SVGImageView svgImageView = new SVGImageView(getActivity());
            //svgImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            SVG svg = SVG.getFromResource(resources, AppContext.mapWeatherIconsAssoc.get(sWeatherIcon));
            if (Height > 0) svg.setDocumentHeight(Height);
            if (Width > 0) svg.setDocumentWidth(Width);
            Picture picture = svg.renderToPicture();
            //drawable.setColorFilter(0x1d1d1d, PorterDuff.Mode.MULTIPLY);

            //layout.addView(svgImageView,
            //        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            return new PictureDrawable(picture);
            //svgImageView.setVisibility(View.VISIBLE);

            //PictureDrawable drawable = new PictureDrawable(SVG.getFromResource(getResources(), R.raw.sunss).renderToPicture());
            //drawable.setColorFilter(0xcccccc, PorterDuff.Mode.MULTIPLY);
            //weatherImg.setImageDrawable(drawable);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
