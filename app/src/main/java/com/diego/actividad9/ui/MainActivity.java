package com.diego.actividad9.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diego.actividad9.R;
import com.diego.actividad9.data.EmployeeDao;
import com.diego.actividad9.model.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements EmployeeAdapter.OnItemClickListener {

    private EmployeeDao dao;
    private EmployeeAdapter adapter;
    private RecyclerView rvEmployees;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes
        initComponents();
        setupRecyclerView();
        loadEmployees();
    }

    private void initComponents() {
        dao = new EmployeeDao(this);
        rvEmployees = findViewById(R.id.rvEmployees);
        fabAdd = findViewById(R.id.fabAdd);

        // Configurar click del FAB
        fabAdd.setOnClickListener(v -> showEmployeeDialog(null));
    }

    private void setupRecyclerView() {
        adapter = new EmployeeAdapter();
        adapter.setOnItemClickListener(this);
        rvEmployees.setLayoutManager(new LinearLayoutManager(this));
        rvEmployees.setAdapter(adapter);
    }

    private void loadEmployees() {
        List<Employee> employees = dao.getAll();
        adapter.setEmployees(employees);
    }

    private void showEmployeeDialog(Employee employee) {
        // Inflar el layout del diálogo
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_employee, null);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etPosition = dialogView.findViewById(R.id.etPosition);
        EditText etSalary = dialogView.findViewById(R.id.etSalary);

        // Si es edición, llenar los campos
        if (employee != null) {
            etName.setText(employee.getName());
            etPosition.setText(employee.getPosition());
            etSalary.setText(String.valueOf(employee.getSalary()));
        }

        String title = employee == null ? "Agregar Empleado" : "Editar Empleado";
        String positiveButton = employee == null ? "Agregar" : "Actualizar";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String position = etPosition.getText().toString().trim();
                    String salaryStr = etSalary.getText().toString().trim();

                    if (validateInput(name, position, salaryStr)) {
                        double salary = Double.parseDouble(salaryStr);

                        if (employee == null) {
                            // Crear nuevo empleado
                            Employee newEmployee = new Employee(name, position, salary);
                            long id = dao.insert(newEmployee);
                            if (id > 0) {
                                newEmployee.setId(id);
                                adapter.addEmployee(newEmployee);
                                Toast.makeText(this, "Empleado agregado exitosamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error al agregar empleado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Actualizar empleado existente
                            employee.setName(name);
                            employee.setPosition(position);
                            employee.setSalary(salary);

                            int rowsAffected = dao.update(employee);
                            if (rowsAffected > 0) {
                                adapter.updateEmployee(employee);
                                Toast.makeText(this, "Empleado actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error al actualizar empleado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validateInput(String name, String position, String salaryStr) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(position)) {
            Toast.makeText(this, "El puesto es requerido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(salaryStr)) {
            Toast.makeText(this, "El salario es requerido", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                Toast.makeText(this, "El salario debe ser positivo", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Formato de salario inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onEdit(Employee employee) {
        showEmployeeDialog(employee);
    }

    @Override
    public void onDelete(Employee employee) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar a " + employee.getName() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    int rowsDeleted = dao.delete(employee.getId());
                    if (rowsDeleted > 0) {
                        adapter.removeEmployee(employee);
                        Toast.makeText(this, "Empleado eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al eliminar empleado", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}