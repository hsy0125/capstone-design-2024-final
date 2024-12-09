package mp.p2.homescreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private MapView mapView;
    private GoogleMap googleMap; // GoogleMap 객체를 전역 변수로 선언
    private List<Hospital> hospitals = new ArrayList<>(); // 병원 리스트 저장

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MapView 초기화
        mapView = findViewById(R.id.mapView);

        // mapView가 null이 아닌지 확인
        if (mapView == null) {
            Log.e(TAG, "MapView가 초기화되지 않았습니다.");
        } else {
            mapView.onCreate(savedInstanceState);  // mapView onCreate 호출
            mapView.getMapAsync(this);  // OnMapReadyCallback을 통해 GoogleMap 객체를 비동기적으로 초기화
        }

        // 버튼 연결
        Button kioskButton = findViewById(R.id.kioskButton);
        Button callvanButton = findViewById(R.id.callvanButton);
        Button disabledTaxiButton = findViewById(R.id.disabledTaxiButton);
        Button backButton = findViewById(R.id.backButton);
        Button homeButton = findViewById(R.id.homeButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        // 버튼 클릭 리스너 설정
        kioskButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "어디로든 키오스크 확인 클릭됨", Toast.LENGTH_SHORT).show();
        });

        callvanButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "콜밴 클릭됨", Toast.LENGTH_SHORT).show();
        });

        disabledTaxiButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "장애인 택시 클릭됨", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "뒤로가기 클릭됨", Toast.LENGTH_SHORT).show();
        });

        homeButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "홈 클릭됨", Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "전체 취소 클릭됨", Toast.LENGTH_SHORT).show();
        });

        // CSV 파일에서 병원 데이터를 로드
        loadHospitalData();
    }

    // 병원 데이터 로드 (CSV 파일에서)
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

                // Geocoding API를 통해 주소에서 좌표를 가져옵니다.
                GeocodingService.getCoordinates(address, new GeocodingService.OnCoordinatesFetchedListener() {
                    @Override
                    public void onCoordinatesFetched(LatLng coordinates) {
                        if (coordinates != null) {
                            //  좌표를 얻었으면 병원 객체에 추가
                            hospitals.add(new Hospital(name, address, coordinates.latitude, coordinates.longitude));
                            // 지도에 마커 추가
                            googleMap.addMarker(new MarkerOptions()
                                    .position(coordinates)
                                    .title(name));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                        } else {
                            Log.e(TAG, "좌표를 가져오지 못한 병원: " + name);
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
        this.googleMap = googleMap; // GoogleMap 객체 초기화

        // 초기화된 GoogleMap 객체에 병원 마커 추가
        for (Hospital hospital : hospitals) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(hospital.getLatitude(), hospital.getLongitude()))
                    .title(hospital.getName()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}



//package mp.p2.homescreen;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // 버튼 연결
//        Button kioskButton = findViewById(R.id.kioskButton);
//        Button callvanButton = findViewById(R.id.callvanButton);
//        Button disabledTaxiButton = findViewById(R.id.disabledTaxiButton);
//        Button backButton = findViewById(R.id.backButton);
//        Button homeButton = findViewById(R.id.homeButton);
//        Button cancelButton = findViewById(R.id.cancelButton);
//
//        // 버튼 클릭 리스너 설정
//        kioskButton.setOnClickListener(v -> {
//            Toast.makeText(MainActivity.this, "어디로든 키오스크 확인 클릭됨", Toast.LENGTH_SHORT).show();
//            // HospitalActivity로 이동
//            Intent intent = new Intent(MainActivity.this, HospitalActivity.class);
//            startActivity(intent);
//        });
//
//        callvanButton.setOnClickListener(v ->
//                Toast.makeText(MainActivity.this, "콜밴 클릭됨", Toast.LENGTH_SHORT).show()
//        );
//
//        // 장애인 택시 버튼 클릭 시 CertificateActivity로 이동
//        disabledTaxiButton.setOnClickListener(v -> {
//            Toast.makeText(MainActivity.this, "장애인 택시 클릭됨", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, CertificateActivity.class);
//            startActivity(intent);
//        });
//
//        backButton.setOnClickListener(v ->
//                Toast.makeText(MainActivity.this, "뒤로가기 클릭됨", Toast.LENGTH_SHORT).show()
//        );
//
//        homeButton.setOnClickListener(v ->
//                Toast.makeText(MainActivity.this, "홈 클릭됨", Toast.LENGTH_SHORT).show()
//        );
//
//        cancelButton.setOnClickListener(v ->
//                Toast.makeText(MainActivity.this, "전체 취소 클릭됨", Toast.LENGTH_SHORT).show()
//        );
//    }
//}
