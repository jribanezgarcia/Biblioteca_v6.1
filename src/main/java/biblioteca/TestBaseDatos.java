package biblioteca;

import biblioteca.modelo.dominio.Direccion;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.Usuarios;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.*;

public class TestBaseDatos {
    public static void main(String[] args) throws Exception {

        try{
            insertar("Juan ","Valdes","Venezolana");
            listar();
            Direccion direccion1= new Direccion("Altamira","13","04005","Almeria");
            Usuario usuario1 = new Usuario("75238369x","rafit","jrig@gmail.com",direccion1);
            Usuarios.getUsuarios().alta(usuario1);

        }catch (Exception e){
            System.out.println("ERROR "+e.getMessage());
        }


    }
    private static void insertar(String nombre,String apellidos,String nacionalidad) throws SQLException {
        Connection conexion= Conexion.establecerConexion();
        PreparedStatement sentencia=null;
        try {
            sentencia= conexion.prepareStatement("INSERT INTO autor (nombre, apellidos, nacionalidad) VALUES (?, ?, ?)");
            sentencia.setString(1,nombre);
            sentencia.setString(2,apellidos);
            sentencia.setString(3,nacionalidad);
            int row = sentencia.executeUpdate();
            System.out.println("Filas insertadas:" + row);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        finally {
            try{
                sentencia.close();
                Conexion.cerrarConexion();
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }
    private static void listar() throws SQLException {
        Connection conexion = Conexion.establecerConexion();
        Statement sentencia = null;
        ResultSet filas = null;
        try{
            String sentenciasStr ="Select idAutor, nombre, apellidos, nacionalidad from autor";
            sentencia= conexion.createStatement();
            filas = sentencia.executeQuery(sentenciasStr);
            while (filas.next()){
                String idAutor= filas.getString(1);
                String nombre= filas.getString(2);
                String apellidos= filas.getString(3);
                String nacionalidad=filas.getString(4);
                System.out.println(idAutor+" | "+nombre+" | "+apellidos+" | "+nacionalidad);
            }

            }catch(SQLException e){
            System.out.println(e.getMessage());

        }
        finally {
            try
            {
                filas.close();
                sentencia.close();

               Conexion.cerrarConexion();

            }catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
