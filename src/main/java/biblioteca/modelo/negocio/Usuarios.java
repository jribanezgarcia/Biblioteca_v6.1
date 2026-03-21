package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Usuario;

import java.util.ArrayList;
import java.util.List;

public class Usuarios {
    private final List<Usuario> usuarios;

    public Usuarios() {
        this.usuarios = new ArrayList<>();
    }

    public void alta(Usuario usuario) throws Exception { //ya configuramos el metodo equals para comparar por isbn
        if(usuario==null){
            throw new Exception("El Usuario para dar de alta no puede ser nulo");
        }
        if (buscar(usuario) != null) {
            throw new Exception("!!!ERROR!!! El Usuario" + buscar(usuario).getNombre() + " ya esta dado de alta");
        }
        usuarios.add(usuario);

    }


    public boolean baja(Usuario usuario) throws Exception {
        if (usuario == null) {
            throw new Exception("No se puede dar de baja un usuario nulo");
        }
    if(usuarios.contains(usuario)){
        usuarios.remove(usuario);
        return true;
    }else{
        throw new Exception("ERROR: El Usuario no ha sido encontrado para dar de baja");
    }

    }

    public Usuario buscar(Usuario usuario) throws Exception {
        if (usuario == null) {
            throw new Exception("el usuario no puede ser nulo");
        }
        int indiceBuscar= usuarios.indexOf(usuario); //aqui si lo podemos hacer porque la clase tiene equals
        if(indiceBuscar>-1){
            return new Usuario(usuarios.get(indiceBuscar));
        }else{
            return null;
        }

    }

    //Devuelve un Array de todos los usuarios activos con copia profunda.
    public List<Usuario> todos() throws Exception {

        List <Usuario> listaSinNulos = new ArrayList<>();
        for(Usuario usuario: usuarios){
            if(usuario!=null){
                listaSinNulos.add(new Usuario(usuario));
            }
        }
        return listaSinNulos;
    }
}
