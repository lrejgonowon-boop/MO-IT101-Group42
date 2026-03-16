
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// Get the Semi-Monthly Salary of Employees 
public class ComputeSemiMonthlySalary {

    public static void main(String[] args) {

        ArrayList<CalculateHoursWorked.Employee> employees = CalculateHoursWorked.readEmployees("MotorPH_Employee Data(Attendance Record).csv"); // Get computed total hours worked from Task 7

        readEmployeeSalary("MotorPH_Employee Data(Employee Details).csv", employees); // Get salary data from Employee Details excel sheet

        // Manually predicted gross pay
        double predictedGrossSelena = 4594.66;
        double predictedGrossManuel = 8785.68;

        for (CalculateHoursWorked.Employee emp : employees) { // Loop through employees and compute gross pay

            if (emp.predictedHours <= 0) {
                continue;
            }

            double hourlyRate = emp.hourlyRate;
            double riceSubsidy = emp.riceSubsidy;
            double phoneAllowance = emp.phoneAllowance;
            double clothingAllowance = emp.clothingAllowance;
            int hoursWorked = emp.totalHoursWorked;

            // Validate inputs before computing
            if (hoursWorked <= 0 || hourlyRate <= 0) {
                System.out.println("Invalid data for: " + emp.fullName);
                continue;
            }

            double grossPay = (hoursWorked * hourlyRate) + riceSubsidy + phoneAllowance + clothingAllowance; // Compute gross semi-monthly pay:

            // Display payroll summary
            System.out.println("Employee Name:        " + emp.fullName);
            System.out.println("Total Hours Worked:   " + hoursWorked);
            System.out.println("Hourly Rate:          PHP" + hourlyRate);
            System.out.println("Rice Subsidy:         PHP" + riceSubsidy);
            System.out.println("Phone Allowance:      PHP" + phoneAllowance);
            System.out.println("Clothing Allowance:   PHP" + clothingAllowance);
            System.out.println("Gross Semi-Monthly Pay: PHP" + grossPay);

            // Verify against manual prediction
            double predicted = emp.fullName.equalsIgnoreCase("Selena De Leon")
                    ? predictedGrossSelena
                    : predictedGrossManuel;

            if (grossPay == predicted) {
                System.out.println("Test passed: Computation is correct");
            } else {
                System.out.println("Test failed: Expected PHP" + predicted
                        + " but got PHP" + grossPay);
            }

            System.out.println("--------------------------------");
        }
    }

    // Reads salary details and matches each record to an employee by name
    public static void readEmployeeSalary(String fileName,
            ArrayList<CalculateHoursWorked.Employee> employees) {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                if (firstLine) { // Skip the header row
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Parse CSV row, handling quoted fields with commas

                String lastName = data[1].trim().replaceAll("\\s+", " ");
                String firstName = data[2].trim().replaceAll("\\s+", " ");
                String fullName = (firstName + " " + lastName)
                        .trim().replaceAll("\\s+", " ");
                // Parse hourly rate and remove quotes and commas from formatting
                double hourlyRate = Double.parseDouble(
                        data[18].trim().replace("\"", "").replace(",", ""));

                // Parse allowances
                double riceSubsidy = Double.parseDouble(
                        data[14].trim().replace("\"", "").replace(",", ""));

                double phoneAllowance = Double.parseDouble(
                        data[15].trim().replace("\"", "").replace(",", ""));

                double clothingAllowance = Double.parseDouble(
                        data[16].trim().replace("\"", "").replace(",", ""));

                double basicSalary = Double.parseDouble(
                        data[13].trim().replace("\"", "").replace(",", ""));

                // Match salary data to the correct employee by name
                for (CalculateHoursWorked.Employee emp : employees) {
                    if (emp.fullName.equalsIgnoreCase(fullName)) {
                        emp.hourlyRate = hourlyRate;
                        emp.riceSubsidy = riceSubsidy;
                        emp.phoneAllowance = phoneAllowance;
                        emp.clothingAllowance = clothingAllowance;
                        emp.basicSalary = basicSalary;
                        break; // Stop once matched
                    }
                }

            }

        } catch (IOException e) {
            System.out.println("Error reading salary CSV: " + e.getMessage());
        }
    }
}
