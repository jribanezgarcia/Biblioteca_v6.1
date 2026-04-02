package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Audiolibro;
import biblioteca.modelo.dominio.Autor;
import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Libros {
    private final List<Libro> libros;

    public Libros() {
        this.libros = new ArrayList<>();
    }

    //validamos que el libro no este añadido y si no lanza la Excepcion añadimos el libro a la lista.
    public void alta(Libro libro) throws Exception { //ya configuramos el metodo equals para comparar por isbn
        List<Autor> autoresLibro = libro.getAutores();
        boolean esAudiolibro=false;
        if (libro == null) {
            throw new Exception("el libro no puede ser nulo para darlo de alta");
        }
        if (buscar(libro) != null) {
            throw new Exception("!!ERROR!!! El libro ya estaba dado de alta");
        }
        if (libro instanceof Audiolibro) {
            esAudiolibro=true;
        } else {
            esAudiolibro=false;
        }
        String insertAutor=
                "INSERT INTO autor (nombre, apellidos, nacionalidad) values (?,?,?)";
        String insertLibro=
                "insert into libro (isbn, titulo, anio, categoria)"+
                " values (?, ?, ?, ?)";
        String insertAudioLibro=
                "insert into audiolibro(isbn, duracion_segundos, formato)"+
                " values (?, ?, ?)";
        String insertLibroAutor=
                "INSERT INTO libro_autor (isbn, idAutor)" +
                " SELECT ?, idAutor  FROM autor " +
                " WHERE nombre = ? AND apellidos = ? AND nacionalidad = ?";


        try(Connection conexion= Conexion.establecerConexion();
        PreparedStatement pstLibro=conexion.prepareStatement(insertLibro);
        PreparedStatement pstAudioLibro=conexion.prepareStatement(insertAudioLibro);){
            pstLibro.setString(1,libro.getIsbn());
            pstLibro.setString(2,libro.getTitulo());
            pstLibro.setInt(3,libro.getAnio());
            pstLibro.setString(4,libro.getCategoria().name()); //tb podemos usar .toString()
            int filasLibro= pstLibro.executeUpdate();
            if(filasLibro!=1){
                throw new Exception("Error al insertar filas libro");
            }
            if(esAudiolibro){
                pstAudioLibro.setString(1,libro.getIsbn());
                pstAudioLibro.setInt(2, (int) ((Audiolibro) libro).getDuracion().toSeconds());
                pstAudioLibro.setString(3,((Audiolibro) libro).getFormato());
                int filasAudioLibro= pstAudioLibro.executeUpdate();
                if(filasAudioLibro!=1){
                    throw new Exception("Error añadir filas audiolibro");
                }
            }
        }catch (SQLException e){

            System.out.println("Error MySQL "+e.getMessage());
        }



        try(Connection conexion= Conexion.establecerConexion();
        PreparedStatement pstAutor= conexion.prepareStatement(insertAutor);
        PreparedStatement pstLibroAutor= conexion.prepareStatement(insertLibroAutor);
        ){

        for(Autor autor : autoresLibro){
            pstAutor.setString(1,autor.getNombre());
            pstAutor.setString(2,autor.getApellidos());
            pstAutor.setString(3,autor.getNacionalidad());
            pstAutor.executeUpdate();
            pstLibroAutor.setString(1,libro.getIsbn());
            pstLibroAutor.setString(2,autor.getNombre());
            pstLibroAutor.setString(3,autor.getApellidos());
            pstLibroAutor.setString(4,autor.getNacionalidad());
            pstLibroAutor.executeUpdate();
        }

        }catch (SQLException e){
            System.out.println("ERROR MySQL "+e.getMessage());
        }



        //primero añadimos los autores por las restricciones de la tabla libro_autor
        //orden de ejecucion autor, libro y libro_autor por la confinguracion de la tabla

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
