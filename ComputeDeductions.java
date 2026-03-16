
import java.util.ArrayList;

public class ComputeDeductions {

    public static double computeSSS(double monthlySalary) {

        double sssMonthly;

        // SSS contribution table based on monthly salary range
        if (monthlySalary < 3250) {
            sssMonthly = 135.00;
        } else if (monthlySalary < 3750) {
            sssMonthly = 157.50;
        } else if (monthlySalary < 4250) {
            sssMonthly = 180.00;
        } else if (monthlySalary < 4750) {
            sssMonthly = 202.50;
        } else if (monthlySalary < 5250) {
            sssMonthly = 225.00;
        } else if (monthlySalary < 5750) {
            sssMonthly = 247.50;
        } else if (monthlySalary < 6250) {
            sssMonthly = 270.00;
        } else if (monthlySalary < 6750) {
            sssMonthly = 292.50;
        } else if (monthlySalary < 7250) {
            sssMonthly = 315.00;
        } else if (monthlySalary < 7750) {
            sssMonthly = 337.50;
        } else if (monthlySalary < 8250) {
            sssMonthly = 360.00;
        } else if (monthlySalary < 8750) {
            sssMonthly = 382.50;
        } else if (monthlySalary < 9250) {
            sssMonthly = 405.00;
        } else if (monthlySalary < 9750) {
            sssMonthly = 427.50;
        } else if (monthlySalary < 10250) {
            sssMonthly = 450.00;
        } else if (monthlySalary < 10750) {
            sssMonthly = 472.50;
        } else if (monthlySalary < 11250) {
            sssMonthly = 495.00;
        } else if (monthlySalary < 11750) {
            sssMonthly = 517.50;
        } else if (monthlySalary < 12250) {
            sssMonthly = 540.00;
        } else if (monthlySalary < 12750) {
            sssMonthly = 562.50;
        } else if (monthlySalary < 13250) {
            sssMonthly = 585.00;
        } else if (monthlySalary < 13750) {
            sssMonthly = 607.50;
        } else if (monthlySalary < 14250) {
            sssMonthly = 630.00;
        } else if (monthlySalary < 14750) {
            sssMonthly = 652.50;
        } else if (monthlySalary < 15250) {
            sssMonthly = 675.00;
        } else if (monthlySalary < 15750) {
            sssMonthly = 697.50;
        } else if (monthlySalary < 16250) {
            sssMonthly = 720.00;
        } else if (monthlySalary < 16750) {
            sssMonthly = 742.50;
        } else if (monthlySalary < 17250) {
            sssMonthly = 765.00;
        } else if (monthlySalary < 17750) {
            sssMonthly = 787.50;
        } else if (monthlySalary < 18250) {
            sssMonthly = 810.00;
        } else if (monthlySalary < 18750) {
            sssMonthly = 832.50;
        } else if (monthlySalary < 19250) {
            sssMonthly = 855.00;
        } else if (monthlySalary < 19750) {
            sssMonthly = 877.50;
        } else if (monthlySalary < 20250) {
            sssMonthly = 900.00;
        } else if (monthlySalary < 20750) {
            sssMonthly = 922.50;
        } else if (monthlySalary < 21250) {
            sssMonthly = 945.00;
        } else if (monthlySalary < 21750) {
            sssMonthly = 967.50;
        } else if (monthlySalary < 22250) {
            sssMonthly = 990.00;
        } else if (monthlySalary < 22750) {
            sssMonthly = 1012.50;
        } else if (monthlySalary < 23250) {
            sssMonthly = 1035.00;
        } else if (monthlySalary < 23750) {
            sssMonthly = 1057.50;
        } else if (monthlySalary < 24250) {
            sssMonthly = 1080.00;
        } else if (monthlySalary < 24750) {
            sssMonthly = 1102.50;
        } else {
            sssMonthly = 1125.00;
        }

        // Return semi-monthly SSS contribution
        return sssMonthly / 2;
    }

    public static double computePhilHealth(double monthlyBasicSalary) {

        double monthlyPhilHealth = monthlyBasicSalary * 0.03; // PhilHealth rate: 3% of monthly basic salary, split equally
        return monthlyPhilHealth / 2; // Employee pays half, deducted semi-monthly

    }

