package mp.p2.homescreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            // HospitalActivity로 이동
            Intent intent = new Intent(MainActivity.this, HospitalActivity.class);
            startActivity(intent);
        });

        callvanButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "콜밴 클릭됨", Toast.LENGTH_SHORT).show()
        );

        // 장애인 택시 버튼 클릭 시 CertificateActivity로 이동
        disabledTaxiButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "장애인 택시 클릭됨", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CertificateActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "뒤로가기 클릭됨", Toast.LENGTH_SHORT).show()
        );

        homeButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "홈 클릭됨", Toast.LENGTH_SHORT).show()
        );

        cancelButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "전체 취소 클릭됨", Toast.LENGTH_SHORT).show()
        );
    }
}
