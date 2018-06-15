package com.bebsolutions.appsolicitantesienge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;
import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {
    private FirebaseFirestore db;
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

            Map<String, Object> postValues = solicitacao.toMap();

            if (TextUtils.isEmpty(solicitacao.id)){
                db.collection("solicitacao")
                        .add(postValues)
                        .addOnSuccessListener(documentReference -> {
                            solicitacao.id = documentReference.getId();
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                            finish();
                        });
            }else{
                DocumentReference solic = db.collection("solicitacao").document(solicitacao.id);
                solic.update(postValues)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Updated Successfully") ;
                            finish();
                        });
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

        db = FirebaseFirestore.getInstance();

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

        if (!TextUtils.isEmpty(solicitacao.id)){
            DocumentReference docRef = db.collection("solicitacao").document(solicitacao.id);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                solicitacao = documentSnapshot.toObject(Solicitacao.class);
                Objects.requireNonNull(solicitacao).id = documentSnapshot.getId();
                edtSolicitante.setText(solicitacao.solicitante);
            });
        }

    }


}
