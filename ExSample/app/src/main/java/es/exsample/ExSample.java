
package es.exsample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExSample extends AppCompatActivity {
    private double _latitude = 0;
    private double _longitude = 0;
    private TextView _tvLatitude;
    private TextView _tvLongitude;
    final List<String> data = Arrays.asList("兼六園","21世紀美術館","近江町市場","東茶屋街",
            "武家屋敷","忍者寺","西茶屋街","金沢駅");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_start);  //リニアレイアウトのXMLファイルをアクティビティに設定

        List<String> shuffled = new ArrayList<>(data);
        Collections.shuffle(shuffled);
        final ArrayList<String> result =  new ArrayList<String>(shuffled.subList(0,5));

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_single_choice,result);
        final ListView list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        Button button = (Button)findViewById(R.id.btreset);
        _tvLatitude = findViewById(R.id.tvLatitude);
        _tvLongitude = findViewById(R.id.tvLongitude);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        GPSLocationListener locationListener = new GPSLocationListener();

        if (ActivityCompat.checkSelfPermission(ExSample.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) { ///パーミッションの設定
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(ExSample.this,permissions,1000);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {///リストビューを
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                        CharSequence msg = ((TextView) view).getText();
                        Toast.makeText(ExSample.this,String.format("選択したのは%sです",
                                msg.toString()), Toast.LENGTH_SHORT).show();
                        String str = String.valueOf(msg);
                        String uriStr = "geo:0.0?q=" +str;
                        Uri uri = Uri.parse(uriStr);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);

                    }
                }
        );

        button.setOnClickListener(
                new View.OnClickListener(){ ///更新ボタンが押された際の動作
                    @Override
                    public void onClick(View v) {
                        adapter.clear();
                        List<String> shuffled = new ArrayList<>(data);
                        Collections.shuffle(shuffled);
                        ArrayList<String> result =  new ArrayList<String>(shuffled.subList(0,5));
                        adapter.addAll(result);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void onMapSearchButtonClick(View view) { ///検索ボタンが押されたときの動作
        EditText etSearchWord = findViewById(R.id.etSearchWord);
        String searchWord = etSearchWord.getText().toString();

        try {
            searchWord = URLEncoder.encode(searchWord, "UTF-8");
            String uriStr = "geo:0.0?q=" + searchWord;
            Uri uri = Uri.parse(uriStr);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (UnsupportedEncodingException ex) {
            Log.e("ExSample", "検索キーワード変換失敗", ex);
        }
    }

    public void onMapShowCurrentButtonClick (View view) { ///現在地ボタンが押されたときの動作
        String uriStr = "geo:" + _latitude + "," + _longitude;
        Uri uri = Uri.parse(uriStr);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    private class GPSLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) { ///座標を変数に代入
            _latitude = location.getLatitude();
            _longitude = location.getLongitude();
            _tvLatitude.setText(Double.toString(_latitude));
            _tvLongitude.setText(Double.toString(_longitude));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){ ///パーミッションによって位置情報の取得の動きを決める
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            GPSLocationListener locationListener = new GPSLocationListener();

            if(ActivityCompat.checkSelfPermission(ExSample.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                return ;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,
                    0,locationListener);
        }
    }

}