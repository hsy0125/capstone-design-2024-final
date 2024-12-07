package mp.p2.homescreen;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 병원 정보를 저장하는 클래스.
 * 이름(name), 주소(address), 위도(latitude), 경도(longitude), 우편번호(postalCode), 전화번호(phone)를 포함합니다.
 */
public class Hospital {
    private String name;       // 병원 이름
    private String address;    // 병원 주소
    private String postalCode; // 병원 우편번호 추가
    private String phone;      // 병원 전화번호 추가
    private double latitude;   // 병원 위도
    private double longitude;  // 병원 경도

    /**
     * 병원 객체 생성자.
     *
     * @param name      병원 이름
     * @param address   병원 주소
     * @param postalCode 병원 우편번호 추가
     * @param phone     병원 전화번호 추가
     * @param latitude  병원 위도
     * @param longitude 병원 경도
     */
    public Hospital(String name, String address, String postalCode, String phone, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;   // 우편번호 초기화
        this.phone = phone;             // 전화번호 초기화
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 병원 이름을 반환합니다.
     *
     * @return 병원 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 병원 주소를 반환합니다.
     *
     * @return 병원 주소
     */
    public String getAddress() {
        return address;
    }

    /**
     * 병원 우편번호를 반환합니다.
     *
     * @return 병원 우편번호
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * 병원 전화번호를 반환합니다.
     *
     * @return 병원 전화번호
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 병원의 위도를 반환합니다.
     *
     * @return 병원의 위도
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 병원의 경도를 반환합니다.
     *
     * @return 병원의 경도
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 병원의 정보를 기반으로 Google Maps MarkerOptions 객체를 생성합니다.
     *
     * @return MarkerOptions 객체
     */
    public MarkerOptions toMarkerOptions() {
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude)) // 위도와 경도를 사용하여 마커 위치 지정
                .title(name)                               // 마커 제목에 병원 이름 표시
                .snippet(address);                        // 마커 설명에 병원 주소 표시
    }
}
