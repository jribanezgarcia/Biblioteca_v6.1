package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.*;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prestamos {
    private static Prestamos prestamos = null;


    private Prestamos(){

    }
    public static Prestamos getPrestamos() {
        //creamos un metodo static para devolver el libro para usarlo.
        if (prestamos == null) {
            prestamos = new Prestamos();
        }
        return prestamos;
    }

    public Prestamo prestar(Libro libro,Usuario usuario,LocalDate fecha) throws Exception {
        if(libro==null){
            throw new Exception("ERROR, el libro no puede ser nulo");
        }
        if(usuario==null){
            throw new Exception("ERROR, el usuario no puede ser nulo");
        }
        if(fecha==null){
            throw new Exception("ERROR, la fecha no puede ser nula");
        }
        //buscar lo valida ya la vista al introducir los datos del usuario y del libro.
        //mantener las validaciones auqnue cueste mas llamadas. por si se cambia la vista (tarea 9).
        /*if(Libros.getLibros().buscar(libro)==null){
            throw new Exception("libro "+libro.getTitulo()+" no encontrado en BD para hacer el préstamo");
        }
        if(Usuarios.getUsuarios().buscar(usuario)==null){
            throw new Exception("usuario "+usuario.getNombre()+" no encontrado en BD para hacer el préstamo");
        }*/
        //hay dos columnas que tienen valores por defecto en la BD, devuelto que sera 0 y fDevolucion null
        //estos valores no se insertan porque deben estar asi al realizar el prestamo.
        Prestamo prestamoNuevo = new Prestamo(libro,usuario,fecha);
        String sqlPrestamo= "insert into prestamo (dni, isbn, fInicio, fLimite)"+
                " values (?, ?, ?, ?)";
        Connection con = Conexion.establecerConexion();
        try(PreparedStatement psPrestar = con.prepareStatement(sqlPrestamo)){
            psPrestar.setString(1,usuario.getDni());
            psPrestar.setString(2,libro.getIsbn());
            psPrestar.setDate(3, Date.valueOf(prestamoNuevo.getfInicio()));
            psPrestar.setDate(4,Date.valueOf(prestamoNuevo.getfLimite()));
            int filas = psPrestar.executeUpdate();
            if (filas != 1) {
                throw new Exception("Error al insertar el préstamo");
            }
        }catch (SQLException e){
            throw new Exception("ERROR MySQL: "+e.getMessage());
        }
        return prestamoNuevo;
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fechaDevolucion) throws Exception {
        if (libro == null) {
            throw new Exception("ERROR, el libro no puede ser nulo");
        }
        if (usuario == null) {
            throw new Exception("ERROR, el usuario no puede ser nulo");
        }
        if (fechaDevolucion == null) {
            throw new Exception("ERROR, la fecha no puede ser nula");
        }
        //probamos creando la conexion aqui aunque creo que n ohace falta porque los metodos buscar ya lo lleva.
        if(Libros.getLibros().buscar(libro)==null){
            throw new Exception("Libro "+libro.getTitulo()+" no encontrado, no se puedo realizar la devolution del préstamo");
        }
        if(Usuarios.getUsuarios().buscar(usuario)==null){
            throw new Exception("Usuario "+usuario.getNombre()+" no encontrado, no se puedo realizar la devolución del préstamo");
        }
        Connection conexion = Conexion.establecerConexion();
        String sqlDevolver="UPDATE prestamo SET devuelto = 1, fDevolucion = ?"+
                " WHERE dni = ? AND isbn = ? AND devuelto = 0";
        try(PreparedStatement psDevolverPrestamo= conexion.prepareStatement(sqlDevolver)){
            psDevolverPrestamo.setDate(1, Date.valueOf(fechaDevolucion));
            psDevolverPrestamo.setString(2,usuario.getDni());
            psDevolverPrestamo.setString(3, libro.getIsbn());
            int filas =psDevolverPrestamo.executeUpdate();
            if (filas==0){
                throw new Exception("Error al devolver el prestamo de "+libro.getTitulo()+" y "+usuario.getNombre());
            }

        }catch (SQLException e){
            throw new Exception("ERROR MySQL "+e.getMessage());
        }

        return true;


    }

    public List<Prestamo> todos(Usuario usuario) throws Exception {
        if(usuario==null){
            throw new Exception("ERROR, el usuario no puede ser nulo");
        }
        List<Prestamo>prestamosUsuario = new ArrayList<>();
        String sqlListUsuario ="select dni, isbn, fInicio, fLimite, devuelto, fDevolucion "+
                "from prestamo " +
                " where dni = ?";
        String leerLibros = "SELECT l.isbn, l.titulo, l.anio, l.categoria, " +
                "al.duracion_segundos, al.formato " +
                "from libro l " +
                "left JOIN audiolibro al ON al.isbn = l.isbn "+
                "where l.isbn = ?";
        String leerAutores = "SELECT a.nombre, a.apellidos, a.nacionalidad " +
                "FROM autor a " +
                "INNER JOIN libro_autor la ON la.idAutor = a.idAutor " +
                "WHERE la.isbn = ?";

        Connection con = Conexion.establecerConexion();
        try(PreparedStatement psListPrestamosUsuario = con.prepareStatement(sqlListUsuario);
        PreparedStatement psListLeerLibros = con.prepareStatement(leerLibros);
        PreparedStatement psListLeerAutores= con.prepareStatement(leerAutores)){
            psListPrestamosUsuario.setString(1,usuario.getDni());
            try(ResultSet filas= psListPrestamosUsuario.executeQuery()){
                while(filas.next()){
                    String dni = filas.getString("dni");
                    String isbn = filas.getString("isbn");
                    LocalDate fInicio = filas.getDate("fInicio").toLocalDate();
                    LocalDate fLimite = filas.getDate("fLimite").toLocalDate();
                    Boolean devuelto= filas.getBoolean("devuelto");
                    //controlamos que sea null antes de parsearlo.
                    LocalDate fDevolucion=null;
                    Date date = filas.getDate("fDevolucion");
                    if(date!=null){
                        fDevolucion = filas.getDate("fDevolucion").toLocalDate();
                    }
                    psListLeerLibros.setString(1,isbn);
                    try(ResultSet filasLibros= psListLeerLibros.executeQuery()){
                        while (filasLibros.next()){
                            Libro libro;
                            List <Autor> autores = new ArrayList<>();
                            String isbn2 = filasLibros.getString("isbn");
                            String titulo = filasLibros.getString("titulo");
                            int anio = filasLibros.getInt("anio");
                            Categoria categoria = Categoria.valueOf(filasLibros.getString("categoria"));
                            Duration duracion = Duration.ofSeconds(filasLibros.getInt("duracion_segundos"));
                            String formato = filasLibros.getString("formato");
                            if(formato==null){
                                libro = new Libro(isbn2,titulo,anio,categoria);
                            }else{
                                libro = new Audiolibro(isbn2,titulo,anio,categoria,duracion,formato);
                            }
                            psListLeerAutores.setString(1,isbn2);
                            try(ResultSet filasAutores= psListLeerAutores.executeQuery()){

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
                            Prestamo p = new Prestamo(libro,usuario,fInicio);
                            if(fDevolucion!=null){
                                p.marcarDevuelto(fDevolucion);
                            }
                            prestamosUsuario.add(p);
                        }

                    }catch (SQLException e){
                        throw new SQLException ("ERROR MySQL "+e.getMessage());
                    }

                }
            }
        }catch (SQLException e){
            throw new Exception("ERROR MySQL "+e.getMessage());
        }

        return prestamosUsuario;
    }

    public List<Prestamo> todos() throws Exception {
        List<Prestamo>prestamosTodos = new ArrayList<>();
        String sqlListUsuario ="select dni, isbn, fInicio, fLimite, devuelto, fDevolucion "+
                "from prestamo ";

        String leerLibros = "SELECT l.isbn, l.titulo, l.anio, l.categoria, " +
                "al.duracion_segundos, al.formato " +
                "from libro l " +
                "left JOIN audiolibro al ON al.isbn = l.isbn "+
                "where l.isbn = ?";
        String leerAutores = "SELECT a.nombre, a.apellidos, a.nacionalidad " +
                "FROM autor a " +
                "INNER JOIN libro_autor la ON la.idAutor = a.idAutor " +
                "WHERE la.isbn = ?";

        Connection con = Conexion.establecerConexion();
        try(PreparedStatement psListPrestamosUsuario = con.prepareStatement(sqlListUsuario);
            PreparedStatement psListLeerLibros = con.prepareStatement(leerLibros);
            PreparedStatement psListLeerAutores= con.prepareStatement(leerAutores)){
            //psListPrestamosUsuario.setString(1,usuario.getDni());
            try(ResultSet filas= psListPrestamosUsuario.executeQuery()){
                while(filas.next()){
                    String dni = filas.getString("dni");
                    String isbn = filas.getString("isbn");
                    LocalDate fInicio = filas.getDate("fInicio").toLocalDate();
                    LocalDate fLimite = filas.getDate("fLimite").toLocalDate();
                    Boolean devuelto= filas.getBoolean("devuelto");
                    //controlamos que sea null antes de parsearlo.
                    LocalDate fDevolucion=null;
                    Date date = filas.getDate("fDevolucion");
                    if(date!=null){
                        fDevolucion = filas.getDate("fDevolucion").toLocalDate();
                    }
                    psListLeerLibros.setString(1,isbn);
                    try(ResultSet filasLibros= psListLeerLibros.executeQuery()){
                        while (filasLibros.next()){
                            Libro libro;
                            List <Autor> autores = new ArrayList<>();
                            String isbn2 = filasLibros.getString("isbn");
                            String titulo = filasLibros.getString("titulo");
                            int anio = filasLibros.getInt("anio");
                            Categoria categoria = Categoria.valueOf(filasLibros.getString("categoria"));
                            Duration duracion = Duration.ofSeconds(filasLibros.getInt("duracion_segundos"));
                            String formato = filasLibros.getString("formato");
                            if(formato==null){
                                libro = new Libro(isbn2,titulo,anio,categoria);
                            }else{
                                libro = new Audiolibro(isbn2,titulo,anio,categoria,duracion,formato);
                            }
                            psListLeerAutores.setString(1,isbn2);
                            try(ResultSet filasAutores= psListLeerAutores.executeQuery()){

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
                            Usuario usuarioDummie= new Usuario(dni,"nombre","dummie@gmail.com",
                                    new Direccion("via","numero","04001","localidad"));
                            Usuario usuario= Usuarios.getUsuarios().buscar(usuarioDummie);
                            Prestamo p = new Prestamo(libro,usuario,fInicio);
                            if(fDevolucion!=null){
                                p.marcarDevuelto(fDevolucion);
                            }
                            prestamosTodos.add(p);
                        }

                    }catch (SQLException e){
                        throw new SQLException ("ERROR MySQL "+e.getMessage());
                    }

                }
            }
        }catch (SQLException e){
            throw new Exception("ERROR MySQL "+e.getMessage());
        }

        return prestamosTodos;
    }



}
