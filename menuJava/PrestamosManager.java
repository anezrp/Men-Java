package Programacion.reto3.menuJava;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class PrestamosManager {

    static final String URL = "jdbc:mysql://localhost:3306/login_register_db";
    static final String USER = "root";
    static final String PASS = "";

    static Connection conn = Io.getConexion(URL, USER, PASS);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            Io.SOP("\n=== MENÚ PRINCIPAL ===");
            Io.SOP("1. Gestión de préstamos");
            Io.SOP("2. Gestión de libros");
            Io.SOP("3. Gestión de autores");
            Io.SOP("4. Consultas e informes");
            Io.SOP("0. Salir");
            Io.SOP("Seleccione una opción:");
            opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1 -> menuPrestamos(sc);
                case 2 -> menuLibros(sc);
                case 3 -> menuAutores(sc);
                case 4 -> menuConsultas(sc);
                case 0 -> Io.SOP("Saliendo del sistema...");
                default -> Io.SOP("Opción no válida.");
            }
        } while (opcion != 0);
    }

    // Menú préstamos
    private static void menuPrestamos(Scanner sc) {
        Io.SOP("\n--- GESTIÓN DE PRÉSTAMOS ---");
        Io.SOP("1. Registrar préstamo");
        Io.SOP("2. Registrar devolución");
        Io.SOP("3. Listar préstamos activos");
        Io.SOP("4. Historial de préstamos");
        Io.SOP("0. Volver");
        int opcion = Integer.parseInt(sc.nextLine());

        switch (opcion) {
            case 1 -> registrarNuevoPrestamo(sc);
            case 2 -> registrarDevolucion(sc);
            case 3 -> listarPrestamosActivos();
            case 4 -> listarHistorialPrestamos();
            case 0 -> {}
            default -> Io.SOP("Opción no válida.");
        }
    }

    private static void registrarNuevoPrestamo(Scanner sc) {
        try {
            Io.SOP("ID del libro:");
            int idLibro = Integer.parseInt(sc.nextLine());
            Io.SOP("ID del usuario:");
            int idUsuario = Integer.parseInt(sc.nextLine());
            LocalDate fechaPrestamo = LocalDate.now();

            String sql = "INSERT INTO prestamos (id_libro, id_usuario, fecha_prestamo) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idLibro);
                stmt.setInt(2, idUsuario);
                stmt.setDate(3, Date.valueOf(fechaPrestamo));
                stmt.executeUpdate();
                Io.SOP("Préstamo registrado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registrarDevolucion(Scanner sc) {
        try {
            Io.SOP("ID del préstamo:");
            int idPrestamo = Integer.parseInt(sc.nextLine());
            LocalDate fechaDevolucion = LocalDate.now();

            String sql = "UPDATE prestamos SET fecha_devolucion = ? WHERE id_prestamo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(fechaDevolucion));
                stmt.setInt(2, idPrestamo);
                stmt.executeUpdate();
                Io.SOP("Devolución registrada correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listarPrestamosActivos() {
        String sql = "SELECT * FROM prestamos WHERE fecha_devolucion IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Io.SOP("Préstamo ID: " + rs.getInt("id_prestamo") + ", Libro: " + rs.getInt("id_libro") + ", Usuario: " + rs.getInt("id_usuario") + ", Fecha: " + rs.getDate("fecha_prestamo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listarHistorialPrestamos() {
        String sql = "SELECT * FROM prestamos";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Io.SOP("ID: " + rs.getInt("id_prestamo") + ", Libro: " + rs.getInt("id_libro") + ", Usuario: " + rs.getInt("id_usuario") + ", Prestado: " + rs.getDate("fecha_prestamo") + ", Devuelto: " + rs.getDate("fecha_devolucion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menú libros
    private static void menuLibros(Scanner sc) {
        Io.SOP("\n--- GESTIÓN DE LIBROS ---");
        Io.SOP("1. Alta de libro");
        Io.SOP("2. Modificación de libro");
        Io.SOP("3. Baja de libro");
        Io.SOP("0. Volver");
        int opcion = Integer.parseInt(sc.nextLine());

        switch (opcion) {
            case 1 -> altaLibro(sc);
            case 2 -> modificarLibro(sc);
            case 3 -> bajaLibro(sc);
            case 0 -> {}
            default -> Io.SOP("Opción no válida.");
        }
    }

    private static void altaLibro(Scanner sc) {
        try {
            Io.SOP("Título del libro:");
            String titulo = sc.nextLine();
            Io.SOP("Fecha de publicación (YYYY-MM-DD):");
            String fecha = sc.nextLine();
            Io.SOP("ID del autor:");
            int idAutor = Integer.parseInt(sc.nextLine());

            String sql = "INSERT INTO libros (titulo, fecha_publicacion, id_autor) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, titulo);
                stmt.setDate(2, Date.valueOf(fecha));
                stmt.setInt(3, idAutor);
                stmt.executeUpdate();
                Io.SOP("Libro añadido correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void modificarLibro(Scanner sc) {
        try {
            Io.SOP("ID del libro a modificar:");
            int idLibro = Integer.parseInt(sc.nextLine());
            Io.SOP("Nuevo título:");
            String nuevoTitulo = sc.nextLine();
            Io.SOP("Nueva fecha de publicación (YYYY-MM-DD):");
            String nuevaFecha = sc.nextLine();

            String sql = "UPDATE libros SET titulo = ?, fecha_publicacion = ? WHERE id_libro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nuevoTitulo);
                stmt.setDate(2, Date.valueOf(nuevaFecha));
                stmt.setInt(3, idLibro);
                stmt.executeUpdate();
                Io.SOP("Libro modificado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void bajaLibro(Scanner sc) {
        try {
            Io.SOP("ID del libro a eliminar:");
            int idLibro = Integer.parseInt(sc.nextLine());

            String sql = "DELETE FROM libros WHERE id_libro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idLibro);
                stmt.executeUpdate();
                Io.SOP("Libro eliminado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menú autores
    private static void menuAutores(Scanner sc) {
        Io.SOP("\n--- GESTIÓN DE AUTORES ---");
        Io.SOP("1. Alta de autor");
        Io.SOP("2. Modificación de autor");
        Io.SOP("3. Baja de autor");
        Io.SOP("0. Volver");
        int opcion = Integer.parseInt(sc.nextLine());

        switch (opcion) {
            case 1 -> altaAutor(sc);
            case 2 -> modificarAutor(sc);
            case 3 -> bajaAutor(sc);
            case 0 -> {}
            default -> Io.SOP("Opción no válida.");
        }
    }

    private static void altaAutor(Scanner sc) {
        try {
            Io.SOP("Nombre del autor:");
            String nombre = sc.nextLine();
            Io.SOP("Fecha de nacimiento (YYYY-MM-DD):");
            String fecha = sc.nextLine();

            String sql = "INSERT INTO autores (nombre, fecha_nacimiento) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setDate(2, Date.valueOf(fecha));
                stmt.executeUpdate();
                Io.SOP("Autor añadido correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void modificarAutor(Scanner sc) {
        try {
            Io.SOP("ID del autor a modificar:");
            int idAutor = Integer.parseInt(sc.nextLine());
            Io.SOP("Nuevo nombre:");
            String nuevoNombre = sc.nextLine();
            Io.SOP("Nueva fecha de nacimiento (YYYY-MM-DD):");
            String nuevaFecha = sc.nextLine();

            String sql = "UPDATE autores SET nombre = ?, fecha_nacimiento = ? WHERE id_autor = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nuevoNombre);
                stmt.setDate(2, Date.valueOf(nuevaFecha));
                stmt.setInt(3, idAutor);
                stmt.executeUpdate();
                Io.SOP("Autor modificado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void bajaAutor(Scanner sc) {
        try {
            Io.SOP("ID del autor a eliminar:");
            int idAutor = Integer.parseInt(sc.nextLine());

            String sql = "DELETE FROM autores WHERE id_autor = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idAutor);
                stmt.executeUpdate();
                Io.SOP("Autor eliminado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Consultas
    private static void menuConsultas(Scanner sc) {
        Io.SOP("\n--- CONSULTAS E INFORMES ---");
        Io.SOP("1. Buscar libros por autor");
        Io.SOP("2. Listar autores por número de libros");
        Io.SOP("3. Ordenar libros por fecha de publicación");
        Io.SOP("4. Máximo y mínimo de libros por autor");
        Io.SOP("0. Volver");
        int opcion = Integer.parseInt(sc.nextLine());

        switch (opcion) {
            case 1 -> buscarLibrosPorAutor(sc);
            case 2 -> listarAutoresPorCantidad();
            case 3 -> ordenarLibrosPorFechaPublicacion();
            case 4 -> mostrarMaxMinLibrosPorAutor();
            case 0 -> {}
            default -> Io.SOP("Opción no válida.");
        }
    }

    private static void buscarLibrosPorAutor(Scanner sc) {
        try {
            Io.SOP("ID del autor:");
            int idAutor = Integer.parseInt(sc.nextLine());

            String sql = "SELECT titulo FROM libros WHERE id_autor = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idAutor);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Io.SOP("Libro: " + rs.getString("titulo"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listarAutoresPorCantidad() {
        String sql = "SELECT a.nombre, COUNT(l.id_libro) AS cantidad FROM autores a LEFT JOIN libros l ON a.id_autor = l.id_autor GROUP BY a.id_autor ORDER BY cantidad DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Io.SOP(rs.getString("nombre") + " - Libros: " + rs.getInt("cantidad"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ordenarLibrosPorFechaPublicacion() {
        String sql = "SELECT titulo, fecha_publicacion FROM libros ORDER BY fecha_publicacion DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Io.SOP(rs.getString("titulo") + " - Publicado en: " + rs.getDate("fecha_publicacion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarMaxMinLibrosPorAutor() {
        String sql = "SELECT a.nombre, COUNT(l.id_libro) AS cantidad FROM autores a LEFT JOIN libros l ON a.id_autor = l.id_autor GROUP BY a.id_autor";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
            String autorMax = "", autorMin = "";
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                String nombre = rs.getString("nombre");
                if (cantidad > max) {
                    max = cantidad;
                    autorMax = nombre;
                }
                if (cantidad < min) {
                    min = cantidad;
                    autorMin = nombre;
                }
            }
            Io.SOP("Autor con más libros: " + autorMax + " (" + max + ")");
            Io.SOP("Autor con menos libros: " + autorMin + " (" + min + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
