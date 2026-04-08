package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prestamos {
    private List<Prestamo> prestamos;


    public Prestamos(){
        this.prestamos= new ArrayList<>();
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
        if(Libros.getLibros().buscar(libro)==null){
            throw new Exception("libro "+libro.getTitulo()+" no encontrado en BD para hacer el préstamo");
        }
        if(Usuarios.getUsuarios().buscar(usuario)==null){
            throw new Exception("usuario "+usuario.getNombre()+" no encontrado en BD para hacer el préstamo");
        }
        //hay dos columnas que tienen valores por defecto en la BD, devuelto que sera 0 y fDevolucion null
        //estos valores no se insertan porque deben estar asi al realizar el prestamo.
        Prestamo prestamoNuevo = new Prestamo(libro,usuario,fecha);
        String sqlPrestamo= "insert into prestamo (dni, isbn, fInicio, fLimite)"+
                " values (?, ?, ?, ?)";
        try(Connection con = Conexion.establecerConexion();
            PreparedStatement psPrestar = con.prepareStatement(sqlPrestamo)){
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




    //Creamos un metodo que devuelve true si se ha podido hacer la devolucion y sino false.
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
        for (Prestamo prestamo : prestamos) {
            if (prestamo!=null&& prestamo.getUsuario().equals(usuario) //Recorremos el Array, sino es nulo, si el usuario coincide e isDevuelto es false.
                    && !prestamo.isDevuelto()&& prestamo.getLibro().equals(libro)) {
                prestamo.marcarDevuelto(fechaDevolucion);
                return true;

            }

        }
        throw new Exception("La devolución del préstamo no se llegó a realizar. Datos incorrectos");
    }

    public List<Prestamo>todos(Usuario usuario) throws Exception {
        if (usuario==null){
            throw new Exception("ERROR, el usuario no existe");
        }
        List<Prestamo>prestamosUsuario = new ArrayList<>();
        for(Prestamo prestamo: prestamos){
            if(prestamo!=null&&prestamo.getUsuario().equals(usuario)&&!prestamo.isDevuelto()){
                prestamosUsuario.add(prestamo); //no hacemos una copia para controlar si esta devuelto o no.
            }
        }
        return prestamosUsuario;
    }

    public List<Prestamo> todos() throws Exception { //HACER COPIA PROFUNDA AQUI?
        List<Prestamo>prestamosTodos = new ArrayList<>();
        for(Prestamo prestamo: prestamos){
            prestamosTodos.add(prestamo);
        }
        return prestamosTodos;
    }


}
