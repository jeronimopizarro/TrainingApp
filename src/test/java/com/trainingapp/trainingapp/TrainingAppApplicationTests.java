package com.trainingapp.trainingapp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Deshabilitado hasta que configuremos una base de datos en memoria (H2) para CI")
@SpringBootTest
class TrainingAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
