package com.nexus.customerimport;

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
	}


}
