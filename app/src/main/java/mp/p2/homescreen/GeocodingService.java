package mp.p2.homescreen;

import com.google.android.gms.maps.model.LatLng;  // LatLng 클래스 추가 필요
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeocodingService {
    private static final String TAG = "GeocodingService";
    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    /**
     * 주소를 입력받아 위도와 경도를 반환합니다.
     *
     * @param address 검색할 주소
     * @param apiKey  구글 Geocoding API 키
     * @return 위도와 경도를 포함한 LatLng 객체 또는 null
     */
    public static LatLng getCoordinates(String address, String apiKey) {
        if (address == null || apiKey == null) {  // null 체크 추가
            Log.e(TAG, "Address or API key is null");
            return null;
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // 주소 인코딩: 공백 및 특수 문자를 안전하게 변환
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            String urlString = GEOCODING_API_URL + "?address=" + encodedAddress + "&key=" + apiKey;
            URL url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // 연결 타임아웃
            connection.setReadTimeout(5000);     // 읽기 타임아웃

            // 서버로부터 응답을 읽어오기
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray results = jsonResponse.getJSONArray("results");

                if (results.length() > 0) {
                    // 첫 번째 결과를 가져와서 위치 데이터를 얻음
                    JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");

                    return new LatLng(lat, lng);  // LatLng 객체 반환
                } else {
                    Log.e(TAG, "No results found for address: " + address);  // 결과가 없는 경우 로그 출력
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error fetching coordinates", e);  // 에러 발생 시 로그 출력
        } finally {
            // 자원 해제는 try-with-resources 구문으로 자동 처리
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;  // 예외 발생 시 null 반환
    }
}
