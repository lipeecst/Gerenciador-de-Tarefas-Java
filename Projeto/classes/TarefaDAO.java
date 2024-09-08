package Projeto.classes;

import Projeto.classes.Tarefa;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.time.LocalDate;

public class TarefaDAO {
    private Connection conexao;

    public TarefaDAO() {
        try {
            conexao = DriverManager.getConnection("jdbc:sqlite:tarefa.db");
            CriarTabelaSenaoTiver();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void FecharConexao() {
        try{
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void CriarTabelaSenaoTiver() throws SQLException {
        String sqlCriarTabela = "CREATE TABLE IF NOT EXISTS Tarefas (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "titulo TEXT NOT NULL, " + "descricao TEXT NOT NULL, " + "dataVencimento DATE," + "prioridade TEXT)";
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sqlCriarTabela);
        }
    }
    public void InserirTarefa(Tarefa tarefa) {
        String sqlInserir = "INSERT INTO Tarefas (id, titulo, descricao, dataVencimento, prioridade) VALUES (null, ?, ?, ?, ?)";
        try {
            executarUpdate(sqlInserir, tarefa.getTitulo(), tarefa.getDescricao(), Date.valueOf(tarefa.getDataVencimento()), tarefa.getPrioridade());
        } catch (SQLException e) {
            System.err.println("Erro ao inserir tarefa " + tarefa);
        }
    }
    public List<Tarefa> ListarTarefa() {
        String sqlSelecionar = "SELECT id, titulo, descricao, dataVencimento, prioridade FROM tarefas";
        try {
            return executarQuery(sqlSelecionar);
            } catch (SQLException e) {
            System.err.println("Erro ao listar tarefas " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public void atualizarTarefa(Tarefa tarefa) {
        String sqlAtualizar = "UPDATE tarefas SET titulo = ?, descricao = ?, dataVencimento = ?, prioridade = ?" + "WHERE id = ?";
        try {
            executarUpdate(sqlAtualizar, tarefa.getTitulo(), tarefa.getDescricao(), Date.valueOf(tarefa.getDataVencimento()), tarefa.getPrioridade(), tarefa.getId());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar tarefa " + tarefa);
        }
    }
    public void DeletarTarefa(int id) {
        String sqlDeletar = "DELETE FROM tarefas WHERE id = ?";
        try {
            executarUpdate(sqlDeletar, id);
        } catch (SQLException e) {
            System.err.println("Erro ao deletar tarefa " + id);
        }
    }

    private void executarUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement declaracao = conexao.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                declaracao.setObject(i + 1, params[i]);
            }
            declaracao.executeUpdate();
        }
    }
    private List<Tarefa> executarQuery(String sql, Object... params) throws SQLException {
        List<Tarefa> lista = new ArrayList<>();
        try (PreparedStatement declaracao = conexao.prepareStatement(sql)) {
            for (int i = 0; i < sql.length(); i++) {
                declaracao.setObject(i + 1, params[i]);
            }
            try (ResultSet resultado = declaracao.executeQuery()) {
                while (resultado.next()) {
                    Tarefa tarefa = new Tarefa(
                            resultado.getString("titulo"),
                            resultado.getString("descricao"),
                            resultado.getDate("dataVencimento").toLocalDate(),
                            resultado.getString("prioridade"));
                    tarefa.setId(resultado.getInt("id"));
                    lista.add(tarefa);
                }
            }
        }
        return lista;

    }
}
