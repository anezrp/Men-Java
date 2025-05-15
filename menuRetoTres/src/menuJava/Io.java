package menuJava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Io {
    public static void sop(String s){
        System.out.println(s);
    }
    public static void Sop(String s){
        sop(s);
    }
    public static void SOP(String s){
        Sop(s);
    }

         
    public static Connection getConexion(String url, String user, String pass){
        Connection conn=null;
        try{
            conn= DriverManager.getConnection(url, user, pass);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return (conn);
    }

    public static boolean esConexion(String url, String user, String pass){
        Connection conn=null;
        try{
            conn= DriverManager.getConnection(url, user, pass);

        } catch (SQLException e){
            e.printStackTrace();
        }
        if (conn==null) {
            return (false);
        }
        return (true);
    }

    public static String PADL(String texto, int longitud){
        if (texto.length()>longitud) {
            return texto.substring(0, longitud);
        } else {
            while (texto.length()<longitud) {
                texto+=" ";
            }
            return texto;
        }
    }
}
