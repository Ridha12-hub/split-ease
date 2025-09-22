ECHO is on.
ECHO is on.
package com.splitease.backend.bill.services;

import com.splitease.backend.bill.models.Bill;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class CsvExportService {

    public String exportBillsToCsv(List<Bill> bills) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "unpaid_bills_" + timestamp + ".csv";

        try (FileWriter fileWriter = new FileWriter(filename);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            // Write the CSV header
            printWriter.println("ID,Description,Amount,Date,Payer ID,Group ID");

            // Write each bill as a new row in the CSV file
            for (Bill bill : bills) {
                printWriter.printf("%d,%s,%.2f,%s,%d,%d\n",
                    bill.getId(),
                    bill.getDescription(),
                    bill.getAmount(),
                    new SimpleDateFormat("yyyy-MM-dd").format(bill.getDate()),
                    bill.getPayerId(),
                    bill.getGroupId());
            }

            return filename;
        }
    }
}
  
