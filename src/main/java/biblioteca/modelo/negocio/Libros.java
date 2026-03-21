package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Audiolibro;
import biblioteca.modelo.dominio.Libro;

import java.util.ArrayList;
import java.util.List;

public class Libros {
    private final List<Libro> libros;

    public Libros() {
        this.libros = new ArrayList<>();
    }

    //validamos que el libro no este añadido y si no lanza la Excepcion añadimos el libro a la lista.
    public void alta(Libro libro) throws Exception { //ya configuramos el metodo equals para comparar por isbn
        if (libro == null) {
            throw new Exception("el libro no puede ser nulo para darlo de alta");
        }
        if (buscar(libro) != null) {
            throw new Exception("!!ERROR!!! El libro ya estaba dado de alta");
        }
        if (libro instanceof Audiolibro) {
            libros.add(new Audiolibro((Audiolibro) libro));
        } else {
            libros.add(new Libro(libro));
        }

    }


    public boolean bajaLibro(Libro libro) throws Exception {
        if (libro == null) {
            throw new Exception("el libro no puede ser nulo para darlo de baja");
        }
        if (buscar(libro) == null) { //lanzamos la excepcion si el metodo buscar retorna null puesto que no se ha encontrado el libro.
            throw new Exception("Libro no encontrado para ser dado de baja");
        }
        libros.remove(libro); //el metodo remove devuelve true?
        return true;
    }

    //Metodo que devuelve un objeto tipo libro en la comparacion y sino lo encuentra retorna null. Utilizado para los metodos de alta en la clase Consola.
    public Libro buscar(Libro libro) throws Exception {
        if (libro == null) {
            throw new Exception("el libro no puede ser nulo");
        }
        int indiceBuscar = libros.indexOf(libro); //usamos index0f en vez de for each para buscar.
        if (indiceBuscar > -1) {
            Libro libroEncontrado = libros.get(indiceBuscar); //buscamos el libro real que será Audiolibro o Libro
            if (libroEncontrado instanceof Audiolibro) {
                return new Audiolibro((Audiolibro)libroEncontrado); //hacemos la copia del libro encontrado si es Audio libro, se podrá devolver solo la copia?
            } else {
                return new Libro(libroEncontrado);
            }
        }
        return null;
    }




    //Metodo para que devuelva un nuevo Array de tipo Libro solo con los libros dados de alta, es decir sin nulos.
    public List<Libro> todos() throws Exception {
        List<Libro> listaSinNulos = new ArrayList<>();
        for (Libro l : libros) {
            if (l instanceof Audiolibro) {
                listaSinNulos.add(new Audiolibro((Audiolibro) l));
            } else {
                listaSinNulos.add(new Libro(l));
            }
        }
        return listaSinNulos;
    }
}
