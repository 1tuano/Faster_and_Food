package com.gustec.fastandfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gustec.fastandfood.R;
import com.gustec.fastandfood.adapter.AdapterPedido;
import com.gustec.fastandfood.helper.ConfiguracaoFirebase;
import com.gustec.fastandfood.helper.RecyclerItemClickListener;
import com.gustec.fastandfood.helper.UsuarioFirebase;
import com.gustec.fastandfood.model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private TextView mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF0000"));
        toolbar.setBackgroundDrawable(colorDrawable);
        toolbar.setTitleTextColor(Color.WHITE);

        //Configura recyclerview
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter( adapterPedido );

        recuperarPedidos();

        //Adiciona evento de clique no recyclerview
        recyclerPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPedidos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Pedido pedido = pedidos.get( position );
                                pedido.setStatus("finalizado");
                                pedido.atualizarStatus();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recuperarPedidos() {

            DatabaseReference pedidoRef = firebaseRef
                    .child("pedidos")
                    .child(idEmpresa);


            Query pedidoPesquisa = pedidoRef.orderByChild("status")
                    .equalTo("confirmado");


            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Carregando dados")
                    .setCancelable( false )
                    .build();
        recyclerPedidos.setVisibility(View.GONE);


            pedidoPesquisa.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    pedidos.clear();
                    if( dataSnapshot.getValue() != null ){
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            Pedido pedido = ds.getValue(Pedido.class);
                            dialog.show();
                            mensagem.setVisibility(View.GONE);
                            recyclerPedidos.setVisibility(View.VISIBLE);
                            pedidos.add(pedido);
                        }
                        adapterPedido.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }







    private void inicializarComponentes() {
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        mensagem = findViewById(R.id.textPedidos);
    }
}