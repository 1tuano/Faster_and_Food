package com.gustec.fastandfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gustec.fastandfood.R;
import com.gustec.fastandfood.adapter.AdapterEmpresa;
import com.gustec.fastandfood.databinding.ActivityHomeBinding;
import com.gustec.fastandfood.helper.ConfiguracaoFirebase;
import com.gustec.fastandfood.helper.RecyclerItemClickListener;
import com.gustec.fastandfood.model.Empresa;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FirebaseAuth auth;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Faster and Food");
        setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF0000"));
        toolbar.setBackgroundDrawable(colorDrawable);
        toolbar.setTitleTextColor(Color.WHITE);

        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Configura recyclerview
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter( adapterEmpresa );

        //Recupera empresas
        recuperarEmpresas();

        //Configuração do search view
        searchView.setHint("Pesquisar restaurantes");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas( newText );
                return true;
            }
        });

        //Configurar evento de clique
        recyclerEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerEmpresa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Empresa empresaSelecionada = empresas.get(position);
                                Intent i = new Intent(HomeActivity.this, CardapioActivity.class);
                                i.putExtra("Empresa", empresaSelecionada);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void pesquisarEmpresas(String pesquisa) {

        DatabaseReference empresasRef = firebaseRef
                .child("Empresas");
        Query query = empresasRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff" );

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add( ds.getValue(Empresa.class) );
                }

                adapterEmpresa.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarEmpresas() {

        DatabaseReference empresaRef = firebaseRef.child("Empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add( ds.getValue(Empresa.class) );
                }

                adapterEmpresa.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void inicializarComponentes() {
        searchView = findViewById(R.id.materialSearchView);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);
        //Configurar botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
        for (int i = 0; i< menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null){
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class));
    }

    private void deslogarUsuario() {
        try {
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, AutenticacaoActivity.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}