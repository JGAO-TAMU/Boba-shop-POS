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
            // Format access level for display
            String accessLevelDisplay = employee.getAccessLevel() == 0 ? "Manager (0)" : "Employee (1)";
            
            // Format clock in/out status for display
            String clockInStatus = employee.getClockIn() == null ? "Not clocked in" : employee.getClockIn().toString();
            String clockOutStatus = employee.getClockOut() == null ? "Not clocked out" : employee.getClockOut().toString();
            
            tableModel.addRow(new Object[]{
                employee.getEmployeeID(),
                employee.getName(),
                accessLevelDisplay,
                clockInStatus,
                clockOutStatus
            });
        }
    }

    private void addEmployee() {
        // Show input dialog to get employee details
        JTextField nameField = new JTextField();
        JComboBox<String> accessLevelComboBox = new JComboBox<>(new String[]{"Manager (0)", "Employee (1)"});
        accessLevelComboBox.setSelectedIndex(1); // Default to regular employee
        
        Object[] message = {
            "Name:", nameField,
            "Access Level:", accessLevelComboBox
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Employee name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Get access level from combo box selection
                int accessLevel = accessLevelComboBox.getSelectedIndex(); // 0 for Manager, 1 for Employee
                
                // Create new employee with null clock in/out times
                Employee newEmployee = new Employee(0, name, accessLevel, null, null);
                EmployeeDAO.addEmployee(newEmployee);
                refreshEmployeesTable();
                JOptionPane.showMessageDialog(this, "Employee added successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            
            // Extract access level from display string or directly from model
            String accessLevelStr = tableModel.getValueAt(selectedRow, 2).toString();
            int accessLevel = accessLevelStr.contains("Manager") ? 0 : 1;
            
            // Get clock in/out values
            Object clockInObj = tableModel.getValueAt(selectedRow, 3);
            Object clockOutObj = tableModel.getValueAt(selectedRow, 4);
            Timestamp clockIn = (clockInObj instanceof Timestamp) ? (Timestamp) clockInObj : null;
            Timestamp clockOut = (clockOutObj instanceof Timestamp) ? (Timestamp) clockOutObj : null;

            // Show input dialog to update employee details
            JTextField nameField = new JTextField(name);
            JComboBox<String> accessLevelComboBox = new JComboBox<>(new String[]{"Manager (0)", "Employee (1)"});
            accessLevelComboBox.setSelectedIndex(accessLevel); // Set to current access level
            
            Object[] message = {
                "Name:", nameField,
                "Access Level:", accessLevelComboBox,
                "Clock In/Out times can only be modified by the system"
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                name = nameField.getText();
                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Employee name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Get access level from combo box selection
                accessLevel = accessLevelComboBox.getSelectedIndex(); // 0 for Manager, 1 for Employee
                
                EmployeeDAO.updateEmployee(new Employee(id, name, accessLevel, clockIn, clockOut));
                refreshEmployeesTable();
                JOptionPane.showMessageDialog(this, "Employee updated successfully.");
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

    /**
     * Helper method to convert access level number to a descriptive string
     */
    private String getAccessLevelDescription(int accessLevel) {
        switch (accessLevel) {
            case 0: return "Manager";
            case 1: return "Employee";
            default: return "Unknown (" + accessLevel + ")";
        }
    }
}