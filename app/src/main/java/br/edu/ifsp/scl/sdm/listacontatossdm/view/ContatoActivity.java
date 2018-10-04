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
    private String MODO = null;
    private int indexEdit;
    private Button cancelarButton;
    private Button salvarButton;

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

            if (MODO == ListaContatosActivity.EXTRA_CONTACT) {

                Contato contato = (Contato) getIntent().getSerializableExtra(ListaContatosActivity.EXTRA_CONTACT);

                if (contato != null) {
                    //Modo Detalhes
                    subtitulo = "Detalhes do Contato";
                    modoDetalhes(contato);
                }
            }
            else if (MODO == ListaContatosActivity.SET_CONTACT){

                Contato contato = (Contato) getIntent().getSerializableExtra(ListaContatosActivity.SET_CONTACT);
                indexEdit = getIntent().getIntExtra(ListaContatosActivity.INDEX_LIST_VIEW, -1);

                if (contato != null) {
                    //Edicao
                    subtitulo = "Edite o contato";
                    Edicao(contato);
                }
            }
            else{
                //Modo Novo Contato
                MODO = ListaContatosActivity.EXTRA_CONTACT;
                subtitulo = "Novo Contato";
            }
        }
        //Setando Sub-titulo
        getSupportActionBar().setSubtitle(subtitulo);
    }

    private Contato getDadosContato() {

        Contato contato = new Contato();

        contato.setNome(nomeEditText.getText().toString());
        contato.setEndereco(enderecoEditText.getText().toString());
        contato.setTelefone(telefoneEditText.getText().toString());
        contato.setEmail(emailEditText.getText().toString());

        return contato;
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

    private void Edicao(Contato contato) {

        nomeEditText.setText(contato.getNome());
        enderecoEditText.setText(contato.getEndereco());
        telefoneEditText.setText(contato.getTelefone());
        emailEditText.setText(contato.getEmail());

        salvarButton.setText("Salvar Edição");
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

                if (MODO == ListaContatosActivity.EXTRA_CONTACT) {

                    resultadoIntent.putExtra(ListaContatosActivity.EXTRA_CONTACT, contato);
                    resultadoIntent.setAction(ListaContatosActivity.EXTRA_CONTACT);
                    setResult(RESULT_OK, resultadoIntent);
                }
                else if (MODO == ListaContatosActivity.SET_CONTACT){

                    resultadoIntent.putExtra(ListaContatosActivity.SET_CONTACT, contato);
                    resultadoIntent.setAction(ListaContatosActivity.SET_CONTACT);
                    resultadoIntent.putExtra(ListaContatosActivity.INDEX_LIST_VIEW, indexEdit);
                    setResult(RESULT_OK, resultadoIntent);
                }
                finish();
                break;
        }

    }


}
