package com.manitsche.appreceitas.bdhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.manitsche.appreceitas.model.Ingrediente;
import com.manitsche.appreceitas.model.Receita;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDados extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "AppReceitas.db";
    private static final int VERSAO_BANCO = 1;

    private static final String CRIA_TABELA_RECEITA =
            "CREATE TABLE RECEITA (" +
                    "idreceita INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titulo TEXT, " +
                    "imagem TEXT, " +
                    "modopreparo TEXT UNIQUE)";

    private static final String CRIA_TABELA_INGREDIENTE =
            "CREATE TABLE INGREDIENTE (" +
                    "idingrediente INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT UNIQUE, " +
                    "idreceita INTEGER, " +
                    "FOREIGN KEY (idreceita) REFERENCES RECEITA(idreceita))";

    public BancoDeDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
        Log.d("BancoDeDados", "Banco de dados iniciado 🚗" + " - " + context.getClass().getName());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CRIA_TABELA_RECEITA);
        db.execSQL(CRIA_TABELA_INGREDIENTE);
        Log.d("BancoDeDados", "Tabelas criadas com sucesso");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS INGREDIENTE");
        db.execSQL("DROP TABLE IF EXISTS RECEITA");
        onCreate(db);
        Log.d("BancoDeDados", "Banco de dados atualizado da versão " + oldVersion + " para a versão " + newVersion);
    }

    @Override
    public synchronized void close() {
        super.close();
        Log.d("BancoDeDados", "Banco de dados fechado 🔐");
    }

    // Metodos referentes a tabela RECEITA

    public Receita inserirReceita(Receita receita) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("titulo", receita.getTitulo().toUpperCase());
        valores.put("imagem", receita.getImagem());
        valores.put("modopreparo", receita.getModopreparo().toUpperCase());

        try {
            long idreceita = db.insert("RECEITA", null, valores);

            if (idreceita == -1) {
                Log.e("BancoDeDados", "Erro ao adicionar a receita");
                return null;
            } else {
                receita.setIdreceita((int) idreceita);
                Log.i("BancoDeDados", "Receita inserida com sucesso - idreceita: " + idreceita +
                        ", titulo: " + receita.getTitulo().toUpperCase() +
                        ", imagem: " + receita.getImagem() +
                        ", modopreparo: " + receita.getModopreparo());
                return receita;
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao adicionar receita: " + e.getMessage(), e);
            return null;
        } finally {
            db.close();
        }
    }

    public List<Receita> listarReceitas() {
        List<Receita> receitas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT idreceita, titulo, imagem, modopreparo FROM RECEITA", null);

            while (cursor.moveToNext()) {
                long idreceita = cursor.getLong(cursor.getColumnIndexOrThrow("idreceita"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String imagem = cursor.getString(cursor.getColumnIndexOrThrow("imagem"));
                String modopreparo = cursor.getString(cursor.getColumnIndexOrThrow("modopreparo"));

                Receita receita = new Receita(idreceita, titulo, imagem, modopreparo);
                receitas.add(receita);
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao listar receitas: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return receitas;
    }

    public void atualizarReceita(Receita receitaAtualizada) throws Exception {
        SQLiteDatabase db = null;

        try {
            if (receitaAtualizada == null || receitaAtualizada.getIdreceita() <= 0) {
                throw new Exception("Receita inválida ou idreceita inválido.");
            }

            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("titulo", receitaAtualizada.getTitulo());
            values.put("imagem", receitaAtualizada.getImagem());
            values.put("modopreparo", receitaAtualizada.getModopreparo());

            // Log dos valores que estão sendo atualizados
            Log.d("BancoDeDados", "Valores a serem atualizados: " + values.toString());

            // Atualiza a movimentação com base no ID da movimentação
            int rowsUpdated = db.update("MOVIMENTACAO", values, "idmovimentacao = ?",
                    new String[]{String.valueOf(receitaAtualizada.getIdreceita())});

            // Log do número de linhas atualizadas
            Log.d("BancoDeDados", "Linhas atualizadas: " + rowsUpdated);

            if (rowsUpdated <= 0) {
                throw new Exception("Erro ao atualizar a receita. Nenhuma linha foi atualizada. Verifique se o idreceita é válido.");
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao atualizar receita: ", e);
            throw e;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public void excluirReceita(Receita receita) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT idreceita FROM RECEITA WHERE idreceita = ?", new String[]{String.valueOf(receita.getIdreceita())});
            if (cursor.moveToFirst()) {
                long idreceita = cursor.getLong(0);
                cursor.close();

                int rowsDeleted = db.delete("RECEITA", "idreceita = ?", new String[]{String.valueOf(idreceita)});
                if (rowsDeleted <= 0) {
                    Log.e("BancoDeDados", "Nenhuma receita foi excluída.");
                } else {
                    Log.d("BancoDeDados", "Receita excluída com sucesso: ID " + idreceita);
                }
            } else {
                Log.e("BancoDeDados", "Receita não encontrada.");
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao excluir receita: " + e.getMessage(), e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Metodos referentes a tabelq INGREDIENTE

    public Ingrediente inserirIngrediente(Ingrediente ingrediente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        try {
            // Utiliza o idReceita diretamente do objeto ingrediente
            long idReceita = ingrediente.getIdreceita(); // Obtém o idReceita associado ao ingrediente

            // Verificar se o idReceita é válido
            if (idReceita == -1) {
                Log.e("BancoDeDados", "idReceita inválido: " + idReceita);
                return null;  // Retorna null se o idReceita não for válido
            }

            // Preencher os valores a serem inseridos na tabela INGREDIENTE
            valores.put("nome", ingrediente.getNome().toUpperCase());  // Nome do ingrediente em maiúsculas
            valores.put("idreceita", idReceita);  // Associar o idReceita à tabela INGREDIENTE

            // Inserir o ingrediente no banco de dados
            long idIngrediente = db.insert("INGREDIENTE", null, valores); // Inserção no banco de dados

            // Verificar se a inserção foi bem-sucedida
            if (idIngrediente != -1) {
                // Atribuir o ID gerado ao objeto Ingrediente
                ingrediente.setIdingrediente((int) idIngrediente);  // Convertendo o long retornado para int

                Log.i("BancoDeDados", "Ingrediente inserido com sucesso - idingrediente: " + idIngrediente +
                        ", nome: " + ingrediente.getNome().toUpperCase() +
                        ", idreceita: " + idReceita);
            } else {
                Log.e("BancoDeDados", "Erro ao adicionar o ingrediente");
                return null; // Retorna null se a inserção falhar
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao adicionar ingrediente: " + e.getMessage(), e);
            return null;  // Retorna null se ocorrer alguma exceção
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Fecha a conexão com o banco de dados
            }
        }

        return ingrediente; // Retorna o objeto Ingrediente atualizado com o ID
    }

    public List<Ingrediente> listarIngredientes() {
        List<Ingrediente> ingredientes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT idingrediente, nome FROM INGREDIENTE", null);

            while (cursor.moveToNext()) {
                long idingrediente = cursor.getLong(cursor.getColumnIndexOrThrow("idingrediente"));
                String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                long idreceita = cursor.getLong(cursor.getColumnIndexOrThrow("idreceita"));

                Ingrediente ingrediente = new Ingrediente(idingrediente, nome, idreceita);
                ingredientes.add(ingrediente);
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao listar ingredientes: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return ingredientes;
    }

    public void atualizarIngrediente(Ingrediente novoIngrediente, Ingrediente ingredienteAntigo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nome", novoIngrediente.getNome().toUpperCase());

        try {
            int rowsAffected = db.update("INGREDIENTE", valores, "nome = ?", new String[]{ingredienteAntigo.getNome().toUpperCase()});
            if (rowsAffected > 0) {
                Log.d("BancoDeDados", "Ingrediente atualizado com sucesso: " + novoIngrediente.getNome().toUpperCase());
            } else {
                Log.e("BancoDeDados", "Nenhum ingrediente foi atualizado.");
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao atualizar ingrediente: " + e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    public void excluirIngrediente(Ingrediente ingrediente) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT idingrediente FROM INGREDIENTE WHERE nome = ?", new String[]{ingrediente.getNome().toUpperCase()});
            if (cursor.moveToFirst()) {
                long idIngrediente = cursor.getLong(0);
                cursor.close();

                // Exclui o ingrediente
                int rowsDeleted = db.delete("INGREDIENTE", "idingrediente = ?", new String[]{String.valueOf(idIngrediente)});
                if (rowsDeleted <= 0) {
                    Log.e("BancoDeDados", "Nenhum ingrediente foi excluído.");
                } else {
                    Log.d("BancoDeDados", "Ingrediente excluído com sucesso: " + ingrediente.getNome());
                }
            } else {
                Log.e("BancoDeDados", "Ingrediente não encontrado.");
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Exceção ao excluir ingrediente: " + e.getMessage(), e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public long getIdReceitaPorTitulo(String tituloReceita) {
        SQLiteDatabase db = this.getReadableDatabase();
        long idReceita = -1; // Valor padrão para indicar que não foi encontrado

        String query = "SELECT idreceita FROM RECEITA WHERE titulo = ?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{tituloReceita.toUpperCase()});
            if (cursor.moveToFirst()) {
                idReceita = cursor.getLong(cursor.getColumnIndexOrThrow("idreceita"));
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Erro ao buscar idReceita por titulo: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return idReceita;
    }

    public List<Ingrediente> listarIngredientesPorReceita(long idReceita) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Ingrediente> ingredientes = new ArrayList<>();

        String query = "SELECT idingrediente, nome, idreceita FROM INGREDIENTE WHERE idreceita = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idReceita)});
            if (cursor.moveToFirst()) {
                do {
                    Ingrediente ingrediente = new Ingrediente(
                            cursor.getInt(cursor.getColumnIndexOrThrow("idingrediente")),
                            cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                            cursor.getLong(cursor.getColumnIndexOrThrow("idreceita"))
                    );
                    ingredientes.add(ingrediente);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BancoDeDados", "Erro ao listar ingredientes por receita: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return ingredientes;
    }
}