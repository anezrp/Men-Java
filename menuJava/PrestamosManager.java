package Programacion.reto3.menuJava;

import java.sql.*;
import java.time.LocalDate; //Importa la fecha local
import java.util.Scanner; //Para realizar la entrada de datos en el menú

public class PrestamosManager {

    //Constantes de conexión para la base de datos
    static final String URL = "jdbc:mysql://localhost:3306/login_register_db";
    static final String USER = "root";
    static final String PASS = "";

    static Connection conn = Io.getConexion(URL, USER, PASS); //Conexión base de datos
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        int opcion;
        do {
            //Menú principal
            Io.SOP("\n=== MENÚ PRINCIPAL ===");
            Io.SOP("1. Gestión de préstamos");
            Io.SOP("2. Gestión de libros");
            Io.SOP("3. Gestión de autores");
            Io.SOP("4. Consultas e informes");
            Io.SOP("0. Salir");

            //Con este bucle nos aseguramos de que se lance un mensaje de error si el usuario 
            //introduce un carácter o número que no consta en el menú
            while (true) {
                Io.sop("\nSeleccione una opción: ");
                String entrada=sc.nextLine();
                try {
                    opcion = Integer.parseInt(entrada);
                    if (opcion>=0 && opcion <=4) {
                        break;
                    } else {
                        Io.sop("\nEntrada no válida. Por favor, introduce un número del menú.");
                    }
                } catch (NumberFormatException e) {
                    Io.SOP("\nEntrada no válida. Por favor, introduce un número del menú.");
                }
            }

            //Con el switch indicamos la función que se ejecuta cuando pulsamos uno de los números del menú
            switch (opcion) {
                case 1:
                    menuPrestamos();
                    break;
                case 2:
                    menuLibros(sc);
                    break;
                case 3:
                    menuAutores(sc);
                    break;
                case 4:
                    menuConsultas(sc);
                    break;
                case 0:
                    Io.SOP("Saliendo del sistema..."); //Si el usuario pulsa 0 finaliza el programa
                    break;
                default:
                    Io.sop("Entrada no válida. Por favor, introduce un número del menú. ");
                    break;
            }
        } while (opcion != 0);
    }

    //Método esperarTecla
    private static void esperarTecla() {
        //Antes de finalizar completamente cada acción implementaremos este método que nos pide insertar una tecla
        Io.SOP("\nPulsa cualquier tecla para continuar...");
        sc.nextLine();
    }
    

    //Menú préstamos
    private static void menuPrestamos() {
        int opcion;

        Io.SOP("\n--- GESTIÓN DE PRÉSTAMOS ---");
        Io.SOP("1. Registrar préstamo");
        Io.SOP("2. Registrar devolución");
        Io.SOP("3. Listar préstamos activos");
        Io.SOP("4. Historial de préstamos");
        Io.SOP("0. Volver");

        while (true) {
            try {
                opcion = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                Io.SOP("Entrada no válida. Por favor, introduce un número del menú.");
            }
        }

        switch (opcion) {
            case 1:
                registrarNuevoPrestamo(sc);
                esperarTecla();
                break;
            case 2:
                registrarDevolucion(sc);
                esperarTecla();
                break;
            case 3:
                listarPrestamosActivos();
                esperarTecla();
                break;
            case 4:
                listarHistorialPrestamos();
                esperarTecla();
                break;
            case 0:
                break;
            default:
                Io.sop("Entrada no válida. Por favor, introduce un número del menú. ");
                sc.nextLine();
                break;
        }
    }

    //Registrar un nuevo préstamo
    private static void registrarNuevoPrestamo(Scanner sc){
        try {
            //Pedimos y guardamos el id del ejemplar
            Io.SOP("\nID del ejemplar:");
            int idLibro = Integer.parseInt(sc.nextLine());
            //Y pedimos y guardamos el id de usuario
            Io.SOP("\nID del usuario:");
            int idUsuario = Integer.parseInt(sc.nextLine());

            if (puedePrestar(idUsuario)) {

                LocalDate fechaPrestamo = LocalDate.now();

                //Guardamos la fecha límite, que es 3 meses después de la que se realiza el préstamo
                LocalDate fechaLimite = fechaPrestamo.plusMonths(3);
    
                Date sqlFechaPrestamo = Date.valueOf(fechaPrestamo);
                Date sqlFechaLimite = Date.valueOf(fechaLimite);
    
                //Se inserta en la tabla prestamos el id_ejemplar, el id_usuario, la fecha_prestamo y la fecha_limite
                String sql = "INSERT INTO prestamos (id_ejemplar, id_usuario, fecha_prestamo, fecha_limite) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    //Se asignan los valores de la variable a cada parámetro de la sentencia SQL
                    stmt.setInt(1, idLibro);
                    stmt.setInt(2, idUsuario);
                    stmt.setDate(3, Date.valueOf(fechaPrestamo));
                    stmt.setDate(4, Date.valueOf(fechaLimite));
                    //Ejecuta la sentencia SQL y se modifica la base de datos
                    int filas = stmt.executeUpdate();
                    
                    if (filas>0) {
                        try(ResultSet rs = stmt.getGeneratedKeys()){
                            if (rs.next()) {
                                int idPrestamo = rs.getInt(1);
                                Io.SOP("\nPréstamo registrado correctamente. ID del préstamo: "+idPrestamo);
                            }
                        }
                    }
                }

            } else {
                System.out.println("El usuario ya tiene 3 préstamos activos. No se puede prestar otro libro.");
            }
            

           
        //}  catch(SQLError.createSQLException e){
           // Io.SOP("Hubo un problema en el create "+e.message());

        //Si no se inserta un carácter valido salta este mensaje de error
        } catch (Exception e) {
            Io.SOP("Error, inserte un caracter válido.");
            //e.printStackTrace();
        }
    }

    public static boolean puedePrestar(int idUsuario) {
        boolean puede = false;
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND fecha_devolucion IS NULL";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                int prestamosActivos = rs.getInt(1);
                puede = prestamosActivos < 3;
            }
        } catch(SQLException e) {
            System.out.println("Error verificando préstamos activos: " + e.getMessage());
        }
    
        return puede;
    }

    //Para registrar una devolución
    private static void registrarDevolucion(Scanner sc) {
        try {
            //Pide y guarda el id del préstamo
            Io.SOP("\nID del préstamo:");
            int idPrestamo = Integer.parseInt(sc.nextLine());

            LocalDate fechaDevolucion = LocalDate.now();

            //Modifica el registro del préstamo añadiéndole la fecha de devolución
            String sql = "UPDATE prestamos SET fecha_devolucion = ? WHERE id_prestamo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(fechaDevolucion));
                stmt.setInt(2, idPrestamo);
                stmt.executeUpdate();
                Io.SOP("\nDevolución registrada correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Muestra los prestamos activos
    private static void listarPrestamosActivos() {
        //Al poner que la fecha de devolución sea null, la sentencia solo encuentra los préstamos activos
        String sql = "SELECT * FROM prestamos WHERE fecha_devolucion IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            Io.sop("\n");
            while (rs.next()) {
                //Muestra los datos de cada préstamo
                Io.SOP("Préstamo ID: " + rs.getInt("id_prestamo") + ", Ejemplar: " + rs.getInt("id_ejemplar") + ", Usuario: " + rs.getInt("id_usuario") + ", Fecha: " + rs.getDate("fecha_prestamo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Muestra el historial de todos los prestamos que se han realizado
    private static void listarHistorialPrestamos() {
        //Se seleccionan todos los campos de préstamos
        String sql = "SELECT * FROM prestamos";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            Io.sop("\n");
            while (rs.next()) {
                Io.SOP("ID: " + rs.getInt("id_prestamo") + ", Ejemplar: " + rs.getInt("id_ejemplar") + ", Usuario: " + rs.getInt("id_usuario") + ", Prestado: " + rs.getDate("fecha_prestamo") + ", Devuelto: " + rs.getDate("fecha_devolucion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Menú libros
    private static void menuLibros(Scanner sc) {
        int opcion;

        Io.SOP("\n--- GESTIÓN DE LIBROS ---");
        Io.SOP("1. Alta de libro");
        Io.SOP("2. Modificación de libro");
        Io.SOP("3. Baja de libro");
        Io.SOP("0. Volver");

        while (true) {
            try {
                opcion = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                Io.SOP("Entrada no válida. Por favor, introduce un número del menú.");
            }
        }

        switch (opcion) {
            case 1:
                altaLibro(sc);
                esperarTecla();
                break;
            case 2:
                modificarLibro(sc);
                esperarTecla();
                break;
            case 3:
                bajaLibro(sc);
                esperarTecla();
                break;
            case 0:
                break;
            default:
                Io.sop("Entrada no válida. Por favor, introduce un número del menú. ");
                sc.nextLine();
                break;
        }
    }

    //Para agregar un nuevo registro en la tabla libros
    private static void altaLibro(Scanner sc) {
        try {
            //Todos los datos que se piden para luego guardarlos
            Io.SOP("\nTítulo del libro:");
            String titulo = sc.nextLine();
            Io.SOP("\nISBN:");
            int isbn = Integer.parseInt(sc.nextLine());
            Io.SOP("\nGénero:");
            String genero = sc.nextLine();
            Io.SOP("\nURL de la imagen del libro:");
            String urlImagen = sc.nextLine();
            Io.SOP("\nID del autor:");
            int idAutor = Integer.parseInt(sc.nextLine());

            //La sentencia para insertar los datos en la tabla libros
            String sqlLibro = "INSERT INTO libros (titulo, isbn, genero, imagen_url) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlLibro, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, titulo);
                stmt.setInt(2, isbn);
                stmt.setString(3, genero);
                stmt.setString(4, urlImagen);
                //Se ejecuta la sentencia y los datos se guardan
                stmt.executeUpdate();

                //Obtener el ID generado del libro
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idLibro = rs.getInt(1);

                        //Insertar el id_libro y id_autor en la tabla libauto
                        String sqlRelacion = "INSERT INTO libauto (id_libro, id_autor) VALUES (?, ?)";
                        try (PreparedStatement stmtRelacion = conn.prepareStatement(sqlRelacion)) {
                            stmtRelacion.setInt(1, idLibro);
                            stmtRelacion.setInt(2, idAutor);
                            stmtRelacion.executeUpdate();
                        }

                        //Insertar un ejemplar para este libro
                        String sqlEjemplar= "insert into ejemplares(id_libro,estado_fisico) values (?, 'Buen estado')";
                        try(PreparedStatement stmtEjemplar=conn.prepareStatement(sqlEjemplar)){
                            stmtEjemplar.setInt(1, idLibro);
                            stmtEjemplar.executeUpdate();
                        }

                        Io.sop("\nLibro añadido correctamente con ID: "+idLibro);
                    } else {
                        Io.SOP("\nError al obtener el ID del libro.");
                    }
                }
            }
        } catch (Exception e) {
            Io.sop("Error. Verifica que los datos sean correctos");
        }
    }

    //Para modificar los datos de un libro
    private static void modificarLibro(Scanner sc) {
        try {
            Io.SOP("\nID del libro a modificar:");
            int idLibro = Integer.parseInt(sc.nextLine());
            Io.SOP("\nNuevo título:");
            String nuevoTitulo = sc.nextLine();
            Io.SOP("\nNueva ISBN:");
            int isbn = Integer.parseInt(sc.nextLine());
            Io.SOP("\nNuevo género:");
            String genero = sc.nextLine();
            Io.SOP("\nNueva URL de la imagen del libro:");
            String urlImagen = sc.nextLine();
            Io.SOP("\nNuevo ID del autor:");
            int idAutor = Integer.parseInt(sc.nextLine());

            //La sentencia necesaria es un update del registro que tiene el id_libro que se ha insertado
            String sqlLibro = "UPDATE libros SET titulo = ?, isbn = ?, genero = ?, imagen_url = ? WHERE id_libro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlLibro)) {
                stmt.setString(1, nuevoTitulo);
                stmt.setInt(2, isbn);
                stmt.setString(3, genero);
                stmt.setString(4, urlImagen);
                stmt.setInt(5, idLibro);
                stmt.executeUpdate();
            }

            //También se actualizan los datos de la tabla libauto
            String sqlLibAuto = "UPDATE libauto SET id_autor = ? WHERE id_libro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlLibAuto)) {
                stmt.setInt(1, idAutor);
                stmt.setInt(2, idLibro);
                stmt.executeUpdate();
            }

            Io.SOP("\nLibro y autor actualizados correctamente.");

        } catch (Exception e) {
            Io.sop("Error. Verifica que los datos sean correctos.");
        }
    }

    //Eliminar un libro
    private static void bajaLibro(Scanner sc) {
        try {
            Io.SOP("\nID del libro a eliminar:");
            int idLibro = Integer.parseInt(sc.nextLine());

            //Con DELETE FROM se elimina el registro que contiene el id_libro que le hemos pasado
            String sql = "DELETE FROM libros WHERE id_libro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idLibro);
                stmt.executeUpdate();
                Io.SOP("\nLibro eliminado correctamente.");
            }
        } catch (Exception e) {
            Io.sop("Error. Verifica que el ID del libro sea correcto.");
        }
    }

    //Menú autores
    private static void menuAutores(Scanner sc) {
        int opcion;

        Io.SOP("\n--- GESTIÓN DE AUTORES ---");
        Io.SOP("1. Alta de autor");
        Io.SOP("2. Modificación de autor");
        Io.SOP("3. Baja de autor");
        Io.SOP("0. Volver");

        while (true) {
            try {
                opcion = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                Io.SOP("\nEntrada no válida. Por favor, introduce un número del menú.");
            }
        }

        switch (opcion) {
            case 1: 
                altaAutor(sc);
                esperarTecla();
                break;
            case 2:
                modificarAutor(sc);
                esperarTecla();
                break;
            case 3:
                bajaAutor(sc);
                esperarTecla();
                break;
            case 0:
                break;
            default:
                Io.sop("Entrada no válida. Por favor, introduce un número del menú. ");
                sc.nextLine();
                break;
        }
    }

    //Agregar un nuevo autor
    private static void altaAutor(Scanner sc) {
        try {
            Io.SOP("\nNombre del autor:");
            String nombre = sc.nextLine();
            Io.SOP("\nApellido del autor:");
            String apellido = sc.nextLine();
    
            String sql = "INSERT INTO autores (nombre, apellidos) VALUES (?, ?)";
            //Con return_generated_keys obtenemos el id de autor que se genera automáticamente
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nombre);
                stmt.setString(2, apellido);
                stmt.executeUpdate();
                
                //Monstramos un mensaje con la clave primaria que se ha creado al insertar el autor
                try(ResultSet rs=stmt.getGeneratedKeys()){
                    if (rs.next()) {
                        int idAutor = rs.getInt(1);
                        Io.sop("\nAutor añadido correctamente con ID: "+idAutor);
                    } else {
                        Io.sop("\nAutor añadido correctamente.");
                    }
                }
            }
        } catch (Exception e) {
            Io.sop("Error. Verifica que los datos sean correctos.");
        }
    }

    //Función para verificar si el id de autor que se va a crear en altaAutor ya existe o no
    private static boolean idAutorExiste(int idAutor) {
        String sql = "SELECT COUNT(*) FROM autores WHERE id_autor = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAutor);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; //Por seguridad, asumimos que existe si hay error
    }
    
    //Modifica el autor
    private static void modificarAutor(Scanner sc) {
        try {
            Io.SOP("\nID del autor a modificar:");
            int idAutor = Integer.parseInt(sc.nextLine());
            Io.SOP("\nNuevo nombre:");
            String nuevoNombre = sc.nextLine();
            Io.SOP("\nNuevo apellido:");
            String nuevosApellidos = sc.nextLine();

            String sql = "UPDATE autores SET nombre = ?, apellidos = ? WHERE id_autor = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nuevoNombre);
                stmt.setString(2, nuevosApellidos);
                stmt.setInt(3, idAutor);
                stmt.executeUpdate();
                Io.SOP("\nAutor modificado correctamente.");
            }
        } catch (Exception e) {
            Io.sop("Error. Verifica que los datos sean correctos.");
        }
    }

    //Elimina un autor
    private static void bajaAutor(Scanner sc) {
        try {
            Io.SOP("\nID del autor a eliminar:");
            int idAutor = Integer.parseInt(sc.nextLine());

            String sql = "DELETE FROM autores WHERE id_autor = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idAutor);
                stmt.executeUpdate();
                Io.SOP("\nAutor eliminado correctamente.");
            }
        } catch (Exception e) {
            Io.sop("Error. Verifica que el ID del autor sea correcto.");
        }
    }

    //Consultas
    private static void menuConsultas(Scanner sc) {
        int opcion;

        Io.SOP("\n--- CONSULTAS E INFORMES ---");
        Io.SOP("1. Buscar libros por autor");
        Io.SOP("2. Listar autores por número de libros");
        Io.sop("3. Listar ejemplares por libro");
        Io.SOP("0. Volver");

        while (true) {
            try {
                opcion = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                Io.SOP("\nEntrada no válida. Por favor, introduce un número del menú.");
            }
        }

        switch (opcion) {
            case 1:
                buscarLibrosPorAutor(sc);
                esperarTecla();
                break;
            case 2:
                listarAutoresPorCantidad();
                esperarTecla();
                break;
            case 3:
                listarEjemplaresPorLibro();
                esperarTecla();
                break;
            case 0:
                break;
            default:
                Io.sop("Entrada no válida. Por favor, introduce un número del menú. ");
                sc.nextLine();
                break;
        }
    }

    //Busca los libros por autor
    private static void buscarLibrosPorAutor(Scanner sc) {
        try {
            //Al pasarle el id_autor, busca y muestra todos los libros que tiene ese autor
            Io.SOP("\nID del autor:");
            int idAutor = Integer.parseInt(sc.nextLine());

            String sql = "select a.nombre as nombre_autor, a.apellidos as apellidos_autor, l.titulo from autores a join libauto la on a.id_autor=la.id_autor join libros l on l.id_libro=la.id_libro where a.id_autor=?";
           
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idAutor);
                ResultSet rs = stmt.executeQuery();

                //Para mostrar el nombre y apellido del autor, vamos a inicializar una variable para verificar si ha sido mostrado o no
                boolean mostrarAutor= false; 
                while (rs.next()) {
                    //Si aún no se ha mostrado, lo muestra almacenandolo en variables y usando sop de Io
                    if (!mostrarAutor) {
                        String nombreAutor= rs.getString("nombre_autor");
                        String apellidosAutor= rs.getString("apellidos_autor");
                        Io.SOP("\nAutor: "+nombreAutor+ " "+ apellidosAutor+"\n");
                        mostrarAutor=true;
                    }
                    //Después se muestran los datos de todos los libros
                    Io.sop("Libro: "+rs.getString("titulo"));
                }
            }
        } catch (Exception e) {
            Io.sop("Error. Verifica que el ID del autor sea correcto.");
        }
    }

    //Lista los autores por cantidad de libros
    private static void listarAutoresPorCantidad() {
        //Muestra todos los autores junto a la cantidad de libros que tienen en la base de datos
        String sql = "SELECT a.id_autor, a.nombre, COUNT(la.id_libro) AS cantidad_libros " +
        "FROM autores a " +
        "JOIN libauto la ON a.id_autor = la.id_autor " +
        "GROUP BY a.id_autor, a.nombre " +
        "ORDER BY cantidad_libros DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            Io.sop("\n");
            while (rs.next()) {
                Io.SOP(rs.getString("nombre") + " - Libros: " + rs.getInt("cantidad_libros"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void listarEjemplaresPorLibro(){
        try{
            Io.sop("Inserte el ID del libro: ");
            int idLibro = Integer.parseInt(sc.nextLine());

            if (!idExiste("libros", "id_libro", idLibro)) {
                Io.sop("El libro con ID "+idLibro+" no existe.");
                esperarTecla();
                return;
            }

            String sql= "select id_ejemplar, estado_fisico from ejemplares where id_libro=?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, idLibro);
                ResultSet rs= stmt.executeQuery();

                boolean hayEjemplares=false;
                Io.sop("\nEjemplares del libro ID: "+idLibro);
                Io.sop("-----------------------------");
                while (rs.next()) {
                    int idEjemplar=rs.getInt("id_ejemplar");
                    String estado=rs.getString("estado_fisico");
                    Io.sop("ID ejemplar: "+idEjemplar+" | Estado: "+estado);
                    hayEjemplares=true;
                }

                if (!hayEjemplares) {
                    Io.sop("Este libro no tiene ejemplares registrados.");
                }
            } catch (SQLException e){
                Io.sop("Error al consultar los ejemplares.");
            }
        } catch (Exception e){
            Io.sop("Error al insertar el ID del libro.");
        }
        
    }

    private static boolean idExiste(String tabla, String campo, int id) {
        String sql = "SELECT COUNT(*) FROM " + tabla + " WHERE " + campo + " = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            Io.SOP("Error al verificar existencia: " + e.getMessage());
        }
        return false;
    }
}
