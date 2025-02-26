package bobaapp.models;

import java.sql.Timestamp;

public class Employee {
    private int employeeID;
    private String name;
    private int accessLevel;
    private Timestamp clockIn;
    private Timestamp clockOut;

    public Employee(int employeeID, String name, int accessLevel, Timestamp clockIn, Timestamp clockOut) {
        this.employeeID = employeeID;
        this.name = name;
        this.accessLevel = accessLevel;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }

    public int getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public int getAccessLevel() { return accessLevel; }
    public Timestamp getClockIn() { return clockIn; }
    public Timestamp getClockOut() { return clockOut; }
}