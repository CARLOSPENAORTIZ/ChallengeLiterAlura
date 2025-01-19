package com.aluracursos.desafioLiteratura.model;

import jakarta.persistence.*;

import java.util.List;


    @Entity
    @Table(name = "autores")
    public class Autor {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Id;

        private String nombre;

        @Column(name = "anio_nacimiento")
        private Integer anioNacimiento;

        @Column(name = "anio_muerte")
        private Integer anioMuerte;

        @OneToMany(mappedBy = "autor",fetch = FetchType.EAGER   )
        private List<Libro> libros;

        public Autor(String nombre, Integer anioNacimiento, Integer anioMuerte) {
            this.nombre = nombre;
            this.anioNacimiento = anioNacimiento;
            this.anioMuerte = anioMuerte;
        }

        public Autor() {

        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Integer getAnioNacimiento() {
            return anioNacimiento;
        }

        public void setAnioNacimiento(Integer anioNacimiento) {
            this.anioNacimiento = anioNacimiento;
        }

        public Integer getAnioMuerte() {
            return anioMuerte;
        }

        public void setAnioMuerte(Integer anioMuerte) {
            this.anioMuerte = anioMuerte;
        }

        public List<Libro> getLibros() {
            return libros;
        }

        public void setLibros(List<Libro> libros) {
            this.libros = libros;
        }

        @Override
        public String toString() {
            return String.format("Autor[nombre='%s', anioNacimiento=%d, anioMuerte=%s]",
                    nombre, anioNacimiento,
                    anioMuerte != null ? anioMuerte.toString() : "N/A");
        }
    }