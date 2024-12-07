package mp.p2.homescreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HospitalActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HospitalActivity";
    private MapView mapView;
    private RecyclerView hospitalList;
    private List<Hospital> hospitals = new ArrayList<>();
    private ExecutorService executorService;  // ExecutorService 선언
    private String apiKey = "YOUR_GEOCODING_API_KEY"; // Google Geocoding API 키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        // ExecutorService 초기화
        executorService = Executors.newFixedThreadPool(3);  // 최대 3개의 스레드로 병원 좌표를 비동기적으로 처리

        // MapView 초기화
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // RecyclerView 초기화
        hospitalList = findViewById(R.id.hospitalList);
        hospitalList.setLayoutManager(new LinearLayoutManager(this));
        HospitalAdapter adapter = new HospitalAdapter(hospitals);
        hospitalList.setAdapter(adapter);

        // 병원 데이터를 로드하여 비동기적으로 좌표를 가져옴
        loadHospitalData();
    }

    private void loadHospitalData() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getResources().openRawResource(R.raw.hospitals_data)) // CSV 파일 경로
            );

            String line;
            reader.readLine(); // 헤더 라인 스킵
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // CSV 파싱
                String name = columns[0];
                String address = columns[1];
                String postalCode = columns[2]; // 우편번호
                String phone = columns[3]; // 전화번호

                // 비동기적으로 좌표를 가져오도록 ExecutorService에 작업 제출
                final String finalName = name;
                final String finalAddress = address;
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        // GeocodingService를 호출하여 주소로부터 좌표를 얻음
                        LatLng coordinates = GeocodingService.getCoordinates(finalAddress, apiKey);
                        if (coordinates != null) {
                            // UI 스레드에서 병원 데이터를 업데이트
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hospitals.add(new Hospital(finalName, finalAddress, postalCode, phone, coordinates.latitude, coordinates.longitude));
                                    hospitalList.getAdapter().notifyDataSetChanged();
                                    mapView.getMapAsync(HospitalActivity.this);  // 지도 업데이트
                                }
                            });
                        } else {
                            Log.e(TAG, "좌표를 가져오지 못함: " + finalName + ", 주소: " + finalAddress);
                        }
                    }
                });
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "병원 데이터를 로드하는 중 오류 발생", e);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 지도에 마커 추가 (여기서는 이미 hospitals 리스트가 채워졌다고 가정)
        for (Hospital hospital : hospitals) {
            googleMap.addMarker(hospital.toMarkerOptions());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ExecutorService 종료
        executorService.shutdown();
    }
}


//package mp.p2.homescreen;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//public class HospitalActivity extends AppCompatActivity implements OnMapReadyCallback {
//
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//    private static final String TAG = "HospitalActivity";
//
//    private MapView mapView;
//    private RecyclerView hospitalList;
//    private List<Hospital> hospitals = new ArrayList<>();
//    private HospitalAdapter adapter;
//    private String apiKey = "AIzaSyDoMFHfwO9UW5kP0V1hdRaC8meI2ULj0yc"; // Google Geocoding API 키
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_hospital);
//
//        // MapView 초기화
//        mapView = findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
//
//        // RecyclerView 초기화
//        hospitalList = findViewById(R.id.hospitalList);
//        hospitalList.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new HospitalAdapter(hospitals);
//        hospitalList.setAdapter(adapter);
//
//        // CSV 데이터를 로드하여 병원 정보를 추가
//        loadHospitalData();
//    }
//
//    /**
//     * CSV 파일에서 병원 데이터를 읽어와 hospitals 리스트에 추가
//     */
//    private void loadHospitalData() {
//        try {
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(getResources().openRawResource(R.raw.hospitals_data)) // CSV 파일 경로
//            );
//
//            String line;
//            reader.readLine(); // 헤더 라인 스킵
//            while ((line = reader.readLine()) != null) {
//                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // CSV 파싱
//                String name = columns[0];
//                String address = columns[1];
//                String postalCode = columns[2];
//                String phone = columns[3];
//
//                // Geocoding API를 사용하여 주소 기반으로 좌표 가져오기
//                LatLng coordinates = GeocodingService.getCoordinates(address, apiKey);
//                if (coordinates != null) {
//                    double latitude = coordinates.latitude;
//                    double longitude = coordinates.longitude;
//                    hospitals.add(new Hospital(name, address, postalCode, phone, latitude, longitude));
//                } else {
//                    Log.e(TAG, "좌표를 가져오지 못함: " + name + ", 주소: " + address);
//                }
//            }
//            reader.close();
//
//            // RecyclerView 업데이트
//            adapter.notifyDataSetChanged();
//        } catch (Exception e) {
//            Log.e(TAG, "병원 데이터를 로드하는 중 오류 발생", e);
//        }
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        googleMap.setMyLocationEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//
//        // 병원 위치를 지도에 추가
//        for (Hospital hospital : hospitals) {
//            googleMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(hospital.getLatitude(), hospital.getLongitude()))
//                    .title(hospital.getName())
//                    .snippet(hospital.getAddress()));
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mapView.getMapAsync(this);
//            } else {
//                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//}
