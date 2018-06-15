package com.bebsolutions.appsolicitantesienge;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;

import java.util.List;

class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
    private final Context context;
    private List<Solicitacao> mSolicitacao;


    public MainAdapter(Context context) {
        this.context = context;
    }

    public void atualizarLista(List<Solicitacao> solicitacaos) {
        mSolicitacao = solicitacaos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_main, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        final Solicitacao solicitacao = mSolicitacao.get(position);

        holder.txtSolicitante.setText(solicitacao.solicitante);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, CadastroActivity.class);
            i.putExtra(CadastroActivity.PARAM_ID, solicitacao.id);
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return mSolicitacao != null ? mSolicitacao.size() : 0;
    }
}
