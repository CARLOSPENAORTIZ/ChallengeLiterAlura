package com.aluracursos.desafioLiteratura.Service;

import com.aluracursos.desafioLiteratura.model.Idioma;
import com.aluracursos.desafioLiteratura.model.Libro;
import com.aluracursos.desafioLiteratura.repository.LibroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroService {

    public void prueba() {
        System.out.println("Método de prueba llamado");
    }

    @Autowired
    private LibroRepository libroRepository;

    @Transactional
    public List<Libro> listarLibrosPorIdioma(String idioma){
        return libroRepository.findAll().stream()
                .filter(libro -> libro.getIdiomas().contains(Idioma.valueOf(idioma)))
                .sorted(Comparator.comparing(Libro::getTitulo))
                .collect(Collectors.toList());
    }

}
