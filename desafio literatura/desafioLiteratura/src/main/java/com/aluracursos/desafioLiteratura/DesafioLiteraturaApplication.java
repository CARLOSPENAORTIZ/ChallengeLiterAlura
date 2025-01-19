package com.aluracursos.desafioLiteratura;


import com.aluracursos.desafioLiteratura.principal.Principal;
import com.aluracursos.desafioLiteratura.repository.AutorRepository;
import com.aluracursos.desafioLiteratura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DesafioLiteraturaApplication implements CommandLineRunner {



	@Autowired
	private LibroRepository libroRepository;

	@Autowired
	private AutorRepository autorRepository;



	public static void main(String[] args) {
		SpringApplication.run(DesafioLiteraturaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroRepository, autorRepository);
		principal.muestraElMenu();
	}

}








