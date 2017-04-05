package com.bj.yatu.twodimension;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bj.yatu.twodimension.utils.GPSInfoProvider;
import com.bj.yatu.twodimension.utils.GpsUtil;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.Renderer;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.Symbol;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG="MainActivity";
    private MapView map;
    private ArcGISTiledMapServiceLayer tileLayer;
    private GraphicsLayer graphicsLayer;
    private FeatureLayer featureLayer;
    private Button location_btn,sure_btn;
    private  List<Point> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        if(!GpsUtil.isOPen(this)){
            showGpsAlert();
        }
    }

    private void initView() {
        //加载图层
        map = (MapView) findViewById(R.id.map);
        location_btn= (Button) findViewById(R.id.location_btn);
        location_btn.setOnClickListener(this);
        sure_btn= (Button) findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);


        tileLayer = new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
        map.addLayer(tileLayer);

        addShape();//加载shape

        graphicsLayer = new GraphicsLayer();
        map.addLayer(graphicsLayer);

    }

    /**
     * 加载shape
     */
    private void addShape() {
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

    /**
     * 网络设置对话框
     */
    private void showGpsAlert() {
        Log.i("TAG","进入gps设置");
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("提示");
        alert.setMessage("GPS定位未开启，去开启？");
        alert.setCancelable(false);
        alert.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.create();
        alert.show();
    }

    /**
     * 设置完gps
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.location_btn:
                contLocation();
                break;
            case R.id.sure_btn:
               drawLine();
                break;
        }
    }

    private void contLocation() {
//        GpsUtil.getLocation(this);
        String locat=GPSInfoProvider.getInstance(this).getLocation();
        Log.i(TAG,"返回结果为："+locat);
        if(!"".equals(locat)){
            String lots[]=locat.split("-");
            String lat=lots[0];
            String lon=lots[1];
            Log.i(TAG,"定位经度为："+lat+"\n"+"纬度为："+lon);
        }else{
            Toast.makeText(this,"获取卫星失败",Toast.LENGTH_SHORT).show();
        }


        //经纬度坐标转投影坐标
//        Point laopPoint = (Point) GeometryEngine.project(wgsPoint ,SpatialReference.create(4326),map.getSpatialReference());
    }

    /**
     * 划线
     */
    //划线
    private void drawLine() {
        points = new ArrayList<Point>();

        Point point1=new Point();
        point1.setX(1.2988176563379282E7);
        point1.setY(4904376.693592213);

        Point point2=new Point();
        point2.setX(1.2925498200185413E7);
        point2.setY(4899484.723781959);

        Point point3=new Point();
        point3.setX(1.2919077489809455E7);
        point3.setY(4852705.262471414);

        Point point4=new Point();
        point4.setX(1.2955461515273213E7);
        point4.setY(4793084.380408954);

        Point point5=new Point();
        point5.setX(1.3037860631764665E7);
        point5.setY(4842921.322850907);

        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        points.add(point5);


        if(points.size()<2){
            Toast.makeText(this,"少于2个点",Toast.LENGTH_SHORT).show();
        }else{
            if (graphicsLayer == null) {
                graphicsLayer = new GraphicsLayer();
                map.addLayer(graphicsLayer);
            }
            if (points.size() <= 1)
                return;
            Graphic graphic;
            MultiPath multipath;
            multipath = new Polyline();
            multipath.startPath(points.get(0));
            for (int i = 1; i < points.size(); i++) {
                multipath.lineTo(points.get(i));
            }
            System.out.println("aaaaaaaaaaa==>DrawPolyline: Array coutn = "+points.size());
            graphic = new Graphic(multipath, new SimpleLineSymbol(Color.RED, 4));
            graphicsLayer.addGraphic(graphic);
        }
    }

}
