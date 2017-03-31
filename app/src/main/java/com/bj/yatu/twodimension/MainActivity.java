package com.bj.yatu.twodimension;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.renderer.Renderer;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.Symbol;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private MapView map;
//    private GraphicsLayer graphicsLayer;

    ArcGISTiledMapServiceLayer tileLayer;
    FeatureLayer featureLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        tileLayer = new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
        map.addLayer(tileLayer);


        String shpPath = getSDPath() + "/shapefile/北京102100.shp";
        try {
            ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(shpPath);
            //渲染featurelayer
            featureLayer = new FeatureLayer(shapefileFeatureTable);
            Symbol symbol = new SimpleFillSymbol(Color.BLUE);
            Renderer renderer = new SimpleRenderer(symbol);
            featureLayer.setRenderer(renderer);

            map.addLayer(featureLayer);

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }
    }

    /**
     * 获取SD卡位置
     * @return
     */
    public String getSDPath(){
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();//获取根目录
        }
        return sdDir.toString();
    }
}
