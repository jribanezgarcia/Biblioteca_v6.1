package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prestamos {
    private List<Prestamo> prestamos;


    public Prestamos(){
        this.prestamos= new ArrayList<>();
    }
    public Prestamo prestar(Libro libro, Usuario usuario, LocalDate fecha) throws Exception {
        if(libro==null){
            throw new Exception("ERROR, el libro no puede ser nulo");
        }
        if(usuario==null){
            throw new Exception("ERROR, el usuario no puede ser nulo");
        }
        if(fecha==null){
            throw new Exception("ERROR, la fecha no puede ser nula");
        }

        for(Prestamo p:prestamos){
            if(p.getLibro().equals(libro)&&p.getUsuario().equals(usuario)&&!p.isDevuelto()){ //usamos aqui el equals porque la clase Prestamo no tiene este metodo.
                throw new Exception("El Usuario no puede volver a prestar el mismo libro");
            }
        }
        Prestamo prestamoNuevo = new Prestamo(libro,usuario,fecha);
        prestamos.add(prestamoNuevo);
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
