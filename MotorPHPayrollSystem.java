
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class MotorPHPayrollSystem {

    // work starts at 8:00 AM and ends at 5:00 PM
    static final LocalTime WORK_START = LocalTime.of(8, 0);
    static final LocalTime WORK_END = LocalTime.of(17, 0);
    static final double BREAK_HOURS = 1.0; // always subtract 1 hour for lunch break

    // these arrays will hold the employee info from the CSV file
    static int[] empNumbers;
    static String[] empLastNames;
    static String[] empFirstNames;
    static String[] empBirthdays;
    static double[] empBasicSalary;
    static double[] empRiceSubsidy;
    static double[] empPhoneAllowance;
    static double[] empClothingAllowance;
    static double[] empHourlyRate;
    static int empCount = 0; // this counts how many employees we found

    // these arrays will hold the attendance records from the CSV file
    static int[] attEmpNumbers;
    static LocalDate[] attDates;
    static LocalTime[] attTimeIn;
    static LocalTime[] attTimeOut;
    static int attCount = 0; // this counts how many attendance rows we found

    // this is the main method, it runs first when we start the program
    public static void main(String[] args) {

    Scanner input = new Scanner(System.in);

    System.out.println("=================================");
    System.out.println("     MOTORPH PAYROLL SYSTEM");
    System.out.println("=================================");

    System.out.print("Username: ");
    String username = input.nextLine();

    System.out.print("Password: ");
    String password = input.nextLine();

    if (!username.equals("payroll_staff") || !password.equals("12345")) {
        System.out.println("\nIncorrect username and/or password.");
        return;
    }

    System.out.println("\nLogin successful!\n");

    readEmployeeDetails("MotorPH_Employee Data(Employee Details).csv");
    readAttendanceRecords("MotorPH_Employee Data(Attendance Record).csv");


System.out.println("=========== MAIN MENU ===========");
System.out.println("1. Process Payroll");
System.out.println("2. Exit Program");

System.out.print("Enter choice: ");
int choice = input.nextInt();

if (choice == 1) {

    // We'll add the Process Payroll menu in Step 3.
    System.out.println("\n=========== PROCESS PAYROLL ===========");
System.out.println("1. One Employee");
System.out.println("2. All Employees");
System.out.println("3. Exit Program");

System.out.print("Enter choice: ");
int payrollChoice = input.nextInt();

if (payrollChoice == 1) {

    System.out.print("\nEnter Employee Number: ");
int employeeNumber = input.nextInt();

printFullPayroll(employeeNumber);

} else if (payrollChoice == 2) {
printFullPayroll(0);
    

} else if (payrollChoice == 3) {

    System.out.println("\nProgram terminated.");

} else {

    System.out.println("\nInvalid choice.");

}

} else if (choice == 2) {

    System.out.println("\nProgram terminated.");

} else {

    System.out.println("\nInvalid choice.");
}
    }
    // this method opens the employee details CSV and saves the data into our arrays
    static void readEmployeeDetails(String fileName) {

        // we read the file once just to count how many employees there are
        // we need to know this so we can make the arrays the right size
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip the first row because it's just the column headers
                }
                if (!line.trim().isEmpty()) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading employee details: " + e.getMessage());
            return;
        }

        // now we create the arrays using the count we got above
        empNumbers = new int[count];
        empLastNames = new String[count];
        empFirstNames = new String[count];
        empBirthdays = new String[count];
        empBasicSalary = new double[count];
        empRiceSubsidy = new double[count];
        empPhoneAllowance = new double[count];
        empClothingAllowance = new double[count];
        empHourlyRate = new double[count];

        // now we read the file again and actually save the data into the arrays
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            int idx = 0;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                // we split the line into separate values using commas
                String[] data = splitCSVLine(line);

                // we save each value into the correct array slot
                empNumbers[idx] = Integer.parseInt(data[0].trim());
                empLastNames[idx] = data[1].trim().replaceAll("\\s+", " ");
                empFirstNames[idx] = data[2].trim().replaceAll("\\s+", " ");
                empBirthdays[idx] = data[3].trim();

                // used parseFormattedDouble because some values have commas inside them
                empBasicSalary[idx] = parseFormattedDouble(data[13]);
                empRiceSubsidy[idx] = parseFormattedDouble(data[14]);
                empPhoneAllowance[idx] = parseFormattedDouble(data[15]);
                empClothingAllowance[idx] = parseFormattedDouble(data[16]);
                empHourlyRate[idx] = parseFormattedDouble(data[18]);

                idx++;
            }
            empCount = idx; // save how many employees we read

        } catch (IOException e) {
            System.out.println("Error reading employee details: " + e.getMessage());
        }
    }

    // this method opens the attendance CSV and saves the records into our arrays
    // we only save records from June to December 2024 because that's what we need
    static void readAttendanceRecords(String fileName) {

        // first we count how many rows are from June to December 2024
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip the header row
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = splitCSVLine(line);
                LocalDate date = LocalDate.parse(data[3].trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                // only count this row if it's in 2024 and the month is June (6) or later
                if (date.getYear() == 2024 && date.getMonthValue() >= 6) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error counting attendance records: " + e.getMessage());
            return;
        }

        // create the arrays using the count we got
        attEmpNumbers = new int[count];
        attDates = new LocalDate[count];
        attTimeIn = new LocalTime[count];
        attTimeOut = new LocalTime[count];

        // read the file again and save the attendance data into the arrays
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            int idx = 0;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip the header row
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = splitCSVLine(line);

                LocalDate date = LocalDate.parse(data[3].trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));

                // skip rows that are not from June to December 2024
                if (date.getYear() != 2024 || date.getMonthValue() < 6) {
                    continue;
                }

                // save the attendance data
                attEmpNumbers[idx] = Integer.parseInt(data[0].trim());
                attDates[idx] = date;

                // parse the time using H:mm format because some times are like 8:05 (single digit hour)
                attTimeIn[idx] = LocalTime.parse(data[4].trim(), DateTimeFormatter.ofPattern("H:mm"));
                attTimeOut[idx] = LocalTime.parse(data[5].trim(), DateTimeFormatter.ofPattern("H:mm"));

                idx++;
            }
            attCount = idx; // save the final count

        } catch (IOException e) {
            System.out.println("Error reading attendance records: " + e.getMessage());
        }
    }

    // this method computes how many hours one employee worked on a single day
    static double computeHoursWorked(LocalTime logIn, LocalTime logOut) {

        // if login is at or before 8:05, treat as 8:00 (grace period)
        // otherwise use actual login time
        LocalTime effectiveIn = logIn;
        if (!logIn.isAfter(LocalTime.of(8, 5))) {
            effectiveIn = WORK_START;
        }

        // cap logout to 17:00 if later
        LocalTime effectiveOut = logOut.isAfter(WORK_END) ? WORK_END : logOut;

        // if effectiveOut is before or equal to effectiveIn, return 0
        if (!effectiveOut.isAfter(effectiveIn)) {
            return 0;
        }

        // compute duration in minutes then convert to hours minus lunch break
        long minutesWorked = java.time.Duration.between(effectiveIn, effectiveOut).toMinutes();
        double hoursWorked = (minutesWorked / 60.0) - BREAK_HOURS;

        return hoursWorked < 0 ? 0 : hoursWorked;
    }

    // this method adds up all the hours worked by one employee within a date range
    static double getTotalHoursForPeriod(int empNumber, LocalDate startDate, LocalDate endDate) {
        double totalHours = 0;
        for (int i = 0; i < attCount; i++) {
            if (attEmpNumbers[i] == empNumber
                    && !attDates[i].isBefore(startDate)
                    && !attDates[i].isAfter(endDate)) {
                totalHours += computeHoursWorked(attTimeIn[i], attTimeOut[i]);
            }
        }
        return totalHours;
    }

    // this method looks up the SSS contribution based on the employee's monthly basic salary
    static double computeSSS(double monthlySalary) {
        double sssMonthly;

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
            sssMonthly = 1125.00; // maximum SSS contribution
        }

        return sssMonthly;
    }

    // this method computes the PhilHealth deduction for the employee
    static double computePhilHealth(double monthlyBasicSalary) {
        double totalPremium;

        if (monthlyBasicSalary <= 10000) {
            totalPremium = 300.00; // minimum premium is 300
        } else if (monthlyBasicSalary < 60000) {
            totalPremium = monthlyBasicSalary * 0.03; // 3% of the basic salary
        } else {
            totalPremium = 1800.00; // maximum premium is 1,800
        }

        // divide by 2 because the employee only shoulders half of the total premium
        return totalPremium / 2;
    }

    // this method computes the Pag-IBIG contribution for the employee
    static double computePagIbig(double monthlyBasicSalary) {
        double contribution = monthlyBasicSalary * 0.02;

        if (contribution > 100) {
            contribution = 100;
        }

        return contribution;
    }

    // this method computes the withholding tax using the BIR monthly tax table
    static double computeWithholdingTax(double monthlyTaxableIncome) {
        if (monthlyTaxableIncome <= 20832) {
            return 0;
        } else if (monthlyTaxableIncome < 33333) {
            return (monthlyTaxableIncome - 20833) * 0.20;
        } else if (monthlyTaxableIncome < 66667) {
            return 2500 + (monthlyTaxableIncome - 33333) * 0.25;
        } else if (monthlyTaxableIncome < 166667) {
            return 10833 + (monthlyTaxableIncome - 66667) * 0.30;
        } else if (monthlyTaxableIncome < 666667) {
            return 40833.33 + (monthlyTaxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (monthlyTaxableIncome - 666667) * 0.35;
        }
    }

    // this method prints the full payroll report for all employees from June to December
    static void printFullPayroll(int employeeNumber) {

        boolean employeeExists = false;

        String[] monthNames = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

        int[] months = {6, 7, 8, 9, 10, 11, 12};

        System.out.println("=======================================================================");
        System.out.println("                  MOTORPH PAYROLL SYSTEM - JUNE TO DECEMBER 2024");
        System.out.println("=======================================================================");

        // loop through each employee one by one
        for (int e = 0; e < empCount; e++) {
if (employeeNumber != 0 && empNumbers[e] != employeeNumber) {
    continue;
}
employeeExists = true;
            String fullName = empFirstNames[e] + " " + empLastNames[e];

            // print the employee's basic info at the top
            System.out.println("\n-----------------------------------------------------------------------");
            System.out.printf("  EMPLOYEE #%d: %s%n", empNumbers[e], fullName);
            System.out.printf("  Birthday: %s | Basic Salary: PHP %s | Hourly Rate: PHP %s%n",
                    empBirthdays[e], empBasicSalary[e], empHourlyRate[e]);
            System.out.println("-----------------------------------------------------------------------");

            // print all the attendance records for this employee
            System.out.println("  ATTENDANCE RECORDS:");
            System.out.printf("  %-12s %-10s %-10s%n", "Date", "Log In", "Log Out");
            System.out.println("  " + "-".repeat(34));
            for (int a = 0; a < attCount; a++) {
                // we check if this attendance row belongs to the current employee
                if (attEmpNumbers[a] == empNumbers[e]) {
                    System.out.printf("  %-12s %-10s %-10s%n",
                            attDates[a].format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                            attTimeIn[a].format(DateTimeFormatter.ofPattern("H:mm")),
                            attTimeOut[a].format(DateTimeFormatter.ofPattern("H:mm")));
                }
            }
            System.out.println("  " + "-".repeat(34));
            System.out.println();

            
            //System.out.printf("  %-16s %-8s %-15s %-20s %-15s %-15s %-15s %-15s %-20s%n",
                    
            //System.out.println("  " + "-".repeat(140));

            // now loop through each month from June to December
            for (int month : months) {

                int year = 2024;

                // 1st cutoff is days 1 to 15
                LocalDate cut1Start = LocalDate.of(year, month, 1);
                LocalDate cut1End = LocalDate.of(year, month, 15);

                // 2nd cutoff is days 16 to the last day of the month
                LocalDate cut2Start = LocalDate.of(year, month, 16);
                LocalDate cut2End = LocalDate.of(year, month,
                        LocalDate.of(year, month, 1).lengthOfMonth());

                // get the total hours worked for each cutoff
                double hours1 = getTotalHoursForPeriod(empNumbers[e], cut1Start, cut1End);
                double hours2 = getTotalHoursForPeriod(empNumbers[e], cut2Start, cut2End);

                // gross pay = hours worked times hourly rate, plus half of the monthly allowances
                // split the allowances in half because they are given across both cutoffs
                double allowancesHalf = (empRiceSubsidy[e] + empPhoneAllowance[e] + empClothingAllowance[e]) / 2;
                double workPay1 = hours1 * empHourlyRate[e];
                double workPay2 = hours2 * empHourlyRate[e];
//Assignment Requirement: Do not add allowances
                double grossPay1 = workPay1;
                double grossPay2 = workPay2;

                // add both cutoffs together to get the total gross for the whole month
                double monthlyGross = grossPay1 + grossPay2;

                // compute all three mandatory government deductions using the basic salary
                double sssMonthly = computeSSS(empBasicSalary[e]);
                double philHealthEmployee = computePhilHealth(empBasicSalary[e]);
                double pagIbigMonthly = computePagIbig(empBasicSalary[e]);

                // add up SSS + PhilHealth + Pag-IBIG to get the total mandatory deductions
                double mandatoryDeductions = sssMonthly + philHealthEmployee + pagIbigMonthly;

                // taxable income is the total work pay minus the mandatory deductions
                // allowances are non-taxable so they are excluded
                double monthlyTaxableIncome = (workPay1 + workPay2) - mandatoryDeductions;
                double withholdingTax = computeWithholdingTax(monthlyTaxableIncome);

                double totalDeductions2nd = mandatoryDeductions + withholdingTax;

                double netPay1 = grossPay1; // no deductions yet for the 1st cutoff
                double netPay2 = grossPay2 - totalDeductions2nd; // all deductions come out here

                // create the label for this month like "June 2024"
                String monthLabel = monthNames[month] + " " + year;

                // print the 1st cutoff row
                System.out.println();

System.out.println("Cutoff Date: " + monthNames[month] + " 1 to 15");
System.out.printf("Total Hours Worked: %.2f%n", hours1);
System.out.printf("Gross Salary: PHP %.2f%n", grossPay1);
System.out.printf("Net Salary: PHP %.2f%n", netPay1);

System.out.println();

int lastDay = LocalDate.of(year, month, 1).lengthOfMonth();

System.out.println("Cutoff Date: " + monthNames[month] + " 16 to " + lastDay);
System.out.printf("Total Hours Worked: %.2f%n", hours2);
System.out.printf("Gross Salary: PHP %.2f%n", grossPay2);

System.out.printf("SSS: PHP %.2f%n", sssMonthly);
System.out.printf("PhilHealth: PHP %.2f%n", philHealthEmployee);
System.out.printf("Pag-IBIG: PHP %.2f%n", pagIbigMonthly);
System.out.printf("Tax: PHP %.2f%n", withholdingTax);
System.out.printf("Total Deductions: PHP %.2f%n", totalDeductions2nd);
System.out.printf("Net Salary: PHP %.2f%n", netPay2);

System.out.println("---------------------------------------------------");
                System.out.println("  " + "-".repeat(140));
            }
        }

        System.out.println("\n=======================================================================");
        System.out.println("                          END OF PAYROLL REPORT");
        System.out.println("=======================================================================");
        if (employeeNumber != 0 && !employeeExists) {
    System.out.println("\nEmployee number does not exist.");
}
    }

    // this method handles splitting a CSV line into individual values
    static String[] splitCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder(); // reset for the next value
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }

    // this method converts a string like "90,000" into an actual number like 90000.0
    static double parseFormattedDouble(String raw) {
        String clean = raw.trim().replace("\"", "").replace(",", ""); // remove any quotes and commas
        if (clean.isEmpty()) {
            return 0; // if the value is missing or blank just return 0
        }
        return Double.parseDouble(clean);
    }
}
