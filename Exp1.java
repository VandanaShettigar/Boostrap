import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Exp1 {
    public static void main(String[] args) {
        String csvFile = "employee_data.csv"; 

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            String prevEmployeeName = null;
            Date prevTimeOut = null;
            Date prevTimeIn = null;
            int consecutiveDaysCount = 0;

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 9) {
                    continue; // Skip incomplete records
                }

                String employeeName = data[7].trim();
                Date timeIn = null;
                Date timeOut = null;

                // Check for non-empty date/time strings before parsing
                if (!data[2].trim().isEmpty() && !data[3].trim().isEmpty()) {
                    try {
                        timeIn = dateFormat.parse(data[2].trim());
                        timeOut = dateFormat.parse(data[3].trim());
                    } catch (ParseException e) {
                        System.err.println("Error parsing date/time: " + e.getMessage());
                        // You can choose to skip or handle this specific record as needed.
                        continue; // Skip this record and move to the next one
                    }
                } else {
                    // Handle empty date/time strings or other errors
                    // You can choose to skip, log, or handle these cases as needed.
                    continue; // Skip this record and move to the next one
                }

                // Check for consecutive days
                if (prevEmployeeName != null && prevEmployeeName.equals(employeeName)) {
                    long dayDifference = (timeIn.getTime() - prevTimeOut.getTime()) / (24 * 60 * 60 * 1000);
                    if (dayDifference == 1) {
                        consecutiveDaysCount++;
                    } else {
                        consecutiveDaysCount = 0; // Reset if not consecutive
                    }
                } else {
                    consecutiveDaysCount = 0; // Reset for a new employee
                }

                // Check for less than 10 hours between shifts but greater than 1 hour
                if (prevTimeIn != null) {
                    long hourDifference = (timeIn.getTime() - prevTimeOut.getTime()) / (60 * 60 * 1000);
                    if (hourDifference > 1 && hourDifference < 10) {
                        System.out.println("Employee: " + employeeName + ", Position: " + data[0].trim()
                                + " had less than 10 hours between shifts.");
                    }
                }

                // Check for shifts longer than 14 hours
                long shiftDuration = (timeOut.getTime() - timeIn.getTime()) / (60 * 60 * 1000);
                if (shiftDuration > 14) {
                    System.out.println("Employee: " + employeeName + ", Position: " + data[0].trim()
                            + " worked for more than 14 hours in a single shift.");
                }

                // Check for 7 consecutive days
                if (consecutiveDaysCount == 6) { // Count includes the current day
                    System.out.println("Employee: " + employeeName + ", Position: " + data[0].trim()
                            + " has worked for 7 consecutive days.");
                }

                prevEmployeeName = employeeName;
                prevTimeOut = timeOut;
                prevTimeIn = timeIn;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
