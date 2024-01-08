package com.danskebank.dummy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info= @Info (
		 title ="Danske Bank App",
				description = "Backed RestApi 's for Danske Bank",
		version = "v1.0",
		contact = @Contact(
				name = "Reena",
				email = "pathak.reena17@gmail.com",
				url = ""
		)

		)
)
public class DummyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DummyApplication.class, args);
	}

}
