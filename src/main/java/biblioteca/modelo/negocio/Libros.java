package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Audiolibro;
import biblioteca.modelo.dominio.Autor;
import biblioteca.modelo.dominio.Categoria;
import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Libros {

    private static Libros libros = null;

    private Libros() {

    }
    public static Libros getLibros() {
        //creamos un metodo static para devolver el libro para usarlo.
        if (libros == null) {
            libros = new Libros();
        }
        return libros;
    }


    public void alta(Libro libro) throws Exception {
        //hay que controlar que el libro no este duplicado.

        if (libro == null) {
            throw new Exception("el libro no puede ser nulo para darlo de alta");
        }
        if (buscar(libro) != null) {
            throw new Exception("!!ERROR!!! El libro ya estaba dado de alta");
        }
        List<Autor> autoresLibro = libro.getAutores();
        boolean esAudiolibro=false;
        // esAudiolibro= libro instanceof Audiolibro; <-- esto devuelve true o false?
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
        //añade isbn y idAutor para ello buscamos el idAutor donde coincidan los tres parametros
        //puesto que es unique key nombre, apellidos, nacionalidad
        String insertLibroAutor=
                "INSERT INTO libro_autor (isbn, idAutor)" +
                " SELECT ?, idAutor  FROM autor " +
                " WHERE nombre = ? AND apellidos = ? AND nacionalidad = ?";


        //añadimos primero libro y audiolibro por las restricciones de las tablas para que luego se referencien
        Connection conexion = Conexion.establecerConexion();
        conexion.setAutoCommit(false);
        try(PreparedStatement pstLibro=conexion.prepareStatement(insertLibro);
            PreparedStatement pstAudioLibro = conexion.prepareStatement(insertAudioLibro);
            PreparedStatement pstAutor = conexion.prepareStatement(insertAutor);
            PreparedStatement pstLibroAutor = conexion.prepareStatement(insertLibroAutor)) {
            pstLibro.setString(1,libro.getIsbn());
            pstLibro.setString(2,libro.getTitulo());
            pstLibro.setInt(3,libro.getAnio());
            pstLibro.setString(4,libro.getCategoria().name()); //tb podemos usar .toString()
            int filasLibro= pstLibro.executeUpdate();
            if(filasLibro!=1){
                throw new SQLException("Error al insertar filas libro");
            }
            if(esAudiolibro){
                pstAudioLibro.setString(1,libro.getIsbn());
                pstAudioLibro.setInt(2, (int) ((Audiolibro) libro).getDuracion().toSeconds());//parseamos la entrada
                pstAudioLibro.setString(3,((Audiolibro) libro).getFormato());
                int filasAudioLibro= pstAudioLibro.executeUpdate();
                if(filasAudioLibro!=1){
                    throw new SQLException("Error añadir filas audiolibro");
                }
            }
            //añadimos el autor y libro_autor en la misma vuelta cuando ya estan referenciados.
            for (Autor autor : autoresLibro) {
                pstAutor.setString(1, autor.getNombre());
                pstAutor.setString(2, autor.getApellidos());
                pstAutor.setString(3, autor.getNacionalidad());
                pstAutor.executeUpdate();
                pstLibroAutor.setString(1, libro.getIsbn());
                pstLibroAutor.setString(2, autor.getNombre());
                pstLibroAutor.setString(3, autor.getApellidos());
                pstLibroAutor.setString(4, autor.getNacionalidad());
                pstLibroAutor.executeUpdate();
            }
        //hacemos el commit si no ha saltado excepcion.
            conexion.commit();
        }catch (SQLException e){
            conexion.rollback();
            throw new Exception("Error MySQL "+e.getMessage());
        }finally{
            //pase lo que pase volvemos a poner el autocommit
            conexion.setAutoCommit(true);
        }

    }

    public boolean bajaLibro(Libro libro) throws Exception {
        if (libro == null) {
            throw new Exception("el libro no puede ser nulo para darlo de baja");
        }
        //no se si hay que buscarlo porque haria otra conexion a la base de datos. ????
        if (buscar(libro) == null) { //lanzamos la excepcion si el metodo buscar retorna null puesto que no se ha encontrado el libro.
            throw new Exception("Libro no encontrado para ser dado de baja");
        }

        /*Primero borramos en la tabla libro por isbn que hará que se borre en audiolibro y libro_autor
        después borramos por autores que no esten en libro_autor, que seran los que estaban relacionados
        con el libro que se acaba de borrar
        */
        String pstDeleteLibro="delete from libro where isbn = ?";
        String pstDeleteAutores="delete from autor where idAutor not in(select idAutor from libro_autor)";

        Connection conexion = Conexion.establecerConexion();
        try(PreparedStatement sentenciaBorrarLibro= conexion.prepareStatement(pstDeleteLibro);
        PreparedStatement sentenciaBorrarAutores= conexion.prepareStatement(pstDeleteAutores)){
            sentenciaBorrarLibro.setString(1,libro.getIsbn());
            int filasBL= sentenciaBorrarLibro.executeUpdate();
            if (filasBL ==0){
                throw new Exception("Error al dar de baja el libro "+ libro.getTitulo());
            }
            //si no se lanza la excepcion borramos los autores que no tienen isbn asociado.
            sentenciaBorrarAutores.executeUpdate();

        }catch (SQLException e){
            throw new Exception("ERROR MySQL "+e.getMessage());
        }

        return true;
    }
    public Libro buscar(Libro libro) throws Exception {
        if (libro == null) {
            throw new Exception("el libro no puede ser nulo");
        }

        Libro libroBuscado = null;
        String buscarLibro = "SELECT l.isbn, l.titulo, l.anio, l.categoria, " +
                "al.duracion_segundos, al.formato " +
                "FROM libro l " +
                "LEFT JOIN audiolibro al ON al.isbn = l.isbn " +
                "WHERE l.isbn = ?";
        String buscarAutores = "SELECT a.nombre, a.apellidos, a.nacionalidad " +
                "FROM autor a " +
                "INNER JOIN libro_autor la ON la.idAutor = a.idAutor " +
                "WHERE la.isbn = ?";

        Connection conexion = Conexion.establecerConexion();
        try (PreparedStatement pstLibro = conexion.prepareStatement(buscarLibro);
             PreparedStatement pstAutor = conexion.prepareStatement(buscarAutores)) {

            pstLibro.setString(1, libro.getIsbn());

            try (ResultSet rsLibro = pstLibro.executeQuery()) {
                // ISBN es unico, usamos if en lugar de while
                if (rsLibro.next()) {
                    String isbn = rsLibro.getString("isbn");
                    String titulo = rsLibro.getString("titulo");
                    int anio = rsLibro.getInt("anio");
                    Categoria categoria = Categoria.valueOf(rsLibro.getString("categoria"));
                    int duracion = rsLibro.getInt("duracion_segundos");
                    String formato = rsLibro.getString("formato");

                    if (formato != null) { // es audiolibro
                        libroBuscado = new Audiolibro(isbn, titulo, anio, categoria,
                                Duration.ofSeconds(duracion), formato);
                    } else { // es libro normal
                        libroBuscado = new Libro(isbn, titulo, anio, categoria);
                    }
                }
            }

            if (libroBuscado != null) {
                pstAutor.setString(1, libro.getIsbn());
                try (ResultSet rsAutor = pstAutor.executeQuery()) {
                    while (rsAutor.next()) {
                        libroBuscado.addAutor(new Autor(
                                rsAutor.getString("nombre"),
                                rsAutor.getString("apellidos"),
                                rsAutor.getString("nacionalidad")));
                    }
                }
            }

        } catch (SQLException e) {
            throw new Exception("ERROR MySQL: " + e.getMessage());
        }

        return libroBuscado;
    }


    public List<Libro> todos() throws Exception {
        List<Libro> librosBD = new ArrayList<>();

        String leerLibros = "SELECT l.isbn, l.titulo, l.anio, l.categoria, " +
                "al.duracion_segundos, al.formato " +
                "FROM libro l " +
                "LEFT JOIN audiolibro al ON al.isbn = l.isbn ";
        String leerAutores = "SELECT a.nombre, a.apellidos, a.nacionalidad " +
                "FROM autor a " +
                "INNER JOIN libro_autor la ON la.idAutor = a.idAutor " +
                "WHERE la.isbn = ?";
        Connection con = Conexion.establecerConexion();
        try(PreparedStatement ptLibros = con.prepareStatement(leerLibros);
        PreparedStatement ptAutores= con.prepareStatement(leerAutores))
        {

            try(ResultSet filasLibros= ptLibros.executeQuery()){
                while(filasLibros.next()){
                    Libro libro;
                    List <Autor> autores = new ArrayList<>();
                    String isbn = filasLibros.getString("isbn");
                    String titulo = filasLibros.getString("titulo");
                    int anio = filasLibros.getInt("anio");
                    Categoria categoria = Categoria.valueOf(filasLibros.getString("categoria"));
                    Duration duracion = Duration.ofSeconds(filasLibros.getInt("duracion_segundos"));
                    String formato = filasLibros.getString("formato");
                    if(formato==null){
                        libro = new Libro(isbn,titulo,anio,categoria);
                    }else{
                        libro = new Audiolibro(isbn,titulo,anio,categoria,duracion,formato);
                    }
                    ptAutores.setString(1,isbn);
                    try(ResultSet filasAutores= ptAutores.executeQuery()){

                        while(filasAutores.next()){
                            String nombre = filasAutores.getString("nombre");
                            String apellidos = filasAutores.getString("apellidos");
                            String nacionalidad = filasAutores.getString("nacionalidad");
                            autores.add(new Autor(nombre,apellidos,nacionalidad));

                        }
                    }
                    for(Autor a : autores){
                        libro.addAutor(a);
                    }
                    librosBD.add(libro);
                }
            }

        } catch (SQLException e) {
            throw new Exception("ERROR MySQL: " + e.getMessage());
        }
        return librosBD;
    }
}
