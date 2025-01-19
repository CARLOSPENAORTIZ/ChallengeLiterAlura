package com.aluracursos.desafioLiteratura.principal;

import com.aluracursos.desafioLiteratura.Service.ConvierteDatos;
import com.aluracursos.desafioLiteratura.Service.IConvierteDatos;
import com.aluracursos.desafioLiteratura.Service.ConsumoAPI;
import com.aluracursos.desafioLiteratura.Service.LibroService;
import com.aluracursos.desafioLiteratura.dto.DatosAPI;
import com.aluracursos.desafioLiteratura.dto.DatosAutor;
import com.aluracursos.desafioLiteratura.dto.DatosLibro;
import com.aluracursos.desafioLiteratura.model.Autor;
import com.aluracursos.desafioLiteratura.model.Idioma;
import com.aluracursos.desafioLiteratura.model.Libro;
import com.aluracursos.desafioLiteratura.repository.AutorRepository;
import com.aluracursos.desafioLiteratura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;


import java.util.*;

@Controller
public class Principal {
    private  Scanner teclado= new Scanner(System.in);
    private ConsumoAPI consumoAPI= new ConsumoAPI();
    private final  String URL_BASE = "https://gutendex.com/books/?search=";
    private IConvierteDatos conversor = new ConvierteDatos();
    private  LibroRepository libroRepository;
    private   AutorRepository autorRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }


    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine(); // Limpiar el buffer
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                teclado.nextLine(); // Limpiar el buffer para evitar un bucle infinito
                continue; // Volver al inicio del bucle
            }
            switch (opcion) {
                case 1:
                    registrarLibroDesdeAPI();
                    break;
                case 2:
                    ListarLibrosRegistrados();
                    break;
                case 3:
                    ListarAutoresRegistrados();
                    break;
                case 4:
                    ListarAutoresVivos();
                    break;
                case 5:
                    ListarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private Optional<DatosLibro> getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var url = URL_BASE + nombreLibro.replace(" ", "+");
        System.out.println(url );
        var json = consumoAPI.obtenerDatos(url);
        DatosAPI respuestaAPI = conversor.obtenerDatos(json, DatosAPI.class);
        List<DatosLibro> libros = respuestaAPI.libros();
        return libros.stream().findFirst();
    }
    private void actualizarLibro(Long libroId, String nuevoTitulo) {
        // Paso 1: Recargar la entidad desde la base de datos
        Optional<Libro> libroRecargadoOptional = libroRepository.findById(libroId);

        if (libroRecargadoOptional.isPresent()) {
            // Paso 2: Obtener el libro recargado
            Libro libroRecargado = libroRecargadoOptional.get();

            // Paso 3: Realizar las modificaciones en el objeto recargado
            libroRecargado.setTitulo(nuevoTitulo);

            // Guardar el libro recargado
            try {
                libroRepository.save(libroRecargado);
                System.out.println("Libro actualizado exitosamente: " + libroRecargado);
            } catch (ObjectOptimisticLockingFailureException e) {
                System.out.println("El libro ha sido modificado por otra transacción. Por favor, recargue la información.");
            }
        } else {
            System.out.println("El libro no existe en la base de datos.");
        }
    }

    private void registrarLibroDesdeAPI() {
        Optional<DatosLibro> datosLibro = getDatosLibro();
        if (datosLibro.isEmpty()) {
            System.out.println("No se encontraron libros con el nombre ingresado, intente de nuevo");
            return; // Salir del método si no hay datos
        }

        DatosAutor datosAutor = datosLibro.get().autores().get(0);
        Autor autor = obtenerORegistrarAutor(datosAutor);

        List<Idioma> idiomas = obtenerIdiomas(datosLibro.get().lenguajes());

        Libro nuevoLibro = new Libro(datosLibro.get().titulo(), autor, idiomas, datosLibro.get().descargas());

        try {
            libroRepository.save(nuevoLibro);
            System.out.println("Libro registrado exitosamente: " + nuevoLibro);
        } catch (DataIntegrityViolationException e) {
            System.out.println("Error al guardar el libro: " + e.getMessage());
        }
    }

    private Autor obtenerORegistrarAutor(DatosAutor datosAutor) {
        Optional<Autor> autorExistente = autorRepository.findByNombre(datosAutor.nombre());
        if (autorExistente.isPresent()) {
            return autorExistente.get(); // Usar el autor existente
        } else {
            Autor nuevoAutor = new Autor(datosAutor.nombre(), datosAutor.anioNacimiento(), datosAutor.anioMuerte());
            autorRepository.save(nuevoAutor); // Guardar el nuevo autor
            System.out.println("Autor registrado exitosamente: " + nuevoAutor);
            return nuevoAutor;
        }
    }

    private List<Idioma> obtenerIdiomas(List<String> lenguajes) {
        return lenguajes.stream()
                .map(String::toUpperCase)
                .filter(lang -> lang.equals("ES") || lang.equals("EN") || lang.equals("FR") || lang.equals("PT"))
                .map(Idioma::valueOf)
                .toList();
    }

    private void ListarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        System.out.println("=".repeat(50));
        System.out.printf("| %-5s | %-30s | %-10s |\n", "ID", "Título", "Descargas");
        System.out.println("=".repeat(50));
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(libro -> System.out.printf("| %-5d | %-30s | %-10d |\n",
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getDescargas()!= null ? libro.getDescargas() : 0));
        System.out.println("=".repeat(50));
    }

    private void ListarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(autor -> System.out.printf("Nombre: %s | Año de Nacimiento: %d | Año de Muerte: %s%n",
                        autor.getNombre(),
                        autor.getAnioNacimiento(),
                        autor.getAnioMuerte() != null ? autor.getAnioMuerte() : "N/A"));
    }

    private void ListarAutoresVivos() {
        System.out.println("Ingrese el año en el que desea buscar autores vivos:");
        int anio = teclado.nextInt();
        teclado.nextLine();

        List<Autor> autoresVivos = autorRepository.findAll().stream()
                .filter(autor -> autor.getAnioNacimiento() <= anio &&
                        (autor.getAnioMuerte() == null || autor.getAnioMuerte() > anio))
                .sorted(Comparator.comparing(Autor::getNombre))
                .toList();

        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año especificado.");
        } else {
            autoresVivos.forEach(autor -> System.out.printf("Nombre: %s | Año de Nacimiento: %d | Año de Muerte: %s%n",
                    autor.getNombre(),
                    autor.getAnioNacimiento(),
                    autor.getAnioMuerte() != null ? autor.getAnioMuerte() : "N/A"));
        }
    }

    private void ListarLibrosPorIdioma() {
        System.out.println("Seleccione el idioma por el cual desea filtrar los libros:");
        System.out.println("1 - Español (ES)");
        System.out.println("2 - Inglés (EN)");
        System.out.println("3 - Francés (FR)");
        System.out.println("4 - Portugués (PT)");
        System.out.println("Ingrese un número entre 1 y 4:");

        int opcion = teclado.nextInt();
        teclado.nextLine();

        String idioma;
        switch (opcion) {
            case 1 -> idioma = "ES";
            case 2 -> idioma = "EN";
            case 3 -> idioma = "FR";
            case 4 -> idioma = "PT";
            default -> {
                System.out.println("Opción inválida. Debe ingresar un número entre 1 y 4. Intente de nuevo.");
                return;
            }
        }


        List<Libro> librosPorIdioma = libroService.listarLibrosPorIdioma(idioma);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma especificado.");
        } else {
            for (Libro libro : librosPorIdioma) {
                String autorNombre = libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido";
                System.out.printf("ID: %d | Título: %s | Autor: %s | Idiomas: %s | Descargas: %d%n",
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getIdiomas(),
                        libro.getDescargas() != null ? libro.getDescargas() : 0);
            }
        }
    }
}



