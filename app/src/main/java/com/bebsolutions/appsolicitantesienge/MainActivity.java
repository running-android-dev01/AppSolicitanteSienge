package com.bebsolutions.appsolicitantesienge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View content = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        final TextView mensagem = content.findViewById(R.id.mensagem);

        mensagem.setText("Carregando...");
        final AlertDialog dialogProgress = new android.support.v7.app.AlertDialog.Builder(this)
                .setView(content)
                .setCancelable(false)
                .create();

        dialogProgress.show();


        lSolicitacao = new ArrayList<>();
        rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
        txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);

        db.collection("solicitacao")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Solicitacao solicitacao = document.toObject(Solicitacao.class);
                            solicitacao.id = document.getId();

                            lSolicitacao.add(solicitacao);
                        }

                        rcwSolicitacao.setVisibility(lSolicitacao.size() > 0 ? View.VISIBLE : View.GONE);
                        txtEmpty.setVisibility(lSolicitacao.size() == 0 ? View.VISIBLE : View.GONE);

                        mainAdapter.atualizarLista(lSolicitacao);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    dialogProgress.dismiss();
                });
    }


}
