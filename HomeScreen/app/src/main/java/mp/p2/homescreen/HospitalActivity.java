package mp.p2.homescreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HospitalActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HospitalActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private RecyclerView hospitalList;
    private List<Hospital> hospitals = new ArrayList<>();
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // MapView 초기화
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // RecyclerView 초기화
        hospitalList = findViewById(R.id.hospitalList);
        hospitalList.setLayoutManager(new LinearLayoutManager(this));
        HospitalAdapter adapter = new HospitalAdapter(hospitals, new HospitalAdapter.OnHospitalClickListener() {
            @Override
            public void onHospitalClick(Hospital hospital) {
                // 병원 클릭 시 해당 병원 위치로 카메라 이동
                LatLng hospitalLocation = new LatLng(hospital.getLatitude(), hospital.getLongitude());
                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15)); // 줌 레벨 15
                }
            }
        });
        hospitalList.setAdapter(adapter);

        // 병원 데이터를 로드하여 위경도를 사용
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
                String latitudeStr = columns[1]; // 위도
                String longitudeStr = columns[2]; // 경도
                String postalCode = columns[3]; // 우편번호
                String phone = columns[4]; // 전화번호

                // 위경도 파싱
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);

                // 병원 데이터를 리스트에 추가
                hospitals.add(new Hospital(name, latitude, longitude, postalCode, phone));
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "병원 데이터를 로드하는 중 오류 발생", e);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한을 요청하고, 현재 위치를 가져옴
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // 위치 서비스 활성화
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // FusedLocationProviderClient를 사용하여 현재 위치 가져오기
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15)); // 줌 레벨 15
                        } else {
                            // 사용자의 위치를 가져오지 못한 경우 기본 위치 설정 (예: 대전 유성구청)
                            LatLng defaultLocation = new LatLng(36.3504, 127.3656); // 대전 유성구청
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15)); // 줌 레벨 15
                        }
                    }
                });

        // 지도에 병원 위치 마커 추가
        for (Hospital hospital : hospitals) {
            LatLng hospitalLocation = new LatLng(hospital.getLatitude(), hospital.getLongitude());
            googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                    .position(hospitalLocation)
                    .title(hospital.getName())
                    .snippet(hospital.getPhone()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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
