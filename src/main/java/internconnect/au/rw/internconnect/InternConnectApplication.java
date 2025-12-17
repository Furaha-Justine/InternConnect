package internconnect.au.rw.internconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class InternConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternConnectApplication.class, args);
	}

}
