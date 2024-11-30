package ar.edu.utn.dds.k3003.persist;

import java.sql.*;
import java.util.Optional;

public class ColaboradorChatRepository {

    private static final String DB_URL = System.getenv("javax.persistence.jdbc.url");
    private static final String USER = System.getenv("javax.persistence.jdbc.user");
    private static final String PASSWORD = System.getenv("javax.persistence.jdbc.password");

    // Guardar o actualizar el chatId de un colaborador
    public void saveOrUpdate(Long colaborador_id, String userChat) {
        String sql = "INSERT INTO colaborador_chat (id_colaborador, chat_id) VALUES (?, ?) " +
                "ON CONFLICT (id_colaborador) DO UPDATE SET chat_id = EXCLUDED.chat_id";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.toIntExact(colaborador_id));
            stmt.setString(2, userChat);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar el chatId por idColaborador
    public String findChatIdByIdColaborador(int idColaborador) {
        String sql = "SELECT chat_id FROM colaborador_chat WHERE id_colaborador = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idColaborador);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("chat_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra el chatId
    }
}
