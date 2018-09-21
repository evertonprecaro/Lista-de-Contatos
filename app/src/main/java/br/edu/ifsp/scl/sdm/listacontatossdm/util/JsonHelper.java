package br.edu.ifsp.scl.sdm.listacontatossdm.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifsp.scl.sdm.listacontatossdm.model.Contato;

public class JsonHelper {
    public static List<Contato> jsonArrayParaListaContatos(JSONArray jsonArray) throws JSONException {
        List<Contato> listaContatos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Contato contato = new Contato();
            contato.setNome(jsonObject.getString("nome"));
            contato.setEndereco(jsonObject.getString("endereco"));
            contato.setTelefone(jsonObject.getString("telefone"));
            contato.setEmail(jsonObject.getString("email"));
            listaContatos.add(contato);
        }
        return listaContatos;
    }

    public static JSONArray listaContatosParaJsonArray(List<Contato> listaContatos)
            throws JSONException {
        JSONArray retornoJSONArray = null;
        String jsonArray = "[";
        if (listaContatos != null) {
            for (Contato contato : listaContatos) {
                String json = "{\"nome\":\"" + contato.getNome() + "\",";
                json += "\"endereco\":\"" + contato.getEndereco() + "\",";
                json += "\"telefone\":\"" + contato.getTelefone() + "\",";
                json += "\"email\":\"" + contato.getEmail() + "\"}";
                jsonArray += json;
                if (listaContatos.indexOf(contato) != listaContatos.size() - 1){
                    jsonArray += ",";
                }
            }
        }
        jsonArray += "]";
        retornoJSONArray = new JSONArray(jsonArray);
        return retornoJSONArray;
    }
}
