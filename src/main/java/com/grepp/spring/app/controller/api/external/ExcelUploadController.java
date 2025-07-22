//package com.grepp.spring.app.controller.api.external;
//
//import com.grepp.spring.app.model.schedule.service.ExcelUploadService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/excel")
//public class ExcelUploadController {
//
//    private final ExcelUploadService excelUploadService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
//        try {
//            excelUploadService.importFromExcel(file);
//            return ResponseEntity.ok("Upload Success");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload Failed: " + e.getMessage());
//        }
//    }
//}