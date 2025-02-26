package bobaapp.views;

import bobaapp.database.EmployeeDAO;
import bobaapp.models.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.Timestamp;

public class EmployeesPanel extends JPanel {
    private JTable employeesTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton updateButton;
    private JButton removeButton;

    public EmployeesPanel() {
        setLayout(new BorderLayout());

        // Create table model
        String[] columns = {"Employee ID", "Name", "Access Level", "Clock In", "Clock Out"};
        tableModel = new DefaultTableModel(columns, 0);
        employeesTable = new JTable(tableModel);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(employeesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons
        addButton = new JButton("Add Employee");
        updateButton = new JButton("Update Employee");
        removeButton = new JButton("Remove Employee");

        // Add buttons to panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployee();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeEmployee();
            }
        });

        refreshEmployeesTable();
    }

    private void refreshEmployeesTable() {
        tableModel.setRowCount(0);
        List<Employee> employees = EmployeeDAO.getEmployees();
        for (Employee employee : employees) {
            tableModel.addRow(new Object[]{
                employee.getEmployeeID(),
                employee.getName(),
                employee.getAccessLevel(),
                employee.getClockIn(),
                employee.getClockOut()
            });
        }
    }

    private void addEmployee() {
        // Show input dialog to get employee details
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField accessLevelField = new JTextField();
        JTextField clockInField = new JTextField();
        JTextField clockOutField = new JTextField();
        Object[] message = {
            "Employee ID:", idField,
            "Name:", nameField,
            "Access Level:", accessLevelField,
            "Clock In:", clockInField,
            "Clock Out:", clockOutField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int accessLevel = Integer.parseInt(accessLevelField.getText());
            Timestamp clockIn = Timestamp.valueOf(clockInField.getText());
            Timestamp clockOut = Timestamp.valueOf(clockOutField.getText());
            EmployeeDAO.addEmployee(new Employee(id, name, accessLevel, clockIn, clockOut));
            refreshEmployeesTable();
        }
    }

    private void updateEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            int accessLevel = (int) tableModel.getValueAt(selectedRow, 2);
            Timestamp clockIn = (Timestamp) tableModel.getValueAt(selectedRow, 3);
            Timestamp clockOut = (Timestamp) tableModel.getValueAt(selectedRow, 4);

            // Show input dialog to update employee details
            JTextField nameField = new JTextField(name);
            JTextField accessLevelField = new JTextField(String.valueOf(accessLevel));
            JTextField clockInField = new JTextField(clockIn.toString());
            JTextField clockOutField = new JTextField(clockOut.toString());
            Object[] message = {
                "Name:", nameField,
                "Access Level:", accessLevelField,
                "Clock In:", clockInField,
                "Clock Out:", clockOutField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                name = nameField.getText();
                accessLevel = Integer.parseInt(accessLevelField.getText());
                clockIn = Timestamp.valueOf(clockInField.getText());
                clockOut = Timestamp.valueOf(clockOutField.getText());
                EmployeeDAO.updateEmployee(new Employee(id, name, accessLevel, clockIn, clockOut));
                refreshEmployeesTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to update.");
        }
    }

    private void removeEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            EmployeeDAO.removeEmployee(id);
            refreshEmployeesTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to remove.");
        }
    }
}