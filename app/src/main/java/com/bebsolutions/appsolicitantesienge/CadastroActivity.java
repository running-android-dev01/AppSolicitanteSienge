package com.bebsolutions.appsolicitantesienge;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {
    private static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private static final int MY_PERMISSIONS_CAMERA = 3;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 4;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 5;

    private String foto_nome = "";
    private Uri fileUri;
    private String imageStringBase64 = "";

    private FirebaseFirestore db;
    private static final String TAG = MainActivity.class.getName();

    public static final String PARAM_ID = TAG + ".ID";

    private EditText edtSolicitante;
    private EditText edtSolicitanteEmail;
    private EditText edtSolicitanteTelefone;
    private EditText edtSolicitanteData;
    private EditText edtSolicitanteDescricao;
    private ImageButton btnFotoAntes;
    private EditText edtSolicitanteMelhorHorario;
    private EditText edtSolicitanteStatus;
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
        edtSolicitanteEmail = findViewById(R.id.edtSolicitanteEmail);
        edtSolicitanteTelefone = findViewById(R.id.edtSolicitanteTelefone);
        edtSolicitanteData = findViewById(R.id.edtSolicitanteData);
        edtSolicitanteDescricao = findViewById(R.id.edtSolicitanteDescricao);
        btnFotoAntes = findViewById(R.id.btnFotoAntes);
        edtSolicitanteMelhorHorario = findViewById(R.id.edtSolicitanteMelhorHorario);
        edtSolicitanteStatus = findViewById(R.id.edtSolicitanteStatus);
        Button btnGravar = findViewById(R.id.btnGravar);

        btnGravar.setOnClickListener(clickSalvar);
        btnFotoAntes.setOnClickListener(clickFoto);
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

    private void atualizar(@NonNull DataSnapshot dataSnapshot){
        switch (dataSnapshot.getKey()){
            case "solicitanteAuth":
                solicitacao.solicitanteAuth = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "solicitante":
                edtSolicitante.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                break;
            case "solicitanteEmail":
                edtSolicitanteEmail.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                break;
            case "solicitanteTelefone":
                edtSolicitanteTelefone.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                break;
            case "solicitanteData":
                edtSolicitanteData.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                break;
            case "descricao":
                edtSolicitanteDescricao.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                break;
            case "fotografia":
                solicitacao.fotografia = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "melhorHorario":
                edtSolicitanteMelhorHorario.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                break;
            case "solicitacaoStatus":
                solicitacao.melhorHorario = dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString();
                break;
            case "solicitacaoStatusDescricao":
                edtSolicitanteStatus.setText(dataSnapshot.getValue()==null?"":dataSnapshot.getValue().toString());
                break;
        }
    }


    private void tirarFoto(){
        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.CAMERA)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir camera?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, MY_PERMISSIONS_CAMERA);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
            return;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                            .setMessage("Permitir memoria?")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                            })
                            .setNegativeButton("Cancelar", null)
                            .create()
                            .show();
                    return;
                }
                ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                return;
            }
        }




        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir memoria?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            return;
        }

        foto_nome = android.text.format.DateFormat.format("yyyyMMddhhmmss", new Date()).toString();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        } else {
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            File file = new File(fileUri.getPath());
            Uri fileContent = FileProvider.getUriForFile(Objects.requireNonNull(CadastroActivity.this), "com.bebsolutions.appsolicitantesienge", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileContent);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(CadastroActivity.this.getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private View.OnClickListener clickFoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tirarFoto();
        }
    };


    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = android.text.format.DateFormat.format("yyyyMMdd_HHmmss", new Date()).toString();
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + foto_nome + "_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + foto_nome + "_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFoto();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a camera!", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFoto();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a memoria!", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFoto();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a memoria!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Toast.makeText(getActivity(), "Image saved to:\n" + fileUri.getPath(), Toast.LENGTH_LONG).show();
                File file = new File(fileUri.getPath());
                if (file.exists()) {
                    try {

                        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();

                        imageStringBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        byte[] imageAsBytes = Base64.decode(imageStringBase64, Base64.DEFAULT);

                        btnFotoAntes.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

                        file.delete();
                    } catch (Exception e) {
                        Log.e(TAG, "ERRO = ", e);
                    }
                }
            }
        }
    }
}
