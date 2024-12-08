package mp.p2.homescreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    private final List<Hospital> hospitals;
    private final OnHospitalClickListener listener;

    // 클릭 리스너 인터페이스 정의
    public interface OnHospitalClickListener {
        void onHospitalClick(Hospital hospital);
    }

    // 생성자에서 리스너를 받아옴
    public HospitalAdapter(List<Hospital> hospitals, OnHospitalClickListener listener) {
        this.hospitals = hospitals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hospital_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.name.setText(hospital.getName());
        holder.address.setText(hospital.getAddress());

        // 병원 항목 클릭 시 클릭 리스너 호출
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHospitalClick(hospital);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.hospitalName);
            address = itemView.findViewById(R.id.hospitalAddress);
        }
    }
}
