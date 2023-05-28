package com.gustec.fastandfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.gustec.fastandfood.R;
import com.gustec.fastandfood.helper.ConfiguracaoFirebase;
import com.gustec.fastandfood.databinding.ActivityAutenticacaoBinding;
import com.gustec.fastandfood.helper.UsuarioFirebase;




public class AutenticacaoActivity extends AppCompatActivity {

    private ActivityAutenticacaoBinding binding;
    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;
    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAutenticacaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        //Verificar usuario logado
        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){//empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else{//usuario
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        binding.logarTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tipoAcesso.isChecked()){
                    tipoAcesso.setChecked(false);
                }
            }
        });

        binding.cadastrarTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tipoAcesso.isChecked()){
                    tipoAcesso.setChecked(true);
                }
            }
        });

        binding.userCadTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tipoUsuario.isChecked()){
                    tipoUsuario.setChecked(false);
                }
            }
        });

        binding.empresaCadTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!tipoUsuario.isChecked()){
                    tipoUsuario.setChecked(true);
                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if ( !email.isEmpty() ){
                    if ( !senha.isEmpty() ){

                        //Verifica estado do switch
                        if( tipoAcesso.isChecked() ){//Cadastro

                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Cadastro realizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = getTipoUsuario();
                                        UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                        abrirTelaPrincipal(tipoUsuario);

                                    }else {

                                        String erroExcecao = "";

                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            erroExcecao = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Por favor, digite um e-mail válido";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            erroExcecao = "Este conta já foi cadastrada";
                                        } catch (Exception e) {
                                            erroExcecao = "ao cadastrar usuário: "  + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro: " + erroExcecao ,
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                        }else {//Login

                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = task.getResult().getUser().getDisplayName();
                                        abrirTelaPrincipal(tipoUsuario);

                                    }else {
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao fazer login : " + task.getException() ,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    }else {
                        Toast.makeText(AutenticacaoActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if( usuarioAtual != null ){
            String tipoUsuario = usuarioAtual.getDisplayName();
            assert tipoUsuario != null;
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario(){
        return  tipoUsuario.isChecked() ? "E" : "U";
    }

    private void abrirTelaPrincipal(String tipoUsuario){
        if(tipoUsuario.equals("E")){//Empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
            finish();
        }else{//Usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        tipoAcesso = findViewById(R.id.switchAcesso);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }

}
