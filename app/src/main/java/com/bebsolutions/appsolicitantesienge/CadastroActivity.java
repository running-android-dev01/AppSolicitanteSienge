package com.bebsolutions.appsolicitantesienge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    public static final String PARAM_ID = TAG + ".ID";

    private EditText edtSolicitante;
    private String id;
    private final View.OnClickListener clickSalvar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            edtSolicitante.setError(null);
            if (TextUtils.isEmpty(edtSolicitante.getText())) {
                edtSolicitante.setError("Informar o solicitante!");
                return;
            }
            try {
                Dao<Solicitacao, Integer> solicitacaoDao = ((AplicationSolicitante) getApplicationContext()).getHelper().getSolicitacaoDao();
                Solicitacao solicitacao = solicitacaoDao.queryBuilder().where().eq("id", id).queryForFirst();
                if (solicitacao == null) {
                    solicitacao = new Solicitacao();
                    solicitacao.id = android.text.format.DateFormat.format("yyyyMMddhhmmss", new Date()).toString();
                }

                solicitacao.solicitante = edtSolicitante.getText().toString();
                solicitacao.solicitanteEmail = "";
                solicitacao.solicitanteTelefone = "";
                solicitacao.solicitanteData = "";
                solicitacao.descricao = "";
                solicitacao.fotografia = "";
                solicitacao.melhorHorario = "";
                solicitacao.solicitacaoStatus = "";

                solicitacaoDao.createOrUpdate(solicitacao);
                finish();
            } catch (SQLException ex) {
                Log.e(TAG, "ERRO", ex);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        id = Objects.requireNonNull(getIntent().getExtras()).getString(PARAM_ID, "");

        edtSolicitante = findViewById(R.id.edtSolicitante);
        Button btnGravar = findViewById(R.id.btnGravar);

        btnGravar.setOnClickListener(clickSalvar);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }

    private void carregarDados() {
        try {
            Dao<Solicitacao, Integer> solicitacaoDao = ((AplicationSolicitante) getApplicationContext()).getHelper().getSolicitacaoDao();
            Solicitacao solicitacao = solicitacaoDao.queryBuilder().where().eq("id", id).queryForFirst();
            edtSolicitante.setText(solicitacao != null ? solicitacao.solicitante : "");
        } catch (SQLException ex) {
            Log.e(TAG, "ERRO", ex);
        }
    }

}
