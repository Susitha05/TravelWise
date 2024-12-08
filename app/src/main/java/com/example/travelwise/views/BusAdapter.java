package com.example.travelwise.views;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelwise.R;
import com.example.travelwise.models.Bus;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private List<Bus> busList;
    private int selectedPosition = -1;

    public BusAdapter(List<Bus> busList) {
        this.busList = busList;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus, parent, false);
        return new BusViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bus bus = busList.get(position);

        holder.busLicense.setText(bus.getBusLicense());
        holder.routeFrom.setText(bus.getRouteFrom());
        holder.routeTo.setText(bus.getRouteTo());
        holder.arrivalTime.setText(bus.getArrivalTime());
        holder.departureTime.setText(bus.getDepartureTime());
        holder.itemView.setSelected(selectedPosition == position);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView busLicense, routeFrom, routeTo, arrivalTime, departureTime;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            busLicense = itemView.findViewById(R.id.bus_license_no);
            routeFrom = itemView.findViewById(R.id.route_from);
            routeTo = itemView.findViewById(R.id.route_to);
            arrivalTime = itemView.findViewById(R.id.arrival_time);
            departureTime = itemView.findViewById(R.id.departure_time);
        }
    }
}
