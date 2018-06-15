package com.bebsolutions.appsolicitantesienge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private DatabaseReference mDatabase;

    private RecyclerView rcwSolicitacao;
    private TextView txtEmpty;
    private MainAdapter mainAdapter;
    private List<Solicitacao> lSolicitacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        lSolicitacao = new ArrayList<>();
        rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
        txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);
        Query myTopPostsQuery = mDatabase.child("solicitacao");
        myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded");

                Solicitacao solicitacao = new Solicitacao();
                solicitacao.getMap(dataSnapshot.getKey(), (HashMap<String, Object>)dataSnapshot.getValue());

                lSolicitacao.add(solicitacao);

                rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
                txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);

                mainAdapter.atualizarLista(lSolicitacao);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged");

                for (Solicitacao solicitacao: lSolicitacao) {
                    if (solicitacao.id.equals(dataSnapshot.getKey())){
                        Solicitacao solic = new Solicitacao();
                        solic.getMap(dataSnapshot.getKey(), (HashMap<String, Object>)dataSnapshot.getValue());

                        solicitacao.solicitanteAuth = solic.solicitanteAuth;
                        solicitacao.solicitante = solic.solicitante;
                        solicitacao.solicitanteEmail = solic.solicitanteEmail;
                        solicitacao.solicitanteTelefone = solic.solicitanteTelefone;
                        solicitacao.solicitanteData = solic.solicitanteData;
                        solicitacao.descricao = solic.descricao;
                        solicitacao.fotografia = solic.fotografia;
                        solicitacao.melhorHorario = solic.melhorHorario;
                        solicitacao.solicitacaoStatus = solic.solicitacaoStatus;
                        solicitacao.solicitacaoStatusDescricao = solic.solicitacaoStatusDescricao;
                    }
                }

                mainAdapter.atualizarLista(lSolicitacao);

                rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
                txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");

                for (int i=0; i<lSolicitacao.size(); i++){
                    if (lSolicitacao.get(i).id.equals(dataSnapshot.getKey())){
                        lSolicitacao.remove(i);
                        break;
                    }
                }

                mainAdapter.atualizarLista(lSolicitacao);

                rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
                txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildMoved");

                for (Solicitacao solicitacao: lSolicitacao) {
                    if (solicitacao.id.equals(dataSnapshot.getKey())){
                        Solicitacao solic = new Solicitacao();
                        solic.getMap(dataSnapshot.getKey(), (HashMap<String, Object>)dataSnapshot.getValue());

                        solicitacao.solicitanteAuth = solic.solicitanteAuth;
                        solicitacao.solicitante = solic.solicitante;
                        solicitacao.solicitanteEmail = solic.solicitanteEmail;
                        solicitacao.solicitanteTelefone = solic.solicitanteTelefone;
                        solicitacao.solicitanteData = solic.solicitanteData;
                        solicitacao.descricao = solic.descricao;
                        solicitacao.fotografia = solic.fotografia;
                        solicitacao.melhorHorario = solic.melhorHorario;
                        solicitacao.solicitacaoStatus = solic.solicitacaoStatus;
                        solicitacao.solicitacaoStatusDescricao = solic.solicitacaoStatusDescricao;
                    }
                }

                rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
                txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
                rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
                txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

}
