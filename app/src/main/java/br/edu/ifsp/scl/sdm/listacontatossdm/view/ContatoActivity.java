package br.edu.ifsp.scl.sdm.listacontatossdm.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.edu.ifsp.scl.sdm.listacontatossdm.R;
import br.edu.ifsp.scl.sdm.listacontatossdm.model.Contato;

public class ContatoActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nomeEditText;
    private EditText enderecoEditText;
    private EditText telefoneEditText;
    private EditText emailEditText;
    private Button cancelarButton;
    private Button salvarButton;

    private String MODO = null;

    private int indexEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contato);

        //Buscando referências de layout
        nomeEditText = findViewById(R.id.nomeEditText);
        enderecoEditText = findViewById(R.id.enderecoEditText);
        telefoneEditText = findViewById(R.id.telefoneEditText);
        emailEditText = findViewById(R.id.emailEditText);

        cancelarButton = findViewById(R.id.cancelarButton);
        salvarButton = findViewById(R.id.salvarButton);

        //Setando Listeners dos botões
        cancelarButton.setOnClickListener(this);
        salvarButton.setOnClickListener(this);

        String subtitulo = "";

        MODO = getIntent().getAction();

        if (MODO != null){

            if (MODO == ListaContatosActivity.CONTATO_EXTRA) {

                Contato contato = (Contato) getIntent().getSerializableExtra(ListaContatosActivity.CONTATO_EXTRA);

                if (contato != null) {
                    //Modo Detalhes
                    subtitulo = "Detalhes do Contato";
                    modoDetalhes(contato);
                }
            }
            else if (MODO == ListaContatosActivity.CONTATO_EDITAR){

                Contato contato = (Contato) getIntent().getSerializableExtra(ListaContatosActivity.CONTATO_EDITAR);
                indexEdit = getIntent().getIntExtra(ListaContatosActivity.INDEX_LIST_VIEW, -1);

                if (contato != null) {
                    //Modo Editar
                    subtitulo = "Editar Contato";
                    modoEditar(contato);
                }
            }
            else{
                //Modo Novo Contato
                MODO = ListaContatosActivity.CONTATO_EXTRA;
                subtitulo = "Novo Contato";
            }
        }
        //Setando Sub-titulo
        getSupportActionBar().setSubtitle(subtitulo);
    }

    private void modoEditar(Contato contato) {

        nomeEditText.setText(contato.getNome());
        enderecoEditText.setText(contato.getEndereco());
        telefoneEditText.setText(contato.getTelefone());
        emailEditText.setText(contato.getEmail());

        salvarButton.setText("Salvar Edição");
    }

    private void modoDetalhes(Contato contato) {
        nomeEditText.setText(contato.getNome());
        nomeEditText.setEnabled(false);

        enderecoEditText.setText(contato.getEndereco());
        enderecoEditText.setEnabled(false);

        telefoneEditText.setText(contato.getTelefone());
        telefoneEditText.setEnabled(false);

        emailEditText.setText(contato.getEmail());
        emailEditText.setEnabled(false);

        salvarButton.setVisibility(View.GONE);
        cancelarButton.setText("Voltar");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.cancelarButton:
                //fechar/voltar tela
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.salvarButton:

                //pega os dados dos campos
                Contato contato = getDadosContato();
                //cria nova intenção para os resultados
                Intent resultadoIntent = new Intent();

                if (MODO == ListaContatosActivity.CONTATO_EXTRA) {

                    resultadoIntent.putExtra(ListaContatosActivity.CONTATO_EXTRA, contato);
                    resultadoIntent.setAction(ListaContatosActivity.CONTATO_EXTRA);
                    setResult(RESULT_OK, resultadoIntent);
                }
                else if (MODO == ListaContatosActivity.CONTATO_EDITAR){

                    resultadoIntent.putExtra(ListaContatosActivity.CONTATO_EDITAR, contato);
                    resultadoIntent.setAction(ListaContatosActivity.CONTATO_EDITAR);
                    resultadoIntent.putExtra(ListaContatosActivity.INDEX_LIST_VIEW, indexEdit);
                    setResult(RESULT_OK, resultadoIntent);
                }
                finish();
                break;
        }

    }

    private Contato getDadosContato() {

        Contato contato = new Contato();

        contato.setNome(nomeEditText.getText().toString());
        contato.setEndereco(enderecoEditText.getText().toString());
        contato.setTelefone(telefoneEditText.getText().toString());
        contato.setEmail(emailEditText.getText().toString());

        return contato;
    }


}