    public static double computePagIbig(double monthlySalary) {

        double monthly = monthlySalary * 0.02; // Pag-IBIG rate: 2% of monthly salary

        if (monthly > 200) {
            monthly = 200; // Maximum monthly contribution is PHP200, so semi-monthly max is PHP100
        }
        return monthly / 2; // Return semi-monthly Pag-IBIG contribution

    }

    public static double computeIncomeTax(double taxableIncome) {

        double tax;

        // BIR semi-monthly withholding tax table
        if (taxableIncome <= 10417) {
            tax = 0;
        } else if (taxableIncome <= 16667) {
            tax = (taxableIncome - 10417) * 0.20;
        } else if (taxableIncome <= 33333) {
            tax = 1250 + (taxableIncome - 16667) * 0.25;
        } else if (taxableIncome <= 83333) {
            tax = 5416.67 + (taxableIncome - 33333) * 0.30;
        } else if (taxableIncome <= 333333) {
            tax = 20416.67 + (taxableIncome - 83333) * 0.32;
        } else {
            tax = 100416.67 + (taxableIncome - 333333) * 0.35;
        }

        return tax;
    }

    public static double computeNetPay(double grossPay,
            double monthlyBasicSalary) {

        double monthlySalary = monthlyBasicSalary; // Compute monthly salary estimate for SSS and Pag-IBIG brackets

        // Compute each deduction using the methods above
        double sss = computeSSS(monthlySalary);
        double philHealth = computePhilHealth(monthlySalary);
        double pagIbig = computePagIbig(monthlySalary);

        double taxableIncome = grossPay - sss - philHealth - pagIbig; // Taxable income = gross pay minus mandatory deductions

        double incomeTax = computeIncomeTax(taxableIncome); // Compute income tax on taxable income

        double totalDeductions = sss + philHealth + pagIbig + incomeTax; // Total deductions

        return grossPay - totalDeductions; // Net pay = gross pay minus total deductions
    }

    public static void main(String[] args) {

        ArrayList<CalculateHoursWorked.Employee> employees // Load attendance data from Task 7
                = CalculateHoursWorked.readEmployees(
                        "MotorPH_Employee Data(Attendance Record).csv");

        ComputeSemiMonthlySalary.readEmployeeSalary( // Load salary data from Task 8
                "MotorPH_Employee Data(Employee Details).csv", employees);

        for (CalculateHoursWorked.Employee emp : employees) { // Process only predicted employees

            if (emp.predictedHours <= 0) {
                continue;
            }

            if (emp.totalHoursWorked <= 0 || emp.hourlyRate <= 0) { // Validate inputs
                System.out.println("Invalid data for: " + emp.fullName);
                continue;
            }

            double grossPay = (emp.totalHoursWorked * emp.hourlyRate) // Compute gross semi-monthly pay
                    + emp.riceSubsidy
                    + emp.phoneAllowance
                    + emp.clothingAllowance;

            double monthlyBasicSalary = emp.basicSalary; // Estimate monthly basic salary (basic salary from CSV)

            // Compute each deduction
            double sss = computeSSS(monthlyBasicSalary);
            double philHealth = computePhilHealth(monthlyBasicSalary);
            double pagIbig = computePagIbig(monthlyBasicSalary);

            double taxableIncome = grossPay - sss - philHealth - pagIbig; // Taxable income = gross pay minus mandatory deductions

            double incomeTax = computeIncomeTax(taxableIncome); // Compute income tax

            double totalDeductions = sss + philHealth + pagIbig + incomeTax; // Total deductions

            double netPay = computeNetPay(grossPay, monthlyBasicSalary); // Net pay

            // Display results
            System.out.println("========================================");
            System.out.println("Employee Name:       " + emp.fullName);
            System.out.println("----------------------------------------");
            System.out.println("Gross Semi-Monthly:  PHP" + String.format("%.2f", grossPay));
            System.out.println("----------------------------------------");
            System.out.println("Deductions:");
            System.out.println("  SSS:               PHP" + String.format("%.2f", sss));
            System.out.println("  PhilHealth:        PHP" + String.format("%.2f", philHealth));
            System.out.println("  Pag-IBIG:          PHP" + String.format("%.2f", pagIbig));
            System.out.println("  Income Tax:        PHP" + String.format("%.2f", incomeTax));
            System.out.println("  Total Deductions:  PHP" + String.format("%.2f", totalDeductions));
            System.out.println("----------------------------------------");
            System.out.println("Net Pay:             PHP" + String.format("%.2f", netPay));
            System.out.println("========================================");
            System.out.println();
        }
    }
}
