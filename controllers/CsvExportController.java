ECHO is on.
  

package com.splitease.backend.bill.controllers;

import com.splitease.backend.bill.models.Bill;
import com.splitease.backend.bill.services.BillService;
import com.splitease.backend.bill.services.CsvExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/splitease/bills/export")
public class CsvExportController {

    @Autowired
    private BillService billService;

    @Autowired
    private CsvExportService csvExportService;

    @GetMapping("/csv")
    public ResponseEntity<Resource> exportAllBills() {
        try {
            // In a real application, you would filter for unpaid/overdue bills.
            List<Bill> allBills = billService.getAllBills();
            String filename = csvExportService.exportBillsToCsv(allBills);

            File file = new File(filename);
            Resource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
