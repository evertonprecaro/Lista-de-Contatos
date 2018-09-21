package br.edu.ifsp.scl.sdm.listacontatossdm.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifsp.scl.sdm.listacontatossdm.R;
import br.edu.ifsp.scl.sdm.listacontatossdm.adapter.ListaContatosAdapter;
import br.edu.ifsp.scl.sdm.listacontatossdm.model.Contato;
import br.edu.ifsp.scl.sdm.listacontatossdm.util.ArmazenamentoHelper;
import br.edu.ifsp.scl.sdm.listacontatossdm.util.Configuracoes;
import br.edu.ifsp.scl.sdm.listacontatossdm.util.JsonHelper;

public class ListaContatosActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    //REQUEST_CODE para abertura da tela ContatoActivity
    private final int NOVO_CONTATO_REQUEST_CODE = 0;
    private final int EDITAR_CONTATO_REQUEST_CODE = 1;

    // Constante para passar parametros para a tela ContatoActivity - Detalhes
    public static final String CONTATO_EXTRA = "CONTATO_EXTRA";
    public static final String CONTATO_EDITAR = "CONTATO_EDITAR";

    public static final String INDEX_LIST_VIEW = "INDEX_LIST_VIEW";

    //Referências para as Views
    private ListView listaContatosListView;

    //Lista de contatos usada para preencher a ListView
    private List<Contato> listaContatos;

    //Adapter que preenche a ListView
    private ListaContatosAdapter listaContatosAdapter;

    //Share preferences usado para salvar as configurações
    private SharedPreferences sharedPreferences;
    private final String CONFIGURACOES_SHARED_PREFERENCES = "CONFIGURACOES";
    private final String TIPO_ARMAZENAMENTO_SHARED_PREFERENCES = "TIPO_ARMAZENAMENTO";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contatos);

        //Referencia para o ListView
        listaContatosListView = findViewById(R.id.listaContatosListView);

        listaContatos = new ArrayList<>();
        //preencheListaContatos();

