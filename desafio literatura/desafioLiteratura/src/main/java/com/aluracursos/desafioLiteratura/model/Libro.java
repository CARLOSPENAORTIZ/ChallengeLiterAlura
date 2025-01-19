package com.aluracursos.desafioLiteratura.model;







import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
//    @Version
//    private Integer  version;

    @Column(unique = true,nullable = false)
    private String titulo;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", referencedColumnName = "id")
    private Autor autor;

    @ElementCollection(targetClass = Idioma.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Enumerated(EnumType.STRING)
    private List<Idioma> idiomas;

    private Long descargas;

    public Libro( String titulo, Autor autor, List<Idioma> idiomas, Long descargas) {

        this.titulo = titulo;
        this.autor = autor;
        this.idiomas = idiomas;
        this.descargas = descargas;
    }
    public long getId() {
        return id;
    }

    public Libro() {

    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public List<Idioma> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<Idioma> idiomas) {
        this.idiomas = idiomas;
    }

    public Long getDescargas() {
        return descargas;
    }

    public void setDescargas(Long descargas) {
        this.descargas = descargas;
    }

    @Override
    public String toString() {
        return String.format(
                "Libro{id=%d, titulo='%s', autor=%s, idiomas=%s, descargas=%d}",
                id, titulo, autor, idiomas, descargas
        );
    }


}
