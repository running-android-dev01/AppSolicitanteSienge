package com.bebsolutions.appsolicitantesienge.model;

import java.util.HashMap;

public class Solicitacao {
    public String id;

    public String solicitanteAuth;

    public String solicitante;

    public String solicitanteEmail;

    public String solicitanteTelefone;

    public String solicitanteData;

    public String descricao;

    public String fotografia;

    public String melhorHorario;

    public String solicitacaoStatus;

    public String solicitacaoStatusDescricao;

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("solicitante", solicitante);
        map.put("solicitanteEmail", solicitanteEmail);
        map.put("solicitanteTelefone", solicitanteTelefone);
        map.put("solicitanteData", solicitanteData);
        map.put("descricao", descricao);
        map.put("fotografia", fotografia);
        map.put("melhorHorario", melhorHorario);
        map.put("solicitacaoStatus", solicitacaoStatus);
        map.put("solicitacaoStatusDescricao", solicitacaoStatusDescricao);

        return map;
    }
}
