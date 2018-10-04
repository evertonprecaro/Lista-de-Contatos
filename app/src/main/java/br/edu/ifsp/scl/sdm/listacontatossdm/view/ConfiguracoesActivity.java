package br.edu.ifsp.scl.sdm.listacontatossdm.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import br.edu.ifsp.scl.sdm.listacontatossdm.R;
import br.edu.ifsp.scl.sdm.listacontatossdm.util.Configuracoes;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfiguracoesActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.armazenamento_radioGroup)
    RadioGroup armazenamentoRadioGroup;

    private final int PERMISSAO_ARMAZENAMENTO_EXTERNO_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        ButterKnife.bind(this);

        getSupportActionBar().setSubtitle("Configurações");

        //Restaurando configuração existente
        switch (Configuracoes.getInstance().getTipoArmazenamento()){
            case Configuracoes.ARMAZENAMENTO_INTERNO:
                armazenamentoRadioGroup.check(R.id.interno_radioButton);
                break;
            case Configuracoes.ARMAZENAMENTO_EXTERNO:
                armazenamentoRadioGroup.check(R.id.externo_radioButton);
                break;
            case Configuracoes.BANCO_DE_DADOS:
                armazenamentoRadioGroup.check(R.id.bd_radioButton);
                break;
        }

        armazenamentoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.externo_radioButton){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                        int pLeitura = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                        int pEscrita = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        if (pLeitura != PackageManager.PERMISSION_GRANTED || pEscrita != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[] {
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, PERMISSAO_ARMAZENAMENTO_EXTERNO_REQUEST_CODE);
                        }
                    }
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSAO_ARMAZENAMENTO_EXTERNO_REQUEST_CODE){

            for (int permissao : grantResults){
                if (permissao != PackageManager.PERMISSION_GRANTED){
                    armazenamentoRadioGroup.check(R.id.interno_radioButton);

                    Toast.makeText(this, "Permissões não concedidas", Toast.LENGTH_SHORT).show();

                }
            }

        }
    }

    @OnClick({R.id.salvarButton, R.id.cancelarButton})
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.cancelarButton:
                finish();
                break;
            case R.id.salvarButton:
                int armazenamentoSelecionado = Configuracoes.getInstance().getTipoArmazenamento();

                switch (armazenamentoRadioGroup.getCheckedRadioButtonId()){
                    case R.id.interno_radioButton:
                        armazenamentoSelecionado = Configuracoes.ARMAZENAMENTO_INTERNO;
                        break;
                    case R.id.externo_radioButton:
                        armazenamentoSelecionado = Configuracoes.ARMAZENAMENTO_EXTERNO;
                        break;
                    case R.id.bd_radioButton:
                        armazenamentoSelecionado = Configuracoes.BANCO_DE_DADOS;
                        break;
                }

                Configuracoes.getInstance().setTipoArmazenamento(armazenamentoSelecionado);
                finish();
                break;
        }
    }
}
