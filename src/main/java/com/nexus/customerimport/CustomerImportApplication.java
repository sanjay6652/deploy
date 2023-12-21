package com.nexus.customerimport;

import com.nexus.customerimport.Service.CSVService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.project.*;

@SpringBootApplication
@RestController

public class CustomerImportApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomerImportApplication.class, args);
		someMethod();
	}
	public static void someMethod() {
		CSVService csvService = new CSVService();
		csvService.startHotFolder("C:\\Users\\SanjayB(TADigital)\\Desktop\\hotfolder");
	}


}
