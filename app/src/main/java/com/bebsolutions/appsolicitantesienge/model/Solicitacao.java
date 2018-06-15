package com.bebsolutions.appsolicitantesienge.model;

import com.j256.ormlite.field.DatabaseField;

public class Solicitacao {
    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String solicitante;

    @DatabaseField
    public String solicitanteEmail;

    @DatabaseField
    public String solicitanteTelefone;

    @DatabaseField
    public String solicitanteData;

    @DatabaseField
    public String descricao;

    @DatabaseField
    public String fotografia;

    @DatabaseField
    public String melhorHorario;

    @DatabaseField
    public String solicitacaoStatus;
}
