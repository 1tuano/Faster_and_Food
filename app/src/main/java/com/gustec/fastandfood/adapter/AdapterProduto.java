package com.gustec.fastandfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gustec.fastandfood.R;
import com.gustec.fastandfood.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setText("R$ " + produto.getPreco());

        String urlImageProduto = produto.getImagemProduto();

        if (urlImageProduto != null){

            try {
                Picasso.get().load(urlImageProduto).into(holder.imageProduto);
            }catch (Exception e){
                Picasso.get().load(R.drawable.padrao).into(holder.imageProduto);
            }
        }

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView valor;

        ImageView imageProduto;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            valor = itemView.findViewById(R.id.textPreco);
            imageProduto = itemView.findViewById(R.id.imageView4);
        }
    }
}
