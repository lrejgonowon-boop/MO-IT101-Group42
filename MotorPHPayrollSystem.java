
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MotorPHPayrollSystem {

    // these are the start and end times for work - 8:00 AM to 5:00 PM only
    static final LocalTime WORK_START = LocalTime.of(8, 0);
    static final LocalTime WORK_END = LocalTime.of(17, 0);
    static final double BREAK_HOURS = 1.0; // we always subtract 1 hour for lunch break

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

        // first we read the employee details from the CSV
        readEmployeeDetails("MotorPH_Employee Data(Employee Details).csv");

        // then we read the attendance records from the CSV
        readAttendanceRecords("MotorPH_Employee Data(Attendance Record).csv");

        // finally we compute and print the payroll for June to December
        printFullPayroll();
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
                    continue;
                } // skip the first row because it's just the column headers
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
                } // skip the header row again
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

                // columns 13 to 16 are the salary and allowances, column 18 is the hourly rate
                // some values have commas like "90,000" so we use parseFormattedDouble to handle that
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
                    continue;
                } // skip the header row
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = splitCSVLine(line);
                LocalDate date = LocalDate.parse(data[3].trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                // we only want rows from June (month 6) up to December (month 12) of 2024
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
                    continue;
                } // skip the header row
                if (line.trim().isEmpty()) {
                    continue;
                }

                // split the line into separate values
                String[] data = splitCSVLine(line);

                LocalDate date = LocalDate.parse(data[3].trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));

                // if the row is not from June to December 2024, we skip it
                if (date.getYear() != 2024 || date.getMonthValue() < 6) {
                    continue;
                }

                // save the attendance data
                attEmpNumbers[idx] = Integer.parseInt(data[0].trim());
                attDates[idx] = date;

                // save the log in and log out times
                attTimeIn[idx] = LocalTime.parse(data[4].trim(), DateTimeFormatter.ofPattern("H:mm"));
                attTimeOut[idx] = LocalTime.parse(data[5].trim(), DateTimeFormatter.ofPattern("H:mm"));

                idx++;
            }
            attCount = idx; // save how many attendance rows we read

        } catch (IOException e) {
            System.out.println("Error reading attendance records: " + e.getMessage());
        }
    }

    // this method figures out how many hours an employee worked in one day
    // we only count time between 8:00 AM and 5:00 PM, then we subtract 1 hour for lunch
    // example: log in 8:30, log out 5:30 = 8.5 hours - 1 break = 7.5 hours
    // example: log in 8:05, log out 5:00 = we treat 8:05 as 8:00 (grace period) = 9 - 1 = 8 hours
    static double computeHoursWorked(LocalTime logIn, LocalTime logOut) {

        // if the employee logged in between 8:00 and 8:10, we treat it as exactly 8:00
        // this is the grace period so they don't get penalized for being a few minutes late
        LocalTime effectiveIn = logIn;
        if (!logIn.isBefore(WORK_START) && !logIn.isAfter(LocalTime.of(8, 10))) {
            effectiveIn = WORK_START; // count from 8:00 AM instead
        } else if (logIn.isBefore(WORK_START)) {
            effectiveIn = WORK_START; // if they came early we still start counting from 8:00 AM
        }
        // if the log in time is after 8:10, we use their actual log in time

        // if the employee logged out after 5:00 PM we only count up to 5:00 PM (no overtime)
        LocalTime effectiveOut = logOut.isAfter(WORK_END) ? WORK_END : logOut;

        // if the log out is before the log in for some reason, return 0
        if (!effectiveOut.isAfter(effectiveIn)) {
            return 0;
        }

        // calculate how many minutes they worked
        long minutesWorked = java.time.Duration.between(effectiveIn, effectiveOut).toMinutes();

        // convert minutes to hours and subtract the 1 hour lunch break
        double hoursWorked = (minutesWorked / 60.0) - BREAK_HOURS;

        // if the result is somehow negative, just return 0
        return hoursWorked < 0 ? 0 : hoursWorked;
    }

    // this method adds up all the hours an employee worked between two dates
    // we use this to get the total hours for the 1st cutoff (days 1-15) and 2nd cutoff (days 16-end)
    static double getTotalHoursForPeriod(int empNumber, LocalDate startDate, LocalDate endDate) {
        double totalHours = 0;
        for (int i = 0; i < attCount; i++) {
            // check if this row belongs to the employee we are looking for
            // and if the date falls between the start and end dates
            if (attEmpNumbers[i] == empNumber
                    && !attDates[i].isBefore(startDate)
                    && !attDates[i].isAfter(endDate)) {
                totalHours += computeHoursWorked(attTimeIn[i], attTimeOut[i]);
            }
        }
        return totalHours;
    }

    // this method computes how much SSS the employee needs to pay for the month
    // SSS uses a bracket system - the higher the salary, the higher the contribution
    static double computeSSS(double monthlySalary) {
        double sssMonthly;

        // we check which bracket the salary falls into and assign the right contribution amount
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

        return sssMonthly;
    }

    // this method computes the PhilHealth deduction
    // it is just 3% of the monthly gross salary
    static double computePhilHealth(double monthlyGrossSalary) {
        return monthlyGrossSalary * 0.03;
    }

    // this method computes the Pag-IBIG deduction
    // it is 2% of the monthly gross salary but the maximum amount is only PHP 200
    static double computePagIbig(double monthlyGrossSalary) {
        double monthly = monthlyGrossSalary * 0.02;
        if (monthly > 200) {
            monthly = 200; // if it goes over 200, we cap it at 200

        }
        return monthly;
    }

    // this method computes the income tax using the BIR semi-monthly tax table
    // the higher the taxable income, the higher the tax rate
    static double computeIncomeTax(double semiMonthlyTaxableIncome) {
        double tax;

        // we check which tax bracket the income falls into
        if (semiMonthlyTaxableIncome <= 10417) {
            tax = 0; // no tax if income is low enough
        } else if (semiMonthlyTaxableIncome <= 16667) {
            tax = (semiMonthlyTaxableIncome - 10417) * 0.20;
        } else if (semiMonthlyTaxableIncome <= 33333) {
            tax = 1250 + (semiMonthlyTaxableIncome - 16667) * 0.25;
        } else if (semiMonthlyTaxableIncome <= 83333) {
            tax = 5416.67 + (semiMonthlyTaxableIncome - 33333) * 0.30;
        } else if (semiMonthlyTaxableIncome <= 333333) {
            tax = 20416.67 + (semiMonthlyTaxableIncome - 83333) * 0.32;
        } else {
            tax = 100416.67 + (semiMonthlyTaxableIncome - 333333) * 0.35;
        }

        return tax;
    }

    // this method prints the full payroll report for all employees from June to December
    static void printFullPayroll() {

        // we use this array so we can convert the month number to a name like "June"
        String[] monthNames = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

        // these are the months we need to show (6 = June, 12 = December)
        int[] months = {6, 7, 8, 9, 10, 11, 12};

        System.out.println("=======================================================================");
        System.out.println("                  MOTORPH PAYROLL SYSTEM - JUNE TO DECEMBER 2024");
        System.out.println("=======================================================================");

        // loop through each employee one by one
        for (int e = 0; e < empCount; e++) {

            // put the first name and last name together
            String fullName = empFirstNames[e] + " " + empLastNames[e];

            // print the employee's basic info at the top
            System.out.println("\n-----------------------------------------------------------------------");
            System.out.printf("  EMPLOYEE #%d: %s%n", empNumbers[e], fullName);
            System.out.printf("  Birthday: %s | Basic Salary: PHP %-12.6f | Hourly Rate: PHP %-12.6f%n",
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

            // print the payroll table header
            System.out.printf("  %-16s %-8s %-14s %-20s %-18s %-18s %-16s %-16s%n",
                    "Period", "Cutoff", "Hrs Worked", "Gross Pay", "SSS", "PhilHealth",
                    "Pag-IBIG", "Net Pay");
            System.out.println("  " + "-".repeat(126));

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

                // compute gross pay = (hours worked x hourly rate) + half of the monthly allowances
                // we divide allowances by 2 because they are split between the two cutoffs
                double allowancesHalf = (empRiceSubsidy[e] + empPhoneAllowance[e] + empClothingAllowance[e]) / 2;
                double grossPay1 = (hours1 * empHourlyRate[e]) + allowancesHalf;
                double grossPay2 = (hours2 * empHourlyRate[e]) + allowancesHalf;

                // add both cutoffs together to get the total monthly gross
                // our instructor said we have to combine them first before computing deductions
                double monthlyGross = grossPay1 + grossPay2;

                // compute the government deductions using the monthly gross
                double sssMonthly = computeSSS(monthlyGross);
                double philHealthMonthly = computePhilHealth(monthlyGross);
                double pagIbigMonthly = computePagIbig(monthlyGross);

                // add up all three mandatory deductions
                double mandatoryDeductions = sssMonthly + philHealthMonthly + pagIbigMonthly;

                // compute the taxable income per cutoff for the income tax computation
                double semiMonthlyTaxable = (monthlyGross - mandatoryDeductions) / 2;

                // compute the income tax per cutoff
                double incomeTaxPerCutoff = computeIncomeTax(semiMonthlyTaxable);

                // divide the monthly deductions by 2 so we can show them per cutoff
                double sssCutoff = sssMonthly / 2;
                double philHealthCutoff = philHealthMonthly / 2;
                double pagIbigCutoff = pagIbigMonthly / 2;

                // total deductions per cutoff = half of SSS + half of PhilHealth + half of PagIbig + income tax
                double totalDeductionsPerCutoff = sssCutoff + philHealthCutoff + pagIbigCutoff + incomeTaxPerCutoff;

                // net pay = gross pay minus total deductions
                double netPay1 = grossPay1 - totalDeductionsPerCutoff;
                double netPay2 = grossPay2 - totalDeductionsPerCutoff;

                // make the month label like "June 2024"
                String monthLabel = monthNames[month] + " " + year;

                // print the 1st cutoff row
                System.out.printf("  %-16s %-8s %-14.6f %-20.6f %-18.6f %-18.6f %-16.6f %-16.6f%n",
                        monthLabel, "1st",
                        hours1, grossPay1,
                        sssCutoff, philHealthCutoff,
                        pagIbigCutoff, netPay1);

                // print the 2nd cutoff row
                System.out.printf("  %-16s %-8s %-14.6f %-20.6f %-18.6f %-18.6f %-16.6f %-16.6f%n",
                        "", "2nd",
                        hours2, grossPay2,
                        sssCutoff, philHealthCutoff,
                        pagIbigCutoff, netPay2);

                // print the total row for the whole month
                System.out.printf("  %-16s %-8s %-14.6f %-20.6f %-18.6f %-18.6f %-16.6f %-16.6f%n",
                        "", "TOTAL",
                        hours1 + hours2, monthlyGross,
                        sssMonthly, philHealthMonthly,
                        pagIbigMonthly, netPay1 + netPay2);

                System.out.println("  " + "-".repeat(126));
            }
        }

        System.out.println("\n=======================================================================");
        System.out.println("                          END OF PAYROLL REPORT");
        System.out.println("=======================================================================");
    }

    // this method splits one line of CSV into separate values
    // we need this because some values have commas inside quotes like "90,000"
    // so we can't just split by comma directly
    static String[] splitCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes; // flip the inQuotes flag when we see a quote mark
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString()); // comma outside quotes means end of a value
                current = new StringBuilder();
            } else {
                current.append(c); // otherwise just add the character to the current value
            }
        }
        fields.add(current.toString()); // don't forget to add the last value

        return fields.toArray(new String[0]);
    }

    // this method converts a number like "90,000" into a regular double like 90000.0
    // we need this because the CSV file has commas inside numbers
    static double parseFormattedDouble(String raw) {
        String clean = raw.trim().replace("\"", "").replace(",", ""); // remove quotes and commas
        if (clean.isEmpty()) {
            return 0; // if there is nothing there, just return 0

        }
        return Double.parseDouble(clean);
    }
}
