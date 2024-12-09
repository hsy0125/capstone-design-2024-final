package mp.p2.homescreen;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 예시 주소
        String address = "대전 유성구 둔산로 105"; // 주소를 Geocoding API로 변환
        String apiKey = "YOUR_GOOGLE_API_KEY"; // 자신의 Google API 키

        LatLng location = getCoordinates(address, apiKey); // Geocoding API로 위도, 경도 얻기
        if (location != null) {
            mMap.addMarker(new MarkerOptions().position(location).title("병원 위치"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    // Geocoding API를 호출하여 주소를 위도/경도로 변환하는 메소드
    public LatLng getCoordinates(String address, String apiKey) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            String urlString = GEOCODING_API_URL + "?address=" + encodedAddress + "&key=" + apiKey;
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");

            if (results.length() > 0) {
                // 첫 번째 결과에서 위도, 경도 값 추출
                JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                return new LatLng(lat, lng);  // LatLng 객체 반환
            } else {
                Log.e(TAG, "No results found for address: " + address);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching coordinates", e);
        }
        return null;  // 예외 발생 시 null 반환
    }
}