//        List<String> listaNomes = new ArrayList<>();
//        for (Contato contato : listaContatos){
//            listaNomes.add(contato.getNome());
//        }
//        ArrayAdapter<String> listaContatosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaNomes);


        listaContatosAdapter = new ListaContatosAdapter(this, listaContatos);
        listaContatosListView.setAdapter(listaContatosAdapter);

        registerForContextMenu(listaContatosListView);

        listaContatosListView.setOnItemClickListener(this);

        //recuperando configurações do SharedPreferences
        sharedPreferences = getSharedPreferences(CONFIGURACOES_SHARED_PREFERENCES, MODE_PRIVATE);
        restauraConfiguracoes();

        restauraContatos();
    }

    private void restauraContatos() {
        try{
            JSONArray jsonArray = null;
            jsonArray = ArmazenamentoHelper.buscarContatos(this, Configuracoes.getInstance().getTipoArmazenamento());

            if (jsonArray != null){
                List<Contato> contatosSalvosList = JsonHelper.jsonArrayParaListaContatos(jsonArray);
                listaContatos.addAll(contatosSalvosList);
                listaContatosAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void restauraConfiguracoes() {
        int tipoArmazenamento = sharedPreferences.getInt(TIPO_ARMAZENAMENTO_SHARED_PREFERENCES, Configuracoes.ARMAZENAMENTO_INTERNO);

        Configuracoes.getInstance().setTipoArmazenamento(tipoArmazenamento);
    }

    private void salvaConfiguracoes() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TIPO_ARMAZENAMENTO_SHARED_PREFERENCES, Configuracoes.getInstance().getTipoArmazenamento());
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        salvaConfiguracoes();

        try{
            JSONArray jsonArray = JsonHelper.listaContatosParaJsonArray(listaContatos);
            if (jsonArray != null){
                ArmazenamentoHelper.salvarContatos(this, Configuracoes.getInstance().getTipoArmazenamento(), jsonArray);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void preencheListaContatos(){

        for(int i = 0; i < 20; i++){
            listaContatos.add(new Contato("C"+i, "ifsp", "1234", i+"@ifsp"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    //Para exibir e tratar eventos do Menu da ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.configuracaoMenuItem:
                Intent configuracoesIntent = new Intent(this, ConfiguracoesActivity.class);
                startActivity(configuracoesIntent);
                return true;
            case R.id.novoContatoMenuItem:
                //abrindo tela de novo contato
                //Intent novoContatoIntent = new Intent(this, ContatoActivity.class);
                Intent novoContatoIntent = new Intent("NOVO_CONTATO_ACTION"); //OUTRA FORMA DE ABRIR UMA ACTIVITY (definido no Manifest)
                startActivityForResult(novoContatoIntent, NOVO_CONTATO_REQUEST_CODE);
                return true;
            case R.id.sairMenuItem:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode){
            case NOVO_CONTATO_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    // Recupera o contato da Intent data
                    Contato novoContato = (Contato) data.getSerializableExtra(CONTATO_EXTRA);
                    // Atualiza a lista e notifico o adapter
                    if (novoContato != null) {
                        listaContatos.add(novoContato);
                        listaContatosAdapter.notifyDataSetChanged();

                        Toast.makeText(this, "Novo Contato adicionado!", Toast.LENGTH_SHORT).show();
                    }
                }
                if (resultCode == RESULT_CANCELED){
                        Toast.makeText(this, "Cadastro Cancelado!", Toast.LENGTH_SHORT).show();
                }
            case EDITAR_CONTATO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //atualizar lista com o usuário atualizado
                    Contato editarContato = (Contato) data.getSerializableExtra(CONTATO_EDITAR);
                    int index = data.getIntExtra(INDEX_LIST_VIEW, -1);
                    if (index != -1) {
                        listaContatos.set(index, editarContato);
                        Toast.makeText(this, "Contato Atualizado!", Toast.LENGTH_SHORT).show();
                    }
                    listaContatosAdapter.notifyDataSetChanged();
                    break;
                }
        }
    }

    //Para exibir o meno de Contexto
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contexto, menu);
    }

    //Para saber que item foi selecionado
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo infoMenu = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Contato contato = listaContatos.get(infoMenu.position);

        switch (item.getItemId()){
            case R.id.editarContatoMenuItem:
                abrirTelaEditarContato(contato, infoMenu.position);
                return true;
            case R.id.ligarContatoMenuItem:
                //Abrir Intent para ligar para o contato
                Uri telefoneUri = Uri.parse("tel:" + contato.getTelefone()); //o prefixo 'tel:' é necessário
                Intent ligarContato = new Intent(Intent.ACTION_DIAL, telefoneUri);
                startActivity(ligarContato);
                return true;
            case R.id.verEnderecoMenuItem:
                //Exibir o Endereço do Contato no MAPS
                Uri enderecoUri = Uri.parse("geo:0,0?q=" + contato.getEndereco()); //prefixo necessário
                Intent verEnderecoIntente = new Intent(Intent.ACTION_VIEW, enderecoUri);
                startActivity(verEnderecoIntente);
                return true;
            case R.id.enviarEmailMenuItem:
                Uri emailUri = Uri.parse("mailto:" + contato.getEmail()); //prefixo necessário
                Intent enviarEmailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
                startActivity(enviarEmailIntent);
                return true;
            case R.id.removerContatoMenuItem:
                removerContato(infoMenu.position);
                return true;
        }
        return false;
    }

    //cria uma nova caixa de dialogo para confirmação da exclusão
    private void removerContato(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Confirmar remoção?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listaContatos.remove(position);
                listaContatosAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nenhuma ação
            }
        });

        AlertDialog removeAlertDialog = builder.create();
        removeAlertDialog.show();
    }

    private void abrirTelaEditarContato(Contato contato, int position) {

        Intent editarContatoIntent = new Intent(this, ContatoActivity.class);

        editarContatoIntent.putExtra(CONTATO_EDITAR, contato);
        editarContatoIntent.putExtra(INDEX_LIST_VIEW, position);
        editarContatoIntent.setAction(CONTATO_EDITAR);
        startActivityForResult(editarContatoIntent, EDITAR_CONTATO_REQUEST_CODE);

    }


    //Ao clicar em algum item da lista exibe apenas as informações
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Contato contato = listaContatos.get(position);
        Intent detalhesContatoIntent = new Intent(this, ContatoActivity.class);

        detalhesContatoIntent.putExtra(CONTATO_EXTRA, contato);
        detalhesContatoIntent.setAction(CONTATO_EXTRA);
        startActivity(detalhesContatoIntent);

    }
}
