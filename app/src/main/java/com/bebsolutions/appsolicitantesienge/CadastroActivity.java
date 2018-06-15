package com.bebsolutions.appsolicitantesienge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private static final String TAG = MainActivity.class.getName();

    public static final String PARAM_ID = TAG + ".ID";

    private EditText edtSolicitante;
    private Solicitacao solicitacao;

    private final View.OnClickListener clickSalvar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            edtSolicitante.setError(null);
            if (TextUtils.isEmpty(edtSolicitante.getText())) {
                edtSolicitante.setError("Informar o solicitante!");
                return;
            }
            //Dao<Solicitacao, Integer> solicitacaoDao = ((AplicationSolicitante) getApplicationContext()).getHelper().getSolicitacaoDao();
            //Solicitacao solicitacao = solicitacaoDao.queryBuilder().where().eq("id", id).queryForFirst();
            if (TextUtils.isEmpty(solicitacao.id)) {
                String key = mDatabase.child("solicitacao").push().getKey();
                solicitacao.id = key;
            }

            solicitacao.solicitanteAuth = "";
            solicitacao.solicitante = edtSolicitante.getText().toString();
            solicitacao.solicitanteEmail = "";
            solicitacao.solicitanteTelefone = "";
            solicitacao.solicitanteData = "";
            solicitacao.descricao = "";
            solicitacao.fotografia = "";
            solicitacao.melhorHorario = "";
            solicitacao.solicitacaoStatus = "";
            solicitacao.solicitacaoStatusDescricao = "";


            Map<String, Object> childUpdates = new HashMap<>();
            Map<String, Object> postValues = solicitacao.toMap();
            childUpdates.put("/solicitacao/" + solicitacao.id, postValues);
            mDatabase.updateChildren(childUpdates);

            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        solicitacao = new Solicitacao();
        solicitacao.id = Objects.requireNonNull(getIntent().getExtras()).getString(PARAM_ID, "");

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
        Query myTopPostsQuery = mDatabase.child("solicitacao").child(solicitacao.id);
        myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded");
                atualizar(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged");
                atualizar(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");
                finish();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildMoved");
                atualizar(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
                finish();
            }
        });
    }


    private void atualizar(@NonNull DataSnapshot dataSnapshot){
        switch (dataSnapshot.getKey()){
            case "solicitanteAuth":
                solicitacao.solicitanteAuth = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "solicitante":
                edtSolicitante.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                //solicitacao.solicitante = dataSnapshot.getValue().toString();
                break;
            case "solicitanteEmail":
                solicitacao.solicitanteEmail = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "solicitanteTelefone":
                solicitacao.solicitanteTelefone = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "solicitanteData":
                solicitacao.solicitanteData = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "descricao":
                solicitacao.descricao = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "fotografia":
                solicitacao.fotografia = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "melhorHorario":
                solicitacao.melhorHorario = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "solicitacaoStatus":
                solicitacao.melhorHorario = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "solicitacaoStatusDescricao":
                solicitacao.solicitacaoStatusDescricao = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
        }
    }

}
