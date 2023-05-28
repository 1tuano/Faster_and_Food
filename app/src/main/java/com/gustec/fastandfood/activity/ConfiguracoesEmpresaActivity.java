package com.gustec.fastandfood.activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gustec.fastandfood.R;
import com.gustec.fastandfood.databinding.ActivityConfiguracoesEmpresaBinding;
import com.gustec.fastandfood.helper.ConfiguracaoFirebase;
import com.gustec.fastandfood.helper.UsuarioFirebase;
import com.gustec.fastandfood.model.Empresa;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;


public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private ActivityConfiguracoesEmpresaBinding binding;
    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo, editEmpresaTaxa;
    private CircleImageView foto;
    private ImageView camera;

    Uri localImagem;


    FirebaseAuth auth;
    FirebaseUser user;

    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracoesEmpresaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Configurações iniciais
        inicializarComponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();
        


        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF0000"));
        toolbar.setBackgroundDrawable(colorDrawable);
        toolbar.setTitleTextColor(Color.WHITE);

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, SELECAO_GALERIA);


            }

        });

        /*Recuperar dados da empresa*/
        recuperarDadosEmpresa();
        
    }

    private void showImagePicDialog() {
    }

    private void recuperarDadosEmpresa() {

        DatabaseReference empresaRef = firebaseRef
                .child("Empresas")
                .child( idUsuarioLogado );

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot.getValue() != null ){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaTaxa.setText(empresa.getPrecoEntrega().toString());
                    editEmpresaTempo.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlImagem();
                    if(!Objects.equals(urlImagemSelecionada, "")){
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(foto);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void validarDadosEmpresa(View view){

        //Valida se os campos foram preenchidos
        String nome = editEmpresaNome.getText().toString();
        String taxa = editEmpresaTaxa.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();


        if( !nome.isEmpty()){
            if( !taxa.isEmpty()){
                if( !categoria.isEmpty()){
                    if( !tempo.isEmpty()){

                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario( idUsuarioLogado );
                        empresa.setNome( nome );

                        empresa.setPrecoEntrega( Double.parseDouble(taxa) );
                        empresa.setCategoria(categoria);
                        empresa.setTempo( tempo );
                        empresa.setUrlImagem( urlImagemSelecionada );
                        empresa.salvar();
                        finish();

                    }else{
                        exibirMensagem("Digite um tempo de entrega");
                    }
                }else{
                    exibirMensagem("Digite uma categoria");
                }
            }else{
                exibirMensagem("Digite uma taxa de entrega");
            }
        }else{
            exibirMensagem("Digite um nome para a empresa");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if( imagem != null){
                    binding.imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("Empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri url = task.getResult();

                            urlImagemSelecionada = String.valueOf(url);

                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Imagem atualizada!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void inicializarComponentes() {


        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        editEmpresaTaxa = findViewById(R.id.editEmpresaTaxa);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        foto = findViewById(R.id.imagePerfilEmpresa);
        camera = findViewById(R.id.imageView3);

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empresa, menu);

        for (int i = 0; i< menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null){
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

     */
}