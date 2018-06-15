package com.bebsolutions.appsolicitantesienge;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MainViewHolder  extends RecyclerView.ViewHolder {
    public TextView txtSolicitante;

    public MainViewHolder(View itemView) {
        super(itemView);

        txtSolicitante =  itemView.findViewById(R.id.txtSolicitante);
    }

}
