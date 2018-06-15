package com.bebsolutions.appsolicitantesienge;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private RecyclerView rcwSolicitacao;
    private TextView txtEmpty;
    private MainAdapter mainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        rcwSolicitacao = findViewById(R.id.rcwSolicitacao);
        txtEmpty = findViewById(R.id.txtEmpty);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, CadastroActivity.class);
            i.putExtra(CadastroActivity.PARAM_ID, "");
            startActivity(i);
        });

        setupRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }

    private void setupRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcwSolicitacao.setLayoutManager(layoutManager);

        mainAdapter = new MainAdapter(this);
        rcwSolicitacao.setAdapter(mainAdapter);

        rcwSolicitacao.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }


    private void carregarDados() {
        try {
            Dao<Solicitacao, Integer> solicitacaoDao = ((AplicationSolicitante) getApplicationContext()).getHelper().getSolicitacaoDao();

            List<Solicitacao> lSolicitacao = solicitacaoDao.queryBuilder().orderBy("id", false).query();
            mainAdapter.atualizarLista(lSolicitacao);
            rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
            txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);

        } catch (SQLException ex) {
            Log.e(TAG, "ERRO = ", ex);
        }
    }

}
