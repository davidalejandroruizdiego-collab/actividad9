package com.diego.actividad9.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diego.actividad9.R;
import com.diego.actividad9.model.Employee;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employees;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Employee employee);
        void onDelete(Employee employee);
    }

    public EmployeeAdapter() {
        this.employees = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
        notifyDataSetChanged();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        notifyItemInserted(employees.size() - 1);
    }

    public void updateEmployee(Employee updatedEmployee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == updatedEmployee.getId()) {
                employees.set(i, updatedEmployee);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeEmployee(Employee employee) {
        int position = employees.indexOf(employee);
        if (position != -1) {
            employees.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.bind(employee);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPosition, tvSalary;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvSalary = itemView.findViewById(R.id.tvSalary);

            // Click para editar
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEdit(employees.get(position));
                }
            });

            // Long click para eliminar
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDelete(employees.get(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Employee employee) {
            tvName.setText(employee.getName());
            tvPosition.setText(employee.getPosition());

            // Formatear salario con formato de moneda
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
            tvSalary.setText(currencyFormat.format(employee.getSalary()));
        }
    }
}