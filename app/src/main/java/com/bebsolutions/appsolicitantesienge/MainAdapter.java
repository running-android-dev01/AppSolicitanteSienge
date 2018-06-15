package com.bebsolutions.appsolicitantesienge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bebsolutions.appsolicitantesienge.model.Solicitacao;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
    private List<Solicitacao> mSolicitacao;
    private final Context context;


    public MainAdapter(Context context){
        this.context = context;
    }

    public void atualizarLista(List<Solicitacao> solicitacaos){
        mSolicitacao = solicitacaos;
        notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_main, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        final Solicitacao solicitacao = mSolicitacao.get(position);

    }

    @Override
    public int getItemCount() {
        return mSolicitacao != null ? mSolicitacao.size() : 0;
    }
}
