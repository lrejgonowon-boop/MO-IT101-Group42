
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CalculateHoursWorked {

    public static class Employee {

        String fullName;       // Employee's full name
        int timeInMinutes;     // Time-in converted to total minutes
        int timeOutMinutes;    // Time-out converted to total minutes
        int breakMinutes;      // Break time in minutes (fixed at 60)
        int predictedHours;    // Manually predicted hours for verification
        int totalHoursWorked;  // Computed total hours worked

        // ComputeSemiMonthlySalary fields (used in Task 8)
        double hourlyRate;
        double riceSubsidy;
        double phoneAllowance;
        double clothingAllowance;

        // ComputeDeductions fields (used in Task 9)
        double basicSalary;

        Employee(String fullName, int timeInMinutes, int timeOutMinutes,
                int breakMinutes, int predictedHours) {

            this.fullName = fullName;
            this.timeInMinutes = timeInMinutes;
            this.timeOutMinutes = timeOutMinutes;
            this.breakMinutes = breakMinutes;
            this.predictedHours = predictedHours;

            calculateTotalHours(); // Compute hours upon creation
        }

        private void calculateTotalHours() {
            int minutesWorked = (timeOutMinutes - timeInMinutes) - breakMinutes; // Compute in minutes to perform accurate calculation for total hours worked
            totalHoursWorked = minutesWorked / 60;  // Convert minutes to whole hours
        }
    }

    public static ArrayList<Employee> readEmployees(String fileName) {
        ArrayList<Employee> employees = new ArrayList<>();

        // Track already-added employees so we only take their first row (June 3)
        Set<String> seen = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                if (firstLine) {  // Skip the header row which are only titles
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Parse each CSV column, handling quoted fields with commas

                // Build full name with space normalization
                String firstName = data[2].trim().replaceAll("\\s+", " ");
                String lastName = data[1].trim().replaceAll("\\s+", " ");
                String fullName = (firstName + " " + lastName).trim().replaceAll("\\s+", " ");

                if (seen.contains(fullName.toLowerCase())) { // Only use the first attendance row per employee (June 3 data)

                    continue;
                }
                seen.add(fullName.toLowerCase());

                String[] inParts = data[4].trim().split(":");
                int timeInMinutes = Integer.parseInt(inParts[0]) * 60 // Convert time-in (HH:MM) to total minutes

                        + Integer.parseInt(inParts[1]);

                String[] outParts = data[5].trim().split(":");
                int timeOutMinutes = Integer.parseInt(outParts[0]) * 60 + Integer.parseInt(outParts[1]); // Convert time-out (HH:MM) to total minutes

                int breakMinutes = 60; // Fixed 60-minute break for all employees

                // Predict hours for selected employees before running
                int predictedHours = 0;
                if (fullName.equalsIgnoreCase("Selena De Leon")) {
                    predictedHours = 6;
                } else if (fullName.equalsIgnoreCase("Manuel III Garcia")) {
                    predictedHours = 8;
                }

                employees.add(new Employee(fullName, timeInMinutes, timeOutMinutes, breakMinutes, predictedHours));
            }

        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        return employees;
    }

    public static void main(String[] args) {

        ArrayList<Employee> employees
                = readEmployees("MotorPH_Employee Data(Attendance Record).csv");

        for (Employee emp : employees) {

            if (emp.predictedHours > 0) { // Display employees we predicted

                // Display employee results
                System.out.println("Employee Name:      " + emp.fullName);
                System.out.println("Time In (minutes):  " + emp.timeInMinutes);
                System.out.println("Time Out (minutes): " + emp.timeOutMinutes);
                System.out.println("Break (minutes):    " + emp.breakMinutes);
                System.out.println("Predicted Hours:    " + emp.predictedHours);
                System.out.println("Total Hours Worked: " + emp.totalHoursWorked);

                if (emp.totalHoursWorked == emp.predictedHours) { // Verify prediction
                    System.out.println("Test passed: Computation is correct");
                } else {
                    System.out.println("Test failed: Expected " + emp.predictedHours + " but got " + emp.totalHoursWorked);
                }

                System.out.println("--------------------------------");
            }
        }
    }
}
