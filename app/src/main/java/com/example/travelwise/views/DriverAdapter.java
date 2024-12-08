package com.example.travelwise.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelwise.R;
import com.example.travelwise.models.Driver;

import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> {

    private List<Driver> drivers;

    public DriverAdapter(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public void updateData(List<Driver> newDrivers) {
        this.drivers = newDrivers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_driver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Driver driver = drivers.get(position);
        holder.nameTextView.setText(driver.getName());
        holder.licenseTextView.setText(driver.getLicenseNumber());
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, licenseTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_driver_name);
            licenseTextView = itemView.findViewById(R.id.text_license_number);
        }
    }
}
