package br.edu.ifsp.scl.sdm.listacontatossdm.util;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ArmazenamentoHelper {
    // Nome do arquivo salvo no armazenamento interno/externo
    private static final String NOME_ARQUIVO = "lista_contatos";

    public static JSONArray buscarContatos(Context contexto, int tipoArmazenamento) throws IOException, JSONException{
        JSONArray jsonArray = null;
        FileInputStream fis = null;
        switch (tipoArmazenamento) {
            case Configuracoes.ARMAZENAMENTO_INTERNO:
                fis = contexto.openFileInput(NOME_ARQUIVO);
                break;
            case Configuracoes.ARMAZENAMENTO_EXTERNO:
                // Cartão de memória montado?
                String estadoArmazenamentoExterno = Environment.getExternalStorageState();
                if (estadoArmazenamentoExterno.equals(Environment.MEDIA_MOUNTED)){
                    // Buscando/Criando o diretório no cartão
                    File diretorio = contexto.getExternalFilesDir(null);
                    // Verificando existência do arquivo
                    File arquivo = new File(diretorio, NOME_ARQUIVO);
                    if (arquivo.exists()) {
                        fis = new FileInputStream(arquivo);
                    }
                }
                break;
        }
        String conteudo = lerInputStream(fis);
        if (conteudo != null && !conteudo.isEmpty()) {
            jsonArray = new JSONArray(conteudo);
        }
        return jsonArray;
    }

    private static String lerInputStream(FileInputStream fileInputStream) throws
            IOException {
        String retorno = null;
        if (fileInputStream != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder sb = new StringBuilder();
            String linha;
            while ((linha = br.readLine()) != null) {
                sb.append(linha);
            }

            // Fecha streams
            br.close();
            fileInputStream.close();
            retorno = sb.toString();
        }
        return retorno;
    }
    public static void salvarContatos(Context contexto, int tipoArmazenamento, JSONArray contatosJsonArray) throws IOException {
        FileOutputStream fileOutputStream = null;
        switch (tipoArmazenamento) {
            case Configuracoes.ARMAZENAMENTO_INTERNO:
                fileOutputStream = contexto.openFileOutput(NOME_ARQUIVO, Context.MODE_PRIVATE);
                break;
            case Configuracoes.ARMAZENAMENTO_EXTERNO:
                String estadoArmazenamentoExterno = Environment.getExternalStorageState();
                if (estadoArmazenamentoExterno.equals(Environment.MEDIA_MOUNTED) && !estadoArmazenamentoExterno.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    // Verificando existência de diretório privado no cartão
                    File diretorio = contexto.getExternalFilesDir(null);
                    // Verificando existência do arquivo
                    File arquivo = new File(diretorio, NOME_ARQUIVO);
                    if (!arquivo.exists()) {
                        arquivo.createNewFile();
                    }
                    fileOutputStream = new FileOutputStream(arquivo);
                    break;
                }
        }
        escreverOutputStream(fileOutputStream, contatosJsonArray);
    }

    private static void escreverOutputStream(FileOutputStream fileOutputStream, JSONArray contatosJsonArray) throws IOException{
        fileOutputStream.write(contatosJsonArray.toString().getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}