//package com.nexus.customerimport.Controller;
//
//import com.nexus.customerimport.Service.CSVService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/csv")
//public class CSVController {
//
//    private final CSVService csvService;
//
//    public CSVController(CSVService csvService) {
//        this.csvService = csvService;
//    }
//
//    @GetMapping("/process")
//    public void startHotFolder() {
//        csvService.startHotFolder("C:\\Users\\SanjayB(TADigital)\\Desktop\\hotfolder");
//    }
//}
