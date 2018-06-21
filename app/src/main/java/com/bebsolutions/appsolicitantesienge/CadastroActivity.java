package com.bebsolutions.appsolicitantesienge;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;
import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {
    private static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURAR_IMAGEM_ANTES = 100;
    private static final int CAPTURAR_IMAGEM_DEPOIS = 200;

    private static final int CAMERA_PERMISSAO_ANTES = 3;
    private static final int LER_EXTERNO_PERMISSAO_ANTES = 4;
    private static final int ESCREVER_EXTERNO_PERMISSAO_ANTES = 5;


    private static final int CAMERA_PERMISSAO_DEPOIS = 6;
    private static final int LER_EXTERNO_PERMISSAO_DEPOIS = 7;
    private static final int ESCREVER_EXTERNO_PERMISSAO_DEPOIS = 8;

    private String foto_nome = "";
    private Uri fileUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static final String TAG = MainActivity.class.getName();

    public static final String PARAM_ID = TAG + ".ID";

    private EditText edtSolicitante;
    private EditText edtSolicitanteEmail;
    private EditText edtSolicitanteTelefone;
    private EditText edtSolicitanteData;
    private EditText edtSolicitanteDescricao;
    private ImageButton btnFotoAntes;
    private ImageButton btnFotoDepois;
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
            solicitacao.solicitanteEmail = edtSolicitanteEmail.getText().toString();
            solicitacao.solicitanteTelefone = edtSolicitanteTelefone.getText().toString();
            solicitacao.solicitanteData = edtSolicitanteData.getText().toString();
            solicitacao.descricao = edtSolicitanteDescricao.getText().toString();
            //solicitacao.fotografiaAntes = imageAntesBase64;
            //solicitacao.fotografiaDepois = imageDepoisBase64;
            solicitacao.melhorHorario = edtSolicitanteMelhorHorario.getText().toString();
            solicitacao.solicitacaoStatus = "1";
            solicitacao.solicitacaoStatusDescricao = edtSolicitanteStatus.getText().toString();

            Map<String, Object> postValues = solicitacao.toMap();


            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            View content = getLayoutInflater().inflate(R.layout.dialog_progress, null);
            final TextView mensagem = content.findViewById(R.id.mensagem);

            mensagem.setText("Carregando...");
            final AlertDialog dialogProgress = new android.support.v7.app.AlertDialog.Builder(CadastroActivity.this)
                    .setView(content)
                    .setCancelable(false)
                    .create();

            dialogProgress.show();

            if (TextUtils.isEmpty(solicitacao.id)){
                db.collection("solicitacao")
                        .add(postValues)
                        .addOnSuccessListener(documentReference -> {
                            solicitacao.id = documentReference.getId();
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                            StorageReference storageRef = storage.getReference();
                            StorageReference spaceRef = storageRef.child(solicitacao.id + "/antes.jpg");

                            btnFotoAntes.setDrawingCacheEnabled(true);
                            btnFotoAntes.buildDrawingCache();
                            if (btnFotoAntes.getDrawable() instanceof BitmapDrawable){
                                Bitmap bitmap = ((BitmapDrawable) btnFotoAntes.getDrawable()).getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();

                                UploadTask uploadTask = spaceRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        finish();
                                        dialogProgress.dismiss();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        StorageReference storageRef = storage.getReference();
                                        StorageReference spaceRef = storageRef.child(solicitacao.id + "/depois.jpg");

                                        btnFotoDepois.setDrawingCacheEnabled(true);
                                        btnFotoDepois.buildDrawingCache();
                                        if (btnFotoDepois.getDrawable() instanceof BitmapDrawable){
                                            Bitmap bitmap = ((BitmapDrawable) btnFotoDepois.getDrawable()).getBitmap();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] data = baos.toByteArray();

                                            UploadTask uploadTask = spaceRef.putBytes(data);
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    finish();
                                                    dialogProgress.dismiss();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    finish();
                                                    dialogProgress.dismiss();
                                                }
                                            });
                                        }else{
                                            finish();
                                            dialogProgress.dismiss();
                                        }
                                    }
                                });
                            }else{
                                finish();
                                dialogProgress.dismiss();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                            finish();
                            dialogProgress.dismiss();
                        });
            }else{
                DocumentReference solic = db.collection("solicitacao").document(solicitacao.id);
                solic.update(postValues)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Updated Successfully");
                                StorageReference storageRef = storage.getReference();
                                StorageReference spaceRef = storageRef.child(solicitacao.id + "/antes.jpg");

                                btnFotoAntes.setDrawingCacheEnabled(true);
                                btnFotoAntes.buildDrawingCache();
                                if (btnFotoAntes.getDrawable() instanceof BitmapDrawable){
                                    Bitmap bitmap = ((BitmapDrawable) btnFotoAntes.getDrawable()).getBitmap();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    UploadTask uploadTask = spaceRef.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            finish();
                                            dialogProgress.dismiss();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                StorageReference storageRef = storage.getReference();
                                                StorageReference spaceRef = storageRef.child(solicitacao.id + "/depois.jpg");

                                                btnFotoDepois.setDrawingCacheEnabled(true);
                                                btnFotoDepois.buildDrawingCache();
                                                if (btnFotoDepois.getDrawable() instanceof BitmapDrawable){
                                                    Bitmap bitmap = ((BitmapDrawable) btnFotoDepois.getDrawable()).getBitmap();
                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                    byte[] data = baos.toByteArray();

                                                    UploadTask uploadTask = spaceRef.putBytes(data);
                                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            finish();
                                                            dialogProgress.dismiss();
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            finish();
                                                            dialogProgress.dismiss();
                                                        }
                                                    });
                                                }else{
                                                    finish();
                                                    dialogProgress.dismiss();
                                                }
                                        }
                                    });
                                }else{
                                    finish();
                                    dialogProgress.dismiss();
                                }

                        })
                        .addOnFailureListener(e -> {
                            finish();
                            dialogProgress.dismiss();
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
        storage = FirebaseStorage.getInstance("gs://centralsiege-82896.appspot.com");

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
        btnFotoDepois = findViewById(R.id.btnFotoDepois);
        edtSolicitanteMelhorHorario = findViewById(R.id.edtSolicitanteMelhorHorario);
        edtSolicitanteStatus = findViewById(R.id.edtSolicitanteStatus);
        Button btnGravar = findViewById(R.id.btnGravar);

        btnGravar.setOnClickListener(clickSalvar);
        btnFotoAntes.setOnClickListener(clickFoto);
        btnFotoDepois.setOnClickListener(clickFotoDepois);
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
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            View content = getLayoutInflater().inflate(R.layout.dialog_progress, null);
            final TextView mensagem = content.findViewById(R.id.mensagem);

            mensagem.setText("Carregando...");
            final AlertDialog dialogProgress = new android.support.v7.app.AlertDialog.Builder(this)
                    .setView(content)
                    .setCancelable(false)
                    .create();

            dialogProgress.show();

            DocumentReference docRef = db.collection("solicitacao").document(solicitacao.id);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                solicitacao = documentSnapshot.toObject(Solicitacao.class);
                Objects.requireNonNull(solicitacao).id = documentSnapshot.getId();
                edtSolicitante.setText(solicitacao.solicitante);
                edtSolicitanteEmail.setText(solicitacao.solicitanteEmail);
                edtSolicitanteTelefone.setText(solicitacao.solicitanteTelefone);
                edtSolicitanteData.setText(solicitacao.solicitanteData);
                edtSolicitanteDescricao.setText(solicitacao.descricao);
                if (!TextUtils.isEmpty(solicitacao.fotografiaAntes)){
                    byte[] imageAsBytes = Base64.decode(solicitacao.fotografiaAntes, Base64.DEFAULT);
                    btnFotoAntes.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }
                if (!TextUtils.isEmpty(solicitacao.fotografiaDepois)){
                    byte[] imageAsBytes = Base64.decode(solicitacao.fotografiaDepois, Base64.DEFAULT);
                    btnFotoDepois.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }
                edtSolicitanteMelhorHorario.setText(solicitacao.melhorHorario);
                edtSolicitanteStatus.setText(solicitacao.solicitacaoStatusDescricao);

                dialogProgress.dismiss();
            });

            StorageReference storageRef = storage.getReference();
            StorageReference spaceRefAntes = storageRef.child(solicitacao.id + "/antes.jpg");
            StorageReference spaceRefDepois = storageRef.child(solicitacao.id + "/depois.jpg");

            final long ONE_MEGABYTE = (1024 * 1024)*10;
            spaceRefAntes.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    btnFotoAntes.setImageBitmap(bmp);
                }
            });


            spaceRefDepois.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    btnFotoDepois.setImageBitmap(bmp);
                }
            });

        }

    }


    private void tirarFotoAntes(){
        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.CAMERA)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir camera?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, CAMERA_PERMISSAO_ANTES);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSAO_ANTES);
            return;
        }

        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir memoria?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, LER_EXTERNO_PERMISSAO_ANTES);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LER_EXTERNO_PERMISSAO_ANTES);
            return;
        }


        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir memoria?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, ESCREVER_EXTERNO_PERMISSAO_ANTES);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ESCREVER_EXTERNO_PERMISSAO_ANTES);
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
            startActivityForResult(intent, CAPTURAR_IMAGEM_ANTES);
        }
    }


    private void tirarFotoDepois(){
        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.CAMERA)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir camera?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, CAMERA_PERMISSAO_DEPOIS);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSAO_DEPOIS);
            return;
        }

        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir memoria?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, LER_EXTERNO_PERMISSAO_DEPOIS);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LER_EXTERNO_PERMISSAO_DEPOIS);
            return;
        }

        if (ContextCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(CadastroActivity.this))
                        .setMessage("Permitir memoria?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", CadastroActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, ESCREVER_EXTERNO_PERMISSAO_DEPOIS);
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ESCREVER_EXTERNO_PERMISSAO_DEPOIS);
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
            startActivityForResult(intent, CAPTURAR_IMAGEM_DEPOIS);
        }
    }

    private View.OnClickListener clickFoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tirarFotoAntes();
        }
    };

    private View.OnClickListener clickFotoDepois = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tirarFotoDepois();
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
            case CAMERA_PERMISSAO_ANTES:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFotoAntes();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a camera!", Toast.LENGTH_LONG).show();
                }
                break;
            case LER_EXTERNO_PERMISSAO_ANTES:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFotoAntes();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a memoria!", Toast.LENGTH_LONG).show();
                }
                break;
            case ESCREVER_EXTERNO_PERMISSAO_ANTES:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFotoAntes();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a memoria!", Toast.LENGTH_LONG).show();
                }
                break;
            case CAMERA_PERMISSAO_DEPOIS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFotoDepois();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a camera!", Toast.LENGTH_LONG).show();
                }
                break;
            case LER_EXTERNO_PERMISSAO_DEPOIS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFotoDepois();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a memoria!", Toast.LENGTH_LONG).show();
                }
                break;
            case ESCREVER_EXTERNO_PERMISSAO_DEPOIS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFotoDepois();
                } else {
                    Toast.makeText(CadastroActivity.this, "Sem permisão a memoria!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURAR_IMAGEM_ANTES) {
            if (resultCode == Activity.RESULT_OK) {
                //Toast.makeText(getActivity(), "Image saved to:\n" + fileUri.getPath(), Toast.LENGTH_LONG).show();
                File file = new File(fileUri.getPath());
                if (file.exists()) {
                    try {

                        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                        btnFotoAntes.setImageBitmap(bitmap);

                        file.delete();
                    } catch (Exception e) {
                        Log.e(TAG, "ERRO = ", e);
                    }
                }
            }
        }else if (requestCode == CAPTURAR_IMAGEM_DEPOIS) {
            if (resultCode == Activity.RESULT_OK) {
                //Toast.makeText(getActivity(), "Image saved to:\n" + fileUri.getPath(), Toast.LENGTH_LONG).show();
                File file = new File(fileUri.getPath());
                if (file.exists()) {
                    try {

                        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                        btnFotoDepois.setImageBitmap(bitmap);

                        file.delete();
                    } catch (Exception e) {
                        Log.e(TAG, "ERRO = ", e);
                    }
                }
            }
        }
    }
}
