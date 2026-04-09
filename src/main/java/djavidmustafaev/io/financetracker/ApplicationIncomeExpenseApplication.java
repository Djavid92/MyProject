package djavidmustafaev.io.financetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ApplicationIncomeExpenseApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ApplicationIncomeExpenseApplication.class, args);
	}

}
